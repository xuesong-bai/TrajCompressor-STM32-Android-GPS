package main.ylm.com.sdmacherosm.enity;

/**
 * Created by YLM on 2018/1/30.
 */

public class GpsPoint {
    public long taxi_id;

    public long timestamp;

    public double latitude;

    public double longitude;

    public double speed;

    public int headingDirection;

    public int cellId;

    public GpsPoint(float latitude,float longitude){
        this(0,0,latitude,longitude,0,0);
    }

    public GpsPoint(long taxi_id,long timestamp,double latitude,double longitude,double speed,int headingDirection){
        this.taxi_id = taxi_id;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.headingDirection = headingDirection;
    }
}

