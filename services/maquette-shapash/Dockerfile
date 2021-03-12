FROM python:3.8

ADD . /opt/maquette/services/shapash
WORKDIR /opt/maquette/services/shapash

RUN ls -al && \
    chmod +x run_server.sh && \
    pip install poetry && \
    poetry install

EXPOSE 8050

CMD /bin/bash -c "source /root/.cache/pypoetry/virtualenvs/maquette-shapash-*/bin/activate && ./run_server.sh"