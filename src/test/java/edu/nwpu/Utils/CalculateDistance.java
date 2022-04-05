package edu.nwpu.Utils;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.junit.Test;

/**
 * 计算两个经纬度之间的距离
 */
public class CalculateDistance {

    private static final double EARTH_RADIUS = 6378.137;//地球半径,单位千米

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * @param lat1 第一个纬度
     * @param lng1 第一个经度
     * @param lat2 第二个纬度
     * @param lng2 第二个经度
     * @return 两个经纬度的距离
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 100000) / 100000.0;
        return s;

    }

    public static double getPrecisionDistance(double lat1, double lon1, double lat2, double lon2) {
        GeodesicData g = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
        return g.s12;
    }

    @Test
    public void testCalculateDistance() {
        System.out.println(Integer.MAX_VALUE);
        System.out.println(CalculateDistance.getDistance(22.7095, 114.093, 22.718, 114.113));
        System.out.println(CalculateDistance.getPrecisionDistance(22.7095, 114.093, 22.718, 114.113));
    }
}
