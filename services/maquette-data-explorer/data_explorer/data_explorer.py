from maquette import Dataset
from data_explorer.data_analyzer import generate_df_statistics
from data_explorer.models.response import ResponseBody

import pandas as pd

def __load_df(dataset_name: str, version: str=None) -> pd.DataFrame:
    return Dataset(dataset_name).get(version=version)


def get_statistics(dataset_name: str, version: str=None, with_images=False) -> ResponseBody:
    return ResponseBody(columns=generate_df_statistics(__load_df(dataset_name, version), with_images), dataset=dataset_name, version=version)



