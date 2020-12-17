from maquette.__mq import EProjectPrivilege, EDatasetPrivilege, EAuthorizationType, EDataClassification, EDataVisibility, EPersonalInformation
import maquette as mq
import pandas as pd

# Create Project
mq.project(name="just-a-sample-project", title="Sample Title", summary="This is a summary").create()

# Show a list of all projects
print(mq.projects(to_csv=True))

# add a dataset to your current project
#mq.dataset('another-dataset').create()

# add a dataset to a specific project
#mq.project(name="just-a-sample-project").dataset('another-dataset').create()

# show all datasets of a specific project
print(mq.project(name="just-a-sample-project").datasets())

# show all datasets of the current project
print(mq.datasets())

# delete the two datasets from above
mq.dataset('another-dataset').remove()
mq.project(name="just-a-sample-project").dataset('another-dataset').delete()


# upload data to the dataset
# testdf = pd.DataFrame({'col1': [1, 2], 'col2': [3, 4]})
# dsv = maquette.dataset('another-dataset').put(testdf, "muahahaha")
# show a list of all datasets of a project
#print(maquette.datasets('sample-project',True))

# show a list of all revisions of a dataset
# print(maquette.project('sample-project').dataset('another-dataset').revisions(True))

#initialize a DatasetVersion from a Dataset
# df = maquette.dataset('another-dataset').version().get()
# print(df)
#

#Delete the project in the end
print(mq.project(name="just-a-sample-project").remove())

# get a dataset from a project
# df = maquette.project("sample-project").dataset('some-dataset').version("latest").get()
# print(df)
# remove a dataset from a project