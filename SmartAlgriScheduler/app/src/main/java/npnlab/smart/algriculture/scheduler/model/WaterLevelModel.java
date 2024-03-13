package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WaterLevelModel {
    @SerializedName("id")
    private int id;
    @SerializedName("sensors")
    private List<Sensor> sensors;
}
