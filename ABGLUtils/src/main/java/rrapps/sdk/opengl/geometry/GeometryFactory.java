package rrapps.sdk.opengl.geometry;

public class GeometryFactory 
{
    public enum GeometryType
    {
        TEXTURED_PYRAMID,
        PYRAMID,
        TEXTURED_CUBE,
        CUBE,
        SQUARE,
        TRIANGLE,
        CUSTOM_GEOMETRY
    }
    public static IGeometry getGeometry(GeometryType type)
    {
        switch(type)
        {
            case TEXTURED_PYRAMID:
            {
                return new TexturedPyramid();
            }
            case PYRAMID:
            {
                return new Pyramid();
            }
            case TEXTURED_CUBE:
                return new TexturedCube();
                
            case CUBE:
                return new Cube();
                
            case SQUARE:
                return new Square();
                
            case TRIANGLE:
                return new Triangle();
                
            default:
                return null;
        }
    }

}
