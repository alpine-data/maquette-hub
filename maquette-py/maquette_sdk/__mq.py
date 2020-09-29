import pandas as pd
import pandavro

from enum import Enum
from io import BytesIO
from typing import Optional

from maquette_lib.__client import Client
from maquette_lib.__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/home'))


class EAuthorizationType(Enum):
    USER = "user"
    ROLE = "role"
    WILDCARD = "*"


class ENamespacePrivilege(Enum):
    MEMBER = "member"
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class EDatasetPrivilege(Enum):
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class Administration:

    def __init__(self):
        pass

    @staticmethod
    def delete_token(name: str, for_user: str = None):
        resp = client.command('user token delete', {
            'name': name,
            'for-user': for_user
        })

        print(resp['output'])

    @staticmethod
    def renew_token(name: str, for_user: str = None):
        resp = client.command('user token renew', {
            'name': name,
            'for-user': for_user
        })

        print(resp['output'])

    @staticmethod
    def register_token(name: str, for_user: str = None):
        resp = client.command('user token register', {
            'name': name,
            'for-user': for_user
        })

        print(resp['output'])

    @staticmethod
    def tokens():
        resp = client.command('user tokens')
        return resp['data'][0]


class DatasetVersion:

    __namespace: str = None

    __dataset: str = None

    __version: str = None

    def __init__(self, dataset: str, version: str = None, namespace: str = None):
        self.__namespace = namespace
        self.__dataset = dataset
        self.__version = version

    def get(self) -> pd.DataFrame:
        ns = self.__namespace or '_'
        ds = self.__dataset
        version = self.__version or 'latest'

        resp = client.get('/datasets/' + ns + '/' + ds + '/versions/' + version + '/data')
        return pandavro.from_avro(BytesIO(resp.content))

    def print(self) -> 'DatasetVersion':
        resp = client.command('dataset version show', {
            'namespace': self.__namespace,
            'dataset': self.__dataset,
            'version': self.__version
        })

        print('VERSION ' + self.__version)
        print()
        print(resp['output'])

        return self

    def __str__(self):
        resp = client.command('dataset version show', {
            'namespace': self.__namespace,
            'dataset': self.__dataset,
            'version': self.__version
        })

        out = 'VERSION ' + self.__version \
            + '\n\n' \
            + resp['output']

        return out

    def __repr__(self):
        return self.__str__()


class Dataset:

    __namespace: str = None

    __name: str = None

    def __init__(self, name: str, namespace: str = None):
        self.__name = name
        self.__namespace = namespace

    def create(self, is_private: bool = False) -> 'Dataset':
        client.command('datasets create', {'dataset': self.__name, 'namespace': self.__namespace, 'is-private': is_private})
        return self

    def create_consumer(self, for_user: str = None) -> 'Dataset':
        resp = client.command('dataset create consumer', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'for-user': for_user
        })

        print(resp['output'])
        return self

    def create_producer(self, for_user: str = None) -> 'Dataset':
        resp = client.command('dataset create producer', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'for-user': for_user
        })

        print(resp['output'])
        return self

    def grant(self, grant: EDatasetPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Dataset':
        client.command('dataset grant', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, revoke: EDatasetPrivilege, auth: EAuthorizationType, from_name: str = None) -> 'Dataset':
        client.command('dataset revoke', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'privilege': revoke.value,
            'authorization': auth.value,
            'from': from_name
        })

        return self

    def print(self):
        resp = client.command('dataset show', {'dataset': self.__name, 'namespace': self.__namespace})
        print(resp['output'])
        return self

    def put(self, data: pd.DataFrame, short_description: str) -> DatasetVersion:
        ns: str = self.__namespace or '_'
        ds: str = self.__name

        file: BytesIO = BytesIO()
        pandavro.to_avro(file, data)
        file.seek(0)

        resp = client.put('/datasets/' + ns + '/' + ds + '/versions', files = {
            'message': short_description,
            'file': file
        })

        return self.version(resp.json())

    def versions(self) -> pd.DataFrame:
        resp = client.command('dataset versions', {'dataset': self.__name, 'namespace': self.__namespace})
        return resp['data'][0]

    def version(self, version: Optional[str] = None):
        return DatasetVersion(self.__name, version, self.__namespace)

    def __str__(self):
        resp = client.command('dataset show', {'dataset': self.__name, 'namespace': self.__namespace})
        return resp['output']

    def __repr__(self):
        return self.__str__()


class Namespace:

    __name: str = None

    def __init__(self, name: str = None):
        self.__name = name

    def create(self, is_private: bool = False) -> 'Namespace':
        client.command('namespaces create', {'namespace': self.__name, 'is-private': is_private})
        return self

    def datasets(self) -> pd.DataFrame:
        resp = client.command('namespace datasets', {'namespace': self.__name})
        return resp['data'][0]

    def dataset(self, name: str) -> Dataset:
        return Dataset(name, self.__name)

    def grant(self, grant: ENamespacePrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Namespace':
        client.command('namespace grant', {
            'namespace': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, grant: ENamespacePrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Namespace':
        client.command('namespace revoke', {
            'namespace': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'from': to_name
        })

        return self

    def print(self) -> 'Namespace':
        resp = client.command('namespace show', {'namespace': self.__name})
        print(resp['output'])
        return self

    def __str__(self):
        resp = client.command('namespace show', {'namespace': self.__name})
        return resp['output']

    def __repr__(self):
        return self.__str__()


def admin() -> Administration:
    return Administration()


def datasets() -> pd.DataFrame:
    resp = client.command('datasets')
    return resp['data'][0]


def namespaces() -> pd.DataFrame:
    resp = client.command('namespaces')
    return resp['data'][0]