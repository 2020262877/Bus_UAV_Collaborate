package edu.nwpu.test;

import edu.nwpu.Utils.CalculateDistance;
import edu.nwpu.bean.Path;
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

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class BusHandler implements Runnable {

    private SqlSession session;
    private InputStream in;
    private BusDao busDao;
    private Bus bus = new Bus();

    //速度为0最小阈值10分钟
    private final int INTERVAL = 600000;
    //路径段最小阈值30分钟
    private final int LENGTH = 1800000;

    //经纬度，主线程通过构造器传入
    private final double latitude;
    private final double longitude;

    private static int counter = 0;
    private final int id = counter++;
    private static CyclicBarrier barrier;
    private static String plateID;
    private List<Path> paths;

    //    无人机消耗功率
    private final double Pc = 123;
    //    无人机供电功率
    private final double Pr = 123;
    //    无人机飞行速度
    private final double Vu = 123;
    //    无人机电池容量
    private final double Eb = 123;
    //    无人机取餐等待
    private final double ttp = 123;
    //    无人机投递等待
    private final double ttd = 123;

    /**
     * 返回的距离<latitude,longitude>使 t1+t2最小的点
     * int[0] Min(t1+t2)
     * int[1] 起点的RowNumber
     * int[2] 终点的RowNumber
     */
    protected static int result[] = new int[3];

    /**
     * 从当前路径文本文件读取第counter行对应的plateID
     */
    public void initialize() {
        int i = 0;
        String fileName = "src/test/java/edu/nwpu/plateID.txt";
        File file = new File(fileName);
        LineIterator lineIterator = null;

        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            if (++i == counter)
                plateID = line;
        }
    }

    public BusHandler(CyclicBarrier barrier, double latitude, double longitude) {
        this.barrier = barrier;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {

                initialize();
                /* select该车牌号对应数据 */
                List<Bus> buses = busDao.selectByPlateId(plateID);
                /* 求解该车辆的所有路径数据段 */
                findBreakPoint(buses);
                /* 获得该车辆所有路径中距离目标坐标最近的点 */
//                int result[] = getMinFly();
                /* 获得所有车辆对应路径下距离目标坐标最近的点 */
/*                synchronized (this) {
                    if (result[0] < this.result[0]) {
                        this.result = result;
                    }
                }*/
                barrier.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 寻找每辆公交车的断点，创建公交车路径
     *
     * @param buses 当前PlateId对应数据点
     */
    public void findBreakPoint(List<Bus> buses) throws ParseException, IndexOutOfBoundsException {
        long endDateStamp = 0, startDateStamp = 0;
        Bus startPoint = buses.get(0), endPoint = startPoint;

        for (int i = 0; i < buses.size(); ) {
            if (i < buses.size() && buses.get(i).getSpeed() != 0)
                ++i;
            else {
                while (i < buses.size() && buses.get(i).getSpeed() == 0) {
                    endDateStamp = new SimpleDateFormat("hh:mm:ss").parse(buses.get(i).getDate()).getTime();
                    if (startDateStamp == 0) {
                        startDateStamp = endDateStamp;
                        if (i > 0)
                            endPoint = buses.get(i - 1);
                    }
                    ++i;
                }

                //判断速度为0的时间间隔是否大于阈值
                if (endDateStamp - startDateStamp > INTERVAL) {

                    //判断数据段的时间间隔是否大于阈值
                    if (i == buses.size() - 1 || (new SimpleDateFormat("hh:mm:ss").parse(endPoint.getDate()).getTime() -
                            new SimpleDateFormat("hh:mm:ss").parse(startPoint.getDate()).getTime()) > LENGTH) {

                        paths.add(new Path(startPoint, endPoint,
                                calculateMinDistance(buses, buses.indexOf(startPoint), buses.indexOf(endPoint)),null,null));

                        if (i < buses.size())
                            startPoint = buses.get(i);
                    }
                    startDateStamp = 0;
                } else {
                    startDateStamp = 0;
                }
            }
        }
    }

    /**
     * 计算公交车轨迹数据到某点（latitude，longitude）最近的一个
     *
     * @param buses      当前PlateId对应数据点
     * @param startIndex buses起始坐标
     * @param endIndex   buses结束坐标
     * @return 最近的点
     */
    public Bus calculateMinDistance(List<Bus> buses, int startIndex, int endIndex) {
        //默认第一个数据点最近
        double minDistance = CalculateDistance.getDistance(buses.get(startIndex).getLatitude(),
                buses.get(startIndex).getLongitude(), latitude, longitude);
        double distance;
        Bus minBus = buses.get(startIndex);
        int i = startIndex + 1;

        while (i <= endIndex) {
            bus = buses.get(i);
            distance = CalculateDistance.getDistance(bus.getLatitude(), bus.getLongitude(), latitude, longitude);
            if (distance < minDistance) {
                minBus = bus;
                minDistance = distance;
            }
            ++i;
        }
        return minBus;
    }

    /**
     * 获得最短t1+t2
     *
     * @return int[]
     */
    public int[] getMinFly() {
        return new int[]{1, 2, 3};
    }

    public List<Path> getPaths() {
        return paths;
    }

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

}