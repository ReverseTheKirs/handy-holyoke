package com.handy_holyoke.handyholyoke;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import static com.handy_holyoke.handyholyoke.MarkerData.getMarkerType;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback  {
    private GoogleMap mMap;
    private static double laty;
    private static double longy;
    private FusedLocationProviderClient mFusedLocationClient;
    RequestQueue mRequestQueue;
    private ArrayList<MarkerData> markerList;

    static String[] marker_type_list = {"Ambulence",
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
    /*
    static LatLng[] position_list = { new LatLng(42.388417, -72.524316),
            new LatLng(42.253243, -72.576144),
            new LatLng(42.393679, -72.528658),
            new LatLng(42.388512, -72.532105),
            new LatLng(42.389803, -72.531468),
            new LatLng(42.393678, -72.528024),
            new LatLng(42.392990, -72.528484),
            new LatLng(42.395588, -72.531342),
            new LatLng(42.388836, -72.528139),
            new LatLng(42.389545, -72.526484),
            new LatLng(42.388572, -72.524764),
            new LatLng(42.394109, -72.520836)
    };
    */
    /*
    static int[] imageId_list = {
            R.drawable.ambulance,
            R.drawable.brokenpowerline,
            R.drawable.brokenwheelchairdoor,
            R.drawable.caraccident,
            R.drawable.carwalkwayblockage,
            R.drawable.constructionblockage,
            R.drawable.deadanimal,
            R.drawable.fallentree,
            R.drawable.firetruck,
            R.drawable.floodedarea,
            R.drawable.pathclosed,
            R.drawable.potholelargehole
    };
    */
    static LatLng construction = new LatLng(42.394109, -72.527355);
    static LatLng dubois = new LatLng(42.389735, -72.528279);
    static LatLng ilc  = new LatLng(42.391006, -72.526201);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO: Say hi to the server and populate the map with markers
        // Get the Intent that started this activity and extract the string
        final Button button = findViewById(R.id.ping_server);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final TextView mTextView = (TextView) findViewById(R.id.test);
                // Code here executes on main thread after user presses button
                // Instantiate the RequestQueue.
                mRequestQueue = Volley.newRequestQueue(MapsActivity.this);
                String url ="https://obscure-dawn-67405.herokuapp.com/get-marker";
                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    mTextView.setText("Response is: "+ response.get("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.
                mRequestQueue.add(jsonObjectRequest);
            }
        });
    }

    /** Called when the user taps the Send button */
    public void openBlockerMenu(View view) {
        Intent intent = new Intent(this, BlockerMenuActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);

        // Add some markers to the map, and add a data object to each marker.
        LatLng holyoke = new LatLng(42.255661, -72.574397);
        Marker mHolyoke = mMap.addMarker(new MarkerOptions()
                .position(holyoke)
                .title("Holyoke"));
        //MarkerData umassData = new MarkerData(0,0, umass.longitude, umass.latitude, 0);
        //mUmass.setTag(umassData);
        mMap.addMarker(new MarkerOptions().position(holyoke));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(holyoke));

        //Set current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        }
        //create an instance of the Fused Location Provider Client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //updates laty and longy
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            laty = location.getLatitude();
                            longy = location.getLongitude();

                        }
                    }
                });


        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        //todo: Load in all markers from server
       // final TextView onMapReadyText = (TextView) findViewById(R.id.onMapReadyText);
        String uniqueID = UUID.randomUUID().toString();
        //String jsonString = (new Gson()).toJson(newMarkerData);
        JSONObject uniqueIdJSON = null;
        try {
            uniqueIdJSON = new JSONObject(uniqueID);
        }
        catch(JSONException exception) {
            exception.printStackTrace();
        }
        String url ="https://obscure-dawn-67405.herokuapp.com/get-markers";
        // Request a string response from the provided URL.
        mRequestQueue = Volley.newRequestQueue(MapsActivity.this);
        JsonObjectRequest populateMapRequest = new JsonObjectRequest(Request.Method.GET, url, uniqueIdJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray tempList = new JSONArray();
                        try {
                            // onMapReadyText.setText(response.toString());
                            JSONObject t = response.getJSONObject("markers");
                            tempList = t.getJSONArray("rows");
                            //onMapReadyText.setText("meow mix");
                        } catch (JSONException e) {
                            //onMapReadyText.setText("dog food");
                            e.printStackTrace();
                        }
                        //iterate throgh all markers
                        for(int i = 0; i < tempList.length(); i++){

                            JSONObject d = null;
                            try {
                                d = tempList.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int markerTypeID = 0;
                            try {
                                markerTypeID = d.getInt("markerTypeID");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            double laty1 = 0;
                            try {
                                laty1 = d.getDouble("latitude");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            double longy1 = 0;
                            try {
                                longy1 = d.getDouble("longitude");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Marker newMarker = createNewMarker(markerTypeID, mMap, laty1, longy1);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                String body;
                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                        // onMapReadyText.setText("Response is: "+ body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                //do stuff with the body...
            }
        });
        mRequestQueue.add(populateMapRequest);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);


    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        //create an instance of the Fused Location Provider Client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //updates laty and longy
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                laty = location.getLatitude();
                                longy = location.getLongitude();

                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_maps_id);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.upvote_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This function executes when an intent is returned from the blocker menu
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int markerTypeID=data.getIntExtra("result", 0);

                Marker newMarker = createNewMarker(markerTypeID, mMap, laty, longy);
                MarkerData newMarkerData = new MarkerData(0,markerTypeID, laty, longy);

                String jsonString = (new Gson()).toJson(newMarkerData);
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(jsonString);
                }
                catch(JSONException exception) {
                    exception.printStackTrace();
                }

                // final TextView mTxtDisplay = (TextView) findViewById(R.id.mTxtDisplay);
                String url ="https://obscure-dawn-67405.herokuapp.com/post-marker";
                // Request a string response from the provided URL.
                mRequestQueue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                //mTxtDisplay.setText("Response is: "+ body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        //do stuff with the body...
                    }
                });
                System.out.println("------------------------------------");
                System.out.println(jsonObjectRequest);
                mRequestQueue.add(jsonObjectRequest);
                newMarker.setTag(newMarkerData);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }
    }//onActivityResult



    private static Marker createNewMarker(int markerType, GoogleMap mMap, double latty, double longgy){
        Marker newMarker;
        LatLng coordinates = new LatLng(latty, longgy);

        switch (markerType) {
            case 0:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance1)));
                    System.out.println(markerType);
                break;
            case 1:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.brokenpowerline1)));
                break;
            case 2:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.brokenwheelchairdoor1)));
                break;
            case 3:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.caraccident1)));
                break;
            case 4:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carwalkwayblockage1)));
                break;
            case 5:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.constructionblockage1)));
                break;
            case 6:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.deadanimal1)));
                break;
            case 7:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fallentree1)));
                break;
            case 8:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.firetruck1)));
                break;
            case 9:  newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.floodedarea1)));
                break;
            case 10: newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pathclosed1)));
                break;
            default: newMarker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(marker_type_list[markerType])
                    .snippet("Be Careful!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.potholelargehole1)));
                break;
        }
        return newMarker;


    }

}
