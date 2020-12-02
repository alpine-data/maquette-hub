FROM python:3.8
RUN \
    mkdir -p /opt/maquette/services/data-explorer && \
    mkdir -p /opt/maquette/maquette-py/dist

ADD . /opt/maquette/services/data-explorer/
WORKDIR /opt/maquette/services/data-explorer

RUN ls -al && \
    chmod +x run_server.sh && \
    mv maquette-0.0.4-py3-none-any.whl ../../maquette-py/dist/ && \
    pip install poetry && \
    poetry install

EXPOSE 9085

CMD /bin/bash -c "source /root/.cache/pypoetry/virtualenvs/maquette-data-explorer-*/bin/activate && ./run_server.sh"