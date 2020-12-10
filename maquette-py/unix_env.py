import pipes

from maquette_lib.__environment import MqEnvironment

env = MqEnvironment.from_config('resources/env_conf.json')
for (key, value) in env._config.items():
    print('export ' + key + '=' + pipes.quote(value)+'\n')