package db;

/**
 * Created by khoanguyen on 12/29/16.
 */
public class colName {
    public static class PromoCols {
        public static String ID = "_id";
        public static String NAME = "name"; // ten khuyen mai
        public static String DESCRIPTION = "desc"; // mo ta
        public static String DATE_FROM = "fromdate"; // khuyen mai tu ngay
        public static String DATE_TO = "todate"; // khuyen mai den ngay
        public static String TRAN_MIN_VALUE = "tranminval"; // giao tri toi thieu cua 1 giao dich de nhan khuyen mai : 20K
        public static String PER_TRAN_VALUE = "pertranval"; // gia tri khuyen mai duoc cong : 10k
        public static String TRAN_TYPE = "trantype";


        //thoi gian keo dai khuyen mai tu luc dang ky vi
        // 0: thuc hien khuyen mai the FromDate to ToDate
        //>1: thuc hien khuyen mai theo ngay tao moi vi
        public static String DURATION = "delaytime";

        public static String MAX_VALUE = "maxval"; // tong gia tri khuyen mai toi da 100K
        public static String INTRO_DATA = "introdata"; // noi dung gio thieu chung trinh khuyen mai bang data
        public static String INTRO_SMS = "introsms"; // noi dung ban sms gioi thieu chuong trinh khuyen mai

        // chuong trinh dang duoc kich hoat hay khong
        // false : khong ; true: kich hoat
        public static String ACTIVE = "active";
        // tinh theo % gia tri giao dich hoac gia tri co dinh
        // val --> gia tri khuyen mai = PROMOTION_VALUE; per gia tri khuyen mai = (PROMOTION_VALUE * Gia tri giao dich)/100
        public static String TYPE = "type";

        //so lan khuyen mai toi da co inviter,10 lan
        public static String MAX_TIMES = "maxtimes";

        //so lan khuyen mai toi thieu cho invitee, 1 lan
        public static String MIN_TIMES = "mintimes";

        //for noti
        public static String NOTI_CAPTION = "noticap";
        //for inviter
        public static String NOTI_BODY_INVITER = "notiboiter";
        public static String NOTI_SMS_INVITER = "notismsiter";

        //for invitee
        public static String NOTI_BODY_INVITEE = "notiboitee";
        public static String NOTI_SMS_INVITEE = "notismsitee";

        public static String NOTI_COMMENT = "noticmt"; // notification comment

        //tai khoan dung de chuyen tien khuyen mai cho khach hang
        public static String ADJUST_ACCOUNT = "adjustacc";

        public static String ADJUST_PIN = "pin";
        public static String DURATION_TRAN = "duratetran";

        public static String CREATE_TIME = "ctime";

        public static String OFF_TIME_FROM = "offtimefrom";
        public static String OFF_TIME_TO = "offtimeto";
        public static String STATUS = "status";
        public static String STATUS_IOS = "status_ios";
        public static String STATUS_ANDROID = "status_android";
        public static String ENABLE_PHASE2 = "enablePhase2";

        public static String TABLE = "promo";
        public static String ADDRESSNAME = "addressName";
        public static String ISVERTICLE = "isVerticle";
        public static String EXTRA = "extra";
    }
}
