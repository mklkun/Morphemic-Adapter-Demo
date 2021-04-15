package morphemic.adapter;

import org.activeeon.morphemic.service.GeoLocationUtils;

public class Draft {
    public static void main(String[] args) {
        System.out.println("Begin");

        //System.out.println("DB: " + GeoLocationUtils.chargeCloudGLsDBSTATIC().toString());

        GeoLocationUtils geoLocationUtils = new GeoLocationUtils();

        System.out.println("geoLocationUtils: " + geoLocationUtils.toString());

        try {
            System.out.println("Waiting ...");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("END");

        System.exit(0);
    }
}
