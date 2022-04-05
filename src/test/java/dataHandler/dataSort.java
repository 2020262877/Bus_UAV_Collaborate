package dataHandler;

import bean.Bus;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import Utils.TimeHandler;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class dataSort {

    @Test
    public void dataManage() throws Exception {

        long startTime = System.currentTimeMillis();
        File file = new File("D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\20140830_train.txt");
        LineIterator lineIterator;
        List<Bus> list = new ArrayList<Bus>();
        int idIndex = 1;

        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            String[] row = line.split(",");
            int id = Integer.parseInt(row[0]);
            if (id != idIndex) {
                sortByTime(list);
                write(list);
                list.clear();
                idIndex = id;
            }
            list.add(new Bus(id, Float.parseFloat(row[1]), Float.parseFloat(row[2]), Integer.parseInt(row[3]), row[4].split(" ")[1]));
        }
        sortByTime(list);
        write(list);

        long endTime = System.currentTimeMillis();
        System.out.println("处理时间：" + (endTime - startTime));
    }

    public void sortByTime(List<Bus> list) {
        Collections.sort(list, new Comparator<Bus>() {
            @SneakyThrows
            public int compare(Bus o1, Bus o2) {
                return TimeHandler.dateToStamp(o1.getTime()) - TimeHandler.dateToStamp(o2.getTime());
            }
        });
    }

    public void write(List<Bus> list) throws IOException {
        File newFile = new File("D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\30_sort.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
        for (int i = 0; i < list.size(); i++) {
            Bus bus = list.get(i);
            String line = bus.getId() + "," + bus.getLatitude() + "," + bus.getLongitude() + "," + bus.getState() + "," + bus.getTime();
            bw.write(line);
            bw.newLine();
        }
        bw.close();
    }
}
