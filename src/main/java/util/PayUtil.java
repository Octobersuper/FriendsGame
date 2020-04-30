package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2019/8/20
 * Time:9:41
 */
public class PayUtil {

    public static String generateOrderId(){
        String keyup_prefix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String keyup_append=String.valueOf(new Random().nextInt(899999)+100000);
        String pay_orderid=keyup_prefix+keyup_append;//订单号
        return pay_orderid;
    }
    public static String generateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
