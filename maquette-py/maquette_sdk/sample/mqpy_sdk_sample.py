from maquette_sdk.__mq import EProjectPrivilege, EDatasetPrivilege, EAuthorizationType
import maquette_sdk
import pandas as pd
# Create Project
# maquette_sdk.Project('sample-project').create()
# print(maquette_sdk.projects())

# add a dataset to a project
# maquette_sdk.project('sample-project').dataset('some-dataset').create()
#TODO: Try out a simple put
# maquette_sdk.project('sample-project').dataset('some-dataset').print()
#testdf = pd.DataFrame({'col1': [1, 2], 'col2': [3, 4]})
#dsv = maquette_sdk.project('sample-project').dataset('some-dataset').put(testdf, "muahahaha")

# show a list of all datasets of a project
#TODO: Michael, CSV und text/plain geht nicht mehr?
#print(maquette_sdk.datasets('sample-project',True))

# get a dataset from a project
df = maquette_sdk.project("sample-project").dataset('some-dataset').version("latest").get()
print(df)
# remove a dataset from a project


### Access rights
#TODO integrate later