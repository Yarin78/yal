#include <cstdio>
#include <iostream>
#include <cmath>

// LINE library
//
// Includes the following functions:
//
// * Line segment intersection
// * Line intersection
// * Check if a point is on a line segment
// * Check if a point is on a line
// * Shortest distance between a point and a line segment
// * Shortest distance between a point and a line
// * Shortest distance between two line segments
// * Check if one line segment contains another line segment
// * Check if two line segments overlap each other
// * Vector transformation
// * Reflection

using namespace std;

//#include "..\rat.h"

inline bool isnull(int v) { return !v; }
inline bool isnull(double v) { return fabs(v)<1e-6; }

//typedef int T;         // Don't use if the actual intersection points are needed!!
typedef double T;      // Flexible, but might be inaccurate
//typedef RatNum<int> T; // Safe!


// A point -OR- a vector with some useful help functions
struct TPoint {
  T x,y;
  TPoint(T _x=0,T _y=0) { x=_x; y=_y; }
  TPoint operator-(TPoint rhs) { return TPoint(x-rhs.x,y-rhs.y); }
  TPoint operator+(TPoint rhs) { return TPoint(x+rhs.x,y+rhs.y); }
};

// Line segment with two endpoints -OR- infinite line with two arbitrary points
struct TLine {
  TPoint f,t;
  TLine(TPoint _f, TPoint _t) { f=_f; t=_t; }
};


// Inline help functions

inline T det(TPoint a, TPoint b) {  // Calculates the determinant of a vector
  return a.x*b.y-a.y*b.x;
}

inline T dot(TPoint a, TPoint b) {  // Calculates the dot product
  return a.x*b.x+a.y*b.y;
}

inline T normsq(TPoint p) {  // Calculates the norm (squared)
  return p.x*p.x+p.y*p.y;
}

inline T min(T a, T b) {
  return a<b?a:b;
}

// Returns TRUE if there exists exactly one point which lines on both
// line segments a and b and which is not an endpoint of a or b.
bool lineseg_intersect(TLine a, TLine b, TPoint *res=0) {
  TPoint vw0(b.f-a.f),v1(a.t-a.f),w1(b.f-b.t);
  T d0=det(v1,w1);
  if (isnull(d0)) // Parallel lines
    return false;
  T fa=det(vw0,w1);
  T fb=det(v1,vw0);
  if (d0<0) { d0=-d0; fa=-fa; fb=-fb; }  	
  if (fa>0 && fa<d0 && fb>0 && fb<d0) {
    if (res) {
      res->x=fa*v1.x/d0+a.f.x;
      res->y=fa*v1.y/d0+a.f.y;
    }
    return true;
  }
  return false;
}

// Returns TRUE if there exists exactly one point which lines on both
// (infinite) lines a and b. Returns FALSE if lines are parallel.
bool line_intersect(TLine a, TLine b, TPoint *res=0) {
  TPoint vw0(b.f-a.f),v1(a.t-a.f),w1(b.f-b.t);
  T d0=det(v1,w1);
  if (isnull(d0)) // Parallel lines
    return false;
  T fa=det(vw0,w1);
  T fb=det(v1,vw0);
  if (res) {
    res->x=fa*v1.x/d0+a.f.x;
    res->y=fa*v1.y/d0+a.f.y;
  }
  return true;
}

// Returns TRUE if the point b lies on the line segment a (including endpoints)
bool on_lineseg(TLine a, TPoint b)
{
  TPoint v1=a.f-b,v2=b-a.t;
  T d0=dot(v1,v2);
  return isnull(det(v1,v2)) && (isnull(d0) || (d0>0));
}

// Returns TRUE if the point b lies anywhere on the (infinite) line a
bool on_line(TLine a, TPoint b) {
  return isnull(det(a.f-b,b-a.t));
}

// Returns the distance between a point and a line segment
double pdist_lineseg(TLine a, TPoint b, TPoint *res=0)
{
  TPoint v=a.t-a.f,u=b-a.f;
  T d0=dot(v,u)/normsq(v);
  if (d0>0 && d0<1) {
    TPoint w(v.x*d0,v.y*d0);
    if (res)
      *res=w+a.f;
    return sqrt(normsq(u-w));
  }
  double fdist=normsq(a.f-b),tdist=normsq(a.t-b);
  if (res)
    *res=fdist<tdist?a.f:a.t;
  return sqrt(min(fdist,tdist));
}

// Returns the distance between a point and a line
double pdist_line(TLine a, TPoint b, TPoint *res=0)
{
  TPoint v=a.t-a.f,u=b-a.f;
  T d0=dot(v,u)/normsq(v);
  TPoint w(v.x*d0,v.y*d0);
  if (res)
    *res=w+a.f;
  return sqrt(normsq(u-w));
}

// Returns the distance between two line segments
T lineseg_dist(TLine a, TLine b)
{
  if (lineseg_intersect(a,b)) return T(0);
  T d1=pdist_lineseg(a,b.f);
  T d2=pdist_lineseg(a,b.t);
  T d3=pdist_lineseg(b,a.f);
  T d4=pdist_lineseg(b,a.t);
  return min(min(d1,d2),min(d3,d4));
}

// Returns true if line segment a contains line segment b
bool lineseg_contain(TLine a, TLine b) {
  return on_lineseg(a,b.f) && on_lineseg(a,b.t);
}

// Returns true if line segment a and b overlaps (shares at least one point)
bool lineseg_overlap(TLine a, TLine b)
{
  return on_lineseg(a,b.f) || on_lineseg(a,b.t);
}

// Transforms point w the same way point u has been transformed into point v
TPoint vector_transform(TPoint u, TPoint v, TPoint w)
{
  T d0=normsq(u);
  TPoint p(det(u,w),dot(u,w));
  return TPoint(det(v,p)/d0,dot(v,p)/d0);
}

// Calculates the half-plane given two distinct points (the bisector of two points)
TLine halfplane(TPoint a, TPoint b)
{
  TLine v;
  v.f=TPoint((a.x+b.x+b.y-a.y)/2,(a.y+b.y-b.x+a.x)/2);
  v.t=TPoint((a.x+b.x-b.y+a.y)/2,(a.y+b.y+b.x-a.x)/2);
  return v;
}

// Reflect a beam (vector b) on a mirror (vector m), return new beam vector
TPoint reflect(TPoint m, TPoint b)
{
  TPoint p(sqrt(normsq(m)),0);
  b=vector_transform(m,p,b);
  b.y=-b.y;
  return vector_transform(p,m,b);
}


int main(void)
{
  TLine a;
  TPoint b,p;
      
  TLine mir,beam;
  mir.f=TPoint(0,0); mir.t=TPoint(100,100);
  beam.f=TPoint(10,0); beam.t=TPoint(10,-1);
  cout << beam.t.x-beam.f.x << "," << beam.t.y-beam.f.y << endl;

  TPoint v=reflect(mir.t-mir.f,beam.t-beam.f);
  cout << v.x << "," << v.y << endl;
  /*
    a.f=TPoint(2,1);
    a.t=TPoint(5,2);
    scanf("%lf %lf",&b.x,&b.y);
    double d=pdist_lineseg(a,b,&p);
    printf("Distance to closest point = %0.3lf\n",d);
    printf("Closest point = %0.3lf,%0.3lf\n",p.x,p.y);
    exit(0);
	
    TLine l[10];
    l[0].f=TPoint(2,1); l[0].t=TPoint(3,6);
    l[1].f=TPoint(2,3); l[1].t=TPoint(7,3);
    l[2].f=TPoint(2,7); l[2].t=TPoint(7,2);
    l[3].f=TPoint(7,2); l[3].t=TPoint(5,5);
    l[4].f=TPoint(3,4); l[4].t=TPoint(2,5);	
  */
  /*
    for(int i=0;i<5;i++) {
    for(int j=0;j<5;j++) {
    TPoint res;
    cout << "Lines " << i << " and " << j << " ";
    if (line_intersect(l[i],l[j],&res))
    cout << "intersect at " << res.x << "," << res.y << endl;
    else
    cout << "doesn't intersect" << endl;
    }
    }
  */
  /*	
    printf("%d\n",on_lineseg(l[2],TPoint(4,5)));
    printf("%d\n",on_lineseg(l[2],TPoint(5,4)));
    printf("%d\n",on_lineseg(l[2],TPoint(5,5)));
    printf("%d\n",on_lineseg(l[2],TPoint(4,4)));
    printf("%d\n",on_lineseg(l[2],TPoint(2,7)));
    printf("%d\n",on_lineseg(l[2],TPoint(1,8)));
    printf("%d\n",on_lineseg(l[2],TPoint(7,2)));
    printf("%d\n",on_lineseg(l[2],TPoint(8,1)));
  */
  /*
    for(int i=-2;i<=8;i++)	
    cout << pdist_line(l[0],TPoint(3,i)) << " " << pdist_lineseg(l[0],TPoint(3,i)) << endl;
  */

  /*	
    TPoint a1(1,1),a2(0,0);
    TPoint b(104,34);
    TPoint c=vector_transform(a1,a2,b);
    cout << c.x << "," << c.y << endl;
	
    TPoint a(2,6),b(1,2);
    TLine v=halfplane(a,b);
    cout << v.f.x << "," << v.f.y << " - " << v.t.x << "," << v.t.y << endl;
  */
  return 0;
}
