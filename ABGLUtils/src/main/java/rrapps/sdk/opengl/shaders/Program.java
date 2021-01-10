/**
 * 
 */
package rrapps.sdk.opengl.shaders;

import rrapps.sdk.utils.LOGUtil;
import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Abhishek Bansal
 * 
 */

/**
 * 
 * TODO
 * 1. restrict validation to only debug builds as its epensive and not required in release code
 *
 */
public class Program
{
    /**
     * this is program Id assigned by OpenGL
     */
    private int _programID = 0;
    private int _vertexShader = 0;
    private int _fragmentShader = 0;

    /**
     * user defined program name. can be used to identify a program in error
     * messages
     */
    private String _programName = "";

    public int getVertexShader()
    {
        return _vertexShader;
    }

    public void setVertexShader(int _vertexShader)
    {
        this._vertexShader = _vertexShader;
    }

    public int getFragmentShader()
    {
        return _fragmentShader;
    }

    public void setFragmentShader(int _fragmentShader)
    {
        this._fragmentShader = _fragmentShader;
    }

    public Program(int vertexShader, int fragmentShader)
    {
        _vertexShader = vertexShader;
        _fragmentShader = fragmentShader;
    }

    public boolean linkProgram()
    {
        _programID = GLES20.glCreateProgram(); // create empty OpenGL Program
        GLES20.glAttachShader(_programID, _vertexShader); // add the vertex shader
        GLES20.glAttachShader(_programID, _fragmentShader); // add the fragment
        GLES20.glLinkProgram(_programID); // create OpenGL program executables
 
        int[] linked = new int[1];
        GLES20.glGetProgramiv(_programID, GLES20.GL_LINK_STATUS, linked, 0);
        if ( linked[0] == GLES20.GL_FALSE) 
        {
            Log.e(LOGUtil.LOG_TAG, "Could not link shader: " + _programName);
            Log.e(LOGUtil.LOG_TAG, GLES20.glGetProgramInfoLog(_programID));
            return false;
        }
 
        // perform general validation that the program is usable
        GLES20.glValidateProgram(_programID);
 
        GLES20.glGetProgramiv(_programID, GLES20.GL_VALIDATE_STATUS, linked, 0);
        if ( linked[0] == GLES20.GL_FALSE)
        {
            Log.e(LOGUtil.LOG_TAG, "Could not validate shader: " + _programName);
            Log.e(LOGUtil.LOG_TAG, GLES20.glGetProgramInfoLog(_programID));
            return false;
        }
        
        return true;
    }

    public int getID()
    {
        return _programID;
    }
}
