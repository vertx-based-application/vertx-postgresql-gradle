package com.trl.vertx_postgresql_gradle.config;

public final class Constans {

  public static final class Endpoints {

    public static final String TEMPERATURE_ALL = "/temperature";

    private Endpoints() {
    }
  }

  public static final class HeaderType {

    public static final String CONTENT_TYPE = "Content-Type";

    private HeaderType() {
    }
  }

  public static final class MediaType {

    public static final String APPLICATION_JSON_VALUE = "application/json";

    private MediaType() {
    }
  }

  public static final class Query {

    public static final String SELECT_ALL_TEMPERATURE_RECORDS = "SELECT * FROM temperature_records";

    private Query() {
    }
  }
}
