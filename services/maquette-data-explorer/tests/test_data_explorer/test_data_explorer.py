from data_explorer.data_explorer import get_statistics

body = get_statistics("sample-project", "some-dataset", "1.0.0", with_images=True)
print(body.json())