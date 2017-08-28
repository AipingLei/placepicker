package com.example.se7en.map.google.model;


public class AddressResult {

    public  String long_name;

    public  String short_name;

    public  String[] types;

    private transient int mType = -1;

    public static final int TYPE_COUNTRY = 1;

    public static final int TYPE_PROVINCE = 2;

    public static final int TYPE_CITY = 3;

    public static final int TYPE_SUBCITY= 4;

    public static final int TYPE_NOT_DEFINE = 0;


// "types" : [ "sublocality_level_1", "sublocality", "political" ]
//          "types" : [ "locality", "political" ]
//          "types" : [ "administrative_area_level_1", "political" ]
//          "types" : [ "country", "political" ]

    public int getType(){
        if (mType != -1) return mType;
        for (String type :types){
            if (("country").equals(type)){
                mType = TYPE_COUNTRY;
                return TYPE_COUNTRY;
            }else if(("administrative_area_level_1").equals(type)) {
                mType = TYPE_PROVINCE;
                return TYPE_PROVINCE;
            }else if(("locality").equals(type)) {
                mType = TYPE_CITY;
                return TYPE_CITY;
            }else if(("sublocality_level_1").equals(type)) {
                mType = TYPE_SUBCITY;
                return TYPE_SUBCITY;
            }
        }
        return TYPE_NOT_DEFINE;
    }

}
