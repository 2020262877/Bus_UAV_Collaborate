package Utils;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.junit.Test;

public class DistanceHandler {

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        GeodesicData g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
        return g.s12;
    }

    @Test
    public void test(){
        System.out.println(getDistance(30.935507,103.974724,30.789,104.11321));
    }
}
