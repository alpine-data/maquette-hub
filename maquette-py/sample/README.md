# sample

HSkasjdhslkajh

## Initialize the workspace

```bash
$ conda env create -f conda.yaml -p ./env && \
    conda activate ./env && \
    poetry install -vvv
```

## Run MLflow

... works with Maquette MLFlow Stack

```bash
$ mq projects activate
$ mlflow run .
```