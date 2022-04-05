package edu.nwpu.test;

import java.text.SimpleDateFormat;
import java.util.*;

public class TestDate {

    public static int dateToStamp(String s) throws Exception {
        s = "2013-10-22 " + s;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return (int) (date.getTime() / 1000);
    }

    public static String stampToDate(long s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(dateToStamp("0:00:00"));
        System.out.println(dateToStamp("2:00:00"));
        System.out.println(dateToStamp("4:00:00"));
        System.out.println(dateToStamp("6:00:00"));
        System.out.println(dateToStamp("09:00:00"));
        System.out.println(dateToStamp("11:00:00"));
        System.out.println(dateToStamp("12:00:00"));
        System.out.println(dateToStamp("13:00:00"));
        System.out.println(dateToStamp("14:00:00"));
        System.out.println(dateToStamp("20:00:00"));
        System.out.println(dateToStamp("23:00:00"));
        System.out.println(dateToStamp("24:00:00"));
        long num = 1382443200;
        System.out.println(stampToDate(num * 1000));
/*        System.out.println(TestDate.dateToStamp("20:23:13"));
        System.out.println(TestDate.stampToDate(44593000));
        System.out.println((int) (1.7888*1000));
        List<Integer> list=new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(0,5);
        int a[]={3,1,2};
        System.out.println(list);
        list.add(2,6);
        System.out.println(list);
        list.remove(list.get(3));
        System.out.println(list);
        Collections.sort(list);
        System.out.println(list);*/
    }
}
