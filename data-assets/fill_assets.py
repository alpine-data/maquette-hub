from maquette import *
import pandas as pd

def init_weather():
    # Upload Weather Dataset to Sample Project
    Dataset('city-temperature', title='City Temperatures', summary="Historical weather data of major cities of Europe").create()
    weather_csv = 'sources/city_temperature_europe.csv'
    weather_df = pd.read_csv(weather_csv, sep=";")
    dataset('city-temperature').put(weather_df, "weather init")
    print("Uplodaded Weather Dataset")
    #print("Checking success:")
    #print(dataset('city-temperature').get())

def init_fraud():
    # Upload Fraud Analysis Dataset
    Dataset('fraud-analysis', title='Fraudulent Transactions', summary="Fraudulent banking transactions of a Spanish bank", classification="internal", personal_information="pi").create()
    fraud_csv = 'sources/banking_fraud.csv'
    fraud_df = pd.read_csv(fraud_csv).head(10000)
    dataset('fraud-analysis').put(fraud_df, "fraud init")
    print("Uploaded Fraud Dataset")
    #print("Checking success:")
    #print(dataset('fraud-analysis').get())

def init_house_prices_training():
    Dataset('house-prices-training', title='House Prices', summary="The Ames Housing dataset was compiled by Dean De Cock for use in data science education. It's an incredible alternative for data scientists looking for a modernized and expanded version of the often cited Boston Housing dataset. This is the training set", classification="public", personal_information="none").create()
    train_csv = 'sources/house-prices/train.csv'
    train_df = pd.read_csv(train_csv, na_values="")
    filter_columns = ['Alley', 'PoolQC', 'MiscFeature', 'FireplaceQu', 'Fence']
    train_df.drop(filter_columns, inplace=True, axis=1)
    train_df.dropna(axis=1, inplace=True)
    train_df.columns = train_df.columns.str.replace("1", "Fir")
    train_df.columns = train_df.columns.str.replace("2", "Seco")
    train_df.columns = train_df.columns.str.replace("3", "Thi")
    dataset('house-prices-training').put(train_df, "house prices train init")

    print("Uploaded House Prices Trainings Dataset")

def init_house_prices_test():
    Dataset('house-prices-test', title='House Prices', summary="The Ames Housing dataset was compiled by Dean De Cock for use in data science education. It's an incredible alternative for data scientists looking for a modernized and expanded version of the often cited Boston Housing dataset. This is the test set", classification="public", personal_information="none").create()
    test_csv = 'sources/house-prices/test.csv'
    test_df = pd.read_csv(test_csv, na_values="")
    filter_columns = ['Alley', 'PoolQC', 'MiscFeature', 'FireplaceQu', 'Fence']
    test_df.drop(filter_columns, inplace=True, axis=1)
    test_df.dropna(axis=1, inplace=True)
    test_df.columns = test_df.columns.str.replace("1", "Fir")
    test_df.columns = test_df.columns.str.replace("2", "Seco")
    test_df.columns = test_df.columns.str.replace("3", "Thi")
    dataset('house-prices-test').put(test_df, "house prices test init")

    print("Uploaded House Prices Test Dataset")

#init_weather()
#init_fraud()
init_house_prices_training()
init_house_prices_test()