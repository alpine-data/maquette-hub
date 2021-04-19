import chevron
import click
import os
import shutil
import subprocess
import sys
import yaml

from git import Repo
from pathlib import Path
from typing import Optional

from maquette_lib.__client import Client
from maquette_lib.__user_config import EnvironmentConfiguration

class Workspaces():

    _client: Client = None

    def __init__(self, client: Client):
        self._client = client

    @staticmethod
    def apply(config: Optional[EnvironmentConfiguration] = None):
        if config is None:
            config = EnvironmentConfiguration()

        return Workspaces(Client.from_config(config))

    def create(self, generator_name: Optional[str], directory: Optional[os.PathLike] = None) -> None:
        """
        Creates a new local workspace based on a generator.

        :param generator_name The name of the generator
        :param directory Optionally the target directory, by default a new directory at the current location with the name of the generator is used.
        """

        generator_config_file = 'mq.generator.yml'
        generators = self._client.command(cmd='workspaces generators list')[1]

        while generator_name is None:
            for i in range(0, len(generators)):
                print(f"[{i}] {generators[i]['name']}")

            selected = click.prompt('Select workspace generator (enter number)', type=int)

            if selected < 0 or selected >= len(generators):
                print(f"Invalid index {selected}, try again.")
            else:
                generator_name = generators[selected]['name']

        #
        # validate input
        #

        if directory is None:
            directory = f"./{generator_name}"

        generator = list(filter(lambda g: g['name'] == generator_name, generators))

        if len(generator) == 0:
            print(f"No generator found with name `{generator_name}`")
            sys.exit(404)
        else:
            generator = generator[0]

        #
        # prepare working directory and clone generator
        #

        tmp_dir = os.path.join(Path.home(), '.mq', 'tmp')
        Path(tmp_dir).mkdir(parents=True, exist_ok=True)
        tmp_dir = os.path.join(tmp_dir, generator_name)
        if Path(tmp_dir).exists():
            shutil.rmtree(tmp_dir)

        print('Fetching generator sources ...')
        repo = Repo.clone_from(generator['repository'], tmp_dir)

        #
        # read generator configuration
        #
        generator_config = os.path.join(tmp_dir, generator_config_file)
        if Path(generator_config).exists():
            with open(generator_config) as file:
                generator_config = yaml.load(file, Loader=yaml.FullLoader)

            files = generator_config.get('include', [])
            files = [ list(Path(tmp_dir).rglob(pattern)) for pattern in files ]
            files = [ f for pattern_files in files for f in pattern_files ]

            template_data = { 
                'USER_NAME': repo.config_reader().get_value('user', 'name'),
                'USER_EMAIL': repo.config_reader().get_value('user', 'email'),
                'DIRECTORY': Path(directory).name
            }

            for variable in generator_config.get('variables', []):
                label = chevron.render(template=variable['label'], data=template_data)
                default = chevron.render(template=variable['default'], data=template_data) if variable.get('default') is not None else None
                template_data[variable['name']] = click.prompt(label, default=default)

            for f in files:
                with open(f) as f_in:
                    rendered = chevron.render(f_in, template_data)

                with open(f, 'w') as f_out:
                    f_out.write(rendered)

            if (len(files) > 0):
                print(f"Rendered {len(files)} file(s) ...")
        else:
            print('No generator configuration found... skipping')

        #
        # Cleanup and copy to target.
        #
        shutil.rmtree(Path(os.path.join(tmp_dir, '.git')))
        files = list(Path(tmp_dir).rglob('*'))
        files = filter(lambda f: f.name != generator_config_file, files)
        files = filter(lambda f: os.path.isfile(f), files)
        
        for file in files:
            rel_path = os.path.relpath(file, tmp_dir)
            target_path = os.path.join(directory, rel_path)
            Path(target_path).parent.mkdir(parents=True, exist_ok=True)
            os.rename(file, target_path)
        
        shutil.rmtree(tmp_dir)

        #
        # run custom scripts
        #
        generator_scripts = generator_config.get('scripts', [])
        for script in generator_scripts:
            print(f"Running `{script['name']}` ...")
            cmd = chevron.render(template=script['cmd'], data=template_data)
            p = subprocess.Popen(cmd, cwd=directory, shell=True)
            p.wait()
            print(f"Executed `{script['name']}`, return code: {p.returncode} ...")

        #
        # show success message
        #
        if generator_config.get('message') is not None:
            print(chevron.render(template=generator_config['message'], data=template_data))

    def generators(self) -> dict:
        result = self._client.command(cmd ='workspaces generators list', args ={}, headers={ 'Accept': 'text/plain' })
        print(result[1])