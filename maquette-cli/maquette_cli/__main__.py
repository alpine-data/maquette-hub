import click

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

@click.group()
def projects():
    """
    Commands for managing projects
    """
    pass


@click.command("init")
@click.option('--count', default=1, help='number of greetings')
@click.argument('name')
def projects_init(count, name):
    print(f"Init project {name}")

@click.command("ls")
def projects_list():
    print("List projects")

projects.add_command(projects_init)
projects.add_command(projects_list)

main.add_command(activate)
main.add_command(deactivate)
main.add_command(projects)

if __name__ == '__main__':
    main()