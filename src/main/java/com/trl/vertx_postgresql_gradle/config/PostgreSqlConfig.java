package com.trl.vertx_postgresql_gradle.config;

import io.vertx.pgclient.PgConnectOptions;

public final class PostgreSqlConfig {

  private PostgreSqlConfig() {
  }

//  private static final String DB_HOST = System.getenv().getOrDefault("db-host", "localhost");
//  private static final int DB_PORT = Integer.parseInt(System.getenv().getOrDefault("db-port", "5432"));
//  private static final String  DB_NAME = System.getenv().getOrDefault("db-name", "vertx-postgresql-gradle-db");
//  private static final String  DB_USER_NAME = System.getenv().getOrDefault("db-user-name", "developer");
//  private static final String  DB_PASSWORD = System.getenv().getOrDefault("db-password", "123");

  private static final String DB_HOST = System.getProperty("db-host", "localhost");
  private static final int DB_PORT = Integer.parseInt(System.getProperty("db-port", "5432"));
  private static final String  DB_NAME = System.getProperty("db-name", "vertx-postgresql-gradle-db");
  private static final String  DB_USER_NAME = System.getProperty("db-user-name", "developer");
  private static final String  DB_PASSWORD = System.getProperty("db-password", "123");

  public static PgConnectOptions pgConnectOpts() {
    return new PgConnectOptions()
      .setHost(DB_HOST)
      .setPort(DB_PORT)
      .setDatabase(DB_NAME)
      .setUser(DB_USER_NAME)
      .setPassword(DB_PASSWORD);
  }
}
