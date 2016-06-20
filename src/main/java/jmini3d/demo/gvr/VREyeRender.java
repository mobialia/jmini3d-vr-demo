package jmini3d.demo.gvr;

import com.google.vr.sdk.base.Eye;

import jmini3d.MatrixUtils;
import jmini3d.Renderer3d;
import jmini3d.Scene;

public class VREyeRender {

	public float[] leftProjectionMatrix = new float[16];
	public float[] leftViewMatrix = new float[16];
	public float[] rightProjectionMatrix = new float[16];
	public float[] rightViewMatrix = new float[16];

	public void render(Scene scene, Eye eye, Renderer3d renderer3d) {
		// Allow concurrence separating matices
		if (eye.getType() == Eye.Type.LEFT) {
			MatrixUtils.copyMatrix(eye.getPerspective(scene.camera.getNear(), scene.camera.getFar()), leftProjectionMatrix);
			MatrixUtils.multiply(eye.getEyeView(), scene.camera.viewMatrix, leftViewMatrix);
			renderer3d.render(scene, leftProjectionMatrix, leftViewMatrix);
		} else {
			MatrixUtils.copyMatrix(eye.getPerspective(scene.camera.getNear(), scene.camera.getFar()), rightProjectionMatrix);
			MatrixUtils.multiply(eye.getEyeView(), scene.camera.viewMatrix, rightViewMatrix);
			renderer3d.render(scene, rightProjectionMatrix, rightViewMatrix);
		}
	}
}
