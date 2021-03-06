package promotion;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by khoanguyen on 12/29/16.
 */
public class Promo {
    public static class PromoReq{
        public static String DEBITOR="debitor";
        public static String CREDITOR ="creditor";
        public static String CREATOR="creator";
        public static String PHONE_NUMBER="phonenumber";
        public static String TIME = "time";
        public static String NOTI_CAPTION ="noticap";
        public static String NOTI_BODY ="notibody";
        public static String NOTI_COMMENT ="noticmt";
        public static String NOTI_SMS ="notisms";
        public static String TRAN_ID ="tranid";
        public static String TRAN_TYPE = "trantype";
        public static String TRAN_AMOUNT = "tranamount";
        public static String PARTNER_ID ="partnerid";
        public static String PARTNER_CODE ="partnercode";
        public static String PARTNER_NAME ="partnername";
        public static String PROMO_CODE ="promocode";
        public static String PROMO_ID = "promo_id";

        public static String PROMO_NAME = "promo_name";


        public static String KEY_VALUE_PAIR_ARR="kvps";
        public static String RESEND="resend";
        public static String COMMAND ="command";
    }

    public static class PromoReqObj{

        public String DEBITOR =""; // tru tien
        public String CREDITOR =""; // nhan tien
        public String CREATOR=""; // khoi tao giao dich
        public String PHONE_NUMBER="";
        public long TIME = 0;
        public int TRAN_TYPE = -1;
        public long TRAN_ID = 0;
        public long TRAN_AMOUNT = 0;
        public String COMMAND ="";

        public String NOTI_CAPTION ="";
        public String NOTI_BODY ="";
        public String NOTI_COMMENT ="";
        public String NOTI_SMS ="";

        public String PARTNER_ID="";
        public String PARTNER_CODE="";
        public String PARTNER_NAME="";
        public String PROMO_CODE ="";

        public String PROMO_ID ="";
        public String PROMO_NAME ="";
        public boolean RESEND = false;


        public HashMap<String,String> KVPs = null;
        public PromoReqObj(){}

        public JsonObject toJsonObject (){

            JsonObject jo = new JsonObject();
            jo.put(PromoReq.COMMAND,COMMAND);
            jo.put(PromoReq.CREDITOR,CREDITOR);
            jo.put(PromoReq.DEBITOR,DEBITOR);
            jo.put(PromoReq.CREATOR,CREATOR);
            jo.put(PromoReq.PHONE_NUMBER,PHONE_NUMBER);
            jo.put(PromoReq.RESEND,RESEND);

            if(!"".equalsIgnoreCase(DEBITOR)){
                jo.put(PromoReq.DEBITOR, DEBITOR);
            }

            if(TIME >0) {
                jo.put(PromoReq.TIME, TIME);
            }
            if(TRAN_TYPE >=0){
                jo.put(PromoReq.TRAN_TYPE, TRAN_TYPE);
            }

            if(TRAN_AMOUNT >0){
                jo.put(PromoReq.TRAN_AMOUNT, TRAN_AMOUNT);
            }
            if(TRAN_ID >0){
                jo.put(PromoReq.TRAN_ID,TRAN_ID);
            }

            if(!"".equalsIgnoreCase(NOTI_CAPTION)){
                jo.put(PromoReq.NOTI_CAPTION,NOTI_CAPTION);
            }

            if(!"".equalsIgnoreCase(NOTI_BODY)){
                jo.put(PromoReq.NOTI_BODY,NOTI_BODY);
            }

            if(!"".equalsIgnoreCase(NOTI_COMMENT)){
                jo.put(PromoReq.NOTI_COMMENT,NOTI_COMMENT);
            }

            if(!"".equalsIgnoreCase(NOTI_SMS)){
                jo.put(PromoReq.NOTI_SMS,NOTI_SMS);
            }

            if(!"".equalsIgnoreCase(PARTNER_ID)){
                jo.put(PromoReq.PARTNER_ID,PARTNER_ID);
            }

            if(!"".equalsIgnoreCase(PARTNER_CODE)){
                jo.put(PromoReq.PARTNER_CODE,PARTNER_CODE);
            }

            if(!"".equalsIgnoreCase(PARTNER_NAME)){
                jo.put(PromoReq.PARTNER_NAME,PARTNER_NAME);
            }

            if(!"".equalsIgnoreCase(PROMO_CODE)){
                jo.put(PromoReq.PROMO_CODE,PROMO_CODE);
            }

            if(!"".equalsIgnoreCase(PROMO_ID)){
                jo.put(PromoReq.PROMO_ID,PROMO_ID);
            }

            if(!"".equalsIgnoreCase(PROMO_NAME)){
                jo.put(PromoReq.PROMO_NAME,PROMO_NAME);
            }

            //KVPs =;
            if(KVPs != null && KVPs.size()>0){

                JsonArray ar = new JsonArray();

                Iterator<Map.Entry<String,String>> entries = KVPs.entrySet().iterator();
                while (entries.hasNext()){
                    Map.Entry<String,String> entry = entries.next();

                    JsonObject o = new JsonObject();
                    o.put(entry.getKey(),entry.getValue());
                    ar.add(o);
                }
                jo.put(PromoReq.KEY_VALUE_PAIR_ARR,ar);
            }
            return jo;
        }

        public PromoReqObj(JsonObject jo){

            COMMAND=jo.getString(PromoReq.COMMAND, "");
            CREDITOR = jo.getString(PromoReq.CREDITOR,"");
            DEBITOR = jo.getString(PromoReq.DEBITOR,"");
            CREATOR=jo.getString(PromoReq.CREATOR, "");
            PHONE_NUMBER=jo.getString(PromoReq.PHONE_NUMBER, "");
            TIME=jo.getLong(PromoReq.TIME, 0L);
            TRAN_TYPE=jo.getInteger(PromoReq.TRAN_TYPE, -1);
            TRAN_AMOUNT=jo.getLong(PromoReq.TRAN_AMOUNT, 0L);
            TRAN_ID = jo.getLong(PromoReq.TRAN_ID,0L);

            NOTI_CAPTION = jo.getString(PromoReq.NOTI_CAPTION, "");
            NOTI_BODY=jo.getString(PromoReq.NOTI_BODY, "");
            NOTI_COMMENT=jo.getString(PromoReq.NOTI_COMMENT, "");
            NOTI_SMS=jo.getString(PromoReq.NOTI_SMS, "");

            PARTNER_ID = jo.getString(PromoReq.PARTNER_ID,"");
            PARTNER_CODE = jo.getString(PromoReq.PARTNER_CODE,"");
            PARTNER_NAME = jo.getString(PromoReq.PARTNER_NAME,"");
            PROMO_CODE = jo.getString(PromoReq.PROMO_CODE,"");
            PROMO_ID = jo.getString(PromoReq.PROMO_ID,"");
            RESEND = jo.getBoolean(PromoReq.RESEND,false);
            PROMO_NAME = jo.getString(PromoReq.PROMO_NAME,"");

            //KVPs =;
            JsonArray ar = jo.getJsonArray(PromoReq.KEY_VALUE_PAIR_ARR,null);

            if(ar!= null){
                KVPs = new HashMap<>();
                for(int i=0;i<ar.size();i++){
                    JsonObject o = ar.getJsonObject(i);

                    Iterator<Map.Entry<String,Object>> entries = o.getMap().entrySet().iterator();
                    while (entries.hasNext()){
                        Map.Entry<String,Object> entry= entries.next();
                        KVPs.put(entry.getKey(),entry.getValue().toString());
                    }
                }
            }
        }
    }

    public static class PromoType{

        public static final String CHICKEND_FEED ="chicken_feed";
        public static final String DO_PROMO_BY_CODE ="do_promo_by_code";
        public static final String INVITE_FRIEND_GEN_CODE ="promo_gen_code";
        public static final String INVITE_CREATE_CODE = "invite_create_code";

        public static final String PROMO_UPDATE_DATA = "promo_update";

        public static final String PROMO_GET_LIST = "promo_list";

        public static final String PROMO_GET_ACTIVE_LIST = "promo_active_list";

        public static final String PROMO_DETAIL_BY_PROMO_ID = "promo_detail_by_id";

        //claim tien vao tai khoan KM
        public static final String CLAIM_POINT_BY_CODE = "claimpntbycode";

        public static final String PROMO_M2M_GEN_CODE = "promo_m2m";
        public static final String PROMO_M2M_CLAIM_CODE = "PROMO_M2M_CLAIM_CODE";
        public static final String FORCE_PROMO_M2M_GEN_CODE = "FORCE_PROMO_M2M_GEN_CODE";

        //for vcb
        public static final String GET_PROMOTION_REC = "GET_PROMOTION_REC";

    }

    public static class PromoRes{
        public static String ERROR="error";
        public static String RESULT ="result";
        public static String PROMO_AMOUNT="promo_amt";
        public static String DESCRIPTION="desc";

    }

    public static class PromoResObj{
        public int ERROR = -1;// loi he thong
        public boolean RESULT =false;
        public String DESCRIPTION="";
        public long PROMO_AMOUNT= 0;


        public JsonObject toJsonObject(){
            JsonObject jo = new JsonObject();
            jo.put(PromoRes.ERROR,ERROR);
            jo.put(PromoRes.RESULT,RESULT);
            jo.put(PromoRes.DESCRIPTION, DESCRIPTION);
            jo.put(PromoRes.PROMO_AMOUNT,PROMO_AMOUNT);
            return jo;
        }
        public PromoResObj(){}
        public PromoResObj(JsonObject jo){

            ERROR = jo.getInteger(PromoRes.ERROR,ERROR);
            RESULT = jo.getBoolean(PromoRes.RESULT,RESULT);
            PROMO_AMOUNT = jo.getLong(PromoRes.PROMO_AMOUNT,PROMO_AMOUNT);
            DESCRIPTION = jo.getString(PromoRes.DESCRIPTION, DESCRIPTION);
        }
    }
}
