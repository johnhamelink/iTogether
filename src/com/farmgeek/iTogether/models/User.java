package com.farmgeek.iTogether.models;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;


/**
 * Created by john on 25/04/2014.
 */
public class User {

    // Y positions are relative to height of bg_distance image.
    private static final double RELATIVE_START_POS = 320.0 / 1110.0;
    private static final double RELATIVE_STOP_POS  = 885.0 / 1110.0;

    private Beacon _beacon = null;
    private Region _region = null;
    private String _name = "";
    private int _major = 0;
    private int _minor = 0;
    private String _uuid = "";
    private double _distance = 0.00;

    public User(Beacon beacon, Region region) {
        this._beacon = beacon;
        this._name   = beacon.getName();
        this._uuid   = "" + this._major + ":" + this._minor;
    }

    /**
     * setName
     * @param name
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * setRegion
     * @param region
     */
    public void setRegion(Region region) {
        this._region = region;
    }

    /**
     * setUUID
     * @param String
     */
    public void setRegion(String uuid) {
        this._uuid = uuid;
    }

    public String get_uuid() {
        return this._uuid;
    }

    /**
     * getDistance
     *
     * @return double The distance
     */
    public double get_distance() {
        // Let's put dot at the end of the scale when it's further than 6m.
        this._distance = Math.min(Utils.computeAccuracy(this._beacon), 6.0);
        return this._distance;
    }

}
