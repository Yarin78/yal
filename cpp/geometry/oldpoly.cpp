#include <stdio.h>
#include <iostream>
#include <vector>
#include <algorithm>
#include <math.h>

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
//struct TPoly {
	//vector<TPoint> v;
//};
typedef vector<TPoint> TPoly;


ostream &operator<<(ostream &lhs, const TPoly &rhs) {
	lhs << "{";
	for(int i=0;i<rhs.v.size();i++) {
		if (i) lhs << ",";
		lhs << "{" << rhs.v[i].x << "," << rhs.v[i].y << "}";
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

inline T cross(TPoint &v1, TPoint &v2, TPoint &v3) {
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
	for(int i=0;i<poly.v.size();i++) {
		int j=(i+1)%poly.v.size();
		area+=poly.v[j].x*poly.v[i].y-poly.v[i].x*poly.v[j].y;
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
	reverse(&poly.v[1],poly.v.end());
}

// Calculate number of points on the polygon that lies on integer coordinates
// The vertices must have integer coordinates!
int poly_countexterior(TPoly &poly)
{
	int cnt=0;
	for(int i=0;i<poly.v.size();i++) {
		int j=(i+1)%poly.v.size();
		cnt+=gcd(abs(poly.v[i].x-poly.v[j].x),abs(poly.v[i].y-poly.v[j].y));
	}
	return cnt;
}

// Calculate number of points in the polygon that lies on integer coordinates
// The vertices must have integer coordinates!
int poly_countinterior(TPoly &poly) {
	return 1+(poly_darea(poly)-poly_countexterior(poly))/2;
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
	int i,j,m,n=poly.v.size();
	TPoly hull;
	hull.v.resize(n);
	for(j=0,i=1;i<n;i++)
  	if ((poly.v[i].x<poly.v[j].x) || (poly.v[i].x==poly.v[j].x && poly.v[i].y<poly.v[j].y))
  		j=i;
	for(i=0;i<n;i++)
    hull.v[i]=poly.v[i]-poly.v[j];
	sort(hull.v.begin(),hull.v.end(),vector_sort);
  for(i=m=0;i<n;i++) {
  	hull.v[i]=hull.v[i]+poly.v[j];
  	if (m && hull.v[i]==hull.v[m-1]) continue;
    while (m>1 && cross(hull.v[m-2],hull.v[m-1],hull.v[i])>=0)
      m--;
    hull.v[m++]=hull.v[i];
  }
  hull.v.resize(m);
  return hull;
}



int main(void)
{
	int n;
	TPoly poly;
	cin >> n;
	while (n--) {
		T x,y;
		cin >> x >> y;
		poly.v.push_back(TPoint(x,y));
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
	cout << poly;
	cout << convex_hull(poly);
	return 0;
}

