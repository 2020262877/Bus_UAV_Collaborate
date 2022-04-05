package dataHandler;

import Utils.DistanceHandler;
import Utils.TimeHandler;
import bean.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TripHandler {

    /**
     * 将一辆 taxi 的 path 按载客与否分割成多条 trip
     *
     * @param pathName 文件路径
     * @return 符合条件的路径数
     * @date 2021/10/11
     */
    public SuitableTrips getTrip(String pathName, int minTripTime, Depot depot, WareHouse wareHouse) throws Exception {

        SuitableTrips suitableTrips = new SuitableTrips(depot, new ArrayList<Trip>(), new ArrayList<Trip>());
        int curId = 1;
        int curState = 0;
        List<Bus> path = new ArrayList<Bus>();
        Bus bus;

        boolean emptyValid = false;

        LineIterator lineIterator = FileUtils.lineIterator(new File(pathName), "UTF-8");

        System.out.println("begin reading " + pathName);

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] row = line.split(",");
            int id = Integer.parseInt(row[0]);
            bus = new Bus(id, Float.parseFloat(row[1]), Float.parseFloat(row[2]), Integer.parseInt(row[3]), row[4]);

            /**
             * 每条路径的开始点，需判断：
             * 1.该点是否在 warehouse 规定范围之内
             * 2.路径长度是否满足条件
             * 3.若该 trip 不载客，需判断其前一个点 (载客的最后一个点) 是否在 depot 规定范围之内
             */
            if (id != curId || bus.getState() != curState) {

                Trip trip = new Trip(path.get(0), path.size());
                Bus start = path.get(0), end = path.get(path.size() - 1);

                // trip 起点在 warehouse 规定范围内有效
                if (DistanceHandler.getDistance(start.getLatitude(), start.getLongitude(),
                        wareHouse.getLatitude(), wareHouse.getLongitude()) <= wareHouse.getRadius()) {
                    // 如果 trip 载客
                    if (end.getState() == 1) {
                        // 若载客 trip 的终点落在 depot 的规定范围内则该路径有效
                        if (DistanceHandler.getDistance(end.getLatitude(), end.getLongitude(),
                                depot.getLatitude(), depot.getLongitude()) <= depot.getRadius()) {
                            // 路径长度要合适
                            if ((TimeHandler.dateToStamp(end.getTime()) -
                                    TimeHandler.dateToStamp(start.getTime()) > minTripTime))
                                suitableTrips.addTripsO(trip);
                        }
                    } else if (emptyValid && (TimeHandler.dateToStamp(end.getTime()) -
                            TimeHandler.dateToStamp(start.getTime()) > minTripTime)) {
                        suitableTrips.addTripsE(trip);
                    }
                }

                path.clear();
                curState = bus.getState();
                curId = id;
                emptyValid = false;

            } else {
                // 若该 trip 不载客, 且途中经过 depot
                if (bus.getState() == 0 && DistanceHandler.getDistance(bus.getLatitude(), bus.getLongitude(),
                        depot.getLatitude(), depot.getLongitude()) <= depot.getRadius())
                    emptyValid = true;
            }
            path.add(bus);
        }

        System.out.println(pathName + " finish reading!");
        return suitableTrips;
    }

    @Test
    public void test() {
        Bus bus = new Bus();
        System.out.println(bus.getId() == null);
    }
}
