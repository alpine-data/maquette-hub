import click

from .__client import Client
from .__user_config import UserConfiguration

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
@click.argument('name', help='the name of the project to be created')
def projects_init(name):
    response = client.command(cmd='projects create', args={'name': name})
    print(response)

@projects.command("ls")
def projects_list():
    response = client.command(cmd='projects create')
    print(response)

# projects.add_command(projects_init)
# projects.add_command(projects_list)
#
# main.add_command(activate)
# main.add_command(deactivate)
# main.add_command(projects)

if __name__ == '__main__':
    main()