#!/usr/bin/env bash

gradle clean build;
java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -Dhttp-port=8181 -jar build/libs/vertx-postgresql-gradle-1.0.0-SNAPSHOT-fat.jar;
