package edu.nwpu;

import edu.nwpu.Utils.CalculateDistance;
import edu.nwpu.Utils.QuickSortDisList;
import edu.nwpu.Utils.TimeChange;
import edu.nwpu.bean.DistanceAndTime;
import edu.nwpu.bean.Path;
import edu.nwpu.dao.BusDao;
import edu.nwpu.domain.Bus;
import edu.nwpu.domain.Order;
import edu.nwpu.evaluation.BusTripDistance;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class BusHandler implements Serializable, IBusHandler {

    private SqlSession session;
    private InputStream in;
    private BusDao busDao;
    private Bus bus = new Bus();

    //车牌号
    private String PlateId;
    //无人机仓库经纬度
    private double uavLatitude;
    private double uavLongitude;
    //餐厅经纬度
    private double resLatitude;
    private double resLongitude;
    //用户经纬度
    private double menLatitude;
    private double menLongitude;

    //电池容量 单位:Wh
    private double Eb;
    //无人机飞行速度 单位:m/s
    private double Vu;
    //电池供电功率 单位:W·h
    private double Pr;
    //无人机耗电功率 单位:W·h
    private double Pc;
    //无人机消耗能量 单位:Wh
    private double Ce;
    //无人机剩余航程 单位:m
    private double Rr;
    //无人机剩余电量 单位:Wh
    private double Er;

    //无人机取餐等待时间 单位:s
    private int ttp;
    //无人机投递等待时间 单位:s
    private int ttd;
    //无人机起飞时间 Time类型
    private String tdp;

    //速度为0点最小阈值 单位:ms
    private int INTERVAL;
    //路径段最小阈值 单位:ms
    private int LENGTH;

    private List<Path> paths = new ArrayList<Path>();

    private int busSize;

    //构造函数
    public BusHandler(String plateId, double uavLatitude, double uavLongitude,
                      double resLatitude, double resLongitude, double menLatitude, double menLongitude,
                      double eb, double vu, double pr, double pc, int ttp, int ttd, int INTERVAL, int LENGTH) {
        PlateId = plateId;
        this.uavLatitude = uavLatitude;
        this.uavLongitude = uavLongitude;
        this.resLatitude = resLatitude;
        this.resLongitude = resLongitude;
        this.menLatitude = menLatitude;
        this.menLongitude = menLongitude;
        Eb = eb;
        Vu = vu;
        Pr = pr;
        Pc = pc;
        this.ttp = ttp;
        this.ttd = ttd;
        this.INTERVAL = INTERVAL;
        this.LENGTH = LENGTH;
    }

    /**
     * 分割出每条公交车的路径，将结果保存到列表 paths中
     * path{起始点，终止点，距离用户最近的点，距离餐厅最近的点，距离无人机仓库最近的点}
     *
     * @param buses 该Bus所有数据点
     * @throws Exception phaseException
     */
    public void findBreakPoint(List<Bus> buses) throws Exception {
        long endDateStamp = 0, startDateStamp = 0;
        Bus startPoint, endPoint;

        int j = 0;
        while (j < busSize && buses.get(j++).getSpeed() == 0) ;

        if (j < busSize) {
            startPoint = buses.get(j);
            endPoint = startPoint;
            for (int i = j; i < busSize; ) {
                if (i < buses.size() && buses.get(i).getSpeed() != 0) {
                    ++i;
                    if ((i == buses.size() - 1) &&
                            (TimeChange.dateToStamp(endPoint.getDate()) - TimeChange.dateToStamp(startPoint.getDate()) > LENGTH)) {
//                    paths.add(calculatePath(buses, startPoint, endPoint));
                        paths.add(new Path(startPoint, endPoint));
                    }
                } else {
                    while (i < busSize && buses.get(i).getSpeed() == 0) {
                        endDateStamp = (TimeChange.dateToStamp(buses.get(i).getDate()));
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
//                    if (TimeChange.dateToStamp(endPoint.getDate()) - TimeChange.dateToStamp(startPoint.getDate()) > LENGTH) {
//                        paths.add(calculatePath(buses, startPoint, endPoint));
                        paths.add(new Path(startPoint, endPoint));
                        if (i < buses.size())
                            startPoint = buses.get(i);
//                    }
                    }
                    startDateStamp = 0;
                }
            }
        }
    }


    /**
     * 计算一条路径 {起始点，终止点，距离用户最近的点，距离餐厅最近的点，距离无人机仓库最近的点}
     *
     * @param buses      该Bus所有数据点
     * @param startPoint 该路径的起始点
     * @param endPoint   该路径的起始点
     * @return Path
     * @throws Exception phaseException
     */
    public Path calculatePath(List<Bus> buses, Bus startPoint, Bus endPoint) throws Exception {
        int startIndex = buses.indexOf(startPoint), endIndex = buses.indexOf(endPoint);
        Bus menMinPoint, resMinPoint, uavMinPoint;
        int menMinStamp, resMinStamp, uavMinStamp;

        //计算距离用户最近的坐标点
        menMinPoint = calculateMinDistance(buses, startIndex, endIndex, menLatitude, menLongitude);
        menMinStamp = TimeChange.dateToStamp(menMinPoint.getDate());
        //计算距离餐厅最近的坐标点
        resMinPoint = calculateMinDistance(buses, startIndex, endIndex, resLatitude, resLongitude);
        resMinStamp = TimeChange.dateToStamp(resMinPoint.getDate());
        //计算距离无人机最近的坐标点
        uavMinPoint = calculateMinDistance(buses, startIndex, endIndex, uavLatitude, uavLongitude);
        uavMinStamp = TimeChange.dateToStamp(uavMinPoint.getDate());

        if (!(uavMinStamp < resMinStamp && resMinStamp < menMinStamp)) {
            List<Bus> list;
            if (resMinStamp >= menMinStamp) {
                list = calculateDistanceList(buses, startIndex, endIndex, resLatitude, resLongitude);
                for (Bus value : Collections.unmodifiableList(list)) {
                    resMinPoint = value;
                    resMinStamp = TimeChange.dateToStamp(resMinPoint.getDate());
                    if (resMinStamp < menMinStamp)
                        break;

                }
                if (uavMinStamp >= resMinStamp) {
                    list = calculateDistanceList(buses, startIndex, endIndex, uavLatitude, uavLongitude);
                    for (Bus value : list) {
                        uavMinPoint = value;
                        uavMinStamp = TimeChange.dateToStamp(uavMinPoint.getDate());
                        if (uavMinStamp < resMinStamp)
                            break;
                    }
                }
            } else if (uavMinStamp >= resMinStamp) {
                list = calculateDistanceList(buses, startIndex, endIndex, uavLatitude, uavLongitude);
                for (Bus value : list) {
                    uavMinPoint = value;
                    uavMinStamp = TimeChange.dateToStamp(uavMinPoint.getDate());
                    if (uavMinStamp < resMinStamp)
                        break;
                }
            }
        }
        return new Path(startPoint, endPoint, menMinPoint, resMinPoint, uavMinPoint);
    }


    /**
     * 计算某路径上距离某个坐标点最近的一个点
     *
     * @param buses      该Bus所有数据点
     * @param startIndex 该路径的起始下标
     * @param endIndex   该路径的终止下标
     * @param latitude   无人机仓库/用户/餐厅的纬度
     * @param longitude  无人机仓库/用户/餐厅的经度
     * @return 最近的数据点
     */
    public Bus calculateMinDistance(List<Bus> buses, int startIndex, int endIndex, double latitude, double longitude) {
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
     * 计算某路径上距离某个坐标点距离的升序排列
     *
     * @param buses      该Bus所有数据点
     * @param startIndex 该路径的起始下标
     * @param endIndex   该路径的终止下标
     * @param latitude   无人机仓库/用户/餐厅的纬度
     * @param longitude  无人机仓库/用户/餐厅的经度
     * @return 距离的升序排列
     */
    public List<Bus> calculateDistanceList(List<Bus> buses, int startIndex, int endIndex, double latitude, double longitude) {
        LinkedList<Double> disList = new LinkedList<Double>();
        LinkedList<Bus> busList = new LinkedList<Bus>();

        //取出 startIndex到 endIndex之间的 bus，计算其对应的距离
        for (int i = startIndex; i <= endIndex; i++) {
            bus = buses.get(i);
            disList.add(CalculateDistance.getDistance(bus.getLatitude(), bus.getLongitude(), latitude, longitude));
            busList.add(bus);
        }

        //快排 busList
        QuickSortDisList.quickSort(disList, busList, 0, disList.size() - 1);
        return busList;
    }

    public void main() throws Exception {

        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        session = factory.openSession(true);
        busDao = session.getMapper(BusDao.class);

        LinkedList<Bus> buses = busDao.selectByPlateId(PlateId);
        busSize = buses.size();
        List<Order> orders = new ArrayList<Order>();
        findBreakPoint(buses);

        BusTripDistance busTripDistance = new BusTripDistance(paths, buses);
        busTripDistance.main();
        List<DistanceAndTime> d = busTripDistance.getDistances();

        try {
            FileWriter fw = new FileWriter("C:\\Users\\晓哥\\Desktop\\evaluation.txt", true);
            for (int i = 0; i < paths.size(); i++) {
                fw.write(PlateId + " ");
                fw.write(String.valueOf(d.get(i).getDistance()) + " ");
                fw.write(String.valueOf(d.get(i).getTime()) + '\n');
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(this.toString());

/*        OptimalScheme opt = new OptimalScheme(uavLatitude, uavLongitude, resLatitude,
                resLongitude, menLatitude, menLongitude, Eb, Vu, Pr, Pc, ttp, ttd);

        for (Path path : paths) {
            orders.add(opt.executeOnePath(buses, path));
        }*/
//        System.out.println(orders);

        session.close();
        in.close();
    }

    @Override
    public String toString() {
        return "{" +
                "PlateId='" + PlateId + '\'' +
                ", paths.size()=" + paths.size() +
                '}';
    }
}
