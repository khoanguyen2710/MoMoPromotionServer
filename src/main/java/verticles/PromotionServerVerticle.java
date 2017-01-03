package verticles;

import data.AppConstant;
import data.StringConstUtil;
import db.ConnectorHTTPPostPathDb;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import promotion.PromotionLoader;
import utils.Misc;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class PromotionServerVerticle extends AbstractVerticle {

    ConnectorHTTPPostPathDb connectorHTTPPostPathDb;
    private Logger logger;
    private JsonObject glbCfg;
    private String HOST_ADDRESS = "";
    private int PORT = 0;
    private PromotionLoader promotionLoader;

    @Override
    public void start() {
        this.logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
        this.glbCfg = config();
        connectorHTTPPostPathDb = new ConnectorHTTPPostPathDb(vertx);
//        joServerConfig = glbCfg.getObject("promotion_server", new JsonObject());
        promotionLoader = new PromotionLoader(vertx, logger, glbCfg);
        loadConfig(connectorHTTPPostPathDb, logger, new Handler<JsonObject>() {
            @Override
            public void handle(JsonObject event) {
                createServer();
            }
        });
    }

    private void createServer() {
        HttpServer server = vertx.createHttpServer();

        Handler<HttpServerRequest> promotionHandler = new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest request) {
                String path = request.path();

                if(path.equalsIgnoreCase("/promotion") && request.method().toString().equalsIgnoreCase("POST")) {
                    //params to execute Promotion process
                    request.bodyHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer buffer) {
                            logger.info("Data receive from client promotion " + buffer.toString());
                            if(Misc.isValidJsonObject(buffer.toString()))
                            {
                                JsonObject joData = new JsonObject(buffer.toString());
                                promotionLoader.executePromotionVerticle(joData, new Handler<JsonArray>() {
                                    @Override
                                    public void handle(JsonArray listPromotionResults) {
                                        logger.info("send buffer " + listPromotionResults.toString());
                                        request.response().end(listPromotionResults.toString());
                                    }
                                });
                            }
                            else {
                                logger.info("data receive from client promotion is not json object " + buffer.toString());
                                responseError(5000, "data receive from client promotion is not json object ", request);
                            }
                        }
                    });
                } else if (path.equalsIgnoreCase("/promotionV2") && request.method().toString().equalsIgnoreCase("POST")) {
                    //params to execute Promotion process
                    request.bodyHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer buffer) {
                            logger.info("Data receive from client promotion " + buffer.toString());
                            if (Misc.isValidJsonObject(buffer.toString())) {
                                JsonObject joData = new JsonObject(buffer.toString());
                                promotionLoader.executePromotionVerticle(joData, new Handler<JsonArray>() {
                                    @Override
                                    public void handle(JsonArray listPromotionResults) {
                                        logger.info("send buffer " + listPromotionResults.toString());
                                        request.response().end(listPromotionResults.toString());
                                    }
                                });
                            } else {
                                logger.info("data receive from client promotion is not json object " + buffer.toString());
                                responseError(5000, "data receive from client promotion is not json object ", request);
                            }
                        }
                    });
                } else {
                    responseError(5001, "data receive from client promotion is not post method", request);
                }
            }
        };

        // set handle to server
        server.requestHandler(promotionHandler);
        server.listen(PORT, HOST_ADDRESS, new Handler<AsyncResult<HttpServer>>() {
            @Override
            public void handle(AsyncResult<HttpServer> event) {
                logger.info("start promotion 2 verticle completed with port: " + PORT + " and host: " + HOST_ADDRESS);
                if (event.failed()) {
                    event.cause().printStackTrace();
                }
            }
        });
    }

    private void loadConfig(ConnectorHTTPPostPathDb connectorHTTPPostPathDb, final Logger logger, final Handler<JsonObject> callback)
    {
        logger.info("FUNCTION " + "loadConfig PROMOTION SERVER");
        if(connectorHTTPPostPathDb == null)
        {
            connectorHTTPPostPathDb = new ConnectorHTTPPostPathDb(vertx);
        }

        connectorHTTPPostPathDb.findOne("promotion_server", new Handler<ConnectorHTTPPostPathDb.Obj>() {
            @Override
            public void handle(ConnectorHTTPPostPathDb.Obj pathObj) {
                if(pathObj == null)
                {
                    logger.info("pathObj = null");
                    HOST_ADDRESS = AppConstant.HOST_SERVER;
                    PORT = 0;
                    JsonObject joCallBack = new JsonObject().put("HOST_ADDRESS", HOST_ADDRESS).put("PORT", PORT);
                    callback.handle(joCallBack);
                }
                else {
                    String portServer = "".equalsIgnoreCase(AppConstant.HOST_SERVER) ? pathObj.path : AppConstant.HOST_SERVER;
                    logger.info("HOST_ADDRESS " + portServer);
                    HOST_ADDRESS = portServer;
                    PORT = pathObj.port;
                    JsonObject joCallBack = new JsonObject().put("HOST_ADDRESS", HOST_ADDRESS).put("PORT", PORT);
                    callback.handle(joCallBack);
                }
            }
        });
    }

    /**
     *
     * @param code
     * @param request
     */
    private void responseError(int code, String desc, HttpServerRequest request) {
        JsonObject joReply = new JsonObject();
        joReply.put(StringConstUtil.PromotionField.ERROR, code);
        joReply.put(StringConstUtil.PromotionField.DESCRIPTION, desc);
        request.response().end(joReply.toString());
    }


}
