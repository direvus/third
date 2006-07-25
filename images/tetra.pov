#include "colors.inc"

camera {
 location <0, 0, -12>
 look_at 0
 angle 18
}

light_source { <3, 15, -15> White }
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
   pigment { color LightSteelBlue }
   finish { ambient .2 diffuse 0.8 specular 1 }
  }
 }

 union {
  triangle { <1, 1, 1>, <-1, -1, 1>, <-1, 1, -1> }
  triangle { <1, 1, 1>, <-1, -1, 1>, <1, -1, -1> }
  triangle { <1, 1, 1>, <-1, 1, -1>, <1, -1, -1> }
  triangle { <-1, -1, 1>, <-1, 1, -1>, <1, -1, -1> }
  texture { 
   pigment { color LightSteelBlue filter 0.8 }
  }
 }
 rotate z * 30
 rotate y * 25
 rotate y * clock * 360
}
