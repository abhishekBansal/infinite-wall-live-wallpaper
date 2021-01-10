package com.rrapps.heavensdoor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.rrapps.heavensdoor.model.Settings;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

public class MyWallpaperService extends GLWallpaperService {
	private HDLWPRenderer mRenderer;
    private GestureDetector mGestureDetecter;

	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

    private class WallpaperEngine extends GLEngine
                                  implements GestureDetector.OnDoubleTapListener,
                                             GestureDetector.OnGestureListener,
                                             SensorEventListener {

        WallpaperEngine() {
            super();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            // Check if the system supports OpenGL ES 2.0.
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            if (supportsEs2)
            {
                // Request an OpenGL ES 2.0 compatible context.
                setEGLContextClientVersion(2);
                mRenderer = new HDLWPRenderer(getApplicationContext());
                setRenderer(mRenderer);
                setRenderMode(RENDERMODE_CONTINUOUSLY);
            }
            else
            {
                // This is where you could create an OpenGL ES 1.x compatible
                // renderer if you wanted to support both ES 1 and ES 2.
                Toast.makeText(getApplicationContext(),
                            "OpenGL ES 2 Not supported on your device ! Wallpaper will no function correctly",
                            Toast.LENGTH_LONG).show();
                // TO-DO show some user message when above toast is not showing up
                return;
            }

            // Instantiate the gesture detector with the
            // application context and an implementation of
            // GestureDetector.OnGestureListener
            mGestureDetecter = new GestureDetector(MyWallpaperService.this, this);
            // Set the gesture detector as the double tap
            mGestureDetecter.setOnDoubleTapListener(this);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

//            if(visible) {
//                // initialize sensor
//                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//                mAccVals = new float[3];
//                initializeAccelerometer();
//            } else {
//                deregisterAccelerometer();
//            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mRenderer != null) {
                mRenderer.release();
            }
            mRenderer = null;
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            mGestureDetecter.onTouchEvent(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.v(HDLWPApplication.LogTag, "onSingleTap");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(HDLWPApplication.LogTag, "onDoubleTap");
            if(Settings.getInstance(MyWallpaperService.this).getSettingsOnDoubleTapPreference()) {
                Intent intent = new Intent(MyWallpaperService.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.v(HDLWPApplication.LogTag, "onDoubleTapEvent");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.v(HDLWPApplication.LogTag, "onSingleTapUp");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        /**
         * Sensor related variables
         */
        private SensorManager mSensorManager;
        private Sensor mAccelerometer;
        private float[] mAccVals;
        boolean mIsAccelerometerRegistered = false;

        public void initializeAccelerometer() {
            if(!mIsAccelerometerRegistered) {
                // register sensor
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                mIsAccelerometerRegistered = true;
            }
        }

        public void deregisterAccelerometer() {
            if(mIsAccelerometerRegistered) {
                mSensorManager.unregisterListener(this);
                mIsAccelerometerRegistered = false;
            }
        }

        // to smooth out
        private final float FILTERING_FACTOR = .3f;
        private final float SENSITIVITY = 5.0f;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            // low-pass filter to make the movement more stable
            mAccVals[0] = (float) (event.values[0] * FILTERING_FACTOR
                                    + mAccVals[0] * (1.0 - FILTERING_FACTOR));
            mAccVals[1] = (float) (event.values[1] * FILTERING_FACTOR
                                    + mAccVals[1] * (1.0 - FILTERING_FACTOR));

//            if(mRenderer != null)
//                mRenderer.setAccelerometerValues(event.values[1] - mAccVals[1] * SENSITIVITY,
//                                                 event.values[0] - mAccVals[0] * SENSITIVITY);

//        scene.camera().position.x = mAccVals.x * .2f;
//        scene.camera().position.y = mAccVals.y * .2f;
//
//        scene.camera().target.x = -scene.camera().position.x;
//        scene.camera().target.y = -scene.camera().position.y;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
