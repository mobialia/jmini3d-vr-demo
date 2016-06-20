package jmini3d.demo.gvr;

import android.opengl.GLES20;
import android.os.Bundle;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

import jmini3d.JMini3d;
import jmini3d.android.Renderer3d;
import jmini3d.android.ResourceLoader;

public class VRActivity extends GvrActivity implements GvrView.StereoRenderer {
	private static final String TAG = "VRActivity";

	public final static boolean USE_VERTEX_DISPLACEMENT_LENS_DISTORTION = false;

	VRScreenController screenController;
	Renderer3d renderer3d;
	int width, height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use the standard OpenGL Axis system
		JMini3d.useOpenglAxisSystem();

		initializeGvrView();

		renderer3d = new Renderer3d(new ResourceLoader(this));
		screenController = new MyScreenController(this);
	}

	public void initializeGvrView() {
		setContentView(R.layout.common_ui);

		GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
		gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

		gvrView.setRenderer(this);
		gvrView.setTransitionViewEnabled(true);
		gvrView.setAlignmentMarkerEnabled(true);
		gvrView.setDistortionCorrectionEnabled(!USE_VERTEX_DISPLACEMENT_LENS_DISTORTION);
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
	public void onSurfaceCreated(EGLConfig config) {
		renderer3d.reset();
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		this.width = width;
		this.height = height;
		renderer3d.setViewPort(width, height);
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		screenController.onNewFrame(headTransform, true);

		// TODO Workaround for a R SDK bug, distortion correction disables GL_DEPTH_TEST
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	@Override
	public void onDrawEye(Eye eye) {
		screenController.render(eye, renderer3d);
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}
}