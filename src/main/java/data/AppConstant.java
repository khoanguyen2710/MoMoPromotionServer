package data;

import io.vertx.core.json.JsonObject;
import utils.Misc;

/**
 * Created by khoanguyen on 12/29/16.
 */
public class AppConstant {
    public static String prefixServer = "";

    public static String hostServer = "";
    public static int portServer = 0;
    public static final String PREFIX = getPrefix();
    public static final String HOST_SERVER = getHostServer();
    public static final int PORT_SERVER = getPortServer();
    public static String MongoVerticle_ADDRESS = PREFIX + "com.mservice.momo.database";
    public static String Promotion_ADDRESS = PREFIX + "PromotionVerticle";public static String getPrefix() {
        if ("".equalsIgnoreCase(prefixServer)) {
            //load file
            JsonObject jsonConfigServer = Misc.readJsonObjectFile("config_server.json");
            prefixServer = jsonConfigServer.getString("prefix", "xxx");
            hostServer = jsonConfigServer.getString("host", "");
            portServer = jsonConfigServer.getInteger("port", 0);
        }

        return prefixServer;
    }

    public static String getHostServer() {
        if ("".equalsIgnoreCase(hostServer)) {
            //load file
            JsonObject jsonConfigServer = Misc.readJsonObjectFile("config_server.json");
            prefixServer = jsonConfigServer.getString("prefix", "xxx");
            hostServer = jsonConfigServer.getString("host", "");
            portServer = jsonConfigServer.getInteger("port", 0);
        }

        return hostServer;
    }

    public static int getPortServer() {
        if (0 == (portServer)) {
            //load file
            JsonObject jsonConfigServer = Misc.readJsonObjectFile("config_server.json");
            prefixServer = jsonConfigServer.getString("prefix", "xxx");
            hostServer = jsonConfigServer.getString("host", "");
            portServer = jsonConfigServer.getInteger("port", 0);
        }

        return portServer;
    }

}
