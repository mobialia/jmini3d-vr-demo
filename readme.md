JMini3D VR Demo
===============
This is a simple Android VR (Virtual Reality) demo using:

* Google VR SDK for Android (https://developers.google.com/vr/android/)
* JMini3D: a 3D library for Android and GWT (https://github.com/albertoruibal/jmini3d)

![Jmini3D VR Demo](https://raw.githubusercontent.com/albertoruibal/jmini3d-vr-demo/master/img/demo.jpeg)

It includes 2 scenes with anmated 3D buttons to change between scenes.

To "click" a button you must look to it for one second.

Lens distortion
===============
This demo uses the integrated lens distortion in the GVR SDK but it can also do the lens distortion
correction at using vertex displacement (as proposed in https://www.youtube.com/watch?v=yJVkdsZc9YA)
calculated with the class PincushionUtils.

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