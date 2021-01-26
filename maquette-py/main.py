import click
import pandas as pd
import os

from maquette_lib.__client import Client
from maquette_lib.__user_config import UserConfiguration
from maquette_lib.__environment import MqEnvironment

client = Client.from_config(UserConfiguration('/home'))
env = MqEnvironment.from_config('resources/env_conf.json')


@click.group()
def main():
    """
    Maquette CLI main routine.
    """
    pass


@main.group()
def projects():
    """
    Commands for managing projects
    """
    pass


@projects.command("create")
@click.argument('name')
@click.argument('title')
@click.argument('summary')
def projects_init(name, title, summary):
    """
    Initialize a project

    Args:
        name : name of the project

    """
    status, response = client.command(cmd='projects create', args={'name': name, "title": name})
    if status == 200:
        print('Heureka! You created a project called ' + name + '(‘-’)人(ﾟ_ﾟ)\n'
               '\n'                                                 
              'To activate the project type: python main.py activate ' + name)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


@projects.command("ls")
def projects_list():
    """
    Print the list of projects.

    """
    status, response = client.command(cmd='projects list')
    if status == 200:
        table_df = pd.json_normalize(response)
        print(table_df)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


@projects.command("activate")
@click.argument('name')
def activate(name):
    """
    Activate project.

    Args:
        name : name of the project

    """
    status, response = client.command(cmd='projects environment', args={'name': name})
    if status == 200:
        env_variables = response['data']
        for (env_key, env_value) in env_variables.items():
            env.add_process_env(env_key, env_value)
        if os.name == 'posix':
            print('You are on a Unix based  system  c[○┬●]כ \n'
                  'Please copy and run the command: eval $(python unix_env.py)')
        else:
            for (env_key, env_value) in env._config.items():
                os.system("SETX {0} {1}".format(env_key, env_value))
            print('Congrats you are on a Windows machine \n'
                  'I activated your project \t\t~~\n'
                  'Now relax and enjoy a hot cup of coffee \t C|__|')

    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


@projects.command("deactivate")
def deactivate():
    """
    Currently only removes the currently activate environment variables from the config, no default env needed or available
    """
    env.remove_process_env()
    print('Removed Environment from Config')


@projects.command("rm")
@click.argument('name')
def projects_remove(name):
    """
    remove a project

    Args:
        name : name of the project

    """
    status, response = client.command(cmd='projects remove', args={'name': name})
    if status == 200:
        print("You successfully killed the project " + name + " and removed all evidences (╯°□°)--︻╦╤─ ")
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


if __name__ == '__main__':
    main()