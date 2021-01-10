/**
 * 
 */
package rrapps.sdk.opengl.geometry;

/**
 * @author abhishek.b1
 *
 */
public interface ICustomGeometry extends IGeometry
{
    void setVertexArray(float [] vertices);
    float [] getVertexArray();
    
    /**
     * 
     * @param programHandle has to be linked handle to program that needs to be applied
     *        setting uniform for this needs to handled by renderer 
     * @return
     */
    void setShaderProgram(int programHandle);
    int getShaderProgram();
    
    void setIndexed(boolean isIndexed);
    boolean getIndexed();
    
    void setIndicesArray(short [] indices);
    short [] getIndicesArray();
    
    /**
     * @return the _coordsPerVertex
     */
    public int getCoordsPerVertex();

    /**
     * @param coordsPerVertex the _coordsPerVertex to set
     */
    public void setCoordsPerVertex(int coordsPerVertex);

}
