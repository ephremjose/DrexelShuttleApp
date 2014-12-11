package com.drexel.rephrem.drexelshuttleapp.vo;

/**
 * Created by REPHREM on 11/26/2014.
 */
public class StopTimeVO {

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public String getFromDrexelTime() {
        return fromDrexelTime;
    }

    public void setFromDrexelTime(String fromDrexelTime) {
        this.fromDrexelTime = fromDrexelTime;
    }

    public boolean isHurryFlag() {
        return hurryFlag;
    }

    public void setHurryFlag(boolean hurryFlag) {
        this.hurryFlag = hurryFlag;
    }

    public boolean isMissedFlag() {
        return missedFlag;
    }

    public void setMissedFlag(boolean missedFlag) {
        this.missedFlag = missedFlag;
    }



    int _id;
    String stopID;
    String fromDrexelTime;


    boolean hurryFlag;
    boolean missedFlag;


}
