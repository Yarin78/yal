

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
   #include <math.h>
   #include <iostream.h>
   #include <fstream.h>
   #include <vector.h>
#else
   #include <cstdlib>
   #include <cmath>
   #include <iostream>
   #include <fstream>
   #include <vector>
#endif

   #include "miniball.H"

   
   template <int d>
       class Dim {};
   template <int d, int D>
       class Dim2 {};
   
   
   
   template <int d, int D, class Point> // embedding from d-space into D-space
   void basic_test (const std::vector<Point>& P,
                    Dim2<d,D> dim,      // enables the compiler to deduce d,D
                    double amount = 0,  // absolute amount of perturbation
                    int copies = 1)     // number of copies of each point
   {
   #ifndef MINIBALL_NO_STD_NAMESPACE
           using std::cout;
   #endif
   
           Miniball<D> mb;
           typedef typename Miniball<D>::Point PointD;
           PointD p;
           for (int i=0; i<P.size(); ++i) {
               for (int k=0; k<copies; ++k) {
   
                   // first d coordinates are taken from P
                   int j;
                   for (j=0; j<d; ++j)
                       p[j] = P[i][j] + (random_double()-0.5)*amount;
   
                   // last D-d coordinates are assumed to be zero
                   for (j=d; j<D; ++j)
                       p[j] = (random_double()-0.5)*amount;
                   mb.check_in(p);
               }
           }
   
           // construction
           mb.build();
   
           // validity check
           double slack, accuracy = mb.accuracy(slack);
           char s;
           if (slack > 0)
               s = '#';
           else {
               if (accuracy > 1e-15) s = '*'; else s = ' ';
           }
           cout << s << "(" << accuracy << ", " << slack << ")" << s << " ";
        }
   
   
   
   template <int d, int D, class Point>
   void test_set (const std::vector<Point>& P,
                  Dim2<d,D> dim, double magnitude)
   {
   #ifndef MINIBALL_NO_STD_NAMESPACE
           using std::cout;
           using std::endl;
   #endif
   
           double    tiny =         magnitude*(1e-50),
                     footnotesize = magnitude*(1e-30),
                     small =        magnitude*(1e-10),
                     large =        magnitude*(1e-3);
   
           for (int tries=0; tries<3; ++tries) {
               cout << " Try: " << tries << endl;
   
               cout << "  Set (d):      ";
               basic_test (P, Dim2<d,d>(), 0);
               basic_test (P, Dim2<d,d>(), tiny);
               basic_test (P, Dim2<d,d>(), footnotesize);
               basic_test (P, Dim2<d,d>(), small);
               basic_test (P, Dim2<d,d>(), large);
               cout << endl;
   
               cout << "  Multiset (d): ";
               basic_test (P, Dim2<d,d>(), 0, 10);
               basic_test (P, Dim2<d,d>(), tiny, 10);
               basic_test (P, Dim2<d,d>(), footnotesize, 10);
               basic_test (P, Dim2<d,d>(), small, 10);
               basic_test (P, Dim2<d,d>(), large, 10);
               cout << endl;
   
               cout << "  Set (D):      ";
               basic_test (P, Dim2<d,D>(), 0);
               basic_test (P, Dim2<d,D>(), tiny);
               basic_test (P, Dim2<d,D>(), footnotesize);
               basic_test (P, Dim2<d,D>(), small);
               basic_test (P, Dim2<d,D>(), large);
               cout << endl;
   
               cout << "  Multiset (D): ";
               basic_test (P, Dim2<d,D>(), 0, 10);
               basic_test (P, Dim2<d,D>(), tiny, 10);
               basic_test (P, Dim2<d,D>(), footnotesize, 10);
               basic_test (P, Dim2<d,D>(), small, 10);
               basic_test (P, Dim2<d,D>(), large, 10);
               cout << endl;
           }
           cout << endl;
       }
   
   
   
   template <int d>
   void test (char* file, Dim<d> dim, double magnitude)
   {
   #ifndef MINIBALL_NO_STD_NAMESPACE
           using std::cout;
           using std::endl;
           using std::ifstream;
   #endif
   
           typedef typename Miniball<d>::Point Point;
   
           Point                   p;
           std::vector<Point>      P;
   
           cout << file << "...";
           ifstream from (file);
           int n; from >> n;
           for (int i=0; i<n; ++i) {
               for (int j=0; j<d; ++j)
                   from >> p[j];
               P.push_back(p);
           }
           cout << endl;
   
           test_set (P, Dim2<d, 2*d>(), magnitude);
       }
   
   
   
   int main (int argc, char* argv[])
   {
   #ifndef MINIBALL_NO_STD_NAMESPACE
           using std::cout;
           using std::endl;
           // using std::atoi; commented out because Visual C++ doesn't like it
   #endif
           if (argc != 2) {
               cout << "Usage: test_suite <seed>" << endl;
               exit(1);
           } else
               random_seed (atoi(argv[1]));
   
           cout << "Miniball testsuite" << endl
                << "------------------" << endl
                << "Note: Results in asterisks *(...)* are usually of lower"<< endl
                << "      accuracy, while results in hash marks #(...)# may"<< endl
                << "      really be wrong. Please consult the documentation"<< endl
                << "      for details." << endl
                << "-------------------------------------------------------"<< endl
                << "Running testsuite (this may take a while)..." << endl << endl;
   
           cout << "columns: input perturbations 0 | 1e-50 | 1e-30 | 1e-10 | 1e-3"
                << endl << endl;
   
           test ("cocircular_points_small_radius_2.data", Dim<2>(), 1e8);
           test ("cocircular_points_large_radius_2.data", Dim<2>(), 1e9);
   
           test ("almost_cospherical_points_3.data", Dim<3>(),1);
           test ("almost_cospherical_points_10.data", Dim<10>(),1);
   
           test ("longitude_latitude_model_3.data", Dim<3>(),1);
   
           test ("random_points_3.data", Dim<3>(),1);
           test ("random_points_5.data", Dim<5>(),1);
           test ("random_points_10.data", Dim<10>(),1);
   
           test ("simplex_10.data", Dim<10>(),1);
           test ("simplex_15.data", Dim<15>(),1);
   
           test ("cube_10.data", Dim<10>(),1);
           test ("cube_12.data", Dim<12>(),1);
   
           return 0;
   
       }
   
   

