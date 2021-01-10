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
public class Square extends AbstractGeometry
{

    /**
     * @param vertices
     */
    public Square(float[] vertices)
    {
        super(vertices);
    }

    /**
     * 
     */
    public Square()
    {
        _setupShader();
        _vertices = new float [] { -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f }; // top right
        setCoordsPerVertex(3);
        
        _indices = new short [] { 0, 1, 2, 0, 2, 3 };
        
        setIndexed(true);
        
        // ask to setup buffers to super class
        _setupVertexBuffer();
        _setupIndexBuffer();
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
        GLES20.glVertexAttribPointer(positionHandle, _coordsPerVertex,
                GLES20.GL_FLOAT, false, 0, _vertexBuffer);

        // get handle to fragment shader's vColor member
        int colorHandle = GLES20.glGetUniformLocation(_program, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, getColor(), 0);

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
        //GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        //GLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, _indices.length, GLES20.GL_UNSIGNED_SHORT, _indexBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
