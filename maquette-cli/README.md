# Maquette CLI

This project contains the CLI implementation for interacting with Maquette.

## Developing maquette-cli

`maquette-cli` uses [Poetry](https://python-poetry.org/) for dependency management and is packaged with [PyInstaller](https://www.pyinstaller.org/). The minimal requirements for a developer workspace are [Conda](https://docs.conda.io/en/latest/miniconda.html) and [Poetry](https://python-poetry.org/docs/#installation).

```
$ git clone $REPOSITORY_URL
$ cd maquette-cli
$ conda create -p ./environment python=3.8

$ poetry install
```