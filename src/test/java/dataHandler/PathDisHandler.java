package dataHandler;

import Utils.DistanceHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PathDisHandler {

    @Test
    public void test() throws IOException {
        int curId = -1;
        double dis = 0;
        float preLat = 0;
        float preLon = 0;

        String filePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\3_sort.txt";
        String disFilePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\day3_distance.txt";
        LineIterator lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        FileWriter fileWriter = new FileWriter(new File(disFilePath));

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] row = line.split(",");
            int id = Integer.parseInt(row[0]);
            float lat = Float.parseFloat(row[1]);
            float lon = Float.parseFloat(row[2]);

            if (id == curId) {
                dis += DistanceHandler.getDistance(lat, lon, preLat, preLon);
            } else {
                dis = Math.floor(dis * 100) / 100.0;
                System.out.println(curId + ":" + dis);
                fileWriter.write(id + ":" + dis + "\r\n");
                dis = 0;
                curId = id;
            }
            preLat = lat;
            preLon = lon;
        }

        lineIterator.close();
        fileWriter.close();
    }

    @Test
    public void avgTest() throws IOException {
        String disFilePath = "D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\day3_distance.txt";
        LineIterator lineIterator = FileUtils.lineIterator(new File(disFilePath), "UTF-8");
        double avg1 = 0, avg2 = 0;
        double avg = 0;

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] row = line.split(":");
            int id = Integer.parseInt(row[0]);
            float dis = (float) (Float.parseFloat(row[1]) / 10000.0);

            avg += dis;
            if (id <= 6802) {
                avg1 += dis;
            } else avg2 += dis;

        }
        System.out.println(avg1 / 6802.0 * 10000);
        System.out.println(avg2 / 6802.0 * 10000);
        System.out.println((avg1 + avg2) / 13604.0 * 10000.0);
        System.out.println(avg / 13604.0);
    }

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
    }
}
