#include <cstdio>
#include <iostream>
#include <cmath>
#include <cstdlib>

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

#include "..\rat.h"

inline bool isnull(long long v) { return !v; }
inline bool isnull(double v) { return fabs(v)<1e-6; }

//typedef int T;         // Don't use if the actual intersection points are needed!!
typedef double T;      // Flexible, but might be inaccurate
//typedef RatNum<long long> T; // Safe!


// A point -OR- a vector with some useful help functions
struct TPoint {
  T x,y;
 	TPoint(T _x=0,T _y=0) { x=_x; y=_y; }

 	TPoint operator-(TPoint rhs) const {
 		return TPoint(this->x-rhs.x,this->y-rhs.y);
 	}
 	TPoint operator+(TPoint rhs) const {
 		return TPoint(this->x+rhs.x,this->y+rhs.y);
 	}
 	bool operator<(const TPoint &rhs) const {
 		if (!isnull(this->y-rhs.y))
 			return this->y>rhs.y;
 		else if (isnull(this->x-rhs.x))
 			return false;
 		else
 			return this->x<rhs.x;
 	}
 	bool operator==(const TPoint &rhs) const {
 		return this->y==rhs.y && this->x==rhs.x;
 	}
};

// Line segment with two endpoints -OR- infinite line with two arbitrary points
struct TLine {
  TPoint f,t;
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

// Returns TRUE if the point b lies on the line segment a (including endpoints)
bool on_lineseg(TLine a, TPoint b)
{
	TPoint v1=a.f-b,v2=b-a.t;
	T d0=dot(v1,v2);
	return isnull(det(v1,v2)) && (isnull(d0) || (d0>T(0)));
}

// Returns TRUE if the point b lies anywhere on the (infinite) line a
bool on_line(TLine a, TPoint b) {
	return isnull(det(a.f-b,b-a.t));
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
 	if (d0<T(0)) { d0=-d0; fa=-fa; fb=-fb; }
 	if (fa>T(0) && fa<d0 && fb>T(0) && fb<d0) {
 		if (res) {
     	res->x=fa*v1.x/d0+a.f.x;
     	res->y=fa*v1.y/d0+a.f.y;
    }
    return true;
	}
  return false;
}

// Returns TRUE if there exists exactly one point which lines on both
// line segments a and b including endpoints
bool lineseg_intersect_inclusive(TLine a, TLine b, TPoint *res=0) {
	TPoint p;
	if (on_lineseg(a,b.f)) p=b.f; else
	if (on_lineseg(a,b.t)) p=b.t; else
	if (on_lineseg(b,a.f)) p=a.f; else
	if (on_lineseg(b,a.t)) p=a.t; else
		return lineseg_intersect(a,b,res);
	if (res)
		*res=p;
	return true;
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

// Returns the distance between a point and a line segment
double pdist_lineseg(TLine a, TPoint b)
{
	TPoint v=a.t-a.f,u=b-a.f;
	T d0=dot(v,u)/normsq(v);
	if (d0>T(0) && d0<T(1))
		return sqrt(normsq(u-TPoint(v.x*d0,v.y*d0)));
	return sqrt(min(normsq(a.f-b),normsq(a.t-b)));
}

// Returns the distance between a point and a line
double pdist_line(TLine a, TPoint b)
{
	TPoint v=a.t-a.f,u=b-a.f;
	T d0=dot(v,u)/normsq(v);
	return sqrt(normsq(u-TPoint(v.x*d0,v.y*d0)));
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
	v.f=TPoint((a.x+b.x+b.y-a.y)/T(2),(a.y+b.y-b.x+a.x)/T(2));
	v.t=TPoint((a.x+b.x-b.y+a.y)/T(2),(a.y+b.y+b.x-a.x)/T(2));
	return v;
}

#include <vector>
#include <map>
#include <set>

int main(void)
{
	T x1,y1,x2,y2;
	vector<TLine> lines;
	map< TPoint, set<int> > isect;
	while (cin >> x1 >> y1 >> x2 >> y2) {
		if (x1==x2 && y1==y2) break;
		TLine l;
		l.f.x=x1; l.f.y=y1;
		l.t.x=x2; l.t.y=y2;
		lines.push_back(l);
	}
	TPoint p;
	int n=lines.size();
	for(int i=0;i<n;i++)
		for(int j=0;j<i;j++)
			if (lineseg_intersect_inclusive(lines[i],lines[j],&p)) {
				isect[p].insert(i);
				isect[p].insert(j);
			}
			
	for(map<TPoint,set<int> >::iterator i=isect.begin();i!=isect.end();i++) {
		cout << (double)i->first.x << "," << (double)i->first.y << ":";
		for(set<int>::iterator j=i->second.begin();j!=i->second.end();j++)
			cout << " " << *j;
		cout << endl;
	}
	return 0;
}
