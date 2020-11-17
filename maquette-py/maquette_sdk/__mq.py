import pandas as pd
import pandavro

from enum import Enum
from io import BytesIO
from typing import Optional

from maquette_lib.__client import Client
from maquette_lib.__user_config import UserConfiguration
from maquette_lib.__util import generate_unique_name

client = Client.from_config(UserConfiguration('/home'))


class EAuthorizationType(Enum):
    USER = "user"
    ROLE = "role"
    WILDCARD = "*"


class EProjectPrivilege(Enum):
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
    def delete_token(name: str, for_user: str = None) -> str:
        status, resp = client.command(cmd='user token delete',args= {
            'name': name,
            'for-user': for_user
        })
        if status == 200:
            return resp['output']
        else:
            raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                               'status code: ' + str(status) + ', content:\n' + resp)

    @staticmethod
    def renew_token(name: str, for_user: str = None) -> str:
        status, resp = client.command(cmd='user token renew', args={
            'name': name,
            'for-user': for_user
        })
        if status == 200:
            return resp['output']
        else:
            raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                               'status code: ' + str(status) + ', content:\n' + resp)

    @staticmethod
    def register_token(name: str, for_user: str = None) -> str:
        status, resp = client.command(cmd='user token register', args={
            'name': name,
            'for-user': for_user
        })
        if status == 200:
            return resp['output']
        else:
            raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                               'status code: ' + str(status) + ', content:\n' + resp)

    @staticmethod
    def tokens() -> pd.DataFrame:
        status, resp = client.command('user tokens')
        if status == 200:
            table_df = pd.json_normalize(resp)
            return table_df
        else:
            raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                               'status code: ' + str(status) + ', content:\n' + resp)


class DatasetVersion:

    __project: str = None

    __dataset: str = None

    __version: str = None

    def __init__(self, dataset: str, version: str = None, project: str = None):
        self.__project = project
        self.__dataset = dataset
        self.__version = version

    def get(self) -> pd.DataFrame:
        pr = self.__project or '_'
        ds = self.__dataset
        version = self.__version or 'latest'

        resp = client.get('/datasets/' + pr + '/' + ds + '/versions/' + version + '/data')
        return pandavro.from_avro(BytesIO(resp.content))

    def print(self) -> 'DatasetVersion':
        status, resp = client.command(cmd='dataset version show', args= {
            'project': self.__project,
            'dataset': self.__dataset,
            'version': self.__version
        })

        print('VERSION ' + self.__version)
        print()
        print(resp['output'])

        return self

    def __str__(self):
        status, resp = client.command(cmd='dataset version show', args={
            'project': self.__project,
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

    __project: str = None

    __name: str = None

    def __init__(self, name: str, project: str = None):
        self.__name = name
        self.__project = project

    def create(self, is_private: bool = False) -> 'Dataset':
        client.command(cmd='datasets create',args= {'dataset': self.__name, 'project': self.__project})
        return self

    def create_consumer(self, for_user: str = None) -> 'Dataset':
        status, resp = client.command(cmd='dataset create consumer', args={
            'dataset': self.__name,
            'project': self.__project,
            'for-user': for_user
        })

        print(resp['output'])
        return self

    def create_producer(self, for_user: str = None) -> 'Dataset':
        status, resp = client.command(cmd='dataset create producer', args={
            'dataset': self.__name,
            'project': self.__project,
            'for-user': for_user
        })

        print(resp['output'])
        return self

    def grant(self, grant: EDatasetPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Dataset':
        client.command(cmd='dataset grant', args={
            'dataset': self.__name,
            'project': self.__project,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, revoke: EDatasetPrivilege, auth: EAuthorizationType, from_name: str = None) -> 'Dataset':
        client.command(cmd='dataset revoke', args={
            'dataset': self.__name,
            'project': self.__project,
            'privilege': revoke.value,
            'authorization': auth.value,
            'from': from_name
        })

        return self

    def print(self):
        resp = client.command(cmd='dataset show', args={'dataset': self.__name, 'project': self.__project})
        print(resp['output'])
        return self

    def put(self, data: pd.DataFrame, short_description: str) -> DatasetVersion:
        pr: str = self.__project or '_'
        ds: str = self.__name

        file: BytesIO = BytesIO()
        pandavro.to_avro(file, data)
        file.seek(0)

        resp = client.put('/datasets/' + pr + '/' + ds + '/versions', files = {
            'message': short_description,
            'file': file
        })

        return self.version(resp.json())

    def versions(self) -> pd.DataFrame:
        resp = client.command(cmd='dataset versions', args={'dataset': self.__name, 'project': self.__project})
        return resp['data'][0]

    def version(self, version: Optional[str] = None):
        return DatasetVersion(self.__name, version, self.__project)

    def __str__(self):
        resp = client.command(cmd='dataset show', args={'dataset': self.__name, 'project': self.__project})
        return resp['output']

    def __repr__(self):
        return self.__str__()


class Project:

    __name: str = None
    __title: str = None

    def __init__(self, title: str = None):
        self.__name = generate_unique_name(title)
        self.__title = title

    def create(self) -> 'Project':
        client.command(cmd='projects create', args= {'title': self.__title, 'name': self.__name})
        return self

    def datasets(self) -> pd.DataFrame:
        resp = client.command(cmd='project datasets', args={'project': self.__name})
        #TODO: Avro
        return resp['data'][0]

    def dataset(self, name: str) -> Dataset:
        return Dataset(name, self.__name)

    def grant(self, grant: EProjectPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Project':
        client.command(cmd='project grant', args={
            'project': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, grant: EProjectPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Project':
        client.command('project revoke', {
            'project': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'from': to_name
        })

        return self

    def print(self) -> 'Project':
        resp = client.command('project show', {'project': self.__name})
        print(resp['output'])
        return self

    def __str__(self):
        resp = client.command('project show', {'project': self.__name})
        return resp['output']

    def __repr__(self):
        return self.__str__()


def admin() -> Administration:
    return Administration()


def datasets() -> pd.DataFrame:
    resp = client.command('datasets')
    return resp['data'][0]


def projects() -> pd.DataFrame:
    status, resp = client.command(cmd='projects list')
    if status == 200:
        return pd.json_normalize(resp)
    else:
        raise RuntimeError('Ups! Something went wrong (ⓧ_ⓧ)\n'
                           'status code: ' + str(status) + ', content:\n' + resp)