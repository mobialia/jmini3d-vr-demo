package jmini3d.demo.gvr;

import android.opengl.GLES20;
import android.os.Bundle;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

import jmini3d.MatrixUtils;
import jmini3d.Scene;
import jmini3d.android.Renderer3d;
import jmini3d.android.ResourceLoader;

public class JMini3dActivity extends GvrActivity implements GvrView.StereoRenderer {
	private static final String TAG = "JMini3dActivity";

	MySceneController sceneController;
	Renderer3d renderer3d;
	int width, height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeGvrView();

		renderer3d = new Renderer3d(new ResourceLoader(this));
		sceneController = new MySceneController(this);
	}

	public void initializeGvrView() {
		setContentView(R.layout.common_ui);

		GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
		gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

		gvrView.setRenderer(this);
		gvrView.setTransitionViewEnabled(true);
		gvrView.setAlignmentMarkerEnabled(true);
		gvrView.setDistortionCorrectionEnabled(true);
		gvrView.setLowLatencyModeEnabled(true);
		gvrView.setScanlineRacingEnabled(true);
		gvrView.setOnCardboardBackButtonListener(
				new Runnable() {
					@Override
					public void run() {
						onBackPressed();
					}
				});
		setGvrView(gvrView);
	}

	@Override
	public void onRendererShutdown() {
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void onSurfaceCreated(EGLConfig config) {
		renderer3d.reset();
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		sceneController.setHeadTransform(headTransform);
		sceneController.updateScene(width, height);

		// TODO Workaround for a R SDK bug, distortion correction disables GL_DEPTH_TEST
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	@Override
	public void onDrawEye(Eye eye) {
		Scene scene = sceneController.getScene();

		scene.camera.updateMatrices();
		MatrixUtils.multiply(eye.getPerspective(scene.camera.getNear(), scene.camera.getFar()), eye.getEyeView(), scene.camera.perspectiveMatrix);
		MatrixUtils.multiply(scene.camera.perspectiveMatrix, scene.camera.modelViewMatrix, scene.camera.perspectiveMatrix);

		renderer3d.render(scene);
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}
}