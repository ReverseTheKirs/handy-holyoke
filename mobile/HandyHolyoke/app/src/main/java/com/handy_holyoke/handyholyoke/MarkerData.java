package com.handy_holyoke.handyholyoke;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Kirs on 11/4/2017.
 */

public class MarkerData {

    int id, markerType,  upvotes, downvotes;
    double longitude, latitude;

    public MarkerData(int id, int type, double latitude, double longitude){
        this.id= id;
        this.markerType = type;
        this.longitude = longitude;
        this.latitude = latitude;
        this.upvotes = 0;
        this.downvotes = 0;
    }

    public static String getMarkerType(int markerTypeId){

        String[] marker_type_list = {"Ambulence",
                "Down Power Line",
                "Broken Power Door",
                "Car Accident",
                "Car Blockage",
                "Construction",
                "Dead Animal",
                "Fallen Branches",
                "Firetruck",
                "Flooded Area",
                "Closed Path",
                "PotHole"};
        String return_str = marker_type_list[markerTypeId];

        return(return_str);
    }

    public static void addMarkerToMap(GoogleMap map, MarkerData markerD){
        LatLng coordinates = new LatLng(markerD.getLongitude(),markerD.getLongitude());
        Marker newMarker = map.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(getMarkerType(markerD.getMarkerTypeId())));
        newMarker.setTag(markerD);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarkerTypeId() {
        return markerType;
    }

    public void setMarkerTypeId(int markerTypeId) {
        this.markerType = markerTypeId;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
