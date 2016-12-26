import data.VerticleString;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import utils.Misc;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class MainVerticle extends AbstractVerticle {

    Logger logger;
    JsonObject joConfig;

    @Override
    public void start() throws Exception {
        logger = Logger.getLogger(this.getClass().getSimpleName());
        joConfig = config();
        JsonObject jsonVerticles = Misc.readJsonObjectFile("verticle.json");

        if (jsonVerticles != null) {
            JsonArray jsonArrayVerticle = jsonVerticles.getJsonArray("verticles", new JsonArray());

            Queue<JsonObject> queueVerticle = new ArrayDeque<JsonObject>();
            for (Object vert : jsonArrayVerticle) {
                queueVerticle.add((JsonObject) vert);
            }
            deployVerticle(queueVerticle);
        }
    }

    private void deployVerticle(final Queue<JsonObject> queueVerticle) {
        if (queueVerticle.size() > 0) {
            JsonObject jsonVerticle = queueVerticle.poll();
            final String nameOfVerticle = jsonVerticle.getString(VerticleString.NAME, "");
            boolean isDeployed = jsonVerticle.getBoolean(VerticleString.IS_DEPLOYED, true);
            if (isDeployed) {
                final String address = jsonVerticle.getString(VerticleString.ADDRESS, "");
                DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(joConfig).setWorker(true);
                vertx.deployVerticle(address, deploymentOptions, new AsyncResultHandler<String>() {
                    @Override
                    public void handle(AsyncResult<String> asyncResult) {
                        if (asyncResult.succeeded()) {
                            logger.info(nameOfVerticle + " deploy successfully");
                        } else {
                            logger.info(nameOfVerticle + " deploy fail .... FAIL");
                        }
                        deployVerticle(queueVerticle);
                    }
                });
            } else {
                logger.info(nameOfVerticle + " is not deployed");
                deployVerticle(queueVerticle);
            }
        }
    }
}
