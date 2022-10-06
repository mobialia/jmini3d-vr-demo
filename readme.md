JMini3d VR Demo
===============
This is a simple Android Virtual Reality (VR) demo for Google Cardboard using:

* Google VR SDK for Android (https://github.com/googlevr/gvr-android-sdk) DEPRECATED
* JMini3d: a 3d library for Android and GWT (https://github.com/mobialia/jmini3d)

![Jmini3d VR Demo](https://raw.githubusercontent.com/mobialia/jmini3d-vr-demo/master/img/demo.jpeg)

It includes 2 scenes with animated 3D buttons to change between scenes.

To "click" a button you must look to it for one second.

Lens distortion
===============
This demo uses the integrated lens distortion in the GVR SDK but it can also do the lens distortion
correction using vertex displacement (as proposed in https://www.youtube.com/watch?v=yJVkdsZc9YA).
This is faster, but it can cause artifacts in some scenes. 

You can enable it setting:
```
public final static boolean USE_VERTEX_DISPLACEMENT_LENS_DISTORTION = true;
```
in the VRActivity. The auxiliar code is in the class PincushionUtils. 


Axis
====
This is the axis system in the Google VR SDK; -Z is front (the OpenGl standard):

```
  y
  |  
  |
  |------x
 /
/
Z
```

Credits
=======
The Monkey model was created with Blender.

The sky box used in de demo "mp_velcor" was created by The Mighty Pete and downloaded from http://www.custommapmakers.org/skyboxes.php.