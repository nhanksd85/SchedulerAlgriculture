package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PumpStationModel {
    @SerializedName("station_id")
    private String station_id = "";

    @SerializedName("station_name")
    private String station_name = "";

    @SerializedName("gps_longitude")
    private double gps_longitude = 106.89;

    @SerializedName("gps_latitude")
    private double gps_latitude = 10.52;

    @SerializedName("sensors")
    private List<PumpModel> sensors = new ArrayList<>();




    public PumpStationModel(int type){
        if(type == 0) {
            station_id = "pump_station_0001";
            station_name = "Trạm tưới tiêu";
            PumpModel pump1 = new PumpModel("pump_0001", "Bơm 1", 0, "");
            PumpModel pump2 = new PumpModel("pump_0002", "Bơm 2", 0, "");
            PumpModel pump3 = new PumpModel("pump_0003", "Bơm 3", 0, "");
            PumpModel pump4 = new PumpModel("pump_0004", "Bơm 4", 0, "");
            PumpModel pump5 = new PumpModel("pump_0005", "Bơm 5", 0, "");
            getSensors().add(pump1);
            getSensors().add(pump2);
            getSensors().add(pump3);
            getSensors().add(pump4);
            getSensors().add(pump5);
        }else if(type == 1){
            station_id = "water_level_station_0001";
            station_name = "Trạm mực nước";
            PumpModel pump1 = new PumpModel("water_level_0001", "Mực nước 1", 10, "%");
            PumpModel pump2 = new PumpModel("water_level_0002", "Mực nước 2", 20, "%");
            PumpModel pump3 = new PumpModel("water_level_0003", "Mực nước 3", 30, "%");
            PumpModel pump4 = new PumpModel("water_level_0004", "Mực nước 4", 40, "%");
            getSensors().add(pump1);
            getSensors().add(pump2);
            getSensors().add(pump3);
            getSensors().add(pump4);
        }

    }

    public List<PumpModel> getSensors() {
        return sensors;
    }

    public void setSensors(List<PumpModel> sensors) {
        this.sensors = sensors;
    }
}
