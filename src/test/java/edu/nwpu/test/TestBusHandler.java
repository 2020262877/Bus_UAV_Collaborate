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
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TestBusHandler {
    private SqlSession session;
    private InputStream in;
    private BusDao busDao;
    private Bus bus = new Bus();

    private String PlateId;
    private List<Path> paths = new ArrayList<Path>();

    private final double latitude = 114.533;
    private final double longitude = 22.1234;

    //速度为0最小阈值10分钟
    private final int INTERVAL = 300000;
    //路径段最小阈值30分钟
    private final int LENGTH = 1800000;

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
        if (lineIterator.hasNext()) {
            PlateId = lineIterator.nextLine();
        }
    }

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




    @Test
    public void main() throws ParseException, IndexOutOfBoundsException {
        initialize();
        List<Bus> buses = busDao.selectByPlateId(PlateId);

        System.out.println("bus大小：" + buses.size());
        findBreakPoint(buses);
        System.out.println("paths大小：" + paths.size());

    }
}
