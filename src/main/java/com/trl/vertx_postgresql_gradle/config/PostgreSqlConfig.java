package com.trl.vertx_postgresql_gradle.config;

import io.vertx.pgclient.PgConnectOptions;

public class PostgreSqlConfig {

  public static PgConnectOptions pgConnectOpts() {
    return new PgConnectOptions()
      .setHost("localhost")
      .setPort(5432)
      .setDatabase("vertx-postgresql-gradle-db")
      .setUser("developer")
      .setPassword("123");
  }
}
