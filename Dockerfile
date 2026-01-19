# Will be updated with an automation of docto-tooling-bot
ARG ROOT_IMAGE=${ROOT_IMAGE:-eclipse-temurin:21-jre}
ARG BASE_VERSION=${BASE_VERSION:-21-jre}

FROM ${ROOT_IMAGE}:${BASE_VERSION}

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

WORKDIR /home/java

# Application
COPY layers/application/dependencies/lib/ /lib/
COPY layers/application/application/lib* /lib/
COPY layers/application/application/application.jar /

# Database Migrations
COPY layers/migrator/dependencies/lib/ /lib/
COPY layers/migrator/application/lib/ /lib/
COPY layers/migrator/application/migrator.jar /

# https://docs.datadoghq.com/tracing/trace_collection/library_config/java/
ARG GIT_COMMIT_SHA1
ENV GIT_COMMIT_SHA1=${GIT_COMMIT_SHA1} \
  DD_VERSION=${GIT_COMMIT_SHA1} \
  DD_GIT_COMMIT_SHA=${GIT_COMMIT_SHA1} \
  DD_GIT_REPOSITORY_URL="github.com/doctolib/kotlin-wordle-training" \
  DD_TAGS="application:kotlin-wordle-training,team:modus" \
  DD_TRACE_OTEL_ENABLED=true \
  DD_TRACE_SAMPLE_RATE=0.1 \
  CLASSPATH="/lib" \
  JAR_TO_RUN=/application.jar \
  spring_profiles_active=prod

USER java
