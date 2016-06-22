package jmini3d.demo.gvr.scenes;

import jmini3d.HudScene;
import jmini3d.Object3d;
import jmini3d.Texture;
import jmini3d.geometry.SpriteGeometry;
import jmini3d.material.SpriteMaterial;

public class CrossHudScene extends HudScene {

	Object3d crossObject;

	public CrossHudScene() {
		super();

		// Add a cross to the HUD
		SpriteGeometry crossGeometry = new SpriteGeometry(1);
		crossGeometry.addSprite(0, 0, 0, 0); // We do not know yet the screen dimensions, it is adjusted in onViewPortChanged
		crossObject = new Object3d(crossGeometry, new SpriteMaterial(new Texture("cross.png")));
		addChild(crossObject);
	}

	@Override
	public void onViewPortChanged(int width, int height) {
		// Set the HUD components position
		int crossSize2 = height / 20;
		((SpriteGeometry) crossObject.geometry3d).setSpritePositionSize(0, (float) (width / 2.0 - crossSize2), (float) (height / 2.0 - crossSize2), crossSize2 * 2, crossSize2 * 2);
	}

}
