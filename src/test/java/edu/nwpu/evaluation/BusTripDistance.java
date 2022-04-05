package edu.nwpu.evaluation;

import edu.nwpu.Utils.CalculateDistance;
import edu.nwpu.Utils.TimeChange;
import edu.nwpu.bean.DistanceAndTime;
import edu.nwpu.bean.Path;
import edu.nwpu.domain.Bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusTripDistance implements Serializable {

    private List<Path> paths;
    private List<Bus> buses;
    private List<DistanceAndTime> distances = new ArrayList<DistanceAndTime>();

    public BusTripDistance(List<Path> paths, List<Bus> buses) {
        this.paths = paths;
        this.buses = buses;
    }

    public void main() throws Exception {
        for (int i = 0; i < paths.size(); i++) {
            double distance = 0;
            int start = buses.indexOf(paths.get(i).getStartPoint());
            int end = buses.indexOf(paths.get(i).getEndPoint());
            int time = TimeChange.dateToStamp(buses.get(end).getDate()) - TimeChange.dateToStamp(buses.get(start).getDate());
            if (start != end) {
                distance = getPathDistance(start, end);
                distance = Math.round(distance * 100000) / 100000.0;
            }
            distances.add(new DistanceAndTime(paths.get(i).getStartPoint(), paths.get(i).getEndPoint(), distance, time));
        }
    }

    public double getPathDistance(int start, int end) {
        double distance = 0;
        for (int i = start + 1; i <= end; i++) {
            distance += CalculateDistance.getPrecisionDistance(buses.get(i).getLatitude(), buses.get(i).getLongitude(),
                    buses.get(i - 1).getLatitude(), buses.get(i - 1).getLongitude());
        }
        return distance;
    }

    public List<DistanceAndTime> getDistances() {
        return distances;
    }
}
