#include "colors.inc"

camera {
 location <4, 2, -13>
 look_at 0
 angle 20
}

light_source { <-1, 6, -15> White }
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
#declare Edge = 2;
#declare NPt = 12;

#declare Points = array[NPt] {
 <0, 1, Phi>,
 <0, 1, -Phi>,
 <0, -1, Phi>,
 <0, -1, -Phi>,
 <1, Phi, 0>,
 <-1, Phi, 0>,
 <1, -Phi, 0>,
 <-1, -Phi, 0>,
 <Phi, 0, 1>,
 <Phi, 0, -1>,
 <-Phi, 0, 1>,
 <-Phi, 0, -1>,
}

#declare NPl = 20;
#declare Planes = array[NPl];
#declare PlanesFound = 0;

union {
 #declare I = 0;
 #while (I < NPt)

  sphere { Points[I], frame_w }

  #local J = 0;
  #while (J < I)
   #if (vlength(Points[I] - Points[J]) = Edge)
    cylinder { Points[I], Points[J], frame_w }

    #local K = J + 1;
    #while (K < NPt)

     #if ((PlanesFound < NPl) & (K != I) & (vlength(Points[K] - Points[I]) = Edge) & (vlength(Points[K] - Points[J]) = Edge))

      #debug concat("Found plane at ", str(I, 2, 0), ", ", str(J, 2, 0), ", ", str(K, 2, 0), "\n")
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
