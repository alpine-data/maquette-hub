from maquette_sdk.__mq import EProjectPrivilege, EDatasetPrivilege, EAuthorizationType

import maquette_sdk

maquette_sdk.Project('jforster').create()
print(maquette_sdk.projects())

