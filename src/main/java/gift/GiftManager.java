package gift;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import utils.Common;
import utils.Misc;

import java.util.*;

/**
 * Created by khoanguyen on 1/1/17.
 */
public class GiftManager {

    public static final String TRANTYPE_TRANSFER = "transfer";
    public static final String TRANTYPE_G2N = "g2n";
    public static long DEFAULT_CORE_TIMEOUT = 7 * 60 * 1000L;

    public String G2N_ACCOUNT = "";
    private long MIN_GIFT_PRICE = 5000;
    private long MAX_GIFT_PRICE = 2000000;

    private Vertx vertx;
    private Logger logger;

    public GiftManager(Vertx vertx, Logger logger, JsonObject globalConfig) {
        this.vertx = vertx;
        this.logger = logger;
    }

    public void adjustGiftValue(final String fromAgent
            , final String toAgent
            , final long amount
            , final ArrayList<Misc.KeyValue> keyValues
            , final Handler<JsonObject> callback) {
        final Common.BuildLog log = new Common.BuildLog(logger, fromAgent, "adjustGiftValue");
        log.add("fromAgent", fromAgent);
        log.add("toAgent", toAgent);
        log.add("amount", amount);

        //todo call adjust gift via redis.
    }
}
