import requests

from typing import List, Tuple

from .__user_config import UserConfiguration


class Client:
    __base_url: str = None
    __headers: dict

    def __init__(self, base_url: str, user_id: str, roles: List):
        self.__base_url = base_url
        self.__headers = {
            'x-user-id': user_id,
            'x-user-roles': ','.join(roles)
        }

    @staticmethod
    def from_config(config: UserConfiguration) -> 'Client':
        return Client(config.url(), config.user(), [])

    def command(self, cmd: str, args: dict = None) -> Tuple[int,dict]:
        request_body = { 'command': cmd }
        if args is not None:
            request_body.update(args)
        response = requests.post(self.__base_url + 'commands', json = request_body, headers = self.__headers)
        if response.status_code < 200 or response.status_code > 299:

            raise RuntimeError("call to Maquette controller was not successful ¯\\_(ツ)_/¯\n"
                               "status code: " + str(response.status_code) + ", content:\n" + response.text)
        else:
            result = response.json()
            return response.status_code, result
