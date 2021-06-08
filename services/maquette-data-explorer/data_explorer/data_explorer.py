from maquette import Dataset, Source
from data_explorer.data_analyzer import generate_df_statistics
from data_explorer.models.response import ResponseBody
from data_explorer.models.request import FormatEnum

import pandas as pd
from pandas_profiling import ProfileReport

def __load_df(data_asset_name: str, version: str=None) -> pd.DataFrame:
    if version:
        return Dataset(data_asset_name).get(version=version)
    else:
        return Source(data_asset_name).get()


def get_ds_statistics(dataset_name: str, version: str=None, with_images=False) -> ResponseBody:
    return ResponseBody(columns=generate_df_statistics(__load_df(dataset_name, version), with_images), dataset=dataset_name, version=version)

def get_so_statistics(source_name: str, with_images=False) -> ResponseBody:
    return ResponseBody(columns=generate_df_statistics(__load_df(source = source_name), with_images), source=source_name)

def get_profile(data_asset_name: str, format: FormatEnum, version: str=None):
    df = __load_df(data_asset_name, version)
    profile = ProfileReport(df, title=data_asset_name+" Report")
    if format == FormatEnum.json:
        return profile.to_json()
    else:
        return profile.to_html()



