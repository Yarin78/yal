

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
   #include <iostream.h>
#else
   #include <iostream>
#endif

   
   template <int d>
   class Wrapped_array {
       private:
           double coord [d];
   
       public:
           // default
           Wrapped_array()
           {}
   
           // copy from Wrapped_array
           Wrapped_array (const Wrapped_array& p)
           {
               for (int i=0; i<d; ++i)
                   coord[i] = p.coord[i];
           }
   
           // copy from double*
           Wrapped_array (const double* p)
           {
               for (int i=0; i<d; ++i)
                   coord[i] = p[i];
           }
   
           // assignment
           Wrapped_array& operator = (const Wrapped_array& p)
           {
               for (int i=0; i<d; ++i)
                   coord[i] = p.coord[i];
               return *this;
           }
   
           // coordinate access
           double& operator [] (int i)
           {
               return coord[i];
           }
           const double& operator [] (int i) const
           {
               return coord[i];
           }
           const double* begin() const
           {
               return coord;
           }
           const double* end() const
           {
               return coord+d;
           }
   };
   
   // Output
   
   #ifndef MINIBALL_NO_STD_NAMESPACE
       template <int d>
       std::ostream& operator << (std::ostream& os, const Wrapped_array<d>& p)
       {
           os << "(";
           for (int i=0; i<d-1; ++i)
               os << p[i] << ", ";
           os << p[d-1] << ")";
           return os;
       }
   #else
       template <int d>
       ostream& operator << (ostream& os, const Wrapped_array<d>& p)
       {
           os << "(";
           for (int i=0; i<d-1; ++i)
               os << p[i] << ", ";
           os << p[d-1] << ")";
           return os;
       }
   #endif
   
   

