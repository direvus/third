#include "colors.inc"

camera {
 location <0, 0, -10>
 look_at 0
 angle 19
}

light_source { <3, -7, -20> White }

#declare frame_w = 0.03;

union {
 union {

  cylinder { -z, -x, frame_w }
  cylinder { -x, z, frame_w }
  cylinder { z, x, frame_w }
  cylinder { x, -z, frame_w }

  cylinder { -z, y, frame_w }
  cylinder { -x, y, frame_w }
  cylinder { z, y, frame_w }
  cylinder { x, y, frame_w }

  cylinder { -z, -y, frame_w }
  cylinder { -x, -y, frame_w }
  cylinder { z, -y, frame_w }
  cylinder { x, -y, frame_w }

  sphere { -z, frame_w }
  sphere { z, frame_w }
  sphere { -x, frame_w }
  sphere { x, frame_w }
  sphere { -y, frame_w }
  sphere { y, frame_w }

  texture {
   pigment { color Red }
   finish { ambient .2 diffuse 0.8 specular 1 }
  }
 }

 union {

  triangle { y, x, z }
  triangle { y, z, -x }
  triangle { y, -x, -z }
  triangle { y, -z, x }
  triangle { -y, x, z }
  triangle { -y, z, -x }
  triangle { -y, -x, -z }
  triangle { -y, -z, x }

  texture { 
   pigment { color Red filter 0.6 }
  }
 }

 rotate <10, 30, 0>
 rotate y * clock * 360
}
