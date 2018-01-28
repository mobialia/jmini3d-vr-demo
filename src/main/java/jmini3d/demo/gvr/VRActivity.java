package jmini3d.demo.gvr;

import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;

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

	// stats-related
	public static final int FRAMERATE_SAMPLEINTERVAL_MS = 5000;
	private boolean logFps = false;
	private long frameCount = 0;
	private float fps = 0;
	private long timeLastSample;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Use the standard OpenGL Axis system
		JMini3d.useOpenglAxisSystem();

		initializeGvrView();

		renderer3d = new Renderer3d(new ResourceLoader(this));
		screenController = new MyScreenController(this);
		setLogFps(true);
	}

	public void initializeGvrView() {
		setContentView(R.layout.common_ui);

		GvrView gvrView = findViewById(R.id.gvr_view);
		gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

		gvrView.setRenderer(this);
		gvrView.setTransitionViewEnabled(true);
		gvrView.setNeckModelEnabled(true);
		gvrView.setDistortionCorrectionEnabled(!USE_VERTEX_DISPLACEMENT_LENS_DISTORTION);
		gvrView.setOnCardboardBackListener(
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

		if (logFps) {
			doFps();
		}
	}

	@Override
	public void onDrawEye(Eye eye) {
		screenController.render(eye, renderer3d);
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}

	/**
	 * If true, framerate and memory is periodically calculated and Log'ed, and
	 * gettable thru getFps()
	 */
	public void setLogFps(boolean b) {
		logFps = b;

		if (logFps) { // init
			timeLastSample = System.currentTimeMillis();
			frameCount = 0;
		}
	}

	private void doFps() {
		frameCount++;

		long now = System.currentTimeMillis();
		long delta = now - timeLastSample;
		if (delta >= FRAMERATE_SAMPLEINTERVAL_MS) {
			fps = frameCount / (delta / 1000f);

			Log.v(TAG, "FPS: " + fps);

			timeLastSample = now;
			frameCount = 0;
		}
	}

	/**
	 * Returns last sampled framerate (logFps must be set to true)
	 */
	public float getFps() {
		return fps;
	}
}