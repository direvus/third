#include "colors.inc"

camera {
 location <4, 2, -13>
 look_at 0
 angle 20
}

light_source { <3, 8, -15> White }
background { Gray75 }

#declare frame_w = 0.03;
#declare frame_t = texture {
 pigment { color Red }
 finish { ambient .2 diffuse 0.8 specular 1 }
}

#declare body_t = texture { 
 pigment { color Red filter 0.6 }
}

#declare Planes = array[10];

#declare I = 0;
#declare J = 1;
#declare N = 10;
intersection {
#while (I < N)
 #local H = sqrt((cos(pi/N)-cos(2 * pi/N))/2) * 3;
 #declare Planes[I] = <sin(2 * pi * I/N), H * J, cos(2 * pi * I/N)>;

 plane { Planes[I], 1 texture { body_t } }

 #declare I = I + 1;
 #declare J = -J;
#end
}
