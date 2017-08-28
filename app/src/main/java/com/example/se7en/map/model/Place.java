package com.example.se7en.map.model;


import java.io.Serializable;

public class Place implements Serializable{

    public String placeID;

    public String name;

    public String address;

    public double latitude;

    public String country;

    public String countryCode;

    public double longitude;

    public String city;

    public transient String province;


}
