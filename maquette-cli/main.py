import click
import pandas as pd

from maquette_cli.__client import Client
from maquette_cli.__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/home'))

@click.group()
def main():
    """
    Maquette CLI main routine.
    """
    pass

@click.command()
def activate():
    "Activate a Maquette project environment"
    print('Activate project')

@click.command()
def deactivate():
    "Deactivate Maquette project environment"
    print('Deactivate project')

@main.group()
def projects():
    """
    Commands for managing projects
    """
    pass


@projects.command("create")
@click.argument('name')
def projects_init(name):
    status, response = client.command(cmd='projects create', args={'name': name})
    if status == 200:
        print('Heureka! You created a project called ' + name + '(‘-’)人(ﾟ_ﾟ)')
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

@projects.command("env")
@click.argument('name')
def projects_environment(name):
    status, response = client.command(cmd='projects environment', args={'name': name})
    if status == 200:
        print("Something with environments")
        print(response)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)

@projects.command("rm")
def projects_remove(name):
    status, response = client.command(cmd='projects remove', args={'name': name})
    if status == 200:
        print("You successfully killed the project " + name + " and removed all evidences ̿' ̿'\̵͇̿̿\з=(◕_◕)=ε/̵͇̿̿/'̿'̿ ̿")
        print(response)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + response)


if __name__ == '__main__':
    main()