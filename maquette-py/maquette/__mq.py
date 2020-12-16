import json
import os
from enum import Enum
from io import BytesIO

import pandas as pd
import pandavro

from maquette_lib.__client import Client
from maquette_lib.__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/home'))


class ERetentionUnit(str, Enum):
    SECONDS = "seconds"
    MINUTES = "minutes"
    HOURS = "hours"
    DAYS = "days"
    WEEKS = "weeks"
    MONTHS = "months"
    YEARS = "years"


class EAccessType(str, Enum):
    DIRECT = "direct"
    CASHED = "cashed"


class EDataAssetType(str, Enum):
    DATASET = "dataset"
    COLLECTION = "collection"
    SOURCE = "source"


class EAuthorizationType(str, Enum):
    USER = "user"
    ROLE = "role"
    WILDCARD = "*"


class EProjectPrivilege(str, Enum):
    MEMBER = "member"
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class EDatasetPrivilege(str, Enum):
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class EDataClassification(str, Enum):
    PUBLIC = "public"
    INTERNAL = "internal"
    CONFIDENTIAL = "confidential"
    RESTRICTED = "restricted"


class EDataVisibility(str, Enum):
    PUBLIC = "public"
    PRIVATE = "private"


class EPersonalInformation(str, Enum):
    NONE = "none"
    PERSONAL_INFORMATION = "pi"
    SENSITIVE_PERSONAL_INFORMATION = "spi"


# TODO Print and Get Methoden
# TODO Doku (of course)

class DataAsset:
    project: str = None
    data_asset_name: str = None

    def __init__(self, data_asset_name: str, title: str = None, summary: str = "Lorem Impsum",
                 visibility: str = EDataVisibility.PUBLIC,
                 classification: str = EDataClassification.PUBLIC,
                 personal_information: str = EPersonalInformation.NONE,
                 project_name: str = None, data_asset_type: EDataAssetType = None):

        self.data_asset_name = data_asset_name
        if title:
            self.title = title
        else:
            self.title = data_asset_name

        self.summary = summary
        self.visibility = visibility
        self.classification = classification
        self.personal_information = personal_information
        self.project = project_name
        self.data_asset_type = data_asset_type

    def create(self):
        client.command(cmd='{}s create'.format(self.data_asset_type),
                       args={'name': self.data_asset_name, 'title': self.title, 'summary': self.summary,
                             'visibility': self.visibility, 'classification': self.classification,
                             'personalInformation': self.personal_information},
                       headers={'x-project': self.project})
        return self

    def update(self, to_update: str):
        client.command(cmd='{}s update'.format(self.data_asset_type),
                       args={self.data_asset_type: to_update, 'name': self.data_asset_name, 'title': self.title,
                             'summary': self.summary,
                             'visibility': self.visibility, 'classification': self.classification,
                             'personalInformation': self.personal_information})
        return self

    def remove(self):
        client.command(cmd='{}s remove'.format(self.data_asset_type),
                       args={'name': self.data_asset_name},
                       headers={'x-project': self.project})

    def delete(self):
        self.remove()


class Collection(DataAsset):

    def __init__(self, data_asset_name: str, title: str = None, summary: str = "Lorem Impsum",
                 visibility: str = EDataVisibility.PUBLIC,
                 classification: str = EDataClassification.PUBLIC,
                 personal_information: str = EPersonalInformation.NONE,
                 project_name: str = None):
        super().__init__(data_asset_name, title, summary, visibility, classification, personal_information,
                         project_name,
                         EDataAssetType.COLLECTION)

    def put(self, data, short_description: str):

        resp = client.post('data/collections/' + self.data_asset_name, files={
            'target': os.path.basename(data.name),
            'file': (short_description, data, 'application/octet-stream', {'Content-Type': 'application/octet-stream'})
        }, headers={'Accept': 'application/octet-stream', 'x-project': self.project})

        return json.loads(resp.content)["version"]

    def get(self, filename, tag: str = None):
        if tag:
            resp = client.get('data/collections/' + self.data_asset_name + '/tags/' + tag + '/' + filename)
        else:
            resp = client.get('data/collections/' + self.data_asset_name + '/latest/' + filename)
        return BytesIO(resp.content)


class Source(DataAsset):

    def __init__(self, data_asset_name: str, title: str = None, summary: str = "Lorem Impsum",
                 visibility: str = EDataVisibility.PUBLIC,
                 classification: str = EDataClassification.PUBLIC,
                 personal_information: str = EPersonalInformation.NONE, access_type: EAccessType = EAccessType.DIRECT,
                 db_properties: dict = {}, project_name: str = None):
        self.access_type = access_type
        self.db_properties = db_properties
        super().__init__(data_asset_name, title, summary, visibility, classification, personal_information,
                         project_name,
                         EDataAssetType.SOURCE)

    def create(self):
        client.command(cmd='sources create'.format(self.data_asset_type),
                       args={'name': self.data_asset_name, 'title': self.title, 'summary': self.summary,
                             'visibility': self.visibility, 'classification': self.classification,
                             'personalInformation': self.personal_information, 'properties': self.db_properties,
                             'accessType': self.access_type},
                       headers={'x-project': self.project})
        return self

    def update(self, to_update: str):
        client.command(cmd='sources update'.format(self.data_asset_type),
                       args={self.data_asset_type: to_update, 'name': self.data_asset_name, 'title': self.title,
                             'summary': self.summary,
                             'visibility': self.visibility, 'classification': self.classification,
                             'accessType': self.access_type, 'properties': self.db_properties,
                             'personalInformation': self.personal_information})
        return self

    def get(self) -> pd.DataFrame:
        resp = client.get('data/sources/' + self.data_asset_name)
        return pandavro.from_avro(BytesIO(resp.content))


class Dataset(DataAsset):

    def __init__(self, data_asset_name: str, title: str = None, summary: str = "Lorem Impsum",
                 visibility: str = EDataVisibility.PUBLIC,
                 classification: str = EDataClassification.PUBLIC,
                 personal_information: str = EPersonalInformation.NONE,
                 project_name: str = None):
        super().__init__(data_asset_name, title, summary, visibility, classification, personal_information,
                         project_name,
                         EDataAssetType.DATASET)

    def put(self, data: pd.DataFrame, short_description: str):
        ds = self.data_asset_name

        file: BytesIO = BytesIO()
        pandavro.to_avro(file, data)
        file.seek(0)

        resp = client.post('data/datasets/' + ds, files={
            'message': short_description,
            'file': (short_description, file, 'avro/binary', {'Content-Type': 'avro/binary'})
        }, headers={'Accept': 'application/csv', 'x-project': self.project})

        return json.loads(resp.content)["version"]

    def get(self, version: str = None) -> pd.DataFrame:
        ds = self.data_asset_name
        if version:
            resp = client.get('data/datasets/' + ds + '/' + version)
        else:
            resp = client.get('data/datasets/' + ds)
        return pandavro.from_avro(BytesIO(resp.content))


class Stream(DataAsset):

    def __init__(self, data_asset_name: str, title: str = None, summary: str = "Lorem Impsum",
                 visibility: str = EDataVisibility.PUBLIC,
                 classification: str = EDataClassification.PUBLIC,
                 personal_information: str = EPersonalInformation.NONE,
                 schema: dict = {},
                 retention: dict = {"unit": ERetentionUnit.HOURS, "retention": 6},
                 project_name: str = None):
        self.schema = schema
        self.retention = retention
        super().__init__(data_asset_name, title, summary, visibility, classification, personal_information,
                         project_name,
                         EDataAssetType.DATASET)

    def update(self, to_update: str):
        client.command(cmd='sources update'.format(self.data_asset_type),
                       args={self.data_asset_type: to_update, 'name': self.data_asset_name, 'title': self.title,
                             'summary': self.summary,
                             'visibility': self.visibility, 'classification': self.classification,
                             'schema': self.schema, 'retention': self.retention,
                             'personalInformation': self.personal_information})
        return self

    def get(self) -> dict:
        resp = client.get('data/streams/' + self.data_asset_name)
        return json.loads(resp.content)


class Project:
    __name: str = None
    __title: str = None
    __summary: str = None

    def __init__(self, name: str, title: str = None, summary: str = None):
        self.__name = name
        self.__summary = summary
        if title:
            self.__title = title
        else:
            self.__title = name

    def create(self) -> 'Project':
        client.command(cmd='projects create',
                       args={'title': self.__title, 'name': self.__name, 'summary': self.__summary})
        return self

    def update(self, to_update: str):
        client.command(cmd='projects update', args={'project': to_update, 'title': self.__title, 'name': self.__name,
                                                    'summary': self.__summary})
        return self

    def remove(self):
        resp = client.command(cmd='projects remove',
                              args={'name': self.__name})
        return resp[1]

    def delete(self):
        return self.remove()

    def datasets(self, to_csv=False):
        if to_csv:
            resp = client.command(cmd='datasets list', headers={'Accept': 'application/csv',
                                                                'x-project': self.__name})
        else:
            resp = client.command(cmd='datasets list',
                                  headers={'x-project': self.__name})
        return resp[1]

    def dataset(self, dataset_name: str = None, dataset_title: str = None, summary: str = None,
                visibility: str = None, classification: str = None, personal_information: str = None) -> Dataset:
        args = [arg for arg in
                [dataset_name, dataset_title, summary, visibility, classification, personal_information] if
                arg]
        return Dataset(project_name=self.__name, *args, )

    def collection(self, collection_name: str = None, collection_title: str = None, summary: str = None,
                   visibility: str = None, classification: str = None, personal_information: str = None) -> Collection:
        args = [arg for arg in
                [collection_name, collection_title, summary, visibility, classification, personal_information] if
                arg]
        return Collection(project_name=self.__name, *args, )


def project(name: str, title: str = None, summary: str = None) -> Project:
    return Project(name=name, title=title, summary=summary)


def dataset(dataset_name: str = None, dataset_title: str = None, summary: str = None,
            visibility: str = None, classification: str = None, personal_information: str = None) -> Dataset:
    args = [arg for arg in
            [dataset_title, summary, visibility, classification, personal_information] if
            arg]
    return Dataset(data_asset_name=dataset_name, project_name=os.environ.get('MQ_PROJECT_NAME', 'Project_42'), *args)


def collection(collection_name: str = None, collection_title: str = None, summary: str = None,
               visibility: str = None, classification: str = None, personal_information: str = None) -> Collection:
    args = [arg for arg in
            [collection_title, summary, visibility, classification, personal_information] if
            arg]
    return Collection(data_asset_name=collection_name, project_name=os.environ.get('MQ_PROJECT_NAME', 'Project_42'),
                      *args)


def source(source_name: str = None, source_title: str = None, summary: str = None,
           visibility: str = None, classification: str = None, personal_information: str = None,
           access_type: str = None, db_properties: dict = None) -> Source:
    args = [arg for arg in
            [source_title, summary, visibility, classification, personal_information, access_type, db_properties] if
            arg]
    return Source(data_asset_name=source_name, project_name=os.environ.get('MQ_PROJECT_NAME', 'Project_42'), *args)


def stream(stream_name: str = None, stream_title: str = None, summary: str = None,
           visibility: str = None, classification: str = None, personal_information: str = None, retention: dict = None,
           schema: dict = None) -> Source:
    args = [arg for arg in
            [stream_title, summary, visibility, classification, personal_information, retention, schema] if
            arg]
    return Source(data_asset_name=stream_name, project_name=os.environ.get('MQ_PROJECT_NAME', 'Project_42'), *args)


# TODO list functions for collections, streams and sources


def datasets(to_csv=False):
    if to_csv:
        resp = client.command(cmd='datasets list', headers={'Accept': 'application/csv',
                                                            'x-project': os.environ.get('MQ_PROJECT_NAME',
                                                                                        'Project_42')})
    else:
        resp = client.command(cmd='datasets list',
                              headers={'x-project': os.environ.get('MQ_PROJECT_NAME', 'Project_42')})
    return resp[1]


def projects(to_csv=False):
    if to_csv:
        resp = client.command(cmd='projects list', headers={'Accept': 'application/csv'})
    else:
        resp = client.command(cmd='projects list')
    return resp[1]
