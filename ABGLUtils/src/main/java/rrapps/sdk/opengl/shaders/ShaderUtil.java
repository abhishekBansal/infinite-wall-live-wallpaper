/**
 * 
 */
package rrapps.sdk.opengl.shaders;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rrapps.sdk.utils.LOGUtil;

/**
 * @author Abhishek Bansal
 *
 */
public final class ShaderUtil
{
    /**
     * 
     * @param shaderType
     * @param source
     * @return shader id
     */
    public static int LoadShaderFromString(IShader.ShaderType shaderType, String source)
    {
        return _compileShader(shaderType.getType(), source);
    }
    
    /**
     * 
     * @param shaderType
     * @param fileName
     * @return id of the shader if compilation was successful 0 otherwise
     */
    public static int LoadShaderFromFile(IShader.ShaderType shaderType, String fileName)
    {
        String source = _getSourceFromFile(fileName);
        return _compileShader(shaderType.getType(), source);
    }

    /**
     *
     * @return id of the shader if compilation was successful 0 otherwise
     */
    public static int LoadResourceFromRawResource(int resId, IShader.ShaderType shaderType, final Context context) {
        InputStream is = context.getResources().openRawResource(resId);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 8192);

        StringBuilder source = new StringBuilder();
        String line = null ;
        try {
            while( (line = br.readLine()) !=null )
            {
                source.append(line);
                source.append('\n');
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("unable to load shader from resource ["+resId+"]", e);
        }

        return _compileShader(shaderType.getType(), source.toString());
    }
    
    private static int _compileShader(int type, String source)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) 
        {
            Log.e(LOGUtil.LOG_TAG, "Could not compile shader: ");
            Log.e(LOGUtil.LOG_TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    /**
     * following function returns shader as string from a text file on disk
     * @param filename shader file
     * @return
     */
    private static String _getSourceFromFile(String filename)
    {
        StringBuilder source = new StringBuilder();
        String line = null ;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while( (line = reader.readLine()) !=null )
            {
                source.append(line);
                source.append('\n');
            }
            reader.close();
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("unable to load shader from file ["+filename+"]", e);
        }
 
        return source.toString();
    }
}
