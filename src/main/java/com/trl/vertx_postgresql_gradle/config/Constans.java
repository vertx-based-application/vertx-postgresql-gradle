package com.trl.vertx_postgresql_gradle.config;

public final class Constans {

  public static final class Endpoints {

    public static final String TEMPERATURE_ALL = "/temperature";
    public static final String TEMPERATURE_BY_ID = "/temperature/:uuid";
    public static final String TEMPERATURE_BY_LAST_MINUTES = "/temperature/:lastMinutes";

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

    public static final String SELECT_ALL_TEMPERATURE_RECORDS = "SELECT * FROM temperature_records;";
    public static final String SELECT_TEMPERATURE_RECORDS_BY_ID = "SELECT tstamp, value FROM temperature_records WHERE uuid=$1;";
    public static final String SELECT_TEMPERATURE_RECORDS_BY_LAST_MINUTES = "SELECT * FROM temperature_records WHERE tstamp >= now() - INTERVAL'$1 minutes';";
    public static final String INSERT_INTO_TEMPERATURE_RECORDS = "INSERT INTO temperature_records(uuid, tstamp, value) VALUES ($1,$2, $3);";

    private Query() {
    }
  }
}
