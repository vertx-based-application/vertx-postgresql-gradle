package com.trl.vertx_postgresql_gradle;

import com.trl.vertx_postgresql_gradle.config.PostgreSqlConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static com.trl.vertx_postgresql_gradle.config.Constans.Endpoints.TEMPERATURE_ALL;
import static com.trl.vertx_postgresql_gradle.config.Constans.Endpoints.TEMPERATURE_BY_ID;
import static com.trl.vertx_postgresql_gradle.config.Constans.Endpoints.TEMPERATURE_BY_LAST_MINUTES;
import static com.trl.vertx_postgresql_gradle.config.Constans.HeaderType.CONTENT_TYPE;
import static com.trl.vertx_postgresql_gradle.config.Constans.MediaType.APPLICATION_JSON_VALUE;
import static com.trl.vertx_postgresql_gradle.config.Constans.Query.INSERT_INTO_TEMPERATURE_RECORDS;
import static com.trl.vertx_postgresql_gradle.config.Constans.Query.SELECT_ALL_TEMPERATURE_RECORDS;
import static com.trl.vertx_postgresql_gradle.config.Constans.Query.SELECT_TEMPERATURE_RECORDS_BY_ID;
import static com.trl.vertx_postgresql_gradle.config.Constans.Query.SELECT_TEMPERATURE_RECORDS_BY_LAST_MINUTES;

public class TemperatureSensorVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(TemperatureSensorVerticle.class);

//  private static final String HOSTNAME =System.getenv().getOrDefault("hostname", "localhost");
//  private static final int HTTP_PORT = Integer.parseInt(System.getenv().getOrDefault("http-port", "8080"));

  private static final String HOSTNAME = System.getProperty("hostname", "localhost");
  private static final int HTTP_PORT = Integer.parseInt(System.getProperty("http-port", "8080"));

  private PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) {

    pgPool = PgPool.pool(vertx, PostgreSqlConfig.pgConnectOpts(), new PoolOptions());

    vertx.eventBus().<JsonObject>consumer("temperature.updates", this::recordTemperature);

    Router router = Router.router(vertx);
    router.get(TEMPERATURE_ALL).handler(this::getAllDataHandler);
    router.get(TEMPERATURE_BY_ID).handler(this::getData);
    router.get(TEMPERATURE_BY_LAST_MINUTES).handler(this::getTemperatureForLastMinutes);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(HTTP_PORT)
      .onSuccess(ok -> {
        LOG.info("HTTP Server is Running: [{}:{}]", HOSTNAME, HTTP_PORT);
        startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void recordTemperature(Message<JsonObject> message) {
    JsonObject body = message.body();
    String uuid = body.getString("uuid");
    OffsetDateTime timestamp = OffsetDateTime.ofInstant(Instant.ofEpochMilli(body.getLong("tstamp")), ZoneId.systemDefault());
    Double temperature = body.getDouble("temperature");
    Tuple tuple = Tuple.of(uuid, timestamp, temperature);
    pgPool.preparedQuery(INSERT_INTO_TEMPERATURE_RECORDS)
      .execute(tuple)
      .onSuccess(row -> LOG.debug("Record {}", tuple.deepToString()))
      .onFailure(failure -> LOG.error("Recording failed", failure));
  }

  private void getTemperatureForLastMinutes(RoutingContext routingContext) {
    String lastMinutes = routingContext.request().getParam("lastMinutes");
    LOG.debug("Request the data for the last [{}] minutes from [{}]", lastMinutes, routingContext.request().remoteAddress());

    pgPool.preparedQuery(SELECT_TEMPERATURE_RECORDS_BY_LAST_MINUTES)
      .execute(Tuple.of(lastMinutes))
      .onSuccess(rows -> {
        JsonArray jsonArray = new JsonArray();
        for (Row row : rows) {
          jsonArray.add(new JsonObject()
            .put("uuid", row.getString("uuid"))
            .put("temperature", row.getString("value"))
            .put("timestamp", row.getString("tstamp"))
            .toString());
        }

        routingContext.response()
          .putHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .end(new JsonObject()
            .put("data", jsonArray)
            .encode());
      })
      .onFailure(failure -> {
        LOG.error("Query failed", failure);
        routingContext.fail(500);
      });
  }

  private void getData(RoutingContext routingContext) {
    String uuid = routingContext.request().getParam("uuid");
    LOG.debug("Request the data for [{}] from [{}]", uuid, routingContext.request().remoteAddress());

    pgPool.preparedQuery(SELECT_TEMPERATURE_RECORDS_BY_ID)
      .execute(Tuple.of(uuid))
      .onSuccess(rows -> {
        JsonArray jsonArray = new JsonArray();
        for (Row row : rows) {
          jsonArray.add(new JsonObject()
            .put("temperature", row.getString("value"))
            .put("timestamp", row.getString("tstamp"))
            .toString());
        }

        routingContext.response()
          .putHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .end(new JsonObject()
            .put("uuid", uuid)
            .put("data", jsonArray)
            .encode());
      })
      .onFailure(failure -> {
        LOG.error("Query failed", failure);
        routingContext.fail(500);
      });
  }

  private void getAllDataHandler(RoutingContext routingContext) {
    LOG.debug("Request all data from: [{}]", routingContext.request().remoteAddress());

    pgPool.preparedQuery(SELECT_ALL_TEMPERATURE_RECORDS)
      .execute()
      .onSuccess(rows -> {
        JsonArray jsonArray = new JsonArray();
        for (Row row : rows) {
          jsonArray.add(new JsonObject()
            .put("uuid", row.getString("uuid"))
            .put("temperature", row.getString("value"))
            .put("timestamp", row.getString("tstamp"))
            .toString());
        }

        routingContext.response()
          .putHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .end(new JsonObject().put("data", jsonArray).encode());
      })
      .onFailure(failure -> {
        LOG.error("Woops", failure);
        routingContext.fail(500);
      });
  }
}
