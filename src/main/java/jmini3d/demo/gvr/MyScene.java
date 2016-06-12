package jmini3d.demo.gvr;

import android.content.Context;

import jmini3d.Color4;
import jmini3d.CubeMapTexture;
import jmini3d.Object3d;
import jmini3d.Scene;
import jmini3d.Vector3;
import jmini3d.android.loader.ObjLoader;
import jmini3d.geometry.Geometry;
import jmini3d.geometry.SkyboxGeometry;
import jmini3d.geometry.VariableGeometry;
import jmini3d.light.AmbientLight;
import jmini3d.light.PointLight;
import jmini3d.material.Material;
import jmini3d.material.PhongMaterial;

public class MyScene extends Scene {

	Object3d o3d;
	float angle = 0;

	public MyScene(Context ctx) {
		// For VR the camera target is always constant, the position can be changed
		camera.setPosition(0, 0, 0);
		camera.setTarget(0, 0, -1f);
		camera.setUpAxis(0, 1, 0);

		// The cube map texture follows the JMini3D standards
		CubeMapTexture envMap = new CubeMapTexture(new String[]{"posx.png", "negx.png", "posy.png", "negy.png", "posz.png", "negz.png"});

		VariableGeometry skyboxGeometry = new SkyboxGeometry(300);
		Material skyboxMaterial = new Material();
		skyboxMaterial.setEnvMap(envMap, 0);
		skyboxMaterial.setUseEnvMapAsMap(true);
		Object3d skybox = new Object3d(skyboxGeometry, skyboxMaterial);
		addChild(skybox);

		addLight(new AmbientLight(new Color4(255, 255, 255), 0.1f));
		addLight(new PointLight(new Vector3(0, 0, 0), new Color4(255, 255, 255), 1.1f));

		Color4 white = new Color4(255, 255, 255, 255);
		PhongMaterial mirrorMat = new PhongMaterial(white, white, white);
		mirrorMat.setEnvMap(envMap, 1f);

		// Load an OBJ model
		ObjLoader loader = new ObjLoader();
		try {
			VariableGeometry myGeometry = loader.load(ctx.getAssets().open("monkey.obj"));
			o3d = new Object3d(myGeometry, mirrorMat);
			o3d.setPosition(0, 0, -5);
			addChild(o3d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		// Rotate the object
		angle += 0.01;
		o3d.setRotationMatrix(new Vector3(1f * (float) Math.cos(angle), 0, -1 * (float) Math.sin(angle)),
				new Vector3(0, 1, 0),
				new Vector3(1 * (float) Math.sin(angle), 0, 1 * (float) Math.cos(angle)));
	}
}
