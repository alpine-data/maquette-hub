import click
import pandas as pd
import pipes
import os

from maquette_cli.__client import Client
from maquette_cli.__user_config import UserConfiguration
from maquette_cli.__environment import MqEnvironment

client = Client.from_config(UserConfiguration('/home'))
env = MqEnvironment.from_config('resources/env_conf.json')

@click.group()
def main():
    """
    Maquette CLI main routine.
    """
    env.init_process_envs()
    pass


@main.group()
def projects():
    """
    Commands for managing projects
    """
    pass


@projects.command("create")
@click.argument('name')
@click.option('--deactivated', default=False, required=False, is_flag=True)
@click.pass_context
def projects_init(ctx, name, deactivated):
    status, response = client.command(cmd='projects create', args={'name': name})
    if status == 200:
        print('Heureka! You created a project called ' + name + '(‘-’)人(ﾟ_ﾟ)')
        if not deactivated:
            print("DEBUG")
            ctx.invoke(activate, name=name)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


@projects.command("ls")
def projects_list():
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
    status, response = client.command(cmd='projects environment', args={'name': name})
    if status == 200:
        env_variables = response['data']
        for (env_key, env_value) in env_variables.items():
            env.add_process_env(env_key, env_value)
        env.init_process_envs()
        print('Activated project ' + name + '  c[○┬●]כ ')
        #ENV SAFETY CHECK, can be removed
        for env_key in env_variables:
            print(env_key, ": ", os.environ[env_key])
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)

@projects.command("deactivate")
def deactivate():
    env.remove_process_env()
    print('Deactivated current project')

@projects.command("rm")
@click.argument('name')
def projects_remove(name):
    status, response = client.command(cmd='projects remove', args={'name': name})
    if status == 200:
        print("You successfully killed the project " + name + " and removed all evidences (╯°□°)--︻╦╤─ ")
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


if __name__ == '__main__':
    main()