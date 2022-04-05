package Utils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHandler {

    public static int dateToStamp(String s) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return (int) (date.getTime() / 1000);
    }

    public static String stampToDate(long s) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(s);
        return simpleDateFormat.format(date);
    }

    @Test
    public void test() throws Exception {
        System.out.println(dateToStamp("10:09:55") - dateToStamp("10:08:55"));
    }
}
