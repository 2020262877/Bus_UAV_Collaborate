package bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuitableTrips implements Serializable {

    //往该 depot 进行配送
    private Depot depot;

    //载客的 trip -- onloadTrips
    private List<Trip> tripsO;

    //空载的 trip -- emptyTrips
    private List<Trip> tripsE;

    public void addTripsO(Trip trip) {
        tripsO.add(trip);
    }

    public void addTripsE(Trip trip) {
        tripsE.add(trip);
    }
}
/**
 * 读取完之后写到文本文件里面,下次启动时从文本文件里面读取,格式:
 * depot:
 * id,latitude,longitude,radius
 * tripsO:
 * Id,latitude,longitude,state,time,length
 * Id,latitude,longitude,state,time,length
 * ...
 * tripsE:
 * Id,latitude,longitude,state,time,length
 * Id,latitude,longitude,state,time,length
 * ...
 */