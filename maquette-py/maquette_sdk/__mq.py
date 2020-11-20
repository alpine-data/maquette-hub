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

#TODO: include optional "pretty string", and csv options for all tables (see dataset(), projects, str, print etc...)

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

        resp = client.get('data/datasets/' + pr + '/' + ds + '/' + version)
        #TODO: Catch error if not Avro, Status?
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

    def __init__(self, name: str, title:str = None, summary: str = "Lorem Impsum", visibility: str="public",
                 classification: str = "public", personal_information: str = "none", description="Lorem Ipsum",
                 project: str = None):
        #TODO: Enums für Classification, Visibility etc. einführen?
        self.__name = name
        if title:
            self.__title = title
        else:
            self.__title = name

        self.__summary = summary
        self.__visibility = visibility
        self.__classification = classification
        self.__personal_information = personal_information
        self.__description = description
        self.__project = project

    def create(self) -> 'Dataset':
        client.command(cmd='datasets create',
                       args= {'name': self.__name, 'title': self.__title, 'summary': self.__summary,
                              'visibility': self.__visibility,'classification': self.__classification,
                              'personalInformation': self.__personal_information, 'description': self.__description,
                              'project': self.__project})
        return self
    #TODO: add posibility to directly upload a dataset with creation

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
        resp = client.command(cmd='datasets get', args={'dataset': self.__name, 'project': self.__project})
        print(resp[1])
        return self

    def put(self, data: pd.DataFrame, short_description: str) -> DatasetVersion:
        pr: str = self.__project or '_'
        ds: str = self.__name

        file: BytesIO = BytesIO()
        pandavro.to_avro(file, data)
        file.seek(0)

        resp = client.post('data/datasets/' + pr + '/' + ds + '/1.0.1', files = {
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

    def __init__(self, name:str, title: str = None):
        #TODO:  self.__name = generate_unique_name(title)
        self.__name = name
        if title:
            self.__title = title
        else:
            self.__title = name

    def create(self) -> 'Project':
        client.command(cmd='projects create', args= {'title': self.__title, 'name': self.__name})
        return self

    def datasets(self) -> pd.DataFrame:
        resp = client.command(cmd='project datasets', args={'project': self.__name})
        return resp['data'][0]

    def dataset(self, dataset_name: str=None, dataset_title: str=None, summary:str=None, description: str=None,
                visibility: str=None, classification: str=None, personal_information: str=None) -> Dataset:
        args = [arg for arg in [dataset_name,dataset_title, summary,description,visibility,classification,personal_information] if arg]
        return Dataset(project=self.__name, *args)

    def grant(self, grant: EProjectPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Project':
        client.command(cmd='project grant', args={
            'project': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, grant: EProjectPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Project':
        client.command(cmd='project revoke', args={
            'project': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'from': to_name
        })

        return self

    def print(self) -> 'Project':
        resp = client.command(cmd='project get', args={'project': self.__name}, headers={'Accept':'text/plain'})
        print(resp)
        return self

    def __str__(self):
        resp = client.command(cmd='project get', args={'project': self.__name}, headers={'Accept': 'text/plain'})
        return resp

    def __repr__(self):
        return self.__str__()


def admin() -> Administration:
    return Administration()

def project(name: str) -> Project:
    return Project(name=name)



def datasets(name: str, to_csv=False) -> pd.DataFrame:
    if to_csv:
        resp = client.command(cmd='datasets list', args={'project': name}, headers={'Accept':'application/csv'})
    else:
        resp = client.command(cmd='datasets list', args={'project': name})
    return resp[1]


def projects(to_csv=False) -> pd.DataFrame:
    if to_csv:
        resp = client.command(cmd='datasets list', headers={'Accept': 'application/csv'})
    else:
        resp = client.command(cmd='projects list')
    return resp[1]
