// Ref: http://adrianboeing.blogspot.in/2011/01/webgl-tunnel-effect-explained.html
//      https://www.shadertoy.com/view/Ms2SWW
#ifdef GL_FRAGMENT_PRECISION_HIGH
    precision highp float;
#else
    precision mediump float;
#endif

uniform float uTime;
uniform float uSpeed;
uniform float uBrightness;
uniform vec2 uResolution;
uniform sampler2D uTexture;

// square realted variables
uniform int uIsSquare;

// accelerometer input
uniform float uDeviationX;
uniform float uDeviationY;

// center bright or dark
uniform int uIsCenterBright;

uniform int uIsRotationEnabled;

uniform float uCenterBandHeight;

void main(void)
{
    // clamp pixel posiiton in [-1,1]
    vec2 p = -1.0 + 2.0 * gl_FragCoord.xy / uResolution.xy;

    //  this is to fix aspect ratio of tunnel center
    p.y = p.y * uResolution.y/uResolution.x;

    // apply accelerometer
    // p.x = p.x + clamp(uDeviationX, -0.7, 0.7);

    vec2 uv;

    // these two lines will make it rotate
    float scaledTime = uTime * uSpeed;
    
    float x, y;
    if(uIsRotationEnabled == 1) 
    {
        x = p.x * cos(scaledTime) - p.y * sin(scaledTime);
        y = p.x * sin(scaledTime) + p.y * cos(scaledTime);
    }
    else
    {
        x = p.x;
        y = p.y;
    }

    // textures are in wrap mode so they are repeating them selves as uvs are incrementing
    float h = .4;
    
    // as per calculations on h blog
    // ok again :-/
    // I understand the maths part but not visualization :(
    uv.x = h *  x / y;

    // think it as inverse relationship y = 1/py which makes it 1 at horizontal edges and infinite 
    // in between. h is just to adjust scaling of wrapping
    uv.y = scaledTime + h / abs(y);

    // Multiply with py for darkening center. Need to multiply with absolute value else 
    // bottom plane will become completely dark because of negative values
    // in blog its py*py which is difficult to understand and i like this effect better 
    if(uIsCenterBright == 1)
    {
        // pump up the brightness for better effect
        float brightness = uBrightness * 2.0;
        gl_FragColor = vec4(texture2D(uTexture, uv).xyz * uCenterBandHeight/abs(y), 1.0) * brightness;
    }
    else
    {
        gl_FragColor = vec4(texture2D(uTexture, uv).xyz * abs(y), 1.0) * uBrightness;
    }
}