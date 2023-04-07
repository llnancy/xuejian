# 2022-05-04 21:11:39 最新rabbitmq-delayed-message-exchange插件支持的rabbitmq server版本为3.9.x.
# 2022-12-09 22:39:21 最新rabbitmq-delayed-message-exchange插件支持的rabbitmq server版本为3.11.x.
# FROM rabbitmq:management-alpine
# FROM rabbitmq:3.9.16-management-alpine
FROM rabbitmq:3.11.4-management-alpine

ARG URL=https://api.github.com/repos/rabbitmq/rabbitmq-delayed-message-exchange/releases/latest
RUN apk add --update --no-cache curl jq wget && \
  VERSION=`curl $URL | jq -r ".tag_name"` && \
  wget -P /plugins https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/${VERSION}/rabbitmq_delayed_message_exchange-${VERSION}.ez && \
  rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange

LABEL maintainer=admin@lilu.org.cn
