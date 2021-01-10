/**
 * 
 */
package rrapps.sdk.opengl.shaders;

/**
 * this class will contain some readymade shaders
 * @author Abhishek Bansal
 *
 */
public abstract class ShaderLibrary
{
    /**
     * following fragment shaders takes modelview matrix as uniform input and 
     * position of vertex as attribute variable
     */
    public static final String DefaultVertexShader =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" + 
            "attribute vec4 vPosition;" + 
            "void main() "    + 
            "{" +
                // The matrix must be included as a modifier of gl_Position.
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" + 
            "}";
    
    /**
     * following shader takes color as attribute variable and pass it on to fragment shader as varying variable
     */
    public static final String DefaultVertexShader2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" + 
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying vec4 vvColor;" +
            "void main() "    + 
            "{" +
                // The matrix must be included as a modifier of gl_Position.
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "vvColor = vColor;" +
                "gl_Position = uMVPMatrix * vPosition;" + 
            "}";
    
    public static final String DefaultFragmentShader2 = 
            "precision mediump float;"   + 
            "varying vec4 vvColor;" + 
            "void main() {" + 
            "  gl_FragColor = vvColor;" + 
            "}";

    public static final String DefaultFragmentShader = 
            "precision mediump float;"   + 
            "uniform vec4 vColor;" + 
            "void main() {" + 
            "  gl_FragColor = vColor;" + 
            "}";
    
    public static final String ConstantGreenFragmentShader = 
            "precision mediump float;"   + 
            "void main() {" + 
            "  gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);" + 
            "}";
    
    public static final String ConstantRedFragmentShader = 
            "precision mediump float;"   + 
            "void main() {" + 
            "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" + 
            "}";
    
    public static final String CubeMapVertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 color;" +
            "attribute vec3 normal;" +
            "attribute vec2 texCoordinate;" +
            
            "varying vec4 vColor;" +
            "varying vec2 vTexCoordinate;" +
            "varying vec4 vvPosition;" +
            
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "vColor = color;" +
            "vTexCoordinate = texCoordinate;" +
            "vvPosition = vPosition;" +
            "gl_Position = uMVPMatrix * vPosition;" +
            "}";

    public static final String CubeMapFragmentShaderCode =
            "precision mediump float;" +
    
            "uniform samplerCube uTexture;" +
            
            "varying vec4 vColor;" +
            "varying vec2 vTexCoordinate;" +
            "varying vec4 vvPosition;" +
            
            "void main() {" +
            "gl_FragColor = vec4(textureCube(uTexture, vvPosition.xyz).rgb, 1.0);" +
            "}";
    
    public static final String SimpleTextureVertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 color;" +
            "attribute vec3 normal;" +
            "attribute vec2 texCoordinate;" +
            
            "varying vec4 vColor;" +
            "varying vec2 vTexCoordinate;" +
            
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "vColor = color;" +
            "vTexCoordinate = texCoordinate;" +
            "gl_Position = uMVPMatrix * vPosition;" +
            "}";

    public static final String SimpleTextureFragmentShaderCode =
            "precision mediump float;" +
    
            "uniform sampler2D uTexture;" +
            
            "varying vec4 vColor;" +
            "varying vec2 vTexCoordinate;" +
            
            "void main() {" +
            "gl_FragColor = vec4(texture2D(uTexture, vTexCoordinate).rgb, 1.0);" +
            "}";
}
