#include "colors.inc"

camera {
 location <0, 0, -10>
 look_at 0
 angle 18
}

light_source { <3, 9, -14> White }

#declare edge = torus { 
 1, 0.03
 rotate z * -90
 texture {
  pigment { color BrightGold }
  finish { ambient .2 diffuse 0.8 specular 1 }
 }
}

union {
 cylinder { 
  <-.05, 0, 0>, <.05, 0, 0>, 1 
  texture { 
   pigment { color BrightGold filter 0.6 }
  }
 }

 torus { edge translate x * .05 }
 torus { edge translate x * -.05 }

 rotate y * 30
 rotate z * 50
 rotate y * clock * 360
}
