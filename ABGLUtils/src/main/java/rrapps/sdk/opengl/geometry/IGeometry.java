/**
 * 
 */
package rrapps.sdk.opengl.geometry;

/**
 * @author Abhishek Bansal
 *
 */
public interface IGeometry 
{
    /**
     * sets color for geometry
     * @param color should be in 4 component, each component should vary from 0-1 
     */
    void setColor(float[] color);
    
    float [] getColor();
    
    void draw(float[] mvpMatrix);
}
