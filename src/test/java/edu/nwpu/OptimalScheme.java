package edu.nwpu;

import edu.nwpu.Utils.CalculateDistance;
import edu.nwpu.Utils.TimeChange;
import edu.nwpu.bean.Path;
import edu.nwpu.bean.Tuples;
import edu.nwpu.domain.Bus;
import edu.nwpu.domain.Order;

import java.io.Serializable;
import java.util.*;

public class OptimalScheme implements Serializable {

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

    //无人机取餐等待时间 单位:s
    private int ttp;
    //无人机投递等待时间 单位:s
    private int ttd;

    //无人机消耗能量 单位:Wh
    private double Ce;
    //无人机剩余航程 单位:m
    private double Rr;
    //无人机剩余电量 单位:Wh
    private double Er;

    //无人机离开无人机站的时刻
    private int tdp;
    //无人机完成外卖拾取的时刻
    private int tpk;
    //无人机完成外卖投递的时刻
    private int tdl;
    //无人机返回无人机站的时刻
    private int trt;

    public OptimalScheme() {
    }

    public OptimalScheme(double uavLatitude, double uavLongitude,
                         double resLatitude, double resLongitude, double menLatitude, double menLongitude,
                         double eb, double vu, double pr, double pc, int ttp, int ttd) {
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
    }

    public Order executeOnePath(List<Bus> buses, Path path) throws Exception {

        // 为该路径生成一个订单
        Order order = new Order();
        //<起点，终点，最短 t1+t2>
        Tuples tuples = new Tuples();

        //==========================================stage A==================================================
        //==========================================stage A==================================================

        //无人机站与到其最短距离的一个数据点的距离 单位 km
        double uavMinDistance = CalculateDistance.getDistance(path.getUavMinPoint().getLatitude(),
                path.getUavMinPoint().getLongitude(), uavLatitude, uavLongitude);

        //该 path的开始点对应在 buses 中的下标
        int pathStartIndex = buses.indexOf(path.getStartPoint());
        int pathEndIndex = buses.indexOf(path.getEndPoint());

        int uavMinIndex = buses.indexOf(path.getUavMinPoint());

        //StageA UAV飞行时间 单位:s
        int stageATime;
        //StageA UAV飞行时间戳
        int flyDateStamp;
        int busDateStamp;

        stageATime = (int) ((int) (uavMinDistance * 1000) / Vu);
        flyDateStamp = TimeChange.dateToStamp(path.getUavMinPoint().getDate()) - stageATime;
        Ce = ((double) stageATime / 3600) * Pc;
        tdp = flyDateStamp;

        //无人机从仓库飞回到公交车上后剩余的电量
        Er = Eb - Ce;
        //无人机剩余飞行航程
        Rr = Er / Pc * Vu;
        //若无人机没电了，返回空订单
        if (Er < 0) return null;

        //==========================================stage B==================================================
        //==========================================stage B==================================================

        double resMinDistance = CalculateDistance.getDistance(path.getResMinPoint().getLatitude(),
                path.getResMinPoint().getLongitude(), resLatitude, resLongitude);
        double resCurrentDistance;

        //打印下 StageA 信息
/*        System.out.println("uavMinPoint:" + path.getUavMinPoint() +
                ", uavMinDistance:" + uavMinDistance +
                ", resMinDistance:" + resMinDistance +
                ", stageATime:" + stageATime +
                ", UAV起飞时间:" + TimeChange.stampToDate(flyDateStamp * 1000) +
                ", 剩余电量Er:" + Er +
                ", 剩余电量可飞行距离:" + Rr);*/

        int t1, t2;
        //无人机消耗能量 单位:Wh
        double curCe;
        //无人机剩余航程 单位:m
        double curRr;
        //无人机剩余电量 单位:Wh
        double curEr;

        int resMinIndex = buses.indexOf(path.getResMinPoint());

        //无人机在Bus上充的电量
        double Se;

        //无人机起飞点该点到餐厅的距离
        List<Double> disList = new ArrayList<Double>();
        List<Integer> flyList = new ArrayList<Integer>();
        //从该点出发时，Bus为无人机充的电量
        List<Double> ErList = new ArrayList<Double>();

        //打印该 path 信息
        System.out.println("\npathStartIndex:" + pathStartIndex + ", uavMinIndex:" + uavMinIndex + "" +
                ", resMinIndex:" + resMinIndex + ", menMinIndex:" + buses.indexOf(path.getMenMinPoint()) +
                ", pathEndIndex" + pathEndIndex);

        for (int i = resMinIndex; i > uavMinIndex; i--) {

            curRr = Rr;
            curEr = Er;
            Se = ((double) (TimeChange.dateToStamp(buses.get(i).getDate()) -
                    TimeChange.dateToStamp(buses.get(uavMinIndex).getDate())) / 3600) * Pr;
//            curEr = (Se + curEr) > Eb ? Eb : (Se + curEr);
            curEr = Math.min(Se + curEr, Eb);
            curRr = curEr / Pc * Vu;


            resCurrentDistance = CalculateDistance.getDistance(buses.get(i).getLatitude(),
                    buses.get(i).getLongitude(), resLatitude, resLongitude);

            System.out.println("Se：" + Se + ", curEr:" + curEr + ", resCurrentDistance:" + resCurrentDistance + ",curRr:" + curRr);

            if (resCurrentDistance < curRr / 2) {
                disList.add(resCurrentDistance);
                flyList.add(i);
                ErList.add(curEr);
            }
        }

        System.out.println("disList:" + disList + ", flyList:" + flyList + ", ErList:" + ErList);


        double minDistance2, minDistance1;
        for (int i = 0; i < flyList.size(); i++) {
            minDistance1 = disList.get(i);

            //无人机从公交车飞到餐厅的时间
            t1 = (int) (minDistance1 / Vu);
            //无人机从公交车飞到餐厅的功耗
            Ce = ((double) t1 / 3600) * Pc;
            //无人机完成外卖拾取的时刻
            tpk = TimeChange.dateToStamp(buses.get(i).getDate()) + t1 + ttp;
            //无人机完成外卖拾取剩余电量
            Er = ErList.get(i) - Ce;
            //剩余电量可飞行距离
            Rr = Er / Pc * Vu;

            //判断公交车开到哪儿了，沿起飞点往后搜索
            for (int j = flyList.get(i) + 1; ; j++) {
                busDateStamp = TimeChange.dateToStamp(buses.get(j).getDate());
                if (busDateStamp >= tpk) {
                    minDistance2 = CalculateDistance.getDistance(buses.get(j).getLatitude(),
                            buses.get(j).getLongitude(), resLatitude, resLongitude);

                    if (minDistance2 < Rr / 2) {
                        if (minDistance2 < tuples.getMinTime() - minDistance1) {
                            tuples = new Tuples(buses.get(i), buses.get(j),
                                    minDistance1 + minDistance2);

                        }
                    } else break;
                }
            }
        }


        //==========================================stage C==================================================
        //==========================================stage C==================================================

        double menMinDistance = CalculateDistance.getDistance(path.getResMinPoint().getLatitude(),
                path.getResMinPoint().getLongitude(), menLatitude, menLongitude);
        double menCurrentDistance;
        int ment1, ment2;

        int menMinIndex = buses.indexOf(path.getMenMinPoint());

        //无人机在Bus上充的电量
        double Se2;

        //无人机起飞点该点到用户的距离
        disList = null;
        flyList = null;
        //从该点出发时，Bus为无人机充的电量
        ErList = null;

        for (int i = menMinIndex; i > uavMinIndex; i--) {

            curRr = Rr;
            curEr = Er;
            Se = ((double) (TimeChange.dateToStamp(buses.get(i).getDate()) -
                    TimeChange.dateToStamp(buses.get(uavMinIndex).getDate())) / 3600) * Pr;
            curEr = (Se + curEr) > Eb ? Eb : (Se + curEr);
            curRr = curEr / Pc * Vu;


            resCurrentDistance = CalculateDistance.getDistance(buses.get(i).getLatitude(),
                    buses.get(i).getLongitude(), resLatitude, resLongitude);

            System.out.println("Se：" + Se + ", curEr:" + curEr + ", resCurrentDistance:" + resCurrentDistance + ",curRr:" + curRr);

            if (resCurrentDistance < curRr / 2) {
                disList.add(resCurrentDistance);
                flyList.add(i);
                ErList.add(curEr);
            }
        }

        System.out.println("disList:" + disList + ", flyList:" + flyList + ", ErList:" + ErList);


        for (int i = 0; i < flyList.size(); i++) {
            minDistance1 = disList.get(i);

            //无人机从公交车飞到餐厅的时间
            t1 = (int) (minDistance1 / Vu);
            //无人机从公交车飞到餐厅的功耗
            Ce = ((double) t1 / 3600) * Pc;
            //无人机完成外卖拾取的时刻
            tpk = TimeChange.dateToStamp(buses.get(i).getDate()) + t1 + ttp;
            //无人机完成外卖拾取剩余电量
            Er = ErList.get(i) - Ce;
            //剩余电量可飞行距离
            Rr = Er / Pc * Vu;

            //判断公交车开到哪儿了，沿起飞点往后搜索
            for (int j = flyList.get(i) + 1; ; j++) {
                busDateStamp = TimeChange.dateToStamp(buses.get(j).getDate());
                if (busDateStamp >= tpk) {
                    minDistance2 = CalculateDistance.getDistance(buses.get(j).getLatitude(),
                            buses.get(j).getLongitude(), resLatitude, resLongitude);

                    if (minDistance2 < Rr / 2) {
                        if (minDistance2 < tuples.getMinTime() - minDistance1) {
                            tuples = new Tuples(buses.get(i), buses.get(j),
                                    minDistance1 + minDistance2);

                        }
                    } else break;
                }
            }
        }

        //==========================================stage D==================================================
        //==========================================stage D==================================================


        return order;
    }
}

