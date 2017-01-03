package data;

/**
 * Created by khoanguyen on 1/1/17.
 */
public class PromoContentNotification {
    public enum Error {

        //GENERAL ERROR
        SUCCESS(0, "Thanh cong", ""),
        TRANTYPE(5800, "transtype ko phu hop ko cho choi chuong trinh ", ""),
        CODE(5801, "Mã Khuyến Mãi không hợp lệ !", "Mã khuyến mãi không hợp lệ. Vui lòng kiểm tra lại hoặc gửi thắc mắc của bạn về: hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        INFO_CUSTOMER(5802, "Khách hàng không thuộc chương trình khuyến mãi.",
                "Khách hàng không thuộc chương trình khuyến mãi, vui lòng nhập mã khác hoặc gọi (08) 399 171 99 để được hỗ trợ"),
        RECEIVED_VOUCHER(5803, "Khách hàng đã nhận voucher.", ""),
        SERVICE_ID(5804, "Service Id khong chinh xac.", ""),
        GIVE_VOUCHER(5805, "Khong tra qua duoc.", ""),
        DEVICE(5806, "Thiet bi da nhap ma khuyen mai.", "Bạn đã nhập mã khuyến mãi, vui lòng nhập mã khác hoặc gọi (08) 399 171 99 để được hỗ trợ."),
        INSERT_DB(5807, "insert db khong thanh cong.", ""),
        NOT_CODE(5808, "Khach hang chua nhap ma khuyen mai.", "Khach hang chua nhap ma khuyen mai, vui lòng nhập mã khác hoặc gọi (08) 399 171 99 để được hỗ trợ"),
        RECEIVED_VOUCHER_1(5809, "Khach hang da nhan voucher lan 1.", "Khach hang da nhan voucher lan 1, vui lòng nhập mã khác hoặc gọi (08) 399 171 99 để được hỗ trợ"),
        CORE_ERROR(5810, "Loi core.", ""),
        UPDATE_DB(5811, "update db khong thanh cong.", ""),
        DUPLICATION(5812, "tra qua bi duplication.", ""),
        BANK_NOT_ACEPTED(5813, "Ngan hang nay khong nam trong ngan hang duoc tham gia khuyen mai.", ""),
        ERROR_TIME_OUT(5814, "|Ngoài thời gian tham gia chương trình.", ""),
        ERROR_MISS_PHONE_NUMBER(5815, "|Thieu thong tin so dien thoai.", ""),
        ERROR_IS_STORE_APP(5816, "|Diem giao dich khong duoc tham gia khuyen mai nay.", ""),
        ERROR_MISS_AGENT(5817, "|Chua cau hinh agent tra thuong.", ""),
        ERROR_CREATE_LOCAL_GIFT(5818, "Tao gift local fail.", ""),
        //GENERAL ERROR

        //SYSTEM ERROR
        SYSTEM_ERROR_TIMEOUT(-1, "SYSTEM TIMEOUT", ""),
        MONGO_ERROR_TIMEOUT(-2, "MONGO TIMEOUT", ""),
        //SYSTEM ERROR

        // BEGIN APPOTA
        INPUT_CODE_GTBB_APPOTA(1, "Khach hang da tham gia chuong trinh GTBB nen khong duoc tham gia chuong trinh Nạp game Gamota", "Ví của bạn đã từng nhập mã giới thiệu nên không được tham gia chương trình Nạp game Gamota. Vui lòng kiểm tra lại hoặc gửi thắc mắc của bạn về: hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        MAP_WALLET_APPOTA(2, "Khach hang da tham gia chuong trinh LKTK nen khong duoc tham gia chuong trinh Nạp game Gamota", "Ví của bạn đã từng liên kết tài khoản ngân hàng nên không được tham gia chương trình <tên CT>. Vui lòng kiểm tra lại hoặc gửi thắc mắc của bạn về: hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        COMMON_APPOTA(3, "Cac truong hop loi mac dinh.", "Bạn không thuộc đối tượng được hưởng khuyến mãi từ chương trình Nạp game Gamota. Vui lòng kiểm tra lại hoặc gửi thắc mắc của bạn về: hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        DEVICE_APPOTA(4, "Khach hang da nhan thuong.", "Bạn đã nhập mã chương trình Nạp game Gamota. Vui lòng kiểm tra lại hoặc gửi thắc mắc của bạn về: hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        GUIDE_MAP_WALLET_APPOTA(5, "Huong dan map vi.", "Bạn đã nhập mã thành công! Chỉ còn 1 vài bước nữa thôi để nhận 100.000đ mua mã thẻ và gift code 100.000đ chơi game Gamota! Chạm để xem hướng dẫn. Liên hệ hotro@momo.vn để được hỗ trợ trong vòng 24h."),
        GIFT_CODE_APPOTA(6, "Khach hang da nhan giftcode.", ""),
        // END APPOTA

        //BEGIN BE009
        GTBB_TIMEOUT(6020, "GTBB|Ngoài thời gian tham gia chương trình.",""),
        GTBB_INVITER_ERROR_MAPPING(6021, "GTBB|Người giới thiệu chưa map ví .",""),
        GTBB_INVITEE_ATM_MAPPED(6022, "GTBB|Nguoi duoc gioi thieu da map the ATM.",""),
        GTBB_INVITER_IS_AGENT(6023, "GTBB|So nguoi gioi thieu la diem giao dich", ""),
        GTBB_DUPLICATE_CARD_ID(6024, "Trung chung minh nhan dan nguoi gioi thieu va nguoi duoc gioi thieu", ""),
        GTBB_INVITEE_GIFT_RECEIED(6025, "Nguoi nhap code da nhan thuong", ""),
        GTBB_INVITER_IS_NULL(6026, "Khong ton tai thong tin nguoi gioi thieu", ""),
        GTBB_UNKNOWN(6027, "Loi khong xac dinh, kiem tra va tra bu sau", ""),
        GTBB_MULTIPLE_WALLET_MAPPING(6028, "So dien thoai da map vi nhieu lan truoc khi cashin", ""),
        GTBB_CARD_ID_ERROR(6029, "Khong ton tai thong tin chung minh nhan dan cua ngan hang", ""),
        GTBB_CODE_INPUT_BLANK(6030, "So dien thoai nay chua nhap code", ""),
        GTBB_INVALID_BANK(6031, "Ngan hang khong duoc tham gia chuong trinh", ""),
        GTBB_SUCCESS(0, "Bạn đã nhập mã thành công! Hãy liên kết tài khoản ngân hàng và nạp tiền/thanh toán để nhận thẻ quà tặng 100.000đ nạp tiền điện thoại! Chạm để xem hướng dẫn. Liên hệ hotro@momo.vn để được hỗ trợ trong vòng 24h.", ""),
        GTBB_INVITEE_LINKED_MAPPED(6032, "GTBB|Nguoi duoc gioi thieu da map the Ngan Hang Lien Ket.",""),
        GTBB_RECEIVED_VGG(6033, "Vi da nhan voucher chuong trinh GoldenGate.",""),
        //END BE009
        //BEGIN BE009 LKTK
        LKTK_ERROR_MISS_PERSONAL_CARD_ID(6100, "LKTK| Thieu thong tin CMND cua user", ""),
        LKTK_ERROR_MISS_BANK(6101, "LKTK| Ngan hang khong duoc tham gia chuong trinh LKTK", ""),
        LKTK_ERROR_TRAN_MIN_AMOUNT(6102, "LKTK| Cashin nho hon gia tri duoc tham gia chuong trinh khuyen mai LKTK", ""),
        LKTK_ERROR_GTBB(6103, "LKTK| Da tham gia chuong trinh GTBB nen khong duoc tham gia LKTK nua", ""),
        LKTK_ERROR_MAP_WITH_OTHER_BANK(6104, "LKTK| Da map voi ngan hang roi nen khong cho tham gia", ""),
        LKTK_ERROR_ATM_CARD_ID_NOT_MAP_PERSONAL_ID(6105, "LKTK| The ATM nay khong duoc dung de dinh danh, vui long dung the ATM co CMND lien ket truoc do.", ""),
        LKTK_ERROR_CARD_INFO(6106, "LKTK| Thong tin the khong hop le.", ""),
        LKTK_RECEIVED_VGG(6107, "LKTK| Vi da nhan voucher tu chuong trinh GoldenGate.", ""),
        LKTK_ERROR_DUPLICATE_CARD_ID(6107, "LKTK| %s.", ""),
        LKTK_ERROR_UNKNOWN_MAPPING_TIME(6108, "LKTK| Loi thoi gian map vi = 0.", ""),
        LKTK_ERROR_TIMEOUT(6109, "LKTK| Ngoài thời gian tham gia chương trình.", ""),
        LKTK_ERROR_TIMEOUT_CHEATING(6110, "LKTK| Thời gian map vi va cashin > 30 ngay.", ""),
        LKTK_ERROR_SYSTEM(6110, "LKTK| Loi he thong.", ""),
        LKTK_ERROR_GIFT_CONFIG(6111, "LKTK| Cau hinh qua tren webadmin loi.", ""),
        LKTK_ERROR_CREATE_GIFT(6112, "LKTK| Backend tao qua local bi loi.", ""),
        //END BE009 LKTK

        // BEGIN GOLDENGATE
        GG_NOT_EU(1, "", "Rất xin lỗi, bạn không đủ điều kiện để tham dự để tham gia chương trình khuyến mãi này."),
        GG_BANKCODE(2, "Ngan hang khong nam trong cac ngan hang khuyen mai.", "Rất xin lỗi, bạn không đủ điều kiện để tham dự để tham gia chương trình khuyến mãi này."),
        GG_TRACKOBJ_NOT_EQUAL_1(3, "CMND nay da duoc nhan voucher 1", ""),
        GG_TRAN_AMOUNT(4, "Giao dich cash in tu ngan hang it nhat 10000 VND", ""),
        GG_GTBB(5, "Vi da nhan thuong tu chuong trinh GTBB", ""),
        GG_LKTK(6, "Vi da nhan thuong tu chuong trinh LKTK", ""),
        GG_RECEIVED_VOUCHER_1(7, "Khach hang da nhan voucher 1 khi cash in roi.", "Tài khoản của bạn đã từng nhận thưởng của chương trình <Tên CT>. Vui lòng liên kết với tài khoản ngân hàng khác hoặc liên hệ: hotro@momo.vn để được hỗ trợ trong vòng 24h.")
        ;
        // END GOLDENGATE




        public int index;
        public String info;
        public String noti;

        Error (int index, String info, String noti) {
            this.index = index;
            this.info = info;
            this.noti = noti;
        }
    }

}
