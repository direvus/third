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

#declare Phi = (1 + sqrt(5)) / 2;
#declare PhiInv = 1 / Phi;
#declare Edge = -1 + sqrt(5);
#declare N = 20;

#declare Points = array[N] {
 <1, 1, 1>,
 <1, 1, -1>,
 <1, -1, 1>,
 <1, -1, -1>,
 <-1, 1, 1>,
 <-1, 1, -1>,
 <-1, -1, 1>,
 <-1, -1, -1>,
 <0, PhiInv, Phi>,
 <0, PhiInv, -Phi>,
 <0, -PhiInv, Phi>,
 <0, -PhiInv, -Phi>,
 <PhiInv, Phi, 0>,
 <PhiInv, -Phi, 0>,
 <-PhiInv, Phi, 0>,
 <-PhiInv, -Phi, 0>,
 <Phi, 0, PhiInv>,
 <Phi, 0, -PhiInv>,
 <-Phi, 0, PhiInv>,
 <-Phi, 0, -PhiInv>
}

#declare Planes = array[12];
#declare PlanesFound = 0;

union {
 #declare I = 0;
 #while (I < N)

  sphere { Points[I], frame_w }

  #local J = 0;
  #while (J < I)
   #if (vlength(Points[I] - Points[J]) = Edge)
    cylinder { Points[I], Points[J], frame_w }

    #local K = J + 1;
    #while (K < N)

     #if ((PlanesFound < 12) & (K != I) & (vlength(Points[K] - Points[I]) = Edge))

      #local P = vnormalize(vcross(Points[J] - Points[I], Points[K] - Points[I]));
      #local Plane = P / vdot(P, Points[I]);

      #local L = 0;
      #local Unique = 1;
      #while (L < PlanesFound)

       #if (vlength(Planes[L] - Plane) = 0) #local Unique = 0; #end
       #local L = L + 1;
      #end

      #if (Unique)

       #declare Planes[PlanesFound] = Plane;
       #declare PlanesFound = PlanesFound + 1;
      #end
     #end
     #local K = K + 1;
    #end

   #end
   #local J  = J + 1;
  #end

  #declare I = I + 1;
 #end

 texture { frame_t }
}

intersection {

 #declare I = 0;
 #while (I < PlanesFound)

  plane { Planes[I], 1 / vlength(Planes[I]) }
  #declare I = I + 1;
 #end

 texture { body_t }
}
