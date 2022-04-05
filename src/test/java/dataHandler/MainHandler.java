package dataHandler;

import bean.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class MainHandler {

    private Float LEFT;
    private Float UP;
    private Float RIGHT;
    private Float DOWN;
    private WareHouse wareHouse;
    private Float depotRadius;
    private Integer minTripTime;

    public void init() throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream("src/test/java/file/parameters.properties");
        properties.load(fileInputStream);

        UP = Float.valueOf(properties.getProperty("range.latitudeLeft"));
        LEFT = Float.valueOf(properties.getProperty("range.longitudeLeft"));
        DOWN = Float.valueOf(properties.getProperty("range.latitudeRight"));
        RIGHT = Float.valueOf(properties.getProperty("range.longitudeRight"));

        wareHouse = new WareHouse();
        wareHouse.setLatitude(Float.valueOf(properties.getProperty("warehouse.latitude")));
        wareHouse.setLongitude(Float.valueOf(properties.getProperty("warehouse.longitude")));
        wareHouse.setRadius(Float.valueOf(properties.getProperty("warehouse.radius")));

        depotRadius = Float.valueOf(properties.getProperty("depot.radius"));

        minTripTime = Integer.valueOf(properties.getProperty("trip.minTimeInterval"));
    }

    @Test
    public void main() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("开始时间:" + start);
        final String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\";
        final String tail = "_sortAndFilter.txt";

        String resSubPath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\result";
        init();
        DepotHandler depotHandler = new DepotHandler();
        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        depotHandler.createDepots(LEFT, UP, RIGHT, DOWN, depotRadius);
        final List<Depot> depots = depotHandler.getDepots();

        List<FutureTask<SuitableTrips>> futureTaskList = new ArrayList<FutureTask<SuitableTrips>>();

/*        // 1.单线程
        for (int i = 0; i < 5; i++) {
            new TripHandler().getTrip(filePath + 3 + tail, minTripTime, depots.get(i), wareHouse);
        }*/

        // 2.多线程直接创建线程
/*        for (int i = 0; i < 5; i++) {
            final int ii = i;
            FutureTask<SuitableTrips> futureTask = new FutureTask<SuitableTrips>(new Callable<SuitableTrips>() {
                @Override
                public SuitableTrips call() throws Exception {
                    return new TripHandler().getTrip(filePath + 3 + tail, minTripTime, depots.get(ii), wareHouse);
                }
            });
            new Thread(futureTask).start();
            futureTaskList.add(futureTask);
        }
        write(futureTaskList, resSubPath, 3);*/

        // 3.线程池
        for (int i = 0; i < 20; i++) {
            final int ii = i;
            FutureTask<SuitableTrips> futureTask = new FutureTask<SuitableTrips>(new Callable<SuitableTrips>() {
                @Override
                public SuitableTrips call() throws Exception {
                    return new TripHandler().getTrip(filePath + 3 + tail, minTripTime, depots.get(ii), wareHouse);
                }
            });
            threadPool.submit(futureTask);
            futureTaskList.add(futureTask);
        }

        write(futureTaskList, resSubPath, 3);
        threadPool.shutdown();

        System.out.println(read(resSubPath, 3));

        System.out.println("完成时间:" + (System.currentTimeMillis() - start) / 1000.0 / 60.0 + "分钟");
    }

    public void write(List<FutureTask<SuitableTrips>> futureTaskList, String subPath, int id) throws IOException, ExecutionException, InterruptedException {
        String resFilePath = subPath + id + ".txt";
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(resFilePath));

        for (int i = 0; i < futureTaskList.size(); i++) {
            SuitableTrips st = futureTaskList.get(i).get();
            if (st.getTripsE().size() == 0 && st.getTripsO().size() == 0)
                continue;
            out.writeObject(st);
        }
        out.close();
    }

    public List<SuitableTrips> read(String subPath, int id) throws IOException, ClassNotFoundException {
        String filePath = subPath + id + ".txt";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
        List<SuitableTrips> list = new ArrayList<SuitableTrips>();

        while (true) {
            try {
                SuitableTrips trips = (SuitableTrips) in.readObject();
                list.add(trips);
            } catch (EOFException e) {
                break;
            }
        }
        in.close();
        return list;
    }

    @Test
    public void testRead() throws IOException, ClassNotFoundException {
        System.out.println(read("D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\result", 3));
    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
//        final String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\result3.txt";
        final String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\test.txt";

        Bus bus = new Bus(1, 1.1F, 1.1F, 1, "12:12:12");
        Bus bus1 = new Bus(2, 1.1F, 1.1F, 1, "12:12:12");
        Bus bus2 = new Bus(3, 1.1F, 1.1F, 1, "12:12:12");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
        out.writeObject(bus);
        out.writeObject(bus1);
        out.writeObject(bus2);

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));

        while (true) {
            try {
                Bus readObject = (Bus) in.readObject();
                System.out.println(readObject);
            } catch (EOFException e) {
                break;
            }
        }
    }

    @Test
    public void test2() throws IOException {
        long startTime = System.currentTimeMillis();
        String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\3_sortAndFilter.txt";
        String testFilePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\3_testFile.txt";

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(testFilePath));

/*        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        LineIterator lineIterator = new LineIterator(reader);*/

        LineIterator lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] row = line.split(",");
            int id = Integer.parseInt(row[0]);
            Bus bus = new Bus(id, Float.parseFloat(row[1]), Float.parseFloat(row[2]), Integer.parseInt(row[3]), row[4]);
            out.writeObject(bus);
        }

        lineIterator.close();
        out.close();
        System.out.println("time:" + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void testStream() throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();
        String testFilePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\3_testFile.txt";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(testFilePath));

        while (true) {
            try {
                Bus bus = (Bus) in.readObject();
                System.out.println(bus);
            } catch (EOFException e) {
                break;
            }
        }
        in.close();
        System.out.println("time:" + (System.currentTimeMillis() - startTime));
    }
}
