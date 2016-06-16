package jmini3d.demo.gvr;

import android.content.Context;

import java.util.Random;

import jmini3d.Color4;
import jmini3d.Object3d;
import jmini3d.Scene;
import jmini3d.Texture;
import jmini3d.Vector3;
import jmini3d.geometry.BoxGeometry;
import jmini3d.geometry.Geometry;
import jmini3d.geometry.SpriteGeometry;
import jmini3d.light.AmbientLight;
import jmini3d.light.PointLight;
import jmini3d.material.PhongMaterial;
import jmini3d.material.SpriteMaterial;

public class SceneCubes extends Scene {

	Object3d o3d;
	float angle = 0;

	Object3d crossObject;

	public SceneCubes(Context ctx) {
		// For VR the camera target is always constant, the position can be changed
		camera.setPosition(0, 0, 0);
		camera.setTarget(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z - 1f);
		camera.setUpAxis(0, 1, 0);

		Random r = new Random();

		addLight(new AmbientLight(new Color4(255, 255, 255), 0f));
		addLight(new PointLight(new Vector3(0, 0, 0), new Color4(255, 255, 255), 1.1f));

		Color4 ambient = new Color4(255, 255, 255, 255);
		Color4 red = new Color4(255, 0, 0, 255);
		Color4 green = new Color4(0, 255, 0, 255);
		Color4 blue = new Color4(0, 0, 255, 255);
		PhongMaterial material1 = new PhongMaterial(ambient, red, red);
		PhongMaterial material2 = new PhongMaterial(ambient, green, green);
		PhongMaterial material3 = new PhongMaterial(ambient, blue, blue);

		Geometry geometry = new BoxGeometry(1.5f);

		for (int i = 1; i < 6; i++) {
			for (int j = 1; j < 6; j++) {
				float x = (i - 3) * 4;
				float y = (j - 3) * 4;
				float z = -25;
				Object3d o3d;
				if ((i + j) % 2 == 0) {
					o3d = new Object3d(geometry, material1);
				} else {
					o3d = new Object3d(geometry, material2);
				}
				o3d.setPosition(x, y, z);
				addChild(o3d);
			}
		}

		// Add a cross to the HUD
		SpriteGeometry crossGeometry = new SpriteGeometry(1);
		crossGeometry.addSprite(0, 0, 0, 0); // We do not know yet the screen dimensions, it is adjusted in onViewPortChanged
		crossObject = new Object3d(crossGeometry, new SpriteMaterial(new Texture("cross.png")));
		addHudElement(crossObject);
	}

	@Override
	public void onViewPortChanged(int width, int height) {
		// Set the HUD components position
		int crossSize2 = height / 20;
		((SpriteGeometry) crossObject.geometry3d).setSpritePositionSize(0, (float) (width / 2.0 - crossSize2), (float) (height / 2.0 - crossSize2), crossSize2 * 2, crossSize2 * 2);
	}

	public void update(long timeElapsed) {
		// Rotate the object
		angle += 0.001 * timeElapsed;
		o3d.setRotationMatrix(new Vector3(1f * (float) Math.cos(angle), 0, -1 * (float) Math.sin(angle)),
				new Vector3(0, 1, 0),
				new Vector3(1 * (float) Math.sin(angle), 0, 1 * (float) Math.cos(angle)));
	}
}
