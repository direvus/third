#include "colors.inc"

camera {
 location <0, 0, -20>
 look_at 0
 angle 15
}

light_source { <3, 15, -20> White }
background { Gray75 }

#declare frame_w = 0.03;

union {
 union {
  cylinder { <1, 1, 1>, <-1, -1, 1>, frame_w }
  cylinder { <1, 1, 1>, <-1, 1, -1>, frame_w }
  cylinder { <1, 1, 1>, <1, -1, -1>, frame_w }
  cylinder { <-1, -1, 1>, <1, -1, -1>, frame_w }
  cylinder { <-1, -1, 1>, <-1, 1, -1>, frame_w }
  cylinder { <1, -1, -1>, <-1, 1, -1>, frame_w }
  sphere { <1, 1, 1>, frame_w }
  sphere { <-1, -1, 1>, frame_w }
  sphere { <-1, 1, -1>, frame_w }
  sphere { <1, -1, -1>, frame_w }
  texture {
   pigment { color Red }
   finish { ambient .2 diffuse 0.8 specular 1 }
  }
 }

 union {
  triangle { <1, 1, 1>, <-1, -1, 1>, <-1, 1, -1> }
  triangle { <1, 1, 1>, <-1, -1, 1>, <1, -1, -1> }
  triangle { <1, 1, 1>, <-1, 1, -1>, <1, -1, -1> }
  triangle { <-1, -1, 1>, <-1, 1, -1>, <1, -1, -1> }
  texture { 
   pigment { color Red filter 0.6 }
  }
 }
 rotate <10, 30, 0>
 rotate y * clock * 360
}
