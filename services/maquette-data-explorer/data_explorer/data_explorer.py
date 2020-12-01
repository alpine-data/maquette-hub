from maquette_sdk.__mq import *

from data_explorer.data_analyzer import generate_df_statistics
from data_explorer.models.response import ResponseBody


def __load_df(dataset_name: str, version: str="") -> pd.DataFrame:
    return Dataset(dataset_name).version(version).get()


def get_statistics(dataset_name: str, version: str="", with_images=False) -> ResponseBody:
        return ResponseBody(columns=generate_df_statistics(__load_df(dataset_name, version), with_images), dataset=dataset_name, version=version)



