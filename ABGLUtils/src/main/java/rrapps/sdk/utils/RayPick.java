/**
 * 
 */
package rrapps.sdk.utils;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;

/**
 * @author Abhishek Bansal following class implements functionality to pick a
 *         coordinate based on window touch coordinate
 */
public final class RayPick {

	private float[] _viewMatrix;
	private float[] _projectionMatrix;
	private int[] _viewPort;

	/**
	 * camera position in world coordinates
	 */
	private float[] _cameraPos;

	public float[] getViewMatrix() {
		return _viewMatrix;
	}

	public void setViewMatrix(float[] viewMatrix) {
		_viewMatrix = viewMatrix;
	}

	public float[] getProjectionMatrix() {
		return _projectionMatrix;
	}

	public void setProjectionMatrix(float[] projectionMatrix) {
		_projectionMatrix = projectionMatrix;
	}

	public int[] getViewPort() {
		return _viewPort;
	}

	public void setViewPort(int[] viewPort) {
		_viewPort = viewPort;
	}

	/**
	 * @return the _cameraPos
	 */
	public float[] getCameraPos() {
		return _cameraPos;
	}

	/**
	 * @param _cameraPos
	 *            the _cameraPos to set
	 */
	public void setCameraPos(float[] cameraPos) {
		_cameraPos = cameraPos;
	}

	/**
     * 
     */
	public RayPick(float[] viewMatrix, float[] projectionMatrix,
			int[] viewPort, float[] cameraPos) {
		_viewMatrix = viewMatrix;
		_projectionMatrix = projectionMatrix;
		_viewPort = viewPort;
		_cameraPos = cameraPos;
	}

	public RayPick() {
	}

	/**
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return normalized direction vector of ray in Object Space
	 */
	private float[] getDirVector(float mouseX, float mouseY) {
		// First calculate a direction vector for ray. We first find near and
		// far plane coordinates
		// and use them to calculate ray direction
		float[] farCoord = { 0.0f, 0.0f, 0.0f, 0.0f };
		float[] nearCoord = { 0.0f, 0.0f, 0.0f, 0.0f };

		// Map window coordinates to object coordinates. gluUnProject maps the
		// specified window coordinates into object
		// coordinates using model, proj, and view
		int result = GLU.gluUnProject(mouseX, mouseY, 1.0f, _viewMatrix, 0,
				_projectionMatrix, 0, _viewPort, 0, farCoord, 0);

		if (result == GLES20.GL_TRUE) {
			farCoord[0] = farCoord[0] / farCoord[3];
			farCoord[1] = farCoord[1] / farCoord[3];
			farCoord[2] = farCoord[2] / farCoord[3];
		}

		result = GLU.gluUnProject(mouseX, mouseY, 0.0f, _viewMatrix, 0,
				_projectionMatrix, 0, _viewPort, 0, nearCoord, 0);

		if (result == GL10.GL_TRUE) {
			nearCoord[0] = nearCoord[0] / nearCoord[3];
			nearCoord[1] = nearCoord[1] / nearCoord[3];
			nearCoord[2] = nearCoord[2] / nearCoord[3];
		}

		return VectorUtils.normalize(VectorUtils.minus(farCoord, nearCoord));
	}

	/**
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return coordinates of picked point in 3D in Object Space (The object
	 *         whose ModelView matrix has been given)
	 */
	public float[] pickOnPlane(float mouseX, float mouseY, float[] planeVertices) {
		// calling glReadPixels() with GL_DEPTH_COMPONENT is not supported in
		// GLES so now i will try to implement ray picking

		// get direction vector in object space
		float[] dirVector = getDirVector(mouseX, mouseY);

		// in camera space camera is already at origin.
		// we will convert it back to object space using inverse of model view
		// matrix
		float[] rayOrigin = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] objectSpaceRayOrigin = new float[4];

		// convert ray origin to object space by multiplying it with inverse of
		// modelview matrix
		float[] inverseMVMatrix = new float[16];
		Matrix.invertM(inverseMVMatrix, 0, _viewMatrix, 0);

		Matrix.multiplyMV(objectSpaceRayOrigin, 0, inverseMVMatrix, 0,
				rayOrigin, 0);

		rayOrigin = objectSpaceRayOrigin;

		// calculate normal for square
		float[] v1 = { planeVertices[3] - planeVertices[0],
				planeVertices[4] - planeVertices[1],
				planeVertices[5] - planeVertices[2] };
		float[] v2 = { planeVertices[9] - planeVertices[0],
				planeVertices[10] - planeVertices[1],
				planeVertices[11] - planeVertices[2] };

		float[] n = VectorUtils.normalize(VectorUtils.crossProduct(v1, v2));

		// now calculate intersection point as per following link
		// http://antongerdelan.net/opengl/raycasting.html

		// our plane passes through origin so finding 't' will be
		float t = -(VectorUtils.dot(rayOrigin, n) / VectorUtils.dot(dirVector,
				n));

		// now substitute above t in ray equation gives us intersection point
		float[] intersectionPoint = VectorUtils.addition(rayOrigin,
				VectorUtils.scalarProduct(t, dirVector));
		return intersectionPoint;
	}

}
