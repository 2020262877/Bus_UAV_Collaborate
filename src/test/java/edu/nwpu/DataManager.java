package edu.nwpu;

import edu.nwpu.dao.BusDao;
import edu.nwpu.domain.Bus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DataManager {

    SqlSession session;
    InputStream in;
    BusDao busDao;
    Bus bus = new Bus();
    static final String regEX = "[^0-9A-Za-z]";
    Pattern pattern;
    Matcher matcher;

    @Before
    public void init() throws Exception {
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        session = factory.openSession(true);
        busDao = session.getMapper(BusDao.class);
    }

    @After
    public void destory() throws Exception {
        session.close();
        in.close();
    }

    /**
     * 读取公交车数据，用正则表达式过滤掉PlateID中的乱码
     *
     * @param fileName 文本文件名
     * @throws Exception
     */
    public void readDatas(String fileName) throws Exception {
        File file = new File(fileName);
        LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] arr = line.split(",");
            matcher = pattern.matcher(arr[2]);
            arr[2] = matcher.replaceAll(" ").trim();
            try {
                insertData(arr, "SQLLOADER");
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
            Thread.sleep(2);
        }
    }

    /**
     * 向数据库中插入数据
     *
     * @param arr 用来保存切割后的公交车数据
     * @param s   过滤掉经度为0的数据
     */
    public void insertData(String arr[], String s) {
        if (!arr[3].equals("0.0")) {
            bus.setPlateId(arr[2]);
            bus.setDate(arr[1]);
            bus.setLatitude(Float.parseFloat(arr[3]));
            bus.setLongitude(Float.parseFloat(arr[4]));
            bus.setSpeed(Float.parseFloat(arr[5]));
            int i = busDao.insertBus(bus);
        }
    }

    /**
     * 读取Bus文本文件 按照Bus的plateID、Time（主键）插入其对应的Speed
     *
     * @param fileName 文本文件名
     * @throws Exception
     */
    public void readAndInsertSpeed(String fileName) throws Exception {
        int i = 0;
        File file = new File(fileName);
        LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8");
        while (lineIterator.hasNext()) {
            ++i;
            String line = lineIterator.nextLine();
            if (i > 23193240) {
                String[] arr = line.split(",");
                matcher = pattern.matcher(arr[2]);
                arr[2] = matcher.replaceAll(" ").trim();
                try {
                    if (!arr[3].equals("0.0")) {
                        bus.setPlateId(arr[2]);
                        bus.setDate(arr[1]);
                        bus.setLatitude(Float.parseFloat(arr[3]));
                        bus.setLongitude(Float.parseFloat(arr[4]));
                        bus.setSpeed(Float.parseFloat(arr[5]));
                        busDao.updateSpeed(bus);
                    }
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                Thread.sleep(1);
            }
        }
    }

    /**
     * 主操作函数
     *
     * @throws Exception
     */
    @Test
    public void shiftData() throws Exception {
        Long startTime = new Date().getTime();
        pattern = Pattern.compile(regEX);
        readDatas("D:\\文档\\Bo Pan\\BusData.txt");
        System.out.println("导入数据总共耗时:" + (new Date().getTime() - startTime) / 1000 + "秒");
    }

    @Test
    public void addSpeed() throws Exception {
        Long startTime = new Date().getTime();
        pattern = Pattern.compile(regEX);
        readAndInsertSpeed("D:\\文档\\Bo Pan\\BusData.txt");
        System.out.println("插入Speed数据总共耗时:" + (new Date().getTime() - startTime) / 1000 + "秒");
    }

    /**
     * 测试正则表达式是否正确
     */
    @Test
    public void test() {
        pattern = Pattern.compile(regEX);
        matcher = pattern.matcher("王�asdf12345CGHGJG把");
        if (matcher.find()) {
            String str = matcher.replaceAll(" ").trim();
            System.out.println(str);
        } else System.out.println(matcher);
    }
}
