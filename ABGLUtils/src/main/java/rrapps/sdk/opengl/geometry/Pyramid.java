/**
 * 
 */
package rrapps.sdk.opengl.geometry;

import rrapps.sdk.opengl.shaders.IShader;
import rrapps.sdk.opengl.shaders.Program;
import rrapps.sdk.opengl.shaders.Shader;
import rrapps.sdk.opengl.shaders.ShaderLibrary;
import android.opengl.GLES20;

/**
 * @author Pankaj Bansal
 * 
 */

/**
 * TODO
 * 1. Support for giving pyramid size
 * 2. Add error/exception handling and logs wherever required 
 */
public class Pyramid extends AbstractGeometry
{
    public Pyramid(float[] vertices)
    {
        super(vertices);
    }
    
    public Pyramid()
    {
        _setupShader();
        _vertices = new float [] {
           -1.0f, -1.0f, 1.0f,  // 0. left-bottom-back
           1.0f, -1.0f, 1.0f,  // 1. right-bottom-back
           1.0f, 1.0f,  1.0f,  // 2. right-bottom-front
          -1.0f, 1.0f,  1.0f,  // 3. left-bottom-front
           0.0f,  0.0f,  -1.0f   // 4. top
        };
        
        setCoordsPerVertex(3);
        
        _indices = new short [] {
                    2, 4, 3,   // front face (CCW)
                    1, 4, 2,   // right face
                    0, 4, 1,   // back face
                    4, 0, 3    // left face
                  };
        
        _colors = new float[] { 
                0.0f, 0.0f, 1.0f, 1.0f,  // 0. blue
                  0.0f, 1.0f, 0.0f, 1.0f,  // 1. green
                  0.0f, 0.0f, 1.0f, 1.0f,  // 2. blue
                  0.0f, 1.0f, 0.0f, 1.0f,  // 3. green
                  1.0f, 0.0f, 0.0f, 1.0f   // 4. red
                  };
        
        setIndexed(true);
        
        // ask to setup buffers to super class
        _setupVertexBuffer();
        _setupIndexBuffer();
        _setupColorBuffer();
    }
    
    private void _setupShader()
    {
        IShader vertexShader = new Shader(IShader.ShaderType.VERTEX_SHADER);
        vertexShader.setShaderSource(ShaderLibrary.DefaultVertexShader2);
        int vertexShaderHandle = vertexShader.load();
        
        IShader fragmentShader = new Shader(IShader.ShaderType.FRAGMENT_SHADER);
        fragmentShader.setShaderSource(ShaderLibrary.DefaultFragmentShader2);
        int fragmentShaderHandle = fragmentShader.load();
        
        Program defaultProgram = new Program(vertexShaderHandle, fragmentShaderHandle);
        defaultProgram.linkProgram();
        _program = defaultProgram.getID();
    }
    
    public void draw(float[] mvpMatrix)
    {
        GLES20.glUseProgram(_program);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CW);
        

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(_program, "vPosition");
        
        // get handle to vertex shader's vPosition member
        int colorHandle = GLES20.glGetAttribLocation(_program, "vColor");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, getCoordsPerVertex(), GLES20.GL_FLOAT, false, 0, _vertexBuffer);
        // Prepare the color data
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, _colorBuffer);
        
        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
        rrapps.sdk.opengl.GLUtils.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        rrapps.sdk.opengl.GLUtils.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, _indices.length, GLES20.GL_UNSIGNED_SHORT, _indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
