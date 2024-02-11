package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

public class PumpModel {
    @SerializedName("sensor_id")
    private String sensor_id;
    @SerializedName("sensor_name")
    private String sensor_name;
    @SerializedName("sensor_value")
    private int sensor_value; //0 or 1
    @SerializedName("sensor_unit")
    private String sensor_unit;
    public PumpModel(String _id, String _name, int _value, String _unit){
        setSensorID(_id);
        setSensorName(_name);
        setSensorValue(_value);
        setSensorUnit(_unit);
    }

    public String getSensorID() {
        return sensor_id;
    }

    public void setSensorID(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getSensorName() {
        return sensor_name;
    }

    public void setSensorName(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public int getSensorValue() {
        return sensor_value;
    }

    public void setSensorValue(int sensor_value) {
        this.sensor_value = sensor_value;
    }

    public String getSensorUnit() {
        return sensor_unit;
    }

    public void setSensorUnit(String sensor_unit) {
        this.sensor_unit = sensor_unit;
    }
}
