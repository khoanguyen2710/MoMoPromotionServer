package utils;

import io.vertx.core.logging.Logger;

import java.util.ArrayList;

/**
 * Created by khoanguyen on 1/1/17.
 */
public class Common {

    public static class BuildLog {
        public String flag;
        private ArrayList<String> keys = new ArrayList<>();
        private ArrayList<Object> values = new ArrayList<>();
        private Logger logger;
        private long time = 0;
        private String phoneNumber = "";

        public BuildLog(Logger logger) {
            this.logger = logger;
            time = System.currentTimeMillis();
        }

        public BuildLog(Logger logger, int phone) {
            this(logger, "0" + phone);
        }

        public BuildLog(Logger logger, int phone, String flag) {
            this(logger, "0" + phone, flag);
        }

        public BuildLog(Logger logger, String phone) {
            this(logger, phone, null);
        }

        public BuildLog(Logger logger, String phone, String flag) {
            this.logger = logger;
            time = System.currentTimeMillis();
            this.phoneNumber = phone;
            this.flag = flag;
        }

        public void add(String key, Object value) {
            if (key == null) {
                logger.info("key null mat roi");
            } else {
                if (value != null) {
                    keys.add(key);
                    values.add(value);
                } else {
                    logger.info("value null for key " + key);
                }
            }
        }

        public String getPhoneNumber() {
            return this.phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void writeLog() {
            int i = 0;
            try {

                String prefix = time + "|" + phoneNumber;
                if (flag != null) {
                    prefix += "|" + flag;
                }

                for (i = 0; i < keys.size(); i++) {

                    /*if(keys.get(i) == null || values.get(i) == null){
                        continue;
                    }*/

                    logger.info(prefix + "|" + keys.get(i) + " -> " + values.get(i));

                    /*logger.info(prefix + " " + (keys.get(i) == null ? "" : keys.get(i)) + " -> " + (values.get(i) == null ?  "" : values.get(i)));


                    if (values.get(i) != null) {
                        if ("".equalsIgnoreCase(values.get(i).toString())) {
                            logger.info(prefix + " " + (keys.get(i) == null ? "" : keys.get(i)));
                        } else {
                            logger.info(prefix + " " + (keys.get(i) == null ? "" : keys.get(i)) + " -> " + (values.get(i) == null ?  "" : values.get(i)));
                        }
                    } else {
                        logger.info(prefix + " " + (keys.get(i) == null ? "" : keys.get(i)) + " -> null");
                    }*/
                }
            } catch (Exception ex) {
                logger.info("bi null o key thu " + i);
                logger.info("key" + (keys.get(i) == null ? "null" : keys.get(i)));
                logger.info("value" + (values.get(i) == null ? "null" : values.get(i)));
            }


        }

        public void writeAndClear() {
            writeLog();
            keys.clear();
            values.clear();
        }
    }
}
