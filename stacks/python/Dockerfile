FROM mq-services--data-explorer:0.0.1

ENV MQ_JUPYTER_TOKEN=abc
ENV MQ_BASE_URL=http://host.docker.internal:9042/api/
ENV MQ_USERNAME=alice
ENV MQ_AUTH_USERNAME=alice
EXPOSE 8888

RUN mkdir /opt/notebook

ADD samples /opt/notebook/samples
ADD init.sh /opt/init.sh

RUN /bin/bash -c "source /root/.cache/pypoetry/virtualenvs/maquette-data-explorer-*/bin/activate && pip install imblearn"
RUN chmod +x init.sh

CMD /bin/bash -c "/opt/init.sh && source /root/.cache/pypoetry/virtualenvs/maquette-data-explorer-*/bin/activate && cd /opt/notebook && jupyter notebook --no-browser --allow-root --NotebookApp.allow_origin='*' --NotebookApp.ip='0.0.0.0' --NotebookApp.token='${MQ_JUPYTER_TOKEN}'"
