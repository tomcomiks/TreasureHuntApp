package com.tommelani.treasurehunt.components;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This view defines the display of the camera view in the HuntActivity activity. It extends SurfaceView and implements SurfaceHolder.Callback.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "CameraPreview";

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Context mContext;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mContext = context;
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    /**
     * Create the screen view
     * @param holder holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Set Display orientation
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
        mCamera.setDisplayOrientation((info.orientation - 0 + 360) % 360);

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    /**
     * Updates the screen size of the camera based on the size of the area
     * @param holder holder
     * @param format format
     * @param width width
     * @param height height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.d(TAG, "surfaceChanged");

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            return;
        }

        try {
            Camera.Parameters params = mCamera.getParameters();
            // Find an appropriate preview size that fits the surface
            List<Size> prevSizes = params.getSupportedPreviewSizes();
            for (Size s : prevSizes) {
                if ((s.height <= height) && (s.width <= width)) {
                    params.setPreviewSize(s.width, s.height);
                    break;
                }

            }
            mCamera.setParameters(params);
            mCamera.startPreview();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview : " + e.getMessage());
        }

    }

    /**
     * Stop the view screen
     * @param holder holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.d(TAG, "surfaceDestroyed");
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

}
