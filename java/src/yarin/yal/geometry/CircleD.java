package yarin.yal.geometry;

public class CircleD {
  private PointD center;
  private double radius;

  public PointD getCenter() {
    return center;
  }

  public double getRadius() {
    return radius;
  }

  public CircleD(double centerX, double centerY, double radius) {
    this(new PointD(centerX, centerY), radius);
  }

  public CircleD(PointD center, double radius)	{
    this.center = center;
    this.radius = radius;
  }

  /**
   * Given a point p outside the circle, find the two points on the circle
   * where there is a tangent to point p.
   */
  public PointD[] findTangentPoints(PointD p) {
    p = PointD.sub(p, center);
    double d = Math.sqrt(p.getNormSqr());
    double y = radius * radius / d;
    double t = Math.sqrt(radius * radius - Math.pow(radius, 4) / (d * d));

    PointD p1 = new PointD(y, t);
    PointD p2 = new PointD(y, -t);
    p1 = PointD.add(p1.vectorTransform(new PointD(d, 0), p), center);
    p2 = PointD.add(p2.vectorTransform(new PointD(d, 0), p), center);
    return new PointD[] {p1, p2};
  }

  /* Not ported
 *
 * #include <stdio.h>
#include <iostream>
#include <vector>
#include <cmath>

// CIRCLE library
//
// Includes the following functions:
//
// * Given three points, determine the circle that passes through these points
// * Calculate the intersection points of two circles
// * Calculate the area of a circle segment
// * Calculate the intersection area of two circles
// * Calculate the x,y,z points given latitude and longitude

using namespace std;

inline bool isnull(double v) { return fabs(v)<1e-6; }

typedef double T;

const T pi=3.1415926535897932384626;

struct TPoint {
  T x,y;
    TPoint(T _x=0,T _y=0) { x=_x; y=_y; }

    TPoint operator-(TPoint rhs) {
        return TPoint(this->x-rhs.x,this->y-rhs.y);
    }
    TPoint operator+(TPoint rhs) {
        return TPoint(this->x+rhs.x,this->y+rhs.y);
    }
    TPoint operator*(T f) {
        return TPoint(this->x*f,this->y*f);
    }
    bool operator==(TPoint rhs) {
        return isnull(this->x-rhs.x) && isnull(this->y-rhs.y);
    }
};

inline T det(TPoint a, TPoint b) {  // Calculates the determinant of a vector
  return a.x*b.y-a.y*b.x;
}

inline T dot(TPoint a, TPoint b) {  // Calculates the dot product
  return a.x*b.x+a.y*b.y;
}

inline T normsq(TPoint p) {  // Calculates the norm (squared)
    return p.x*p.x+p.y*p.y;
}

inline T cross(TPoint v1, TPoint v2, TPoint v3) {
    return (v2.x-v1.x)*(v3.y-v1.y)-(v3.x-v1.x)*(v2.y-v1.y);
}

// Convert latitude and longitude (in degrees) to a xyz location on a sphere
void latlon(double lat, double lon, double &x, double &y, double &z)
{
    lat=lat*pi/180;
    lon=lon*pi/180;
    x=sin(lon)*cos(lat);
    y=cos(lon)*cos(lat);
    z=sin(lat);
}

// Returns a vector with 0, 1 or 2 intersection points.
// If the circles are the same, an empty vector will be returned.
vector<TPoint> circle_intersect(TPoint p, T r1, TPoint q, T r2)
{
    vector<TPoint> ipoints;
    TPoint pq,v;
    T dist,a;
    if (p==q && r1==r2)
        return ipoints; // Infinite number of intersection point
    if (r1<r2) {
        swap(p,q); // Make sure circle p is bigger
        swap(r1,r2);
    }
    dist=sqrt(normsq(q-p));
    pq=(q-p)*(T(1)/dist);
    if (isnull(dist-r1-r2) || isnull(dist-r1+r2)) {
        ipoints.push_back(p+pq*r1); // One intersection point
        return ipoints;
    }
    if (dist>r1+r2 || dist<r1-r2)
        return ipoints; // No intersection points
    a=(r1*r1+dist*dist-r2*r2)/(2*dist);
  v=TPoint(pq.y,-pq.x)*sqrt(r1*r1-a*a);
  ipoints.push_back(p+pq*a+v);
  ipoints.push_back(p+pq*a-v);
  return ipoints;
}

// Returns the centre of the circle
TPoint find_circle(TPoint a, TPoint b, TPoint c)
{
    TPoint ab=b-a,ac=c-a;
    T v=2*det(ab,c-b);
    // If isnull(v), points are colinear!
    TPoint p(dot(ab,a+b),dot(ac,a+c));
    return TPoint(
        det(p,TPoint(ab.y,ac.y))/v,
        -det(p,TPoint(ab.x,ac.x))/v);
}


T calcint(T a, T x)
{
    return x/2*sqrt(a*a-x*x)+a*a/2*asin(x/a);
}

// Returns the area of the circle segment from x (0<=x<=r) to r
T circle_to_edge(T r, T x)
{
    if (x>=r) return 0;
    return calcint(r,r)-calcint(r,x);
}

// Returns the area of the circle segment from 0 to x (0<=x<=r)
T circle_from_center(T r, T x)
{
    if (x>=r) return 0;
    return r*r*pi/4.0-circle_to_edge(r,x);
}

// Returns the area of the circle segment of size theta
T circle_segment(T r, T theta)
{
    return r*r/2*(theta-sin(theta));
}

// Returns the area of the intersection of circle p and circle q
T circle_intersection_area(TPoint p, T r1, TPoint q, T r2)
{
    if (r1<r2) {
        swap(p,q); // Make sure circle p is bigger
        swap(r1,r2);
    }
    T dist=sqrt(normsq(p-q));
    if (isnull(dist-r1+r2) || (dist<r1-r2))
        return r2*r2*pi; // q is completely enclosed by p
    if (isnull(dist-r1-r2) || (dist>r1+r2))
        return 0; // p and q doesn't overlap
    vector<TPoint> ip=circle_intersect(p,r1,q,r2);
    T d=sqrt(normsq(ip[0]-ip[1]))/2;
    T theta1=asin(d/r1),theta2=asin(d/r2);
    // Check if intersection line is between the centre points of the circles
    if (cross(ip[0],p,ip[1])*cross(ip[0],q,ip[1])>0)
        theta2=pi-theta2;
    return
        circle_segment(r1,2*theta1)+
        circle_segment(r2,2*theta2);
}

T estimate(TPoint p, T r1, TPoint q, T r2)
{
    T x,y,x1,y1,x2,y2,a=0;
    x1=max(p.x-r1,q.x-r2);
    x2=min(p.x+r1,q.x+r2);
    y1=max(p.y-r1,q.y-r2);
    y2=min(p.y+r1,q.y+r2);
    T xstep=(x2-x1)/1000;
    T ystep=(y2-y1)/1000;
    for(T x=x1;x<x2;x+=xstep)
        for(T y=y1;y<y2;y+=ystep) {
            TPoint b(x,y);
            if (normsq(b-p)<r1*r1 && normsq(b-q)<r2*r2)
                a+=xstep*ystep;
        }
    return a;
}

int main(void)
{
    T x1,y1,r1,x2,y2,r2;
    //while (cin >> x1 >> y1 >> r1 >> x2 >> y2 >> r2) {
    for(int i=0;i<100;i++) {
        x1=rand()%100; y1=rand()%100; r1=rand()%50+40;
        x2=rand()%100; y2=rand()%100; r2=rand()%50+40;
        T a1=circle_intersection_area(TPoint(x1,y1),r1,TPoint(x2,y2),r2);
        T a2=estimate(TPoint(x1,y1),r1,TPoint(x2,y2),r2);
        //cout << a1 << endl << "Estimate: " << a2 << endl;
        if (isnull(a1)) a1=a2=1;
        printf("Error: %0.2lf%%\n",abs(a1-a2)/a1*100);
        //cout << endl;
    }
    return 0;
}
*/
}
