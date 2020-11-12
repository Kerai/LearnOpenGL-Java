package common;

import org.joml.*;
import org.joml.Math;

public class Camera {
	public float fov = 75;
	public float vWidth = 800;
	public float vHeight = 600;
	
	public float zNear = 0.2f;
	public float zFar = 1000f;
	public boolean reversedZ = false;

	public final Matrix4f transform = new Matrix4f();
	public final Matrix4fc projection = new Matrix4f();
	public final Matrix4fc view = new Matrix4f();
	public final Matrix4fc projectionView = new Matrix4f();
	
	public final Vector3f position = new Vector3f();
	public final Vector3f forward = new Vector3f();
	public final Vector3f up = new Vector3f();
	public final Vector3f right = new Vector3f();

	public Camera(float fov, float width, float height) {
		this.fov = fov;
		this.vWidth = width;
		this.vHeight = height;
		
		//view.lookAt(0, 0, -5, 0, 0, 0, 0, 1, 0);
		transform.translate(0, 0, 5); // just to make it recalculate broken state after lookAt
		recalculatePerspective();
	}
	
	public void recalculatePerspective() {
		if(reversedZ) {
			((Matrix4f) projection).setPerspective(Math.toRadians(fov), vWidth/vHeight, zFar, zNear, true);
		} else {
			((Matrix4f) projection).setPerspective(Math.toRadians(fov), vWidth/vHeight, zNear, zFar);
		}
	}

	public void calulateView() {
		var trans = new Matrix4f(transform);
		trans.invertAffine((Matrix4f) view);
		projection.mulPerspectiveAffine(view, (Matrix4f) projectionView);
//		Matrix4f inverted = view.invertAffine(new Matrix4f());
		position.set(0, 0, 0).mulPosition(trans);
		forward .set(0, 0,-1).mulDirection(trans);
		up      .set(0, 1, 0).mulDirection(trans);
		right   .set(1, 0, 0).mulDirection(trans);
	}
}
