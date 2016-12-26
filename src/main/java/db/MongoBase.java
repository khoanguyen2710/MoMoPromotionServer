package db;

import com.mongodb.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhoaNguyen on 12/26/2016.
 */
public class MongoBase extends AbstractVerticle{

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
        logger = new Logger();
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
        JsonArray seedsProperty = config.getArray("seeds");

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
//            if (username != null && password != null) {
//                db.authenticate(username, password.toCharArray());
//            }

            logger.debug("Connect to Mongo server done - use : " + host + ":" + port + "/" + dbName);

        } catch (UnknownHostException e) {
            logger.debug("Failed to connect to Mongo server:port/ " + host + ":" + port, e);
        }
        String fullBusAddress = AppConstant.PREFIX + busAddress;

        logger.info("fullBusAddress: " + fullBusAddress);

//        aggregateStages();

        eb.registerLocalHandler(fullBusAddress, this);
    }
}
