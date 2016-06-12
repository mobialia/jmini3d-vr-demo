/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jmini3d.demo.gvr;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Vibrator;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

import jmini3d.MatrixUtils;
import jmini3d.Scene;
import jmini3d.SceneController;
import jmini3d.android.Renderer3d;
import jmini3d.android.ResourceLoader;

public class JMini3dActivity extends GvrActivity implements GvrView.StereoRenderer {
	private static final String TAG = "JMini3dActivity";

	SceneController sceneController;
	Renderer3d renderer3d;
	MyScene scene;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeGvrView();

		renderer3d = new Renderer3d(new ResourceLoader(this));
		scene = new MyScene(this);
		// For VR the camera target is always constant, the position can be changed
		scene.camera.setPosition(0, 0, 0);
		scene.camera.setTarget(0, 0, -1f);
		scene.camera.setUpAxis(0, 1, 0);
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
		scene.setViewPort(width, height);
		scene.camera.updateMatrices();
	}

	@Override
	public void onSurfaceCreated(EGLConfig config) {
		renderer3d.reset();
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		scene.update();

		// TODO Workaround for a R SDK bug, distortion correction disables GL_DEPTH_TEST
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	@Override
	public void onDrawEye(Eye eye) {
		scene.camera.updateMatrices();
		MatrixUtils.multiply(eye.getPerspective(scene.camera.getNear(), scene.camera.getFar()), eye.getEyeView(), scene.camera.perspectiveMatrix);
		MatrixUtils.multiply(scene.camera.perspectiveMatrix, scene.camera.modelViewMatrix, scene.camera.perspectiveMatrix);

		renderer3d.render(scene);
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}
}