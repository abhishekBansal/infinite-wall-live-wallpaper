package com.rrapps.heavensdoor;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.rrapps.heavensdoor.model.Settings;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rrapps.sdk.opengl.geometry.ITexturedGeometry;


/**
 * author: Abhishek Bansal
 */

public class HDLWPRenderer
        implements GLWallpaperService.Renderer {

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    private float[] mModelViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /**
     * Tunnel plane
     */
    private ITexturedGeometry mTunnelPlane;

    /**
     * shader program
     */
    private int mViewportHeight;
    private int mViewPortWidth;

    public Context getContext() {
        return mContext;
    }

    Context mContext;
    HDLWPRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.v(HDLWPApplication.LogTag, "onSurfaceCreated");
        // sabse pahele bijli bachao
        setFrameRate(30);
        GLES20.glClearColor(0.2f, 0.4f, 0.2f, 1f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.45f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // initialize plane
        mTunnelPlane = new HeavensDoorGeometry(getContext());
        mTunnelPlane.setTextureEnabled(true);
        mTunnelPlane.loadTextureFromResource(getContext(),
                                            Settings.getInstance(getContext()).getCurrentTextureResId());

        int [] range =  new int[2];
        int [] precision =  new int[1];

        GLES20.glGetShaderPrecisionFormat(GLES20.GL_FRAGMENT_SHADER,
                GLES20.GL_HIGH_FLOAT,
                range, 0,
                precision, 0);
        if(precision[0] == 0) {
            Log.i(HDLWPApplication.LogTag, "High Precision not supported on this device");
            Settings.getInstance(getContext()).setIsHighPrecisionSupported(false);
        } else {
            Log.i(HDLWPApplication.LogTag, "High Precision is supported on this device");
            Settings.getInstance(getContext()).setIsHighPrecisionSupported(true);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.v(HDLWPApplication.LogTag, "onSurfaceChanged Ratio" + (float)width/height);
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        mViewPortWidth = width;
        mViewportHeight = height;
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        ((HeavensDoorGeometry)mTunnelPlane).setViewportDimensions(width, height);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;

        final float eyeZ;
        if(ratio <= 1)
            eyeZ = 1.45f;
        else
            eyeZ = 1.001f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -1.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        // get the time at the start of the frame
        long time = System.currentTimeMillis();

        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mModelViewMatrix, 0);
        if(mViewportHeight > mViewPortWidth)
            Matrix.scaleM(mModelMatrix, 0, 1.0f, (float)mViewportHeight/mViewPortWidth, 1.0f);
        else
            Matrix.scaleM(mModelMatrix, 0, (float)mViewPortWidth/mViewportHeight, 1.0f, 1.0f);

        Matrix.multiplyMM(mModelViewMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0);
        mTunnelPlane.draw(mMVPMatrix);

        // get the time taken to render the frame
        long time2 = System.currentTimeMillis() - time;

        // if time elapsed is less than the frame interval
        if(time2 < mFrameInterval){
            try {
                // sleep the thread for the remaining time until the interval has elapsed
                // let it sleep a little less(5 milis less)in order to save flickering
                long timeout = mFrameInterval - time2 - 5;
                if(timeout > 0)
                    Thread.sleep(timeout);
            } catch (InterruptedException e) {
                // Thread error
                e.printStackTrace();
            }
        } else {
            // framerate is slower than desired
        }
    }

    /**
     * Called when the engine is destroyed. Do any necessary clean up because
     * at this point your renderer instance is now done for.
     */
    public void release() {
    }

    private long mFrameInterval; // the time it should take 1 frame to render
    public void setFrameRate(long fps){
        mFrameInterval = 1000 / fps;
    }

    public void setAccelerometerValues(float valX, float valY) {
        Log.v(HDLWPApplication.LogTag, "Acc val.x: " + valX +" val.y: " + valY);
        if(mTunnelPlane != null)
            ((HeavensDoorGeometry)mTunnelPlane).setCameraDeviations(valX, valY);
    }
}
