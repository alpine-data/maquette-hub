# Maquette Platform

## Glossar

**Data Access Token**
Data Access Tokens can be generated on behalf of a project to access data without user authentication, but with access tokens instead (just like API keys for usual APIs). The token can be used to consume or produce data automatically from running applications.

**Data Asset**
Data Asset is the generic term for Datasets, Streams, Data Sources, Collections and Data Repositories.

**Dataset**
A dataset is a Maquette Data Resource. A dataset contains versioned sets of records. Each version must have a defined (Avro) schema.

**Revision**
A revision is an uncommitted version of a dataset. A revision has a specified (Avro) schema. As long as the revision is not committed, records of data can be added or removed.