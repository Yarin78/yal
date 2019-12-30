#include <cstdio>
#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>

// POLY library
//
// Includes the following functions:
//
// * Convex hull
// * Polygon area calculation
// * Count number of integer coordinates on polygon edges
// * Count number of integer coordinates inside a polygon
// * Determine whether a point lies on the polygon edge
// * Determine whether a point lies inside the polygon

using namespace std;

#include "..\rat.h"

inline bool isnull(int v) { return !v; }
inline bool isnull(double v) { return fabs(v)<1e-6; }

typedef int T;         // Use if possible
//typedef double T;      // Flexible, but might be inaccurate
//typedef RatNum<int> T; // Safe!

// A point -OR- a vector with some useful help functions
struct TPoint {
  T x,y;
 	TPoint(T _x=0,T _y=0) { x=_x; y=_y; }
 	
 	TPoint operator-(TPoint rhs) {
 		return TPoint(this->x-rhs.x,this->y-rhs.y);
 	}
 	TPoint operator+(TPoint rhs) {
 		return TPoint(this->x+rhs.x,this->y+rhs.y);
 	}
 	bool operator==(TPoint rhs) {
 		return isnull(this->x-rhs.x) && isnull(this->y-rhs.y);
 	}
};

// A polygon in clockwise or anticlockwise direction
typedef vector<TPoint> TPoly;

ostream &operator<<(ostream &lhs, const TPoly &rhs) {
	lhs << "{";
	for(int i=0;i<rhs.size();i++) {
		if (i) lhs << ",";
		lhs << "{" << rhs[i].x << "," << rhs[i].y << "}";
	}
	lhs << "}" << endl;
  return lhs;
}

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

// If v1->v2->v3 is a left turn, answer is positive
// If v2 is to the right of v1->v3, answer is positive
// If v1,v2,v3 are colinear, answer is 0
inline T cross(TPoint v1, TPoint v2, TPoint v3) {
	return (v2.x-v1.x)*(v3.y-v1.y)-(v3.x-v1.x)*(v2.y-v1.y);
}

// a & b should be positive integers
int gcd(int a, int b) {
	return !b?a:gcd(b,a%b);
}

// Returns the signed doubled area of a clockwise polgyon
T poly_signed_darea(const TPoly &poly)
{
	T area=T(0);
	for(int i=0;i<poly.size();i++) {
		int j=(i+1)%poly.size();
		area+=poly[j].x*poly[i].y-poly[i].x*poly[j].y;
	}
	return area;
}

// Returns the doubled area of a polgyon
T poly_darea(const TPoly &poly) {
	T a=poly_signed_darea(poly);
	return a<0?-a:a;
}

// Returns the area of a polgyon
double poly_area(const TPoly &poly) {
	return poly_darea(poly)/2.0;
}

// Reverse order of the vertices in the polygon
void poly_reverse(TPoly &poly) {
	reverse(&poly[1],poly.end());
}

// Calculate number of points on the polygon that lies on integer coordinates
// The vertices must have integer coordinates!
int poly_countexterior(TPoly &poly)
{
	int cnt=0;
	for(int i=0;i<poly.size();i++) {
		int j=(i+1)%poly.size();
		cnt+=gcd(abs(poly[i].x-poly[j].x),abs(poly[i].y-poly[j].y));
	}
	return cnt;
}

// Calculate number of points in the polygon that lies on integer coordinates
// The vertices must have integer coordinates!
int poly_countinterior(TPoly &poly) {
	return 1+((int)poly_darea(poly)-poly_countexterior(poly))/2;
}

// Predicate for sorting vectors according to angle (x coordinates >= 0)
bool vector_sort(const TPoint &v1, const TPoint &v2)
{
  T r=det(v1,v2);
 	if (!isnull(r)) return r<0;
  return normsq(v1)-normsq(v2)<0;
}

// Calculate the convex hull of a polygon (or just a set of points)
// The coordinates in the convex hull will be in clockwise direction.
TPoly convex_hull(TPoly &poly)
{
	int i,j,m,n=poly.size();
	TPoly hull;
	hull.resize(n);
	for(j=0,i=1;i<n;i++)
  	if ((poly[i].x<poly[j].x) || (poly[i].x==poly[j].x && poly[i].y<poly[j].y))
  		j=i;
	for(i=0;i<n;i++)
    hull[i]=poly[i]-poly[j];
	sort(hull.begin(),hull.end(),vector_sort);
  for(i=m=0;i<n;i++) {
  	hull[i]=hull[i]+poly[j];
  	if (m && hull[i]==hull[m-1]) continue;
    while (m>1 && cross(hull[m-2],hull[m-1],hull[i])>=0)
      m--;
    hull[m++]=hull[i];
  }
  hull.resize(m);
  return hull;
}

// Determines whether a point is on the polygon edge
bool point_on_poly(TPoly &poly, TPoint p)
{
	for(int i=0;i<poly.size();i++) {
		TPoint v1=poly[i]-p,v2=p-poly[(i+1)%poly.size()];
		T d0=dot(v1,v2);
		if (isnull(det(v1,v2)) && (isnull(d0) || (d0>0)))
			return true;
	}
	return false;
}

// Determines whether a point is inside the polygon
// UNDEFINED if point is on polygon edge (use point_on_poly!)
// Works with both CW and CCW polygons
bool point_in_poly(TPoly &poly, TPoint p)
{
	bool flag=false;
	for(int i=0;i<poly.size();i++) {
		TPoint a=poly[i];
		TPoint b=poly[(i+1)%poly.size()]-a,q=p-a;
		if (b.y>0 ? q.y>=0 && q.y<b.y && det(q,b)<0 : b.y<=q.y && q.y<0 && det(q,b)>0)
			flag=!flag;
	}
	return flag;
} 

int main(void)
{
	int n;
	TPoly poly;
	cin >> n;
	while (n--) {
		T x,y;
		cin >> x >> y;
		poly.push_back(TPoint(x,y));
	}
	/*	
	cout << poly;
	cout << "Polygon area = " << poly_signed_darea(poly)/2.0 << endl;
	poly_reverse(poly);
	cout << poly;
	cout << "Polygon area = " << poly_signed_darea(poly)/2.0 << endl;
	cout << "Exterior integer coordinates = " << poly_countexterior(poly) << endl;
	cout << "Interior integer coordinates = " << poly_countinterior(poly) << endl;
	*/
	//cout << poly;
	//cout << convex_hull(poly);
	
	for(int y=13;y>=0;y--) {
		for(int x=0;x<=13;x++) {
			if (point_on_poly(poly,TPoint(x,y)))
				printf("X");
			else if (point_in_poly(poly,TPoint(x,y)))
				printf("#");
			else
				printf(".");
		}
		printf("\n");
	}
	
	return 0;
}

