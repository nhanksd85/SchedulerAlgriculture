package npnlab.smart.algriculture.scheduler.model;

import java.sql.Time;

public class SchedulerModel {

    private String schedulerName;
    private String startTime ="7:15";
    private String stopTime = "7:45";

    private int flow1 = 20;
    private int flow2 = 20;
    private int flow3 = 20;
    private int cycle = 15;

    private boolean isActive = false;

    public SchedulerModel(String strName){
        setSchedulerName(strName);
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public int getFlow1() {
        return flow1;
    }

    public void setFlow1(int flow1) {
        this.flow1 = flow1;
    }

    public int getFlow2() {
        return flow2;
    }

    public void setFlow2(int flow2) {
        this.flow2 = flow2;
    }

    public int getFlow3() {
        return flow3;
    }

    public void setFlow3(int flow3) {
        this.flow3 = flow3;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
