package promotion;

import data.StringConstUtil;
import db.PromotionDb;
import data.colName;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import utils.Misc;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by khoanguyen on 12/29/16.
 */
public class PromotionLoader {
    Vertx vertx;
    Logger logger;
    JsonObject glbConfig;
    public PromotionLoader(Vertx vertx, Logger logger, JsonObject globalConfig)
    {
        this.vertx = vertx;
        this.logger = logger;
        this.glbConfig = globalConfig;

    }

    /*
        This method used to get all active promotions.
     */
    public void executePromotionVerticle(final JsonObject joData, final Handler<JsonArray> callback)
    {
        final JsonArray jArrNoti = new JsonArray();
        Promo.PromoReqObj promoReqObj = new Promo.PromoReqObj();
        promoReqObj.COMMAND = Promo.PromoType.PROMO_GET_ACTIVE_LIST; //This command used to get all promotions that is running.
        Misc.requestPromoRecord(vertx, promoReqObj, logger, new Handler<JsonObject>() {
            @Override
            public void handle(JsonObject jPromotionResponse) {
                JsonArray jArrListPromotions = jPromotionResponse.getJsonArray("array", null);
                Queue<JsonObject> activePromotionQueued = new ArrayDeque<JsonObject>();
//                JsonArray jArrActivePromotions = new JsonArray();
                if (jArrListPromotions != null && jArrListPromotions.size() > 0) {
                    PromotionDb.Obj promoProgramObj = null;
                    for (Object o : jArrListPromotions) { //Loop all active promotion
                        promoProgramObj = new PromotionDb.Obj((JsonObject) o);
                        if(Misc.isValidJsonObject(promoProgramObj.EXTRA.toString().trim())) //Extra String is Json, is not is???
                        {
                            JsonObject joPromotionExtra = new JsonObject(promoProgramObj.EXTRA.toString().trim()); //Parse Extra String to JsonObject
                            if(joPromotionExtra.containsKey(StringConstUtil.PromotionField.ADDRESS) && !"".equalsIgnoreCase(joPromotionExtra.getString(StringConstUtil.PromotionField.ADDRESS, "")))
                            {
                                activePromotionQueued.add(promoProgramObj.toJsonObject()); //GET ALL PROMOTIONS THAT HAVE VERTICLE ADDRESS IN WEB ADMIN.
                            }
                        }
                    }
                    //After get all suitable promotions, do it.
                    //Check suitable promotions array
                    if(activePromotionQueued.size() == 0)
                    {
                        callback.handle(jArrNoti);
                        return;
                    }
                    //If having some promotions in array
                    executePromotion(joData, activePromotionQueued, jArrNoti, callback);

                }
                else {
                    logger.info("DONT HAVE ANY ACTIVE PROMOTION IS RUNNING");
                    callback.handle(jArrNoti);
                }
            }
        });
    }


    public void executePromotion(final JsonObject joData,final Queue<JsonObject> activedPromotionQueued, final JsonArray jArrNoti, final Handler<JsonArray> callback)
    {
        if(activedPromotionQueued.size() == 0)
        {
            callback.handle(jArrNoti);
            return;
        }
        JsonObject joProgramPromotion = activedPromotionQueued.poll();
        String verticleAddress = joProgramPromotion.getJsonObject(colName.PromoCols.EXTRA, new JsonObject()).getString(StringConstUtil.PromotionField.ADDRESS, "");
        if("".equalsIgnoreCase(verticleAddress))
        {
            executePromotion(joData, activedPromotionQueued, jArrNoti, callback);
        }
        else {
            joData.put(StringConstUtil.PromotionField.PROMOTION_CONFIG, joProgramPromotion);
            DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(60000L);
            vertx.eventBus().send(verticleAddress, joData, deliveryOptions, event -> {
                if(event.succeeded() && event != null && event.result() != null && event.result().body() != null)
                {
                    JsonObject joRespond = (JsonObject)event.result().body();
                    if(joRespond.containsKey(StringConstUtil.PromotionField.NOTIFICATION))
                    {
                        jArrNoti.add(joRespond);
                    }
                }
                executePromotion(joData, activedPromotionQueued, jArrNoti, callback);
            });

        }

    }
}

