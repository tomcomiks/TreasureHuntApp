package com.tommelani.treasurehunt.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.activities.HuntActivity;
import com.tommelani.treasurehunt.models.Treasure;

/**
 * This view defines the display of treasures that will be superimposed on the CameraView in the activity HuntActivity.
 * Each treasure has its own OverlayView which disappears when clicked.
 * It extends the View class.
 */
public class OverlayView extends View {

    //public final static String TAG = "OverlayView Log";

    private Treasure treasure;
    private float verticalFOV;
    private float horizontalFOV;
    private Paint targetPaint;
    private Bitmap mTreasure;
    private Bitmap scaledBitmap;
    private RectF rectangle;
    private Context mContext;

    public float[] orientation;
    private float bearing;

    private Matrix rotator;
    private Matrix matrix;


    public OverlayView(Context context, Camera camera, Treasure treasure) {
        super(context);

        this.mContext =  context;

        Camera.Parameters params = camera.getParameters();
        verticalFOV = params.getVerticalViewAngle();
        horizontalFOV = params.getHorizontalViewAngle();
        this.treasure = treasure;

        //Initialize matrix
        rotator = new Matrix();
        matrix = new Matrix();

        //Get bitmaps
        mTreasure = BitmapFactory.decodeResource(getResources(), treasure.getImageId());
        scaledBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

        targetPaint = new Paint();
    }

    /**
     * Draw the treasure and the placeholder based on the data orientation
     * @param canvas screen
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(DEBUG_TAG,"OnDraw");
        super.onDraw(canvas);

        if(orientation != null) {

            // Get translation and rotation from orientation data
            float translateX = (float) (0.0f - (getWidth() / horizontalFOV) * (Math.toDegrees(orientation[0]) - bearing));
            float translateY = (float) (0.0f - (getHeight() / verticalFOV) * Math.toDegrees(orientation[1]));
            float rotationXY = (float) (0.0f - Math.toDegrees(orientation[2]));

            //Apply translation and rotation to object
            rotator.reset();
            rotator.postRotate(rotationXY,  (float)getWidth() / 2, (float)getHeight() / 2);
            rotator.postTranslate(translateX, translateY);

            //Get Bitmap for Treasure and corresponding rectangle
            rectangle = new RectF(0, 0, mTreasure.getWidth(), mTreasure.getHeight());
            rotator.mapRect(rectangle);

            RectF screen = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mTreasure, rotator, targetPaint);

            if (!rectangle.intersect(screen)) {
                matrix.reset();
                double rotation2 = Math.toDegrees(Math.atan2(rectangle.centerX() - (float)getWidth() / 2, rectangle.centerY() - (float)getHeight() / 2));
                matrix.postRotate((float) rotation2 * -1);
                matrix.postRotate(180);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                PointF indicator = getIndicatorPosition(rectangle, screen);
                canvas.drawBitmap(rotatedBitmap, indicator.x - (float)rotatedBitmap.getWidth() / 2, indicator.y - (float)rotatedBitmap.getHeight() / 2, targetPaint);
            }

        }
    }

    /**
     * If the touched point contains the treasure, its view is removed and the treasure is added
     * in the list of treasures found by the user in the SharedPreferences.
     * @param event motion event
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            if (rectangle.contains(x, y)) {

                String text = getResources().getText(R.string.well_done) + " " + getResources().getText(R.string.you_found) + " " + treasure.getTitle().toLowerCase() + ".";

                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();

                HuntActivity.registerTreasure(treasure.getId());
                this.setVisibility(View.GONE);
                this.invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * Updates view with data orientation obtained from the sensors.
     * @param orientation orientation
     */
    public void updateOrientationData(final float[] orientation) {

        this.orientation = orientation;
        invalidate();

    }

    /**
     * Update the view with the data orientation obtained from geolocation.
     * @param userLocation location
     */
    public void updateLocationData(Location userLocation) {

        Location treasureLocation = new Location(treasure.getTitle());
        treasureLocation.setLatitude(treasure.getPosition().latitude);
        treasureLocation.setLongitude(treasure.getPosition().longitude);
        this.bearing = userLocation.bearingTo(treasureLocation);
        invalidate();

     }

    /**
     * Calculate the position indicator position pointing to a target outside of the screen.
     * @param target target
     * @param screen screen
     * @return point
     */
    private PointF getIndicatorPosition(RectF target, RectF screen) {

        //determine padded screen
        int padding = 50;
        float paddedHeight = screen.height() - padding;
        float paddedWidth = screen.width() - padding;

        //shift coordinate space
        float x = (target.centerX() - screen.width() / 2);
        float y = (target.centerY() - screen.height() / 2);

        //calculate slope
        float slope = y / x;

        if (y < -paddedHeight / 2) { //top of screen
            x = -1 * paddedHeight / 2 / slope;
            y = -1 * paddedHeight / 2;
        } else if (y > paddedHeight / 2) { //bottom of screen
            x = paddedHeight / 2 / slope;
            y = paddedHeight / 2;
        }

        if (x < -paddedWidth / 2) { //left of screen
            x = -1 * paddedWidth / 2;
            y = -1 * paddedWidth / 2 * slope;
        } else if (x > paddedWidth / 2) { //right of screen
            x = paddedWidth / 2;
            y = paddedWidth / 2 * slope;
        }

        //change coordinate position back to original space
        y = (y + screen.height() / 2);
        x = (x + screen.width() / 2);

        return new PointF(x, y);
    }

}
