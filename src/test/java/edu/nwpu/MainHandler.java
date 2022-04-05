package edu.nwpu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.util.Properties;

public class MainHandler implements Serializable {

    //无人机站的 GPS坐标
    private static double uavLatitude;
    private static double uavLongitude;

    //餐厅的GPS坐标
    private static double resLatitude;
    private static double resLongitude;

    //用户的 GPS坐标
    private static double menLatitude;
    private static double menLongitude;

    //电池容量 单位:Wh
    private static double Eb;
    //无人机飞行速度 单位:m/s
    private static double Vu;
    //电池供电功率 单位:W·h
    private static double Pr;
    //无人机耗电功率 单位:W·h
    private static double Pc;
    //无人机取餐等待时间 单位:s
    private static int ttp;
    //无人机投递等待时间 单位:s
    private static int ttd;

    //速度为0点最小阈值 单位:s
    private static int INTERVAL;
    //路径段最小阈值 单位:s
    private static int LENGTH;

    /**
     * 初始化函数：从pram.properties中加载系统变量
     *
     * @throws IOException
     */
    public static void initialize() throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream("src/test/java/edu/nwpu/file/pram.properties");
        properties.load(fileInputStream);

        uavLatitude = Double.parseDouble(properties.getProperty("uavLatitude"));
        uavLongitude = Double.parseDouble(properties.getProperty("uavLongitude"));

        resLatitude = Double.parseDouble(properties.getProperty("resLatitude"));
        resLongitude = Double.parseDouble(properties.getProperty("resLongitude"));

        menLatitude = Double.parseDouble(properties.getProperty("menLatitude"));
        menLongitude = Double.parseDouble(properties.getProperty("menLongitude"));

        Eb = Double.parseDouble(properties.getProperty("Eb"));
        Vu = Double.parseDouble(properties.getProperty("Vu"));
        Pr = Double.parseDouble(properties.getProperty("Pr"));
        Pc = Double.parseDouble(properties.getProperty("Pc"));
        ttp = Integer.parseInt(properties.getProperty("ttp"));
        ttd = Integer.parseInt(properties.getProperty("ttd"));
        INTERVAL = Integer.parseInt(properties.getProperty("INTERVAL"));
        LENGTH = Integer.parseInt(properties.getProperty("LENGTH"));
    }

    /**
     * 从 plateID.txt中读取车牌号，创建每个车牌号的 BusHandler
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        initialize();

        String fileName = "src/test/java/edu/nwpu/file/plateID.txt";
        File file = new File(fileName);
        LineIterator lineIterator = null;

        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (lineIterator.hasNext()) {
            String PlateId = lineIterator.nextLine();
            IBusHandler busHandler = new BusHandler(PlateId, uavLatitude, uavLongitude,
                    resLatitude, resLongitude, menLatitude, menLongitude, Eb, Vu, Pr, Pc, ttp, ttd, INTERVAL, LENGTH);
            busHandler.main();
//            System.out.print(busHandler);
        }

/*        IBusHandler busHandler = new BusHandler("67413", uavLatitude, uavLongitude,
                resLatitude, resLongitude, menLatitude, menLongitude, Eb, Vu, Pr, Pc, ttp, ttd, INTERVAL, LENGTH);
        busHandler.main();*/
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间:" + (endTime - startTime));
    }

    @Override
    public String toString() {
        return "SingleMainHandler{" +
                "uavLatitude=" + uavLatitude +
                ", uavLongitude=" + uavLongitude +
                ", resLatitude=" + resLatitude +
                ", resLongitude=" + resLongitude +
                ", menLatitude=" + menLatitude +
                ", menLongitude=" + menLongitude +
                ", Eb=" + Eb +
                ", Vu=" + Vu +
                ", Pr=" + Pr +
                ", Pc=" + Pc +
                ", ttp=" + ttp +
                ", ttd=" + ttd +
                '}';
    }
}
