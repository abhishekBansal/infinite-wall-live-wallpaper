/**
 * 
 */
package rrapps.sdk.opengl.geometry;

import rrapps.sdk.opengl.GLUtils;
import rrapps.sdk.opengl.shaders.IShader;
import rrapps.sdk.opengl.shaders.Program;
import rrapps.sdk.opengl.shaders.Shader;
import rrapps.sdk.opengl.shaders.ShaderLibrary;
import android.opengl.GLES20;

/**
 * @author Pankaj Bansal
 * this class renders a pyramid with same texture on all side
 * API user need to call loadTexture* function after creating object 
 */

/**
 *  TODO 
 *  1. Provide function to load texture from bitmaps
 *  2. Support for giving pyramid size
 *  3. Support for texture blending modes
 *  4. Add error/exception handling and logs wherever required 
 */
public class TexturedPyramid extends AbstractGeometry
{
    /**
     * @param vertices
     */
    public TexturedPyramid(float[] vertices)
    {
        super(vertices);
        // TODO Auto-generated constructor stub
    }

    /**     * @param vertices
     * @param indices
     */
    public TexturedPyramid(float[] vertices, short[] indices)
    {
        super(vertices, indices);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public TexturedPyramid()
    {
       // _setupShader();
        _vertices = new float []{
                // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
                // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                // usually represent the back1.0fof an object and aren't visible anyways.
                
                //front face (CCW)
                   1.0f, 1.0f,  1.0f,  // 2. right-bottom-front
                   0.0f,  0.0f,  -1.0f,   // 4. top
                  -1.0f, 1.0f,  1.0f,  // 3. left-bottom-front
                
                   //right face
                   1.0f, -1.0f, 1.0f,  // 1. right-bottom-back
                   0.0f,  0.0f,  -1.0f,   // 4. top
                  1.0f, 1.0f,  1.0f,  // 2. right-bottom-front
                       
                   //back face
                  -1.0f, -1.0f, 1.0f,  // 0. left-bottom-back
                   0.0f,  0.0f,  -1.0f,   // 4. top
                  1.0f, -1.0f, 1.0f,  // 1. right-bottom-back
                   
                   //left face   
                  -1.0f, 1.0f,  1.0f,  // 3. left-bottom-front
                  0.0f,  0.0f,  -1.0f,   // 4. top
                  -1.0f, -1.0f, 1.0f  // 0. left-bottom-back
                };
        
        _texCoords = new float [] {
                //front face (CCW)
                1.0f, 0.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
              
                //right face
                1.0f, 0.0f,
                0.0f, 1.0f,
                0.5f, 0.0f,
                
                //back face
                0.5f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
             
                //left face
                1.0f, 0.0f,
                0.0f, 1.0f,
                0.5f, 0.0f,
            };
        
        setCoordsPerVertex(3);
        setIndexed(true);
        
        // ask to setup buffers to super class
        _setupVertexBuffer();
        _setupTexCoordBuffer();
    }
    
    private void _setupShader()
    {
        IShader vertexShader = new Shader(IShader.ShaderType.VERTEX_SHADER);
        IShader fragmentShader = new Shader(IShader.ShaderType.FRAGMENT_SHADER);
        
        if(_isCubeMap == true)
        {
            vertexShader.setShaderSource(ShaderLibrary.CubeMapVertexShaderCode);
            fragmentShader.setShaderSource(ShaderLibrary.CubeMapFragmentShaderCode);
        }
        else
        {
            vertexShader.setShaderSource(ShaderLibrary.SimpleTextureVertexShaderCode);
            fragmentShader.setShaderSource(ShaderLibrary.SimpleTextureFragmentShaderCode);
        }
        
        int vertexShaderHandle = vertexShader.load();
        int fragmentShaderHandle = fragmentShader.load();
        
        Program shaderProgram = new Program(vertexShaderHandle, fragmentShaderHandle);
        shaderProgram.linkProgram();
        _program = shaderProgram.getID();
    }

    /* (non-Javadoc)
     * @see rrapps.sdk.opengl.geometry.IGeometry#draw(float[])
     */
    @Override
    public void draw(float[] mvpMatrix)
    {
        if(_program == 0)
            _setupShader();

        //  Add program to OpenGL environment
        GLES20.glUseProgram(_program);
        
        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(_program, "vPosition");
        rrapps.sdk.opengl.GLUtils.checkGlError("glGetAttribLocation");
        
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        
        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 
                                getCoordsPerVertex(),
                                GLES20.GL_FLOAT, 
                                false,
                                0, 
                                _vertexBuffer);

        // get handle to vertex shader's vPosition member
        int texCoordHandle = GLES20.glGetAttribLocation(_program, "texCoordinate");
        rrapps.sdk.opengl.GLUtils.checkGlError("glGetAttribLocation");
        
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        
        // Prepare color data
        GLES20.glVertexAttribPointer(texCoordHandle, 
                                    2,
                                    GLES20.GL_FLOAT, 
                                    false,
                                    0, 
                                    _texCoordBuffer);

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(_program, "uMVPMatrix");
        rrapps.sdk.opengl.GLUtils.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        rrapps.sdk.opengl.GLUtils.checkGlError("glUniformMatrix4fv");
        
        int textureUniformHandle = GLES20.glGetUniformLocation(_program, "uTexture");
        GLUtils.checkGlError("glGetUniformLocation");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureHandle);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureUniformHandle, 0);
         
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);
            
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }
}
