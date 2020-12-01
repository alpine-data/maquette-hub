from data_explorer.data_explorer import get_statistics

body = get_statistics(dataset_name="city-temperature", version="4.0.0", with_images=True)
print(body.json())