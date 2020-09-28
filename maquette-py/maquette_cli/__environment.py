import json
import os

class MqEnvironment:
    _config = None
    _config_path = None

    def __init__(self, config_path):
        with open(config_path) as json_file:
            self._config = json.load(json_file)
        self._config_path = config_path

    @staticmethod
    def from_config(config_path):
        return MqEnvironment(config_path)

    def init_process_envs(self):
        for (env_key, env_value) in self._config.items():
            os.environ[env_key] = env_value

    def add_process_env(self, key, value):
        self._config[key] = value
        with open(self._config_path, 'w') as f:
            json.dump(self._config, f, ensure_ascii=False)

    def remove_process_env(self):
        self._config = {}
        with open(self._config_path, 'w') as f:
            json.dump(self._config, f, ensure_ascii=False)

    def get_property(self, property_name):
        if property_name not in self._config.keys():
            return None
        return self._config[property_name]