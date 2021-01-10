precision mediump float;

uniform mat4 uMVPMatrix;
attribute vec4 aPosition;

void main() {
	vec4 position = aPosition;
	gl_Position = uMVPMatrix * position;
}