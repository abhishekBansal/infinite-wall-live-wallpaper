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
public class Triangle extends AbstractGeometry
{

    /**
     * @param vertices
     */
    public Triangle(float[] vertices)
    {
        super(vertices);
        _setupShader();
    }
    
    public Triangle()
    {
        _setupShader();
        _vertices = new float[]{                // in counterclockwise order:
                0.0f, 0.622008459f, 0.0f, // top
                -0.5f, -0.311004243f, 0.0f, // bottom left
                0.5f, -0.311004243f, 0.0f}; // bottom right
        
        setCoordsPerVertex(3);
        // from super class
        _setupVertexBuffer();
    }
    
    private void _setupShader()
    {
        IShader vertexShader = new Shader(IShader.ShaderType.VERTEX_SHADER);
        vertexShader.setShaderSource(ShaderLibrary.DefaultVertexShader);
        int vertexShaderHandle = vertexShader.load();
        
        IShader fragmentShader = new Shader(IShader.ShaderType.FRAGMENT_SHADER);
        fragmentShader.setShaderSource(ShaderLibrary.DefaultFragmentShader);
        int fragmentShaderHandle = fragmentShader.load();
        
        Program defaultProgram = new Program(vertexShaderHandle, fragmentShaderHandle);
        defaultProgram.linkProgram();
        _program = defaultProgram.getID();
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.IGeometry#draw(float[])
     */
    @Override
    public void draw(float[] mvpMatrix)
    {
        // Add program to OpenGL environment
        GLES20.glUseProgram(_program);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(_program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 
                                    3,
                                    GLES20.GL_FLOAT, 
                                    false, 
                                    0, 
                                    _vertexBuffer);

        // get handle to fragment shader's vColor member
        int colorHandle = GLES20.glGetUniformLocation(_program, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, getColor(), 0);

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(_program, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, getVertexCount());

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
