package com.tommelani.treasurehunt.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.dao.DatabaseAccess;
import com.tommelani.treasurehunt.helpers.PermissionHelper;
import com.tommelani.treasurehunt.helpers.Serializer;
import com.tommelani.treasurehunt.models.Milestone;
import com.tommelani.treasurehunt.models.Track;
import com.tommelani.treasurehunt.models.Treasure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This activity implements the interfaces OnMapReadyCallback, GoogleMap.OnMarkerClickListener and LocationListener.
 * The track database is managed locally on the phone.
 */
public class TrackActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //public static final String TAG = "TrackActivity Log";

    public final static String SESSION_MILESTONE_ID = MainActivity.SESSION_MILESTONE_ID;
    public final static String SESSION_PASSED_MILESTONES = MainActivity.SESSION_PASSED_MILESTONES;
    public final static String SESSION_FOUND_TREASURES = MainActivity.SESSION_FOUND_TREASURES;
    public final static String SESSION = MainActivity.SESSION;

    Set<Long> foundTreasureList = new HashSet<>();
    LatLng initialPosition;
    LatLng lastClickablePosition;
    LatLng lastPosition;
    LocationManager locationManager;
    private GoogleMap mMap;
    private Set<Marker> clickableMarkers = new HashSet<>();
    private Map<Marker, Milestone> allMarkers = new HashMap<>();
    private DatabaseAccess database;
    private Set<Long> passedMilestoneList;
    private long milestoneId;
    private SharedPreferences preferences;

    /**
     * Load the activity_track layout;
     * Initialize the SharedPreferences;
     * Initialize the database ;
     * Initialize the Google map;
     * Initialize the geolocation.
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        //Get Preferences
        preferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);

        //Open Database
        database = DatabaseAccess.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Open the database
     * Load the list of past Milestones
     * Load the list of treasures found
     * Put the markers on the Google map.
     */
    @Override
    protected void onResume() {
        //Log.d(DEBUG_TAG,"OnResume");
        super.onResume();

        //Open Database
        database.open();

        //Get Preferences
        String json = preferences.getString(SESSION_PASSED_MILESTONES, "");
        passedMilestoneList = Serializer.deserializeToSetLong(json);

        String json2 = preferences.getString(SESSION_FOUND_TREASURES, "");
        foundTreasureList = Serializer.deserializeToSetLong(json2);

        if (mMap != null) {
            placeMarkers();
        }


    }

    /**
     * Close the database
     */
    @Override
    protected void onPause() {
        //Log.d(DEBUG_TAG,"OnPause");
        super.onPause();

        database.close();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap the google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestPermissions(this);
        }
        mMap.setMyLocationEnabled(true);

        placeMarkers();
    }

    /**
     * All markers on the map are reset.
     * Then for each Track recorded, the app puts the Milestones on the Google map and links them with a line.
     * Past milestones are identified with a different icon.
     */
    public void placeMarkers() {
        //Log.d(DEBUG_TAG, "Refresh map");

        mMap.clear();

        initialPosition = null;

        List<Track> trackList = database.getAllTracks();
        for (Track track : trackList) {
            PolylineOptions defaultLine = new PolylineOptions().width(5).color(Color.GRAY).geodesic(true);
            List<Milestone> milestoneList = database.getMilestonesFromTrack(track.getId());

            for (Milestone milestone : milestoneList) {
                //Default markers
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(milestone.getPosition())
                        .title(milestone.getTitle());
                defaultLine.add(milestone.getPosition());
                Marker marker = mMap.addMarker(markerOptions);
                allMarkers.put(marker, milestone);

                //If the milestone is not passed
                if (!passedMilestoneList.contains(milestone.getId())) {
                    if (lastClickablePosition == null) {
                        lastClickablePosition = milestone.getPosition();
                    }
                    clickableMarkers.add(marker);
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.click));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.museum));
                }
                lastPosition = milestone.getPosition();
            }
            mMap.addPolyline(defaultLine);

        }

        initialPosition = lastClickablePosition;
        if (initialPosition == null) {
            initialPosition = lastPosition;
        }
        //Log.d(DEBUG_TAG, initialPosition.toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15.0f));

    }

    /**
     *  When you click on a marker on the Google map, information is displayed in the box at the bottom of the map.
     * If it's a past Milestone, it displays the Treasures found;
     * otherwise, it displays a button to launch the activity QuestionActivity.
     * @param marker the map marker
     * @return boolean
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        Milestone milestone = allMarkers.get(marker);
        if (milestone == null) {
            return false;
        }

        milestoneId = milestone.getId();
        LinearLayout infoLayout = findViewById(R.id.ll_more_info);
        infoLayout.removeAllViews();

        if (clickableMarkers.contains(marker)) {
            View info = getLayoutInflater().inflate(R.layout.activity_track_panel_clickable_marker, infoLayout, false);
            infoLayout.addView(info);

            TextView tv_title = findViewById(R.id.tv_title);
            TextView tv_description = findViewById(R.id.tv_description);

            tv_title.setText(milestone.getTitle());
            tv_description.setText(milestone.getDescription());

        } else {

            View info = getLayoutInflater().inflate(R.layout.activity_track_panel_treasure_list, infoLayout, false);
            infoLayout.addView(info);

            List<Treasure> treasureList = database.getTreasuresFromMilestone(milestoneId);

            GridLayout gl_treasure_list = findViewById(R.id.gv_treasure_list);

            boolean areTreasures = false;
            for (Treasure treasure : treasureList) {
                if (foundTreasureList.contains(treasure.getId())) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), treasure.getImageId());
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(resizedBitmap);
                    gl_treasure_list.addView(iv);
                    areTreasures = true;
                }
            }

            if (!areTreasures) {
                TextView tv = new TextView(this);
                tv.setText(getResources().getText(R.string.no_treasures_found));
                infoLayout.addView(tv);
            }


        }
        return false;
    }

    /**
     * When you click on the button to go to a question, start the activity QuestionActivity.
     * @param v the view
     */
    public void onClick(View v) {

        if (v.getId() == R.id.btn_to_question) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(SESSION_MILESTONE_ID, milestoneId);
            editor.apply();

            Intent i = new Intent(this, QuestionActivity.class);
            startActivity(i);
        }

    }
}
