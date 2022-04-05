package dataHandler;

import bean.Bus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class dataFilter {

    @Test
    public void filter() throws IOException {
        long startTime = System.currentTimeMillis();
        File file = new File("D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\15_sort.txt");
        LineIterator lineIterator;
        List<Bus> path = new ArrayList<Bus>();
        Bus bus, preBus = new Bus();
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
                write(path);
                path.clear();
                idIndex = id;
            }

            bus = new Bus(id, Float.parseFloat(row[1]), Float.parseFloat(row[2]), Integer.parseInt(row[3]), row[4]);
            if (path.size() == 0)
                path.add(bus);
            else {
                preBus = path.get(path.size() - 1);
                if (preBus.getState() != bus.getState() || (!bus.getLatitude().equals(preBus.getLatitude()) && !bus.getLongitude().equals(preBus.getLongitude()))) {
                    path.add(bus);
                }
            }
        }
        write(path);

        long endTime = System.currentTimeMillis();
        System.out.println("处理时间：" + (endTime - startTime));
    }

    public void write(List<Bus> list) throws IOException {
        System.out.println(list.size());
        File newFile = new File("D:\\BaiduNetdiskDownload\\taxi_data\\cd_taxi_data\\15_sortAndFilter.txt");
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
