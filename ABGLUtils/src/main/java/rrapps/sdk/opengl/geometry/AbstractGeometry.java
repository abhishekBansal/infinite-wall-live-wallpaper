/**
 * 
 */
package rrapps.sdk.opengl.geometry;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import rrapps.sdk.opengl.GLUtils;
import rrapps.sdk.utils.LOGUtil;

/**
 * @author Abhishek Bansal
 *
 */
public abstract class AbstractGeometry implements ICustomGeometry, ITexturedGeometry
{
    protected float[] _colors = {1.0f, 1.0f, 1.0f, 1.0f};
    protected float[] _vertices;
    protected short[] _indices;
    protected float[] _texCoords;
    
    protected boolean _isTextureEnabled = false;
    protected boolean _isIndexedGeometry = false;
    
    protected int _program;
    
    protected FloatBuffer _vertexBuffer;
    protected FloatBuffer _colorBuffer;
    protected FloatBuffer _texCoordBuffer;
    protected ShortBuffer _indexBuffer;
    
    protected int _vertexCount;
    
    protected int _coordsPerVertex = 3;
    
    protected int _textureResourceID = 0;
    /**
     * opengl texture handle
     */
    protected int _textureHandle = 0;
    
    protected boolean _isCubeMap;


    public int getTextureHandle()
    {
        return _textureHandle;
    }

    public void setTextureHandle(final int textureHandle)
    {
        _textureHandle = textureHandle;
    }

    public int getTextureResourceID()
    {
        return _textureResourceID;
    }

    public void setTextureResourceID(int textureID)
    {
        _textureResourceID = textureID;
        setTextureEnabled(true);
    }

    /**
     * @return the _vertexCount
     */
    public int getVertexCount()
    {
        return _vertexCount;
    }

    /**
     * @param vertexCount the _vertexCount to set
     */
    public void setVertexCount(int vertexCount)
    {
        _vertexCount = vertexCount;
    }

    protected AbstractGeometry(float [] vertices)
    {
        _vertices = vertices;
        _setupVertexBuffer();
    }
    
    public void setCubeMap(boolean isCubeMap)
    {
        _isCubeMap = isCubeMap;
    }
    
    public boolean getCubeMap()
    {
        return _isCubeMap ;
    }
    
    protected AbstractGeometry(float [] vertices, short [] indices)
    {
        _vertices = vertices;
        _indices = indices;
        _setupVertexBuffer();
        _setupIndexBuffer();
    }
    
    /**
     * protected constructor only meant to be used by child classes
     * mainly by predefined geometries
     */
    protected AbstractGeometry()
    {    }
    
    protected void _setupVertexBuffer()
    {
        try
        {
            // initialize vertex byte buffer for shape coordinates
            // (number of coordinate values * 4 bytes per float)
            // Ridiculously surprising that java doesn't have anything like sizeof()
            ByteBuffer bb = ByteBuffer.allocateDirect(_vertices.length * 4);
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());
            // create a floating point buffer from the ByteBuffer
            _vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            _vertexBuffer.put(_vertices);
            // set the buffer to read the first coordinate
            _vertexBuffer.position(0);
        }
        catch(IllegalArgumentException e)
        {
            Log.e(LOGUtil.LOG_TAG, "Invalid Vertices Array. Could not create geometry !");
            throw(e);
        }
        
        setIndexed(true);
    }
    
    protected void _setupIndexBuffer()
    {
        try
        {
            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(_indices.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            _indexBuffer = dlb.asShortBuffer();
            _indexBuffer.put(_indices);
            _indexBuffer.position(0);
        }
        catch(IllegalArgumentException e)
        {
            Log.e(LOGUtil.LOG_TAG, "Invalid Vertices Array. Could not create geometry !");
            throw(e);
        }
    }
    
    protected void _setupColorBuffer()
    {
        try
        {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(_colors.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            _colorBuffer = byteBuf.asFloatBuffer();
            _colorBuffer.put(_colors);
            _colorBuffer.position(0);
        }
        catch(IllegalArgumentException e)
        {
            Log.e(LOGUtil.LOG_TAG, "Invalid Vertices Array. Could not create buffer for colors !");
            throw(e);
        }
    }
    
    protected void _setupTexCoordBuffer()
    {
        try
        {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(_texCoords.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            _texCoordBuffer = byteBuf.asFloatBuffer();
            _texCoordBuffer.put(_texCoords);
            _texCoordBuffer.position(0);
        }
        catch(IllegalArgumentException e)
        {
            Log.e(LOGUtil.LOG_TAG, "Invalid TexCoordinates Array. Could not create buffer for Texture Coordinate !");
            throw(e);
        }
    }
    
    public boolean loadTextureFromResource(Context context, int texResourceID)
    {
        _textureHandle = GLUtils.LoadTexture(context, texResourceID);
        _textureResourceID = texResourceID;
        
        if (_textureHandle != 0)
            return true;
        return false;
    }
    
    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.IGeometry#setColor(float[])
     */
    @Override
    public void setColor(float[] color)
    {
        _colors = color;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.IGeometry#getColor()
     */
    @Override
    public float[] getColor()
    {
        return _colors;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomIndexedGeometry#setIndicesArray(float[])
     */
    @Override
    public void setIndicesArray(short [] indices)
    {
        _indices = indices;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomIndexedGeometry#getIndicesArray()
     */
    @Override
    public short [] getIndicesArray()
    {
        return _indices;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#setVertexArray(float[])
     */
    @Override
    public void setVertexArray(float[] vertices)
    {
        _vertices = vertices;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#getVertexArray()
     */
    @Override
    public float[] getVertexArray()
    {
        return _vertices;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#setTexCoordArray(float[])
     */
    @Override
    public void setTexCoordArray(float[] texCoords)
    {
        _texCoords = texCoords;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#getTexCoordArray()
     */
    @Override
    public float[] getTexCoordArray()
    {
        return _texCoords;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#getTextureEnabled()
     */
    @Override
    public boolean getTextureEnabled()
    {
        return _isTextureEnabled;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#setTextureEnabled(boolean)
     */
    @Override
    public void setTextureEnabled(boolean isTextureEnabled)
    {
        _isTextureEnabled = isTextureEnabled;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#setShaderProgram(int)
     */
    @Override
    public void setShaderProgram(int programHandle)
    {
        _program = programHandle;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#getShaderProgram()
     */
    @Override
    public int getShaderProgram()
    {
        return _program;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#setIndexed(boolean)
     */
    @Override
    public void setIndexed(boolean isIndexed)
    {
        _isIndexedGeometry = isIndexed;
    }

    /* (non-Javadoc)
     * @see abhishek.sdk.opengl.geometry.ICustomGeometry#getIndexed()
     */
    @Override
    public boolean getIndexed()
    {
        return _isIndexedGeometry;
    }
    
    /**
     * @return the _coordsPerVertex
     */
    public int getCoordsPerVertex()
    {
        return _coordsPerVertex;
    }

    /**
     * @param coordsPerVertex the _coordsPerVertex to set
     */
    public void setCoordsPerVertex(int coordsPerVertex)
    {
        _coordsPerVertex = coordsPerVertex;
        _vertexCount = _vertices.length/_coordsPerVertex;
    }

}
