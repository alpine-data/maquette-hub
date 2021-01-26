# Client Configuration

Maquette clients which use one of the SDKs or the CLI can have a local configuration file. This file is used to store information about the Maquette Hub server, or configurations of the current project environment. The default location for the file is `${USER_HOME}/.mq/config.yaml`. 

The configuration file is not required. If it doesn't exist default values will be applied when using CLI and SDKs. The file might also be created automatically by the CLI to store settings (e.g. project environment).

Some configuration values can be superseded by environment variables. See the configuration setting descriptions for details.

## Settings

### General settings

`url` - The base url for the Maquette Hub Server.

### Project settings

`project.name` - The name of the current project. Might be overriden by environment variable `MQ_PROJECT_NAME`.

`project.id` - The unique id of the current project. Might be overriden by environment variable `MQ_PROJECT_ID`.

### Environment settings

`environment.id` - An optional id to identify the environment. Might be overriden by environemnt variable `MQ_ENVIRONMENT_ID`.

`environment.type` - An optional type name of the environment. Might be overriden by environment variable `MQ_ENVIRONMENT_TYPE`.

### Authentication

Currently there are three authentication mechanisms: Stupid Authentication, Project Key Authentication and Data Asset Key Authentication. Stupid Authentication just sends the username and the user`s roles with each request. This mechanisms is intended to be used for demo and development environments. The Project Key Authentication can be used for applications accessing project resources or data assets. Data Asset Key Authentication is similar to Project Key authentication, but the key is bound to a data asset instead of a project.

#### Stupid Authentication

`authentication.type` - Must be set to `stupid`. This is the default value for `authentication.type`. Might be overriden by environment variable `MQ_AUTH_TYPE`.

`authentication.username` - The name (id) of the user. Might be overridden by environment variable `MQ_AUTH_USERNAME`.

`authentication.roles` - A list of roles assigned to the user. Might be overridden by environment variable `MQ_AUTH_ROLES`.

#### Project Key Authentication

`authentication.type` - Must be set to `project-key`. Might be overriden by environment variable `MQ_AUTH_TYPE`.

`authentication.key` - The name of the project key. Might be overridden by environment variable `MQ_AUTH_KEY`.

`authentication.secret` - The secret of the project key. Might be overridden by environment variable `MQ_AUTH_SECRET`.

#### Data Asset Key Authentication

`authentication.type` - Must be set to `data-asset-key`. Might be overriden by environment variable `MQ_AUTH_TYPE`

`authentication.key` - The name of the access key. Might be overridden by environment variable `MQ_AUTH_KEY`

`authentication.secret` - The secret of the access key. Might be overridden by environment variable `MQ_AUTH_SECRET`.

## Example file

```yaml
url: http://localhost:3030

project:
    name: some-project
    id: a6721f

environment:
    key: 0
    type: local

authentication: 
    type: stupid
    username: alice
    roles:
        - a-team
        - b-team
```