import pandas as pd
import seaborn as sns
import base64
from io import BytesIO

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

def __generate_obj_image(desc, column_data, df):
    series = column_data.value_counts(dropna=False).sort_values(ascending=False).head(5)
    plot = sns.barplot(x=series.index, y=series.values, data=df).get_figure()
    img = BytesIO()
    plot.savefig(img, format='png')
    img.seek(0)
    image = base64.b64encode(img.getvalue())
    img.close()
    return image

def __generate_num_statistic(desc, column_data):
    num_details = []
    mean = desc.mean()
    num_details.append(["Mean", round(mean.item(), 3), ""])
    std = desc.std()
    num_details.append(["Standard Deviation", round(std.item(), 3), ""])
    quantiles = []
    quant_dict = dict(desc)
    quantiles.append(["Quantiles", quant_dict["min"], "Min"])
    quantiles.append(["", quant_dict["25%"], "25%"])
    quantiles.append(["", quant_dict["50%"], "50%"])
    quantiles.append(["", quant_dict["75%"], "75%"])
    quantiles.append(["", quant_dict["max"], "Max"])
    return [num_details, quantiles]

def __generate_num_image(desc, column_data, df):
    series = column_data.value_counts(dropna=False).sort_index(ascending=False).head(5)
    plot = sns.barplot(x=series.index, y=series.values, data=df).get_figure()
    img = BytesIO()
    plot.savefig(img, format='png')
    img.seek(0)
    image = base64.b64encode(img.getvalue())
    img.close()
    return image

def generate_df_statistics(df: pd.DataFrame, with_images: bool):
    column_list = []
    for (column_name, column_data) in df.iteritems():
        desc = column_data.describe()
        mismatched, valid, missing = __generate_general_statistic(desc, column_data)
        if missing[1] == 100.0:
            continue
        if desc.dtype == "object":
            type_ = Type.text
            if with_images:
                column_list.append(Column(name=column_name, type=type_,
                                        image=__generate_num_image(desc, column_data, df),
                                       stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                   details=__generate_obj_statistic(desc, column_data),
                                                      image=__generate_obj_image(desc, column_data, df))))
            else:
                column_list.append(Column(name=column_name, type=type_,
                                          stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                      details=__generate_obj_statistic(desc, column_data))))
        else:
            type_ = Type.numeric
            if with_images:
                column_list.append(Column(name=column_name, type=type_,
                                    image=__generate_num_image(desc, column_data, df),
                                      stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                  details=__generate_num_statistic(desc, column_data))))
            else:
                column_list.append(Column(name=column_name, type=type_,
                                          stats=Stats(mismatched=mismatched, valid=valid, missing=missing,
                                                      details=__generate_num_statistic(desc, column_data))))
    return column_list