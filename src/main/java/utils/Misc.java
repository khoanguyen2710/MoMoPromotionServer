package utils;

import data.AppConstant;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import promotion.Promo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class Misc {

    public static JsonObject readJsonObjectFile(String filename) {
        BufferedReader br = null;
        JsonObject jsonObject = new JsonObject();
        try {

            try {
                br = new BufferedReader(new FileReader(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                String fullContent = sb.toString();
                jsonObject = new JsonObject(fullContent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static boolean isValidJsonArray(String jarray) {
        return jarray.startsWith("[") && jarray.endsWith("]");
    }

    public static boolean isValidJsonObject(String text) {
        try {
            JsonObject jsonObject = new JsonObject(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void requestPromoRecord(Vertx _vertx
            , Promo.PromoReqObj promoReq
            , final Logger logger
            , final Handler<JsonObject> callback) {

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(60000);
        _vertx.eventBus().send(AppConstant.Promotion_ADDRESS
                , promoReq.toJsonObject(), deliveryOptions, message -> {
                    if (message.succeeded()) {
                        callback.handle((JsonObject)message.result().body());
                    } else {
                        callback.handle(new JsonObject());
                    }
                });
    }
}
