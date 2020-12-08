from maquette_sdk import *
import pandas as pd


def init_weather():
    # Upload Weather Dataset to Sample Project
    Dataset(dataset_name='city-temperature', title='City Temperatures', summary="Historical weather data of major cities of Europe").create()
    weather_csv = 'sources/city_temperature_europe.csv'
    weather_df = pd.read_csv(weather_csv, sep=";")
    dataset('city-temperature').put(weather_df, "weather init")
    print("Uplodaded Weather Dataset")
    # print("Checking success:")
    # print(dataset('city-temperature').version().get())

def init_fraud():
    # Upload Fraud Analysis Dataset
    Dataset('fraud-analysis', title='Fraudulent Transactions', summary="Fraudulent banking transactions of a Spanish bank", classification="internal", personal_information="pi").create()
    fraud_csv = 'sources/banking_fraud.csv'
    fraud_df = pd.read_csv(fraud_csv).head(10000)
    dataset('fraud-analysis').put(fraud_df, "fraud init")
    print("Uploaded Fraud Dataset")
    # print("Checking success:")
    # print(dataset('fraud-analysis').version().get())


init_weather()
init_fraud()