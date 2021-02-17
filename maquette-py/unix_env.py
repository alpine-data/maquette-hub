import pipes

from maquette_lib.__user_config import EnvironmentConfiguration

def unix_env():
    env = EnvironmentConfiguration()
    for (key, value) in env.mq_yaml_list['environment'].items():
        print('export ' + key + '=' + pipes.quote(value)+'\n')


if __name__ == "__main__":
    unix_env()
