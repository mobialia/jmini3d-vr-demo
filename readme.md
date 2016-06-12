JMini3D VR demo
===============

This is a simple Android VR (Virtual Reality) demo using:

* Google VR SDK for Android (https://developers.google.com/vr/android/)
* JMini3D: a 3D library for Android and GWT (https://github.com/albertoruibal/jmini3d)

![Jmini3D VR Demo](https://raw.githubusercontent.com/albertoruibal/jmini3d-vr-demo/master/img/demo.jpeg)

It is only a spinning monkey head, but it is a good base for other VR projects.

Axis
====
This is the axis system in the Google VR SDK; -Z is front:

```
  y
  |  
  |
  |------x
 /
/
Z
```
But the envmap textures follows the JMini3D convention (Z Up, Y front).

Credits
=======
The Monkey model was created with Blender.

The sky box used in de demo "hw_apls" was created by Hazel Whorley and downloaded from http://www.custommapmakers.org/skyboxes.php.