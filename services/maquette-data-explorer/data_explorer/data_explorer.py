from maquette import Dataset, Source
from data_explorer.data_analyzer import generate_df_statistics
from data_explorer.models.response import ResponseBody

import pandas as pd

def __load_df(dataset_name: str = None, source: str=None, version: str=None) -> pd.DataFrame:
    if dataset_name:
        return Dataset(dataset_name).get(version=version)
    elif source:
        return Source(source).get()


def get_ds_statistics(dataset_name: str, version: str=None, with_images=False) -> ResponseBody:
    return ResponseBody(columns=generate_df_statistics(__load_df(dataset_name, version), with_images), dataset=dataset_name, version=version)

def get_so_statistics(source_name: str, with_images=False) -> ResponseBody:
    return ResponseBody(columns=generate_df_statistics(__load_df(source_name), with_images), source=source_name)

