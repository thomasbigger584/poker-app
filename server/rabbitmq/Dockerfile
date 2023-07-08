FROM rabbitmq:3.12.1-management-alpine

RUN rabbitmq-plugins enable --offline rabbitmq_stomp

COPY rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
COPY definitions.json /etc/rabbitmq/definitions.json

EXPOSE 61613
