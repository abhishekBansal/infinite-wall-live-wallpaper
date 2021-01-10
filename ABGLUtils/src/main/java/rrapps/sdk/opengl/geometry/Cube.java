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
 * @author Abhishek Bansal
 * 
 */

/**
 * TODO
 * 1. Support for giving cube size
 * 2. Add error/exception handling and logs wherever required 
 */
public class Cube extends AbstractGeometry
{
    public Cube(float[] vertices)
    {
        super(vertices);
    }
    
    public Cube()
    {
        _setupShader();
        _vertices = new float [] {
                -1.0f, -1.0f, -1.0f, 
                1.0f, -1.0f, -1.0f, 
                1.0f, 1.0f, -1.0f, 
                -1.0f, 1.0f, -1.0f, 
                -1.0f, -1.0f, 1.0f, 
                1.0f, -1.0f, 1.0f, 
                1.0f, 1.0f, 1.0f, 
                -1.0f, 1.0f, 1.0f };
        
        setCoordsPerVertex(3);
        
        _indices = new short [] {
                0, 4, 5, 0, 5, 1, // two triangles for side 1
                1, 5, 6, 1, 6, 2, // two triangles for side 2
                2, 6, 7, 2, 7, 3, // etc.
                3, 7, 4, 3, 4, 0, 
                4, 7, 6, 4, 6, 5, 
                3, 0, 1, 3, 1, 2
              };
        
        _colors = new float[] { 
                0.0f, 1.0f, 0.0f, 1.0f, 
                0.0f, 1.0f, 0.0f, 1.0f, 
                1.0f, 0.5f, 0.0f, 1.0f,
                1.0f, 0.5f, 0.0f, 1.0f, 
                1.0f, 0.0f, 0.0f, 1.0f, 
                1.0f, 0.0f, 0.0f, 1.0f, 
                0.0f, 0.0f, 1.0f, 1.0f, 
                1.0f, 0.0f, 1.0f, 1.0f };
        
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