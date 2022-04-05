package Utils;

import bean.Bus;
import bean.Depot;
import bean.SuitableTrips;
import bean.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cluster implements Serializable {
    //簇心的个数
    private Integer K;

    //阈值,当簇心前后移动距离小于 T 时,表示聚类结束
    private Float T;

    //保存了 suitableTrip 的文件路径
    private String filePath;

    private List<SuitableTrips> tripsList;

    //old 聚类中心
    SuitableTrips[] pastCenter = null;

    //new 聚类中心
    SuitableTrips[] newCenter = null;

    //判断每个 SuitableTrips 是属于哪个 center
    int[] belongs = null;

    final Float u = 1F;
    final int co = 10;
    final int ce = 20;

    //将 SuitableTrips 文件内容读取到 tripsList
    public void readTrips() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));

        while (true) {
            try {
                SuitableTrips trips = (SuitableTrips) in.readObject();
                tripsList.add(trips);
            } catch (EOFException e) {
                break;
            }
        }
        in.close();
    }

    public void initClusterCenter() {
        pastCenter = new SuitableTrips[K];
        newCenter = new SuitableTrips[K];
        int pointNum = tripsList.size();
        belongs = new int[pointNum];
        int[] flag = new int[pointNum];

        //从 tripsList 中随机产生 K 个不重复的簇心
        for (int i = 0; i < K; i++) {
            int curNo = new Random().nextInt(pointNum);
            while (flag[curNo] == 1) {
                curNo = new Random().nextInt(pointNum);
            }
            flag[curNo] = 1;
            pastCenter[i] = tripsList.get(curNo);
        }
    }

    //根据聚类算法,找出每个点属于哪个聚类中心
    public void searchBelong() throws IOException {
        for (int i = 0; i < tripsList.size(); i++) {
            SuitableTrips t1 = tripsList.get(i);
            double min = Integer.MAX_VALUE;

            for (int j = 0; j < K; j++) {
                SuitableTrips t2 = pastCenter[j];
                //欧式距离
                double curL = DistanceHandler.getDistance(t1.getDepot().getLatitude(), t1.getDepot().getLongitude(), t2.getDepot().getLatitude(), t2.getDepot().getLongitude());
                double xo = -2 * co * comSet(t1, t2, 1, "***********") / (t1.getTripsO().size() + t2.getTripsO().size());
                double xe = -2 * ce * comSet(t1, t2, 0, "***********") / (t1.getTripsE().size() + t2.getTripsE().size());
                curL += (2 + xo + xe);
                if (curL < min) {
                    min = curL;
                    belongs[i] = j;
                }
            }
        }
    }

    // 更新聚类中心
    public void updateCenter() {
        for (int i = 0; i < K; i++) {
            double latSum = 0, lonSum = 0;
            int num = 0;
            for (int j = 0; j < tripsList.size(); j++) {
                if (belongs[j] == i) {
                    num++;
                    latSum += tripsList.get(j).getDepot().getLatitude();
                    lonSum += tripsList.get(j).getDepot().getLongitude();
                }
            }
            double lat = latSum / num, lon = lonSum / num, minDis = Integer.MAX_VALUE;
            SuitableTrips minTrip = new SuitableTrips();
            for (int j = 0; j < tripsList.size(); j++) {
                SuitableTrips curTrip = tripsList.get(j);
                double curDis = DistanceHandler.getDistance(curTrip.getDepot().getLatitude(), curTrip.getDepot().getLongitude(), lat, lon);
                if (curDis < minDis) {
                    minTrip = curTrip;
                    minDis = curDis;
                }
            }
            newCenter[i] = minTrip;
        }
    }

    public void newToOld() {
        newCenter = pastCenter;
    }

    public void change() {

    }

    //可以同时对 d1,d2 进行配送的 trip 的数量
    public int comSet(SuitableTrips t1, SuitableTrips t2, int state, String dataFilePath) throws IOException {
        Depot d1 = t1.getDepot(), d2 = t2.getDepot();
        int sum = 0;

        LineIterator lineIterator = FileUtils.lineIterator(new File(dataFilePath), "utf-8");

        //若求空载
        if (state == 0) {
            List<Trip> trips = t2.getTripsE();
            //看满足 d2 的路径是否适合 d1
            for (int i = 0; i < trips.size(); i++) {
                Bus start = trips.get(i).getStart();
                int len = trips.get(i).getLength();

                //起点是否满足
                if (DistanceHandler.getDistance(start.getLatitude(), start.getLongitude(), d1.getLatitude(), d2.getLongitude()) <= d1.getRadius()) {
                    //将 start 序列化
                    String serialStart = start.getId() + "," + start.getLatitude() + "," + start.getLongitude() + "," + start.getState() + "," + start.getTime();
                    int subLen = 0;
                    while (lineIterator.hasNext()) {
                        if (subLen > len) {
                            lineIterator.close();
                            break;
                        }
                        String curLine = lineIterator.next();
                        if (subLen > 0 && subLen <= len) {
                            float curLat = Float.parseFloat(curLine.split(",")[2]);
                            float curLon = Float.parseFloat(curLine.split(",")[3]);
                            if (DistanceHandler.getDistance(d1.getLatitude(), d1.getLongitude(), curLat, curLon) <= d1.getRadius()) {
                                ++sum;
                                lineIterator.close();
                                break;
                            }
                            ++subLen;
                        }
                        lineIterator.nextLine();
                        if (curLine.equals(serialStart))
                            ++subLen;
                    }
                }
            }
            return sum;
        }

        //若求载客 state==1
        List<Trip> trips = t2.getTripsO();
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            Bus start = trip.getStart();
            int len = trip.getLength(), subLen = 0;

            if (DistanceHandler.getDistance(start.getLatitude(), start.getLongitude(), d1.getLatitude(), d2.getLongitude()) <= d1.getRadius())
                continue;
            String serialStart = start.getId() + "," + start.getLatitude() + "," + start.getLongitude() + "," + start.getState() + "," + start.getTime();
            while (lineIterator.hasNext()) {
                String curLine = lineIterator.nextLine();
                if (subLen > 0) {
                    ++subLen;
                    //该 trip 的终点
                    if (subLen == len) {
                        float curLat = Float.parseFloat(curLine.split(",")[2]);
                        float curLon = Float.parseFloat(curLine.split(",")[3]);
                        if (DistanceHandler.getDistance(d1.getLatitude(), d1.getLongitude(), curLat, curLon) <= d1.getRadius()) {
                            ++sum;
                            lineIterator.close();
                            break;
                        }
                    }
                } else if (curLine.equals(serialStart))
                    ++subLen;
                lineIterator.next();
            }
        }
        return sum;
    }

    public static void main(String[] args) throws IOException {
        Bus bus = new Bus(1, 1.1F, 2.2F, 0, "33:33:33");
        String s = bus.getId() + "," + bus.getLatitude() + "," + bus.getLongitude() + "," + bus.getState() + "," + bus.getTime();
        String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\test.txt";
        LineIterator lineIterator = FileUtils.lineIterator(new File(filePath), "utf-8");

        if (lineIterator.hasNext()) {
            String cur = lineIterator.next();
            System.out.println(cur.equals(s));
        }

        lineIterator.close();
    }
}
