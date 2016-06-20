package jmini3d.demo.gvr;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.HeadTransform;

import java.util.ArrayList;

import jmini3d.Renderer3d;
import jmini3d.Scene;
import jmini3d.Vector3;

/**
 * The scene controller selects the scene to show
 */
public class MyScreenController implements VRScreenController {

	VREyeRender eyeRender = new VREyeRender();

	ArrayList<Scene> scenes = new ArrayList<>();

	float[] forward = new float[3];
	Vector3 forwardVector = new Vector3();
	long lastTimeStamp = -1;
	int sceneIndex = 0;

	VRTextureButton buttonLeft, buttonRight;
	CrossHudScene sceneHUD;

	public MyScreenController(GvrActivity ctx) {
		buttonLeft = new VRTextureButton(2, 2, "arrow_left.png", new VRTextureButton.VRClickListener() {
			@Override
			public void onClickListener() {
				sceneIndex--;
				if (sceneIndex < 0) {
					sceneIndex = scenes.size() - 1;
				}
			}
		});
		buttonLeft.setPosition(-3, -3, -10);
		buttonRight = new VRTextureButton(2, 2, "arrow_right.png", new VRTextureButton.VRClickListener() {
			@Override
			public void onClickListener() {
				sceneIndex++;
				if (sceneIndex >= scenes.size()) {
					sceneIndex = 0;
				}
			}
		});
		buttonRight.setPosition(3, -3, -10);

		// The distortion correction can be done at the vertex level adding a Shader Plugin to the scene
		// An disabling the SDK correction in VRActivity
//		GvrView gvrView = ctx.getGvrView();
//		float coefs[] = PincushionUtils.approximateInverse(gvrView.getGvrViewerParams().getDistortion());
//		float maxRadius = PincushionUtils.getMaxRadius(gvrView.getScreenParams(), gvrView.getGvrViewerParams());
//		ShaderPlugin lensDistortionCorrection = new LensDistortion(coefs[0], coefs[1], coefs[2], coefs[3], coefs[4], coefs[5], maxRadius * maxRadius);

		Scene scene = new ObjLoaderScene(ctx);
		scene.addChild(buttonLeft.object3d);
		scene.addChild(buttonRight.object3d);
//		scene.addShaderPlugin(lensDistortionCorrection);
		scenes.add(scene);

		scene = new CubesScene(ctx);
		scene.addChild(buttonLeft.object3d);
		scene.addChild(buttonRight.object3d);
//		scene.addShaderPlugin(lensDistortionCorrection);
		scenes.add(scene);

		sceneHUD = new CrossHudScene();
	}

	@Override
	public boolean onNewFrame(HeadTransform headTransform, boolean forceRedraw) {
		scenes.get(sceneIndex).camera.updateViewMatrix();

		headTransform.getForwardVector(forward, 0);
		forwardVector.setAll(forward, 0);

		// Check if we are looking to one of the two buttons
		buttonLeft.checkIsLooking(scenes.get(sceneIndex).camera.getPosition(), forwardVector);
		buttonRight.checkIsLooking(scenes.get(sceneIndex).camera.getPosition(), forwardVector);

		// Animations
		long currentTime = System.currentTimeMillis();
		long timeElapsed = lastTimeStamp == -1 ? 0 : currentTime - lastTimeStamp;
		lastTimeStamp = currentTime;

		// First animate the buttons (it can also generate an scene change)
		buttonLeft.animate(timeElapsed);
		buttonRight.animate(timeElapsed);

		// Now update the shown scene
		if (scenes.get(sceneIndex) instanceof ObjLoaderScene) {
			((ObjLoaderScene) scenes.get(sceneIndex)).update(timeElapsed);
		}

		return true;
	}

	@Override
	public void render(Eye eye, Renderer3d renderer3d) {
		eyeRender.render(scenes.get(sceneIndex), eye, renderer3d);
		renderer3d.render(sceneHUD);
	}

}