from maquette.__mq import EProjectPrivilege, EDatasetPrivilege, EAuthorizationType, EDataClassification, EDataVisibility, EPersonalInformation
import maquette as mq
import pandas as pd
import numpy as np

### Create Project
#mq.project(name="just-a-sample-project", title="Sample Title", summary="This is a summary").create()

### Show a list of all projects
print(mq.projects(to_csv=True))

### add a dataset to your current project
# mq.dataset('another-dataset').create()

### add a dataset to a specific project
#mq.project(name="just-a-sample-project").dataset('de-dataset').create()

### show all datasets of a specific project
print(mq.project(name="just-a-sample-project").datasets())

# show all datasets of the current project
#print(mq.datasets())

### upload data to the dataset
testdf = pd.DataFrame({'col1': [1, 2], 'col2': [3, 4]})
df = pd.DataFrame(np.random.choice(['foo','bar','baz'], size=(1000,3)))
df = df.apply(lambda col: col.astype('category'))
print(mq.project(name="just-a-sample-project").dataset('de-dataset').put(df, "muahahaha"))

### Get a Dataframe from a Dataset
df = mq.project(name="just-a-sample-project").dataset('another-dataset').get()
print(df)

### delete the two datasets from above
# mq.dataset('another-dataset').remove()
#mq.project(name="just-a-sample-project").dataset('another-dataset').delete()

# TODO Collection testen
### Create a new collection
# mq.project("just-a-sample-project").collection('another-collection').create()

### Put a file into the collection
#ifile = open('maquette/sample/just-a.csv', 'rb')
#mq.project("just-a-sample-project").collection('another-collection').put(data=ifile, short_description="just a file")

### Get a file from the collection
#ofile = mq.project("just-a-sample-project").collection('another-collection').get('just-a.csv')

### Remove Collection
# mq.project("just-a-sample-project").collection('another-collection').remove()
# TODO Data Source testen
### Create a Data Source

# TODO Streams testen

### Delete the project in the end
print(mq.project(name="just-a-sample-project").remove())
