package rrapps.sdk.opengl.shaders;

import android.content.Context;
import android.opengl.GLES20;

/**
 * 
 * @author Abhishek Bansal
 * this interface will parent interface for fragment and vertex shader
 * geometry shaders and tesellation shaders can be added in future
 */
public interface IShader
{
    enum ShaderType
    {
        VERTEX_SHADER(GLES20.GL_VERTEX_SHADER),
        FRAGMENT_SHADER(GLES20.GL_FRAGMENT_SHADER);
        
        private int _type;
        
        public int getType()
        {
            return _type;
        }

        public void setType(int _type)
        {
            this._type = _type;
        }

        ShaderType(int type)
        {
            _type = type;
        }
    };
    
    public IShader.ShaderType getShaderType();

    public void setShaderSource(String _shaderSource);

    public void setShaderType(IShader.ShaderType _shaderType);

    public int load(String fileName);

    public int load(int rawResourceID, final Context context);
    
    public int load();
}



