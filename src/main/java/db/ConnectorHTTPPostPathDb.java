package db;

import data.AppConstant;
import data.MongoKeyWords;
import data.colName;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

/**
 * Created by khoanguyen on 12/29/16.
 */
public class ConnectorHTTPPostPathDb {
    private Vertx vertx;
    private DeliveryOptions deliveryOptions;
    public ConnectorHTTPPostPathDb(Vertx vertx) {
        this.vertx = vertx;
        deliveryOptions = new DeliveryOptions().setSendTimeout(60000L);
    }

    public void searchWithFilter(JsonObject filter, final Handler<ArrayList<Obj>> callback) {

        //query
        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.FIND);
        query.put(MongoKeyWords.COLLECTION, colName.ConnectorHTTPPostPath.TABLE);

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
                        Obj obj = new Obj( joArr.getJsonObject(i));
                        arrayList.add(obj);
                    }
                }
            }

            callback.handle(arrayList);

        });
    }

    public void findContain(String contain, final Handler<ArrayList<Obj>> callback) {
        //query
        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.FIND);
        query.put(MongoKeyWords.COLLECTION, colName.ConnectorHTTPPostPath.TABLE);

        JsonObject matcher = new JsonObject();
        JsonObject joRegex = new JsonObject();
        joRegex.put("$regex", ".*"+contain+".*");
        matcher.put(colName.ConnectorHTTPPostPath.SERVICE_ID, joRegex);
        query.put(MongoKeyWords.MATCHER, matcher);

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

    public void findOne(String serviceId, final Handler<Obj> callback) {

        //query
        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.FIND_ONE);
        query.put(MongoKeyWords.COLLECTION, colName.ConnectorHTTPPostPath.TABLE);

        JsonObject matcher = new JsonObject();
        matcher.put(colName.ConnectorHTTPPostPath.SERVICE_ID, serviceId);
        query.put(MongoKeyWords.MATCHER, matcher);

        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {
            Obj obj = null;
            if(event.succeeded())
            {
                JsonObject joResult = ((JsonObject)event.result().body()).getJsonObject(MongoKeyWords.RESULT, null);
                if (joResult != null) {
                    obj = new Obj(joResult);
                }

            }

            callback.handle(obj);

        });

    }

    public void removeObj(String id, final Handler<Boolean> callback) {

        JsonObject query = new JsonObject();
        JsonObject match = new JsonObject();

        query.put(MongoKeyWords.ACTION, MongoKeyWords.DELETE);
        query.put(MongoKeyWords.COLLECTION, colName.ConnectorHTTPPostPath.TABLE);

        match.put(colName.ConnectorHTTPPostPath.SERVICE_ID, id);

        query.put(MongoKeyWords.MATCHER, match);
        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {
            int count = 0;
            if (event.succeeded())
            {
                count = ((JsonObject)event.result().body()).getInteger("number", 0);
            }
            callback.handle(count > 0);
        });
    }

    public void search(final String service, final Handler<ArrayList<Obj>> callback) {

        JsonObject query = new JsonObject();
        query.put(MongoKeyWords.ACTION, MongoKeyWords.FIND);
        query.put(MongoKeyWords.COLLECTION, colName.ConnectorHTTPPostPath.TABLE);

        //sort by _id desc
        JsonObject sort = new JsonObject();
        sort.put(colName.ConnectorHTTPPostPath.SERVICE_ID, 1);
        query.put(MongoKeyWords.SORT, sort);

        JsonObject match = new JsonObject();
        if (service != null && !"".equalsIgnoreCase(service)) {
            JsonObject ne = new JsonObject();
            ne.put(MongoKeyWords.REGEX, service);
            match.put(colName.ConnectorHTTPPostPath.SERVICE_ID, ne);
            query.put(MongoKeyWords.MATCHER, match);
        }

        vertx.eventBus().send(AppConstant.MongoVerticle_ADDRESS, query, deliveryOptions, event -> {
            ArrayList<Obj> arrayList = new ArrayList<Obj>();
            if(event.succeeded())
            {
                JsonArray results = ((JsonObject)event.result().body()).getJsonArray(MongoKeyWords.RESULT_ARRAY);

                if (results != null && results.size() > 0) {
                    arrayList = new ArrayList<>();
                    for (Object o : results) {
                        arrayList.add(new Obj((JsonObject) o));
                    }
                }
            }

            callback.handle(arrayList);
        });
    }


    public static class Obj {

        public String serviceId = "";
        public String path = "";
        public String host = "";
        public int port = 0;
        public int version = 0;
        public Obj() {
        }

        public Obj(JsonObject jo) {
//
            serviceId = jo.getString(colName.ConnectorHTTPPostPath.SERVICE_ID, "").trim();
            path = jo.getString(colName.ConnectorHTTPPostPath.PATH, "").trim();
            host = jo.getString(colName.ConnectorHTTPPostPath.HOST, "").trim();
            port = jo.getInteger(colName.ConnectorHTTPPostPath.PORT, 0);
            version = jo.getInteger(colName.ConnectorHTTPPostPath.VERSION, 0);
        }

        public JsonObject toJson() {
            JsonObject jo = new JsonObject();
            jo.put(colName.ConnectorHTTPPostPath.SERVICE_ID, serviceId.trim());
            jo.put(colName.ConnectorHTTPPostPath.PATH, path.trim());
            jo.put(colName.ConnectorHTTPPostPath.HOST, host.trim());
            jo.put(colName.ConnectorHTTPPostPath.PORT, port);
            jo.put(colName.ConnectorHTTPPostPath.VERSION, version);
            return jo;
        }
    }
}
