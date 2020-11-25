import pandas as pd

from data_explorer.models.response import Column, Stats, Type

def __generate_general_statistic(desc, column_data):
    size_abs, size_perc = column_data.size, 100.0

    valid_abs = column_data.count().item()
    valid_perc = valid_abs/size_abs*100

    mismatched = (0, 0.0)

    valid = (valid_abs, valid_perc)
    missing = (size_abs-valid_abs, size_perc-valid_perc)

    return mismatched, valid, missing

def __generate_obj_statistic(desc, column_data):
    str_details = []
    count = desc.count()
    str_details.append(["Count", count.item(), ""])
    unique = desc.unique()
    str_details.append(["Unique", unique[1], ""])
    top = desc.top
    str_details.append(["Most Common", top, ""])
    top_freq = desc.freq
    str_details.append(["Most Common Frequency", top_freq.item(), ""])

    return [str_details]

def __generate_num_statistic(desc, column_data):
    num_details = []
    mean = desc.mean()
    num_details.append(["Mean", mean.item(), ""])
    std = desc.std()
    num_details.append(["Standard Deviation", std.item(), ""])
    quantiles = []
    quant_dict = dict(desc)
    quantiles.append(["Quantiles", quant_dict["min"], "Min"])
    quantiles.append(["", quant_dict["25%"], "25%"])
    quantiles.append(["", quant_dict["50%"], "50%"])
    quantiles.append(["", quant_dict["75%"], "75%"])
    quantiles.append(["", quant_dict["max"], "Max"])
    return [num_details,quantiles]

def generate_df_statistics(df: pd.DataFrame):
    column_list = []
    for (column_name, column_data) in df.iteritems():
        desc = column_data.describe()
        mismatched, valid, missing = __generate_general_statistic(desc, column_data)
        if desc.dtype == "object":
            type = Type.text
            column_list.append(Column(name=column_name, type=type,
                                       stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                   details=__generate_obj_statistic(desc, column_data))))
        #TODO: alternative for datetimes
        else:
            type = Type.numeric
            column_list.append(Column(name=column_name, type=type,
                                      stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                  details=__generate_num_statistic(desc, column_data))))
    return column_list