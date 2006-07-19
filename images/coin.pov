#include "colors.inc"

camera {
 location <0, 0, -20>
 look_at 0
 angle 15
}

light_source { <3, 15, -20> White }
background { Gray75 }

#declare edge = torus { 
 1, 0.03
 rotate z * -90
 texture {
  pigment { color Red }
  finish { ambient .2 diffuse 0.8 specular 1 }
 }
}

union {
 cylinder { 
  <-.05, 0, 0>, <.05, 0, 0>, 1 
  texture { 
   pigment { color Red filter 0.6 }
  }
 }

 torus { edge translate x * .05 }
 torus { edge translate x * -.05 }

 rotate y * 30
 rotate z * 20
 rotate y * clock * 360
}
