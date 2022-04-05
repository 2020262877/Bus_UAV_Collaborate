package dataHandler;

import bean.Depot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public class DepotHandler {

    private List<Depot> depots;

    public DepotHandler() {
        depots = new ArrayList<Depot>();
    }

    //在目标区域随机生成 1000 个 depot
    public void createDepots(float LEFT, float UP, float RIGHT, float DOWN, float RADIUS) {
        for (int i = 0; i < 1000; i++) {
            Random rand = new Random();
            float ranLon = LEFT + (RIGHT - LEFT) * rand.nextFloat();
            float ranLat = DOWN + (UP - DOWN) * rand.nextFloat();
            depots.add(new Depot(i, ranLat, ranLon, RADIUS));
        }
    }
}
