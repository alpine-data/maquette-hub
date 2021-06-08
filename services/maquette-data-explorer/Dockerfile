FROM python:3.8
RUN \
    mkdir -p /opt/maquette/services/data-explorer && \
    mkdir -p /opt/maquette/maquette-py/dist

ADD ./pyproject.toml /opt/maquette/services/data-explorer/
ADD ./poetry.lock /opt/maquette/services/data-explorer/

WORKDIR /opt/maquette/services/data-explorer

RUN ls -al && \
    pip install poetry && \
    poetry update -vvv && \
    poetry install -vvv && \
    poetry add maquette -vvv

ADD . /opt/maquette/services/data-explorer/
RUN chmod +x run_server.sh

EXPOSE 9085

RUN mkdir /root/.mq
ADD config.yaml /root/.mq/config.yaml

CMD /bin/bash -c "source /root/.cache/pypoetry/virtualenvs/maquette-data-explorer-*/bin/activate && ./run_server.sh"