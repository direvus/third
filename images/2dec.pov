#include "colors.inc"

camera {
 location <4, 2, -13>
 look_at 0
 angle 25
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

#macro pentagonal_deltohedron()

 union {

  #declare Planes = array[10];

  #declare I = 0;
  #declare J = 1;
  #declare N = 10;
  intersection {
   #while (I < N)
    #local H = sqrt((cos(pi/N)-cos(2 * pi/N))/2) * 3;
    #declare Planes[I] = <sin(2 * pi * I/N), H * J, cos(2 * pi * I/N)>;

    plane { Planes[I], 1 / vlength(Planes[I])  }

    #declare I = I + 1;
    #declare J = -J;
   #end
   texture { body_t }
  }

  #declare Points = array[10];

  #declare P = vnormalize(vcross(Planes[2] - Planes[0], Planes[0] - Planes[4]));
  #declare Top = P / vdot(P, Planes[0]);

  #declare P = vnormalize(vcross(Planes[3] - Planes[1], Planes[1] - Planes[5]));
  #declare Bottom = P / vdot(P, Planes[1]);

  union {
   #declare I = 0;
   #while (I < N)
    #declare J = (I = 0 ? N - 1 : I - 1);
    #declare K = (I = (N - 1) ? 0 : I + 1);

    #local P = vnormalize(vcross(Planes[J] - Planes[I], Planes[I] - Planes[K]));
    #declare Points[I] = P / vdot(P, Planes[I]);

    sphere { Points[I], frame_w }
    cylinder { Points[I], (mod(I, 2) = 0 ? Bottom : Top), frame_w }
    #if (I > 0) cylinder { Points[I], Points[J], frame_w } #end

    #declare I = I + 1;
   #end

   cylinder { Points[0], Points[N - 1], frame_w }

   sphere { Top, frame_w }
   sphere { Bottom, frame_w }

   texture { frame_t }
  }
 }
#end

object {
 pentagonal_deltohedron()
 translate <1, .7, 0>
}

object {
 pentagonal_deltohedron()
 rotate 180 * x
 translate <-1, -.7, 0>
}
