/**
 * 
 */
package rrapps.sdk.opengl.geometry;

import android.content.Context;

/**
 * @author Abhishek Bansal
 *
 */
public interface ITexturedGeometry extends IGeometry
{
    void setTexCoordArray(float[] texCoords);
    float [] getTexCoordArray();
    
    void setTextureResourceID(int resId);
    int getTextureResourceID();
    
    void setTextureHandle(final int handle);
    int getTextureHandle();

    boolean getTextureEnabled();
    void setTextureEnabled(boolean isTextureEnabled);
    
    /**
     * If this is geometry is cubemapped set it to true.
     * pass a cube map texture handle while setting up texture 
     * @param isCubeMap
     */
    
    void setCubeMap(boolean isCubeMap);
    boolean getCubeMap();
    /**
     * following function loads a texture from android drawable resource
     * @param texResourceID
     * @return true if texture is loaded successfully false otherwise
     */
    boolean loadTextureFromResource(Context context, int texResourceID);
}
