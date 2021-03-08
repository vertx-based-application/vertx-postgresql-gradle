package com.trl.vertx_postgresql_gradle;

import com.trl.vertx_postgresql_gradle.config.PostgreSqlConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.trl.vertx_postgresql_gradle.config.Constans.Endpoints.TEMPERATURE_ALL;
import static com.trl.vertx_postgresql_gradle.config.Constans.HeaderType.CONTENT_TYPE;
import static com.trl.vertx_postgresql_gradle.config.Constans.MediaType.APPLICATION_JSON_VALUE;
import static com.trl.vertx_postgresql_gradle.config.Constans.Query.SELECT_ALL_TEMPERATURE_RECORDS;

public class TemperatureSensorVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(TemperatureSensorVerticle.class);

  //  public static final int HTTP_PORT = Integer.parseInt(System.getenv().getOrDefault("http-port", "8080"));
  public static final int HTTP_PORT = Integer.parseInt(System.getProperty("http-port", "8080"));

  @Override
  public void start(Promise<Void> startPromise) {

    Router router = Router.router(vertx);
    router.get(TEMPERATURE_ALL).handler(this::getAllDataHandler);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(HTTP_PORT)
      .onSuccess(ok -> {
        LOG.info("HTTP Server is Running: {}", HTTP_PORT);
        startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void getAllDataHandler(RoutingContext routingContext) {
    LOG.debug("Request all data from: [{}]", routingContext.request().remoteAddress());

    PgPool pgPool = PgPool.pool(vertx, PostgreSqlConfig.pgConnectOpts(), new PoolOptions());
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
