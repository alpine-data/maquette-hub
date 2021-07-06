# Working with maquette projects, data assets and sandboxes

## 1. Installation

### Hub
something something... Docker

### CLI and SDK
Maquette is on Pypi! Therefore, you can install the CLI and the SDK via the classical `pip install maquette`

## 2a. Create a Workspace (new)
Projects are the highest level of concepts in the Maquette framework. They include user rights, code repositories, access to data assets and sandboxes. You first need to create or copy a project.

### CLI
You can create a project via the CLI and the following command
```
workspaces create <NAME> <TITLE> <SUMMARY> <CODE_REPOSITORY>
```

`NAME` mandatory unique name of the workspace, only small letters and -\
`TITLE` optional describing title of the workspace\
`SUMMARY` optional summary of the workspace\

The successful creation of a project will result in following output
```
# Heureka! You created a project called ' + name + '(‘-’)人(ﾟ_ﾟ)
# You can clone the code repository with: git clone git@github.com:AlpineDataGroup/maquette_example.git
# To activate the project type: mq project activate ' + name
```

### WebUI
<Screenshot benötigt>


## 2b. Create a Workspace (copy)

## 3. Create Sandboxes

`CODE_REPOSITORY` TODO: you can provide a git repository, where the code base will be synched to. Please be aware, that existing files will be overwritten with the Maquette default files and configs. If you want to clone an existing Maquette project, please refer to "Copy Maquette Project". If you do not provide a git repository, a new code repository address will be provided
<Git clone Befehl zur Verfügung stellen>
### CLI

### WebUI
<Screenshot benötigt>

## 4. Data Asset access

### request access via WebUI
<Screenshot benötigt>

## 5. Develop locally

### activate project via CLI

### inline access via SDK

### push data via SDK

## 6. Start Training on Sandbox







