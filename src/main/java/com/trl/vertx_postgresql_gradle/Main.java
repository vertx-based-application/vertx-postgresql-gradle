package com.trl.vertx_postgresql_gradle;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {

    Vertx.clusteredVertx(new VertxOptions())
      .onSuccess(vertx -> {
        vertx.deployVerticle(new TemperatureSensorVerticle());
      })
      .onFailure(failure -> {
        LOG.error("Woops", failure);
      });

  }
}
