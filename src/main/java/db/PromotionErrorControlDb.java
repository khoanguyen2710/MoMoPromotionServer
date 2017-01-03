package db;

import data.AppConstant;
import data.MongoKeyWords;
import data.PromotionColName;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;

import java.util.ArrayList;

/**
 * Created by khoanguyen on 1/1/17.
 */
public class PromotionErrorControlDb {
    private Vertx vertx;
    private Logger logger;
    private DeliveryOptions deliveryOptions;


    public PromotionErrorControlDb(Vertx vertx, Logger logger) {
        this.logger = logger;
        this.vertx = vertx;
        deliveryOptions = new DeliveryOptions().setSendTimeout(60000L);
    }

    public void insert(String program, final Obj obj, final Handler<Integer> callback) {

        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.INSERT)
                .put(MongoKeyWords.COLLECTION, PromotionColName.PromotionErrorControlCol.TABLE + "_" + program)
                .put(MongoKeyWords.DOCUMENT, obj.toJson());


        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {
            int result = -1;
            if(((JsonObject)event.result().body()).getString(MongoKeyWords.STATUS).equalsIgnoreCase("ok"))
            {
                JsonObject error = new JsonObject(((JsonObject)event.result().body()).getString("message", "{code:-1}"));
                result = error.getInteger("code", -1);
            }
            callback.handle(result);
        });
    }

    public void updatePartial(String phone, String program
            , JsonObject joUpdate, final Handler<Boolean> callback) {

        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.UPDATE);
        query.put(MongoKeyWords.COLLECTION, PromotionColName.PromotionErrorControlCol.TABLE + "_" + program);
        JsonObject match = new JsonObject();

        //matcher
        match.put(PromotionColName.PromotionErrorControlCol.PHONE_NUMBER, phone);
        query.put(MongoKeyWords.CRITERIA, match);


        JsonObject fieldsSet = new JsonObject();
        fieldsSet.put(MongoKeyWords.SET_$, joUpdate);

        query.put(MongoKeyWords.OBJ_NEW, fieldsSet);
        query.put(MongoKeyWords.UPSERT, false);

        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {

            boolean result = false;
            if(event.succeeded())
            {
                JsonObject obj = (JsonObject)event.result().body();
                result = obj.getString("ok", "").equalsIgnoreCase("ok");
            }
            callback.handle(result);

        });
    }

    public void upSert(String phone, String program
            , JsonObject joUpdate, final Handler<Boolean> callback) {

        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.UPDATE);
        query.put(MongoKeyWords.COLLECTION, PromotionColName.PromotionErrorControlCol.TABLE + "_" + program);
        JsonObject match = new JsonObject();

        //matcher
        match.put(PromotionColName.PromotionErrorControlCol.PHONE_NUMBER, phone);
        query.put(MongoKeyWords.CRITERIA, match);


        JsonObject fieldsSet = new JsonObject();
        fieldsSet.put(MongoKeyWords.SET_$, joUpdate);

        query.put(MongoKeyWords.OBJ_NEW, fieldsSet);
        query.put(MongoKeyWords.UPSERT, true);

        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {

            boolean result = false;
            if(event.succeeded())
            {
                JsonObject obj = (JsonObject)event.result().body();
                result = obj.getString("ok", "").equalsIgnoreCase("ok");
            }
            callback.handle(result);

        });

    }

    public void searchWithFilter(String program, JsonObject filter, final Handler<ArrayList<Obj>> callback) {

        //query
        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.FIND);
        query.put(MongoKeyWords.COLLECTION, PromotionColName.PromotionErrorControlCol.TABLE + "_" + program);

        if (filter != null && filter.fieldNames().size() > 0) {
            query.put(MongoKeyWords.MATCHER, filter);
        }

        query.put(MongoKeyWords.BATCH_SIZE, 100000);

        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {
            ArrayList<Obj> arrayList = new ArrayList<Obj>();
            if(event.succeeded())
            {
                JsonArray joArr = ((JsonObject)event.result().body()).getJsonArray(MongoKeyWords.RESULT_ARRAY, null);
                if (joArr != null && joArr.size() > 0) {
                    for (int i = 0; i < joArr.size(); i++) {
                        Obj obj = new Obj(joArr.getJsonObject(i));
                        arrayList.add(obj);
                    }
                }
            }
            callback.handle(arrayList);
        });
    }

    public static class Obj {

        public int phone = 0;
        public String program = "";
        public long time = 0;
        public int error_code = 0;
        public String desc = "";
        public String deviceInfo = "";
        public Obj() {
        }

        public Obj(JsonObject jo) {
            phone = jo.getInteger(PromotionColName.PromotionErrorControlCol.PHONE_NUMBER, 0);
            program = jo.getString(PromotionColName.PromotionErrorControlCol.PROGRAM, "");
            time = jo.getLong(PromotionColName.PromotionErrorControlCol.TIME, 0L);
            error_code = jo.getInteger(PromotionColName.PromotionErrorControlCol.ERROR_CODE, 0);
            desc = jo.getString(PromotionColName.PromotionErrorControlCol.DESC, "");
            deviceInfo = jo.getString(PromotionColName.PromotionErrorControlCol.DEVICE_INFO, "");

        }

        public JsonObject toJson() {
            JsonObject jo = new JsonObject();
            jo.put(PromotionColName.PromotionErrorControlCol.PHONE_NUMBER, phone);
            jo.put(PromotionColName.PromotionErrorControlCol.PROGRAM, program);
            jo.put(PromotionColName.PromotionErrorControlCol.TIME, time);
            jo.put(PromotionColName.PromotionErrorControlCol.ERROR_CODE, error_code);
            jo.put(PromotionColName.PromotionErrorControlCol.DESC, desc);
            jo.put(PromotionColName.PromotionErrorControlCol.DEVICE_INFO, deviceInfo);
            return jo;
        }
    }
}
