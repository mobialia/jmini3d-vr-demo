package jmini3d.demo.gvr;

import android.content.Context;

import jmini3d.Scene;
import jmini3d.SceneController;

/**
 * A scene controller can change between two or more scenes
 */
public class MySceneController implements SceneController {

	MyScene scene;

	public MySceneController(Context ctx) {
		scene = new MyScene(ctx);
	}

	@Override
	public Scene getScene() {
		return scene; // Only one scene
	}

	@Override
	public boolean updateScene(int width, int height) {
		scene.setViewPort(width, height); // Mandatory

		// Now update the shown scene
		scene.update();
		return true;
	}
}
