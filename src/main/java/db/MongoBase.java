package db;

import com.mongodb.*;
import com.mongodb.util.JSON;
import data.MongoKeyWords;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bson.types.ObjectId;
import verticles.BusModBase;

import javax.net.ssl.SSLSocketFactory;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class MongoBase extends BusModBase implements Handler<Message<JsonObject>> {

    public static final int DUPLICATE_CODE_ERROR = 11000;
    public String host;
    public int port;
    public String dbName;
    public String username;
    public String password;
    public String busAddress;
    public boolean autoConnectRetry;
    public int socketTimeout;
    public boolean useSSL;
    public int poolSize = 10;

    protected Mongo mongo;
    protected DB db;
    protected Logger logger;
    public void LoadCfg(JsonObject mongoCfg) {
        logger = Logger.getLogger(this.getClass().getSimpleName());
        host = mongoCfg.getString("host", "localhost");
        port = mongoCfg.getInteger("port", 27017);
        dbName = mongoCfg.getString("db_name", "default_db");
        username = mongoCfg.getString("username", null);
        password = mongoCfg.getString("password", null);
        busAddress = mongoCfg.getString("bus_address", "com.mservice.momo.database");
        poolSize = mongoCfg.getInteger("pool_size", 10);
        autoConnectRetry = mongoCfg.getBoolean("auto_connect_retry", true);
        socketTimeout = mongoCfg.getInteger("socket_timeout", 60000);
        useSSL = mongoCfg.getBoolean("use_ssl", false);

    }

    @Override
    public void start() {

        LoadCfg(config());
        logger.info(busAddress + " - start()");
        JsonArray seedsProperty = config().getJsonArray("seeds");

        try {
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.connectionsPerHost(poolSize);
            builder.socketTimeout(socketTimeout);

            //builder.requiredReplicaSetName("newmomo");

            if (useSSL) {
                builder.socketFactory(SSLSocketFactory.getDefault());
            }

            if (seedsProperty == null) {
                ServerAddress address = new ServerAddress(host, port);
                ArrayList<MongoCredential> mongoCredentials = new ArrayList<>();
                if (username != null && password != null) {
                    mongoCredentials.add(
                            MongoCredential.createMongoCRCredential(
                                    username
                                    ,dbName
                                    ,password.toCharArray())
                    );
                }
                mongo = new MongoClient(address,mongoCredentials, builder.build());
            } else {
                List<ServerAddress> seeds = makeSeeds(seedsProperty);

                //primary down, nearest secondary will be primary node
                builder.readPreference(ReadPreference.secondaryPreferred());

                ArrayList<MongoCredential> mongoCredentials = new ArrayList<>();
                if (username != null && password != null) {
                    for (int i=0; i<seeds.size(); i++){
                        mongoCredentials.add(
                                MongoCredential.createMongoCRCredential(
                                        username
                                        ,dbName
                                        ,password.toCharArray())
                        );
                    }
                    mongo = new MongoClient(seeds, mongoCredentials,builder.build());

                    //operations read will be execute on secondary
                    mongo.setReadPreference(ReadPreference.secondaryPreferred());
                } else {
                    mongo = new MongoClient(seeds, builder.build());
                }
            }

            db = mongo.getDB(dbName);

            logger.info("Connect to Mongo server done - use : " + host + ":" + port + "/" + dbName);

        } catch (UnknownHostException e) {
            logger.info("Failed to connect to Mongo server:port/ " + host + ":" + port + " | " + e);
        }

        vertx.eventBus().consumer("", this);
    }

    protected List<ServerAddress> makeSeeds(JsonArray seedsProperty) throws UnknownHostException {
        List<ServerAddress> seeds = new ArrayList<>();
        for (Object elem : seedsProperty) {
            JsonObject address = (JsonObject) elem;
            String host = address.getString("host");
            int port = address.getInteger("port");
            seeds.add(new ServerAddress(host, port));
        }
        return seeds;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        String action = message.body().getString("action");

        if (action == null) {
            sendError(message, "action must be specified");
            return;
        }

        try {

            // Note actions should not be in camel case, but should use underscores
            // I have kept the version with camel case so as not to break compatibility

            switch (action) {
                case "insert":
                    doInsert(message);
                    break;
                case "save":
                    doSave(message);
                    break;
                case "update":
                    doUpdate(message);
                    break;
                case "find":
                    doFind(message);
                    break;
                case "findone":
                    doFindOne(message);
                    break;
                case "findoneWithLike":
                    doFindOneWithLike(message);
                    break;
                case "findWithFilter":
                    doCountWithFilter(message);
                    break;

                case "findWithLike":
                    doFindWithLike(message);
                    break;

                case "findAndModify":
                case "find_and_modify":
                    doFindAndModify(message);
                    break;
                case "delete":
                    doDelete(message);
                    break;
                case "count":
                    doCount(message);
                    break;
                case "getCollections":
                case "get_collections":
                    getCollections(message);
                    break;
                case "dropCollection":
                case "drop_collection":
                    dropCollection(message);
                    break;
                case "collectionStats":
                case "collection_stats":
                    getCollectionStats(message);
                    break;
                case "command":
                    runCommand(message);
                    break;
                case "aggregate":
                    aggregate(message);
                    break;
                case "aggregateLocation":
                    aggregateLocation(message);
                    break;
                case "aggregateStages":
//                    aggregateStages(message);
                    break;
                default:
                    sendError(message, "Invalid action: " + action);
            }
        } catch (MongoException e) {
            sendError(message, e.getMessage(), e);
        }
    }




    protected void doSave(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject doc = getMandatoryObject("document", message);
        if (doc == null) {
            return;
        }
        String genID;
        if (doc.getValue("_id") == null) {
            genID = UUID.randomUUID().toString();
            doc.put("_id", genID);
        } else {
            genID = null;
        }
        DBCollection coll = db.getCollection(collection);
        DBObject obj = jsonToDBObject(doc);
        WriteConcern writeConcern = WriteConcern.valueOf(getOptionalStringConfig("writeConcern", ""));
        // Backwards compatibility
        if (writeConcern == null) {
            writeConcern = WriteConcern.valueOf(getOptionalStringConfig("write_concern", ""));
        }
        if (writeConcern == null) {
            writeConcern = db.getWriteConcern();
        }

        try {
            WriteResult res = coll.save(obj, writeConcern);
            JsonObject reply = convertWriteResultToJson(res);

            if (genID != null) {
                reply.put("_id", genID);
            }

            sendOK(message, reply);
        }
        catch (MongoException ex){
            sendError(message, ex.getMessage());
        }
    }


    protected void doInsert(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject doc = getMandatoryObject("document", message);
        if (doc == null) {
            return;
        }
        String genID;
        if (doc.getValue("_id") == null) {
            genID = UUID.randomUUID().toString();
            doc.put("_id", genID);
        } else {
            genID = null;
        }
        DBCollection coll = db.getCollection(collection);
        DBObject obj = jsonToDBObject(doc);
        WriteConcern writeConcern = WriteConcern.valueOf(getOptionalStringConfig("writeConcern", ""));
        // Backwards compatibility
        if (writeConcern == null) {
            writeConcern = WriteConcern.valueOf(getOptionalStringConfig("write_concern", ""));
        }
        if (writeConcern == null) {
            writeConcern = db.getWriteConcern();
        }

        try {
            WriteResult res = coll.insert(obj, writeConcern);
            JsonObject reply = convertWriteResultToJson(res);

            if (genID != null) {
                reply.put("_id", genID);
            }

            sendOK(message, reply);
        }
        catch (MongoException ex){
            sendError(message, ex.getMessage());
        }
    }

    protected void doUpdate(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject criteriaJson = getMandatoryObject("criteria", message);
        if (criteriaJson == null) {
            return;
        }
        DBObject criteria = jsonToDBObject(criteriaJson);

        JsonObject objNewJson = getMandatoryObject("objNew", message);
        if (objNewJson == null) {
            return;
        }

        DBObject objNew = jsonToDBObject(objNewJson);
        Boolean upsert = message.body().getBoolean("upsert", false);
        Boolean multi = message.body().getBoolean("multi", false);
        DBCollection coll = db.getCollection(collection);
        WriteConcern writeConcern = WriteConcern.valueOf(getOptionalStringConfig("writeConcern", ""));
        // Backwards compatibility
        if (writeConcern == null) {
            writeConcern = WriteConcern.valueOf(getOptionalStringConfig("write_concern", ""));
        }

        if (writeConcern == null) {
            writeConcern = db.getWriteConcern();
        }

        try {
            WriteResult res = coll.update(criteria, objNew, upsert, multi, writeConcern);
            JsonObject reply = convertWriteResultToJson(res);
            sendOK(message,reply);
        }
        catch (MongoException ex){
            sendError(message, ex.getMessage());
        }

    }


    protected void doCountWithFilter(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        Integer batchSize = (Integer) message.body().getInteger("batch_size");
        if (batchSize == null) {
            batchSize = 100;
        }
        Integer timeout = (Integer) message.body().getInteger("timeout");
        if (timeout == null || timeout < 0) {
            timeout = 10000; // 10 seconds
        }
        JsonObject matcher = message.body().getJsonObject("matcher");

        DBCollection coll = db.getCollection(collection);

        long count = coll.getCount(jsonToDBObject(matcher));
        JsonObject reply = new JsonObject();
        reply.put("count", count);
        message.reply(reply);
    }

    protected void doFind(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        Integer limit = (Integer) message.body().getInteger("limit");
        if (limit == null) {
            limit = -1;
        }
        Integer skip = (Integer) message.body().getInteger("skip");
        if (skip == null) {
            skip = -1;
        }
        Integer batchSize = (Integer) message.body().getInteger("batch_size");
        if (batchSize == null) {
            batchSize = 700;
        }
        Integer timeout = (Integer) message.body().getInteger("timeout");
        if (timeout == null || timeout < 0) {
            timeout = 10000; // 10 seconds
        }

       /* BasicDBObject q = new BasicDBObject();
        q.put("name",  java.util.regex.Pattern.compile(m));
        dbc.find(q);*/

        JsonObject matcher = message.body().getJsonObject("matcher");


        JsonObject keys = message.body().getJsonObject("keys");

        Object hint = message.body().getValue("hint");
        Object sort = message.body().getValue("sort");
        DBCollection coll = db.getCollection(collection);
        DBCursor cursor;
        if (matcher != null) {
            cursor = (keys == null) ?
                    coll.find(jsonToDBObject(matcher)) :
                    coll.find(jsonToDBObject(matcher), jsonToDBObject(keys));
        } else {
            cursor = coll.find();
        }
        //
        cursor.setReadPreference(ReadPreference.secondaryPreferred());

        if (skip != -1) {
            cursor.skip(skip);
        }
        if (limit != -1) {
            cursor.limit(limit);
        }
        if (sort != null) {
            cursor.sort(sortObjectToDBObject(sort));
        }

        if (hint != null) {
            if (hint instanceof JsonObject) {
                cursor.hint(jsonToDBObject((JsonObject) hint));
            } else if (hint instanceof String) {
                cursor.hint((String) hint);
            } else {
                throw new IllegalArgumentException("Cannot handle type " + hint.getClass().getSimpleName());
            }
        }
        sendBatch(message, cursor, batchSize, timeout);
    }

    protected void doFindWithLike(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        Integer limit = (Integer) message.body().getInteger("limit");
        if (limit == null) {
            limit = -1;
        }
        Integer skip = (Integer) message.body().getInteger("skip");
        if (skip == null) {
            skip = -1;
        }
        Integer batchSize = (Integer) message.body().getInteger("batch_size");
        if (batchSize == null) {
            batchSize = 100;
        }
        Integer timeout = (Integer) message.body().getInteger("timeout");
        if (timeout == null || timeout < 0) {
            timeout = 10000; // 10 seconds
        }

       /* BasicDBObject q = new BasicDBObject();
        q.put("name",  java.util.regex.Pattern.compile(m));
        dbc.find(q);*/

        JsonObject liker = message.body().getJsonObject(MongoKeyWords.LIKER);

        JsonObject keys = message.body().getJsonObject("keys");

        Object hint = message.body().getValue("hint");
        Object sort = message.body().getValue("sort");
        DBCollection coll = db.getCollection(collection);
        DBCursor cursor;

        if (liker != null) {

            BasicDBObject q = new BasicDBObject();
            for (String s : liker.fieldNames()) {
                q.put(s,java.util.regex.Pattern.compile(liker.getString(s)));
            }
            cursor = (keys == null) ?
                    coll.find(q) :
                    coll.find(q, jsonToDBObject(keys));
        } else {
            cursor = coll.find();
        }
        //
        cursor.setReadPreference(ReadPreference.secondaryPreferred());

        if (skip != -1) {
            cursor.skip(skip);
        }
        if (limit != -1) {
            cursor.limit(limit);
        }
        if (sort != null) {
            cursor.sort(sortObjectToDBObject(sort));
        }

        if (hint != null) {
            if (hint instanceof JsonObject) {
                cursor.hint(jsonToDBObject((JsonObject) hint));
            } else if (hint instanceof String) {
                cursor.hint((String) hint);
            } else {
                throw new IllegalArgumentException("Cannot handle type " + hint.getClass().getSimpleName());
            }
        }
        sendBatch(message, cursor, batchSize, timeout);
    }



    protected DBObject sortObjectToDBObject(Object sortObj) {
        if (sortObj instanceof JsonObject) {
            // Backwards compatability and a simpler syntax for single-property sorting
            return jsonToDBObject((JsonObject) sortObj);
        } else if (sortObj instanceof JsonArray) {
            JsonArray sortJsonObjects = (JsonArray) sortObj;
            DBObject sortDBObject = new BasicDBObject();
            for (Object curSortObj : sortJsonObjects) {
                if (!(curSortObj instanceof JsonObject)) {
                    throw new IllegalArgumentException("Cannot handle type "
                            + curSortObj.getClass().getSimpleName());
                }

                sortDBObject.putAll(((JsonObject) curSortObj).getMap());
            }

            return sortDBObject;
        } else {
            throw new IllegalArgumentException("Cannot handle type " + sortObj.getClass().getSimpleName());
        }
    }

    protected void sendBatch(Message<JsonObject> message, final DBCursor cursor, final int max, final int timeout) {
        int count = 0;
        JsonArray results = new JsonArray();
        while (cursor.hasNext() && count < max) {
            DBObject obj = cursor.next();
            Map map = obj.toMap();
//            map.remove("_id");
            Object id = map.get("_id");
            if (id instanceof ObjectId) {
                map.put("_id", String.valueOf(id));
            }
            JsonObject m = new JsonObject(map);
            results.add(m);
            count++;
        }
        if (cursor.hasNext()) {
            JsonObject reply = createBatchMessage("more-exist", results);

            // If the user doesn't reply within timeout, close the cursor
            final long timerID = vertx.setTimer(timeout, new Handler<Long>() {
                @Override
                public void handle(Long timerID) {
                    logger.info("Closing DB cursor on timeout");
                    try {
                        cursor.close();
                    } catch (Exception ignore) {
                    }
                }
            });

            message.reply(reply, messageAsyncResult -> {
                vertx.cancelTimer(timerID);
                // Get the next batch
                sendBatch((Message<JsonObject>) messageAsyncResult, cursor, max, timeout);
            });
//            message.reply(reply, new Handler<Message<JsonObject>>() {
//                @Override
//                public void handle(Message<JsonObject> msg) {
//                    vertx.cancelTimer(timerID);
//                    // Get the next batch
//                    sendBatch(msg, cursor, max, timeout);
//                }
//            });

        } else {
            JsonObject reply = createBatchMessage("ok", results);
            message.reply(reply);
            cursor.close();
        }
    }

    protected JsonObject createBatchMessage(String status, JsonArray results) {
        JsonObject reply = new JsonObject();
        reply.put("results", results);
        reply.put("status", status);
        reply.put("number", results.size());
        return reply;
    }

    protected void doFindOne(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject matcher = message.body().getJsonObject("matcher");
        JsonObject keys = message.body().getJsonObject("keys");
        DBCollection coll = db.getCollection(collection);
        DBObject res;
        if (matcher == null) {
            res = keys != null ? coll.findOne(null, jsonToDBObject(keys)) : coll.findOne();
        } else {
            res = keys != null ? coll.findOne(jsonToDBObject(matcher), jsonToDBObject(keys)) : coll.findOne(jsonToDBObject(matcher));
        }
        JsonObject reply = new JsonObject();
        if (res != null) {
            Map map = res.toMap();
            Object id = map.get("_id"); // return an BsonId
            if (id != null) {
                map.put("_id", String.valueOf(id));
            }
            JsonObject m = new JsonObject(map);
            reply.put("result", m);
        }
        sendOK(message, reply);
    }

    protected void doFindOneWithLike(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }

        BasicDBObject q = new BasicDBObject();
        JsonObject liker = message.body().getJsonObject(MongoKeyWords.LIKER);

        if (liker != null) {
            for (String s : liker.fieldNames()) {
                q.put(s,java.util.regex.Pattern.compile(liker.getString(s)));
            }
        }

        JsonObject keys = message.body().getJsonObject("keys");
        DBCollection coll = db.getCollection(collection);
        DBObject res;
        if (q == null) {
            res = keys != null ? coll.findOne(null, jsonToDBObject(keys)) : coll.findOne();
        } else {
            res = keys != null ? coll.findOne(q, jsonToDBObject(keys))
                    :coll.findOne(q);
        }

        JsonObject reply = new JsonObject();
        if (res != null) {
            Map map = res.toMap();
            Object id = map.get("_id"); // return an BsonId
            if (id != null) {
                map.put("_id", String.valueOf(id));
            }
            JsonObject m = new JsonObject(map);
            reply.put("result", m);
        }
        sendOK(message, reply);
    }

    /*
    * ### Find and modify

     The findAndModify command atomically modifies and returns a single document. By default, the returned document does not include the modifications made on the update. To return the document with the modifications made on the update, use the `new` option. See http://docs.mongodb.org/manual/reference/command/findAndModify/ for details:

         {
             "action": "find_and_modify",
             "collection": <collection>,
             "matcher": <document>,
             "sort": <document>,
             "remove": <boolean>,
             "update": <document>,
             "new": <boolean>,
             "fields": <document>,
             "upsert": <boolean>
         }

     When the operation is successful a reply message is sent back to the sender with the relevant document:

         {
             "status": "ok",
             "result": <document>
         }

     Otherwise, it sends an error:

         {
             "status": "error",
             "message": <string>
         }

     An example would be:

         {
             "action": "find_and_modify",
             "collection": "counters",
             "matcher": { "_id": "people" },
             "update": { "$inc": { "seq": 1 } },
             "new": true
         }

    * */
    protected void doFindAndModify(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        DBObject update = jsonToDBObject(getMandatoryObject("update", message));
        if (collection == null || update == null) {
            return;
        }
        JsonObject msgBody = message.body();
        DBObject query = jsonToDBObject(msgBody.getJsonObject("matcher"));
        DBObject sort = jsonToDBObject(msgBody.getJsonObject("sort"));

        DBObject fields = jsonToDBObject(msgBody.getJsonObject("fields"));
        boolean remove = msgBody.getBoolean("remove", false);
        boolean returnNew = msgBody.getBoolean("new", false);
        boolean upsert = msgBody.getBoolean("upsert", false);

        DBCollection coll = db.getCollection(collection);
        DBObject result = coll.findAndModify(query, fields, sort, remove,
                update, returnNew, upsert);

        JsonObject reply = new JsonObject();
        if (result != null) {
            Map map = result.toMap();
            map.remove("_id");
            JsonObject m = new JsonObject(map);
            reply.put("result", m);
        }
        sendOK(message, reply);
    }

    protected void aggregate(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null)
            return;
        JsonObject msgBody = message.body();

        DBObject matcher = jsonToDBObject(msgBody.getJsonObject("matcher"));
        DBObject grouper = jsonToDBObject(msgBody.getJsonObject("grouper"));

        DBObject match = new BasicDBObject().append("$match", matcher);
        DBObject group = new BasicDBObject().append("$group", grouper);


        DBCollection coll = db.getCollection(collection);

        AggregationOutput output = coll.aggregate(match, group);

        JsonArray result = new JsonArray();

        for (DBObject obj : output.results()) {
            Map map = obj.toMap();
            //map.remove("_id");
            result.add(new JsonObject(map));
        }

        message.reply(new JsonObject()
                        .put("result", result)
        );
    }

    protected void aggregateStages() {
        try {
            LinkedList<DBObject> pipeline = new LinkedList<DBObject>();

            BasicDBObjectBuilder matchDbObjectBuilder = new BasicDBObjectBuilder();
            matchDbObjectBuilder.push("$match").push("time").append("$lte", 1481907600000l).append("$gte", 1481302800000l);
            pipeline.add(matchDbObjectBuilder.get());

            BasicDBObjectBuilder projectDbObjectBuilder = new BasicDBObjectBuilder();
            LinkedList<Object> llObject = new LinkedList<Object>();
            llObject.add(new Date(0));
            llObject.add("$time");
            projectDbObjectBuilder.push("$project").push("date").append("$add", llObject).pop()
                    .append("error_code","$error_code").append("desc", "$desc").append("time", "$time");
            pipeline.add(projectDbObjectBuilder.get());

            BasicDBObjectBuilder groupDbObjectBuider = new BasicDBObjectBuilder();
            groupDbObjectBuider.push("$group").push("_id").push("year").append("$year", "$date").pop()
                    .push("dayOfYear").append("$dayOfYear", "$date").pop().append("error_code", "$error_code").pop()
                    .push("total").append("$sum", 1).pop().push("error_code").append("$first", "$error_code").pop()
                    .push("desc").append("$first", "$desc").pop()
                    .push("time").append("$first", "$time").pop();

            pipeline.add(groupDbObjectBuider.get());

            pipeline.add(new BasicDBObject().append("$sort", new BasicDBObject("time", -1)));

            DBCollection coll = db.getCollection("promotion_error_goldengate_promo");

            AggregationOutput output = coll.aggregate(pipeline);

            JsonArray result = new JsonArray();

            for (DBObject obj : output.results()) {
                Map map = obj.toMap();
                result.add(new JsonObject(map));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void aggregateLocation(Message<JsonObject> message) {
        JsonArray result = new JsonArray();
        try {
            String collection = getMandatoryString("collection", message);
            if (collection == null)
                return;
            JsonObject msgBody = message.body();
            JsonObject aggCon = msgBody.getJsonObject("aggCon");

            List<DBObject> listObj = new ArrayList<>();

            if (aggCon.getJsonObject("$geoNear") != null) {
                DBObject geoNear = jsonToDBObject(aggCon.getJsonObject("$geoNear"));
                listObj.add(new BasicDBObject().append("$geoNear", geoNear));
            }
            if (aggCon.getJsonObject("$match") != null) {
                DBObject match = jsonToDBObject(aggCon.getJsonObject("$match"));
                listObj.add(new BasicDBObject().append("$match", match));
            }
            listObj.add(new BasicDBObject().append("$skip", aggCon.getInteger("$skip")));
            listObj.add(new BasicDBObject().append("$limit", aggCon.getInteger("$limit")));

            DBCollection coll = db.getCollection(collection);

            AggregationOutput output = coll.aggregate(listObj);

            for (DBObject obj : output.results()) {
                Map map = obj.toMap();
                Object id = map.remove("_id");
                map.put("_id", id.toString());
                result.add(new JsonObject(map));
            }
        } catch (Exception ex) {
            logger.info("aggregateLocation error: " + ex.getMessage());
        }
        message.reply(new JsonObject().put("results", result));
    }

    protected void doCount(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject matcher = message.body().getJsonObject("matcher");
        DBCollection coll = db.getCollection(collection);
        long count;
        if (matcher == null) {
            count = coll.count();
        } else {
            count = coll.count(jsonToDBObject(matcher));
        }
        JsonObject reply = new JsonObject();
        reply.put("count", count);
        sendOK(message, reply);
    }

    protected void doDelete(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject matcher = getMandatoryObject("matcher", message);
        if (matcher == null) {
            return;
        }
        DBCollection coll = db.getCollection(collection);
        DBObject obj = jsonToDBObject(matcher);
        WriteConcern writeConcern = WriteConcern.valueOf(getOptionalStringConfig("writeConcern", ""));
        // Backwards compatibility
        if (writeConcern == null) {
            writeConcern = WriteConcern.valueOf(getOptionalStringConfig("write_concern", ""));
        }

        if (writeConcern == null) {
            writeConcern = db.getWriteConcern();
        }

        try {
            WriteResult res = coll.remove(obj, writeConcern);
            JsonObject reply = convertWriteResultToJson(res);
            sendOK(message,reply);
        }
        catch (MongoException ex){
            sendError(message, ex.getMessage());
        }
    }

    protected void getCollections(Message<JsonObject> message) {
        JsonObject reply = new JsonObject();
        reply.put("collections", new JsonArray(db.getCollectionNames().toArray().toString()));
        sendOK(message, reply);
    }

    protected void dropCollection(Message<JsonObject> message) {

        JsonObject reply = new JsonObject();
        String collection = getMandatoryString("collection", message);

        if (collection == null) {
            return;
        }

        DBCollection coll = db.getCollection(collection);

        try {
            coll.drop();
            sendOK(message, reply);
        } catch (MongoException mongoException) {
            sendError(message, "exception thrown when attempting to drop collection: " + collection + " \n" + mongoException.getMessage());
        }
    }

    protected void getCollectionStats(Message<JsonObject> message) {

        String collection = getMandatoryString("collection", message);

        if (collection == null) {
            return;
        }

        DBCollection coll = db.getCollection(collection);
        CommandResult stats = coll.getStats();

        JsonObject reply = new JsonObject();
        reply.getJsonObject("stats", new JsonObject(stats.toMap()));
        sendOK(message, reply);

    }

    protected void runCommand(Message<JsonObject> message) {
        JsonObject reply = new JsonObject();

        String command = getMandatoryString("command", message);

        if (command == null) {
            return;
        }

        DBObject commandObject = (DBObject) JSON.parse(command);
        CommandResult result = db.command(commandObject);

        reply.getJsonObject("result", new JsonObject(result.toMap()));
        sendOK(message, reply);
    }

    protected DBObject jsonToDBObject(JsonObject object) {
        BasicDBObject jsontoObj = null;
        try {
            jsontoObj = new BasicDBObject(object.getMap());
            try {
                Object _id = jsontoObj.get("_id");
                if (_id != null) {
                    jsontoObj.put("_id", new ObjectId((String) _id));
                }
            } catch (ClassCastException e) {

            } catch (IllegalArgumentException e) {

            }
        } catch (Exception ex) {
            jsontoObj = null;
        }
        return jsontoObj;
    }

    protected JsonObject convertWriteResultToJson(WriteResult wres){
        JsonObject reply = new JsonObject();
        reply.put("number", wres.getN());
        reply.put("upsertedId", wres.getUpsertedId() == null ? "":wres.getUpsertedId().toString());
        reply.put("isUpdated", wres.isUpdateOfExisting());
        return reply;
    }

    //    db.gift.update(
//    { "$and":[{endDate:{"$lte" : 1437982118000}}, {status:3}]},
//    {
//        $set: {status: 10}
//    },
//    { upsert: false, multi: true }
//    )
    protected void doUpdateWithCode(Message<JsonObject> message) {
        String collection = getMandatoryString("collection", message);
        if (collection == null) {
            return;
        }
        JsonObject criteriaJson = getMandatoryObject("criteria", message);
        if (criteriaJson == null) {
            return;
        }
        DBObject criteria = jsonToDBObject(criteriaJson);

        JsonObject objNewJson = getMandatoryObject("objNew", message);
        if (objNewJson == null) {
            return;
        }

        DBObject objNew = jsonToDBObject(objNewJson);
        Boolean upsert = message.body().getBoolean("upsert", false);
        Boolean multi = message.body().getBoolean("multi", false);
        DBCollection coll = db.getCollection(collection);
        WriteConcern writeConcern = WriteConcern.valueOf(getOptionalStringConfig("writeConcern", ""));
        // Backwards compatibility
        if (writeConcern == null) {
            writeConcern = WriteConcern.valueOf(getOptionalStringConfig("write_concern", ""));
        }

        if (writeConcern == null) {
            writeConcern = db.getWriteConcern();
        }

        try {
            WriteResult res = coll.update(criteria, objNew, upsert, multi, writeConcern);
            JsonObject reply = convertWriteResultToJson(res);
            sendOK(message,reply);
        }
        catch (MongoException ex){
            sendError(message, ex.getMessage());
        }

    }
}
