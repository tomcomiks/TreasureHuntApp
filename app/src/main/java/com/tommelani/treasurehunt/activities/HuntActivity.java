package com.tommelani.treasurehunt.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import android.widget.FrameLayout;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.dao.DatabaseAccess;
import com.tommelani.treasurehunt.helpers.PermissionHelper;
import com.tommelani.treasurehunt.helpers.Serializer;
import com.tommelani.treasurehunt.models.Treasure;
import com.tommelani.treasurehunt.components.CameraPreview;
import com.tommelani.treasurehunt.components.OverlayView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The database of objects to search is managed locally on the phone.
 * The application uses the sensors (accelerometer, magnetometer) to know the orientation of the
 * phone and also identifies actions on the touchscreen to retrieve found items.
 * Therefore, the activity implements the SensorEventListener and LocationListener interfaces.
 */
public class HuntActivity extends Activity implements SensorEventListener, LocationListener {

    //public final static String TAG = "HuntActivity Log";

    public final static String SESSION_MILESTONE_ID = MainActivity.SESSION_MILESTONE_ID;
    public final static String SESSION_FOUND_TREASURES = MainActivity.SESSION_FOUND_TREASURES;
    public final static String SESSION = MainActivity.SESSION;
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.

    private static SharedPreferences preferences;

    Set<Long> foundTreasureList = new HashSet<>();
    long milestoneId;
    FrameLayout fl_view;
    float[] orientation = new float[3];

    Handler handler = new Handler();

    private DatabaseAccess database;
    private Camera mCamera;
    private CameraPreview mPreview;
    private LocationManager locationManager = null;
    private Location userLocation;
    private float[] lastAccelerometer;
    private float[] lastCompass;
    private SensorManager mSensorManager;
    private Sensor accelerometerSensor;
    private Sensor compassSensor;
    private List<OverlayView> overlayViewList = new ArrayList<>();

    /**
     * Load the activity_hunt layout
     * Initialize the SharedPreferences
     * Open the database
     * Initialize the geolocation
     * Initialize the sensors
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);

        //Get Preferences
        preferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        milestoneId = preferences.getLong(SESSION_MILESTONE_ID, 1);

        //Open Database
        database = DatabaseAccess.getInstance(this);

        //Location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Start Sensor Manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //View
        fl_view = findViewById(R.id.camera_view);

    }

    /**
     * Close the database
     * Stop the sensors
     * Stop geolocation
     * Stop the camera
     */
    @Override
    protected void onPause() {
        super.onPause();
        database.close();
        mSensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        releaseCamera();
    }

    /**
     * Open the database
     * Load the milestone treasure list
     * Start geolocation
     * Start the sensors
     * Get the list of treasures found
     * Start the camera
     * Stack the overlay views for every treasures
     */
    @Override
    protected void onResume() {
        super.onResume();

        database.open();
        List<Treasure> treasureList = database.getTreasuresFromMilestone(milestoneId);

        //Start Location Manager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionHelper.requestPermissions(this);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

        //Sensors
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Get updated foundTreasureList
        String jsonTreasure = preferences.getString(SESSION_FOUND_TREASURES, "");
        foundTreasureList = Serializer.deserializeToSetLong(jsonTreasure);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);

        overlayViewList.clear();
        for (Treasure treasure : treasureList) {
            if (!foundTreasureList.contains(treasure.getId())) {
                OverlayView content = new OverlayView(this, mCamera, treasure);
                overlayViewList.add(content);
            }
        }

        //Recreate the views
        fl_view.removeAllViews();
        fl_view.addView(mPreview);
        for (OverlayView view : overlayViewList) {
            fl_view.addView(view);
        }

    }

    /**
     * Updates the user's current position via a separated Thread
     * @param location location
     */
    @Override
    public void onLocationChanged(Location location) {
        userLocation = new Location("User");
        userLocation.setLatitude(location.getLatitude());
        userLocation.setLongitude(location.getLongitude());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (OverlayView view : overlayViewList) {
                    view.updateLocationData(userLocation);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(r);
    }

    /*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    */

    /**
     * Updates sensor data after applying a low pass filter
     * and sends them to the corresponding views via a separated Thread.
     * @param event sensor event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        //Log.d(DEBUG_TAG, "onSensorChanged");
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                lastAccelerometer = applyLowPassFilter(event.values.clone(), lastAccelerometer);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastCompass = applyLowPassFilter(event.values.clone(), lastCompass);
                break;
        }

            // compute rotation matrix
            float[] rotation = new float[9];
            float[] identity = new float[9];

            if (lastAccelerometer != null && lastCompass != null) {
                boolean gotRotation = SensorManager.getRotationMatrix(rotation,
                        identity, lastAccelerometer, lastCompass);

                if (gotRotation) {
                    float[] cameraRotation = new float[9];
                    // remap such that the camera is pointing straight down the Y axis
                    SensorManager.remapCoordinateSystem(rotation,
                            SensorManager.AXIS_X, SensorManager.AXIS_Z,
                            cameraRotation);

                    // orientation vector
                    SensorManager.getOrientation(cameraRotation, orientation);
                }
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for (OverlayView view : overlayViewList) {
                        view.updateOrientationData(orientation);
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(r);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Stop the camera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Start the camera
     * @return the camera
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Add the treasure found in the list of treasures found in the SharedPreferences
     * @param treasureId id of the treasure
     */
    public static void registerTreasure(long treasureId) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = preferences.getString(SESSION_FOUND_TREASURES, "");
        editor.putString(SESSION_FOUND_TREASURES, Serializer.addLongToList(json, treasureId));
        editor.apply();
    }

    /**
     * Apply a low pass filter to attenuate the high frequencies given by the sensors
     * @param input input
     * @param output output
     * @return filtered output
     */
    float[] applyLowPassFilter(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


}
