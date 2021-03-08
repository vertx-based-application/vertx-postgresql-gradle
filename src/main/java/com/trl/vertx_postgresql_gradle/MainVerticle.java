package com.trl.vertx_postgresql_gradle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {

    String verticleName = TemperatureSensorVerticle.class.getName();

    DeploymentOptions options = new DeploymentOptions();
    options.setConfig(config());

    vertx.deployVerticle(verticleName, options, ar -> {
      if (ar.succeeded()) {
        LOG.info(String.format("Deployment verticle %s ok ", verticleName));
        startPromise.complete();
      } else {
        LOG.info(String.format("Deployment verticle %s ko ", verticleName));
        startPromise.fail(ar.cause());
      }
    });
  }
}
