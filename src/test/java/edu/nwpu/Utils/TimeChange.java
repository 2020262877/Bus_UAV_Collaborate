package edu.nwpu.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeChange {
    public static int dateToStamp(String s) throws Exception {
        s = "2013-10-22 " + s;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return (int) (date.getTime() / 1000);
    }

    public static String stampToDate(long s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(s);
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(dateToStamp("8:00:00"));
        System.out.println(dateToStamp("20:00:00"));
        long l = 1382443200;
        System.out.println(stampToDate(l * 1000));
    }
}
