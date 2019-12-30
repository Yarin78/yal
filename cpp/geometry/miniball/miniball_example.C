

//    Copright (C) 1999
//    $Revision: 1.4 $
//    $Date: 1999/07/19 14:09:21 $
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA,
//    or download the License terms from prep.ai.mit.edu/pub/gnu/COPYING-2.0.
//
//    Contact:
//    --------
//    Bernd Gaertner
//    Institut f. Informatik
//    ETH Zuerich
//    ETH-Zentrum
//    CH-8092 Zuerich, Switzerland
//    http://www.inf.ethz.ch/personal/gaertner
//


   #include "miniball_config.H"
#ifdef MINIBALL_NO_STD_NAMESPACE
   #include <stdlib.h>
   #include <iostream.h>
#else
   #include <cstdlib>
   #include <iostream>
#endif

   #include "miniball.H"
   
   int main ()
   {
   #ifndef MINIBALL_NO_STD_NAMESPACE
           using std::cout;
           using std::endl;
   #endif
   
           const int       d = 5;
           const int       n = 100000;
           Miniball<d>     mb;
   
           // generate random points and check them in
           // ----------------------------------------
           Miniball<d>::Point p;
           random_seed (1999);
           for (int i=0; i<n; ++i) {
               for (int j=0; j<d; ++j)
                   p[j] = random_double();
               mb.check_in(p);
           }
   
           // construct ball, using the pivoting method
           // -----------------------------------------
           cout << "Constructing miniball..."; cout.flush();
           mb.build();
           cout << "done." << endl << endl;
   
           // output center and squared radius
           // --------------------------------
           cout << "Center:         " << mb.center() << endl;
           cout << "Squared radius: " << mb.squared_radius() << endl << endl;
   
           // output number of support points
           // -------------------------------
           cout << mb.nr_support_points() << " support points: " << endl << endl;
   
           // output support points
           // ---------------------
           Miniball<d>::Cit it;
           for (it=mb.support_points_begin(); it!=mb.support_points_end(); ++it)
               cout << *it << endl;
           cout << endl;
   
           // output accuracy
           // ---------------
           double slack;
           cout << "Relative accuracy: " << mb.accuracy (slack) << endl;
           cout << "Optimality slack:  " << slack << endl;
   
           return 0;
       }
   
   

