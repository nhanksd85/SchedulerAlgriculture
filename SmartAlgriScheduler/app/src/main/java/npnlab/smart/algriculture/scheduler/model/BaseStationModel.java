package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

public class BaseStationModel {
    @SerializedName("station_id")
    private String station_id = "";

    @SerializedName("station_name")
    private String station_name = "";

    @SerializedName("gps_longitude")
    private double gps_longitude = 106.89;

    @SerializedName("gps_latitude")
    private double gps_latitude = 10.52;
}
