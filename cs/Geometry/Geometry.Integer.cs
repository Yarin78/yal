using System;
using System.Collections;
using System.Collections.Generic;

namespace Algorithms.Geometry.Integer
{
	[Serializable]
	public class Point : IComparable<Point>
	{
		public int x, y;
		public Point() : this(0,0) { }
		public Point(int x, int y) { this.x = x; this.y = y; }
		public static Point operator+(Point p1, Point p2) { return new Point(p1.x+p2.x, p1.y+p2.y); }
		public static Point operator-(Point p1, Point p2) { return new Point(p1.x-p2.x, p1.y-p2.y); }
		public static Point operator*(Point p1, int scalar) { return new Point(p1.x*scalar, p1.y*scalar); }
		public static Point operator/(Point p1, int scalar) { return new Point(p1.x/scalar, p1.y/scalar); }
		public static int Det(Point a, Point b) { return a.x*b.y-a.y*b.x; }
		public static int Dot(Point a, Point b) { return a.x*b.x+a.y*b.y; }
		public static int Cross(Point a, Point b, Point c) { return Det(b-a, c-a); }
		public int NormSqr { get { return x*x+y*y; } }
		public int CompareTo(Point other) { Point p=other; return x==p.x ? y-p.y : x-p.x; }

		public override bool Equals(object obj) { return x==((Point)obj).x && y==((Point)obj).y; }
		public override int GetHashCode() { return x.GetHashCode() ^ y.GetHashCode(); }

		public override string ToString()
		{
			return string.Format("({0}, {1})", x, y);
		}
	}

	/// <summary>
	/// Sorts a set of point by treating them as vectors and sorting by the angle.
	/// </summary>
	/// <remarks>
	/// <para>
	/// Starts at angle 90 degrees (x=0, y=-9999), goes to 0 (x=9999, y=0),
	/// -90 (x=0, y=9999) to 180 (x=-9999, y=0). Points at 0,0 are undefined.
	/// </para>
	/// <para>
	/// Points with the same angle are sorted according to the length from origo, closest first.</para>
	/// </remarks>
	public class PolarPointComparer : IComparer<Point>
	{
		public int Compare(Point p1, Point p2)
		{
			// All non-negative x-coordinates appear before all negative x-coordinates
			if (p1.x >= 0 && p2.x < 0) return -1;
			if (p1.x < 0 && p2.x >= 0) return 1;
			if (p1.x==0 && p2.x==0 && p1.y*p2.y < 0)
				return p2.y - p1.y;
			
			int r = Point.Det(p1, p2);
			return r != 0 ? r : p1.NormSqr - p2.NormSqr;
		}
	}

	/*
	 * Formulas:
	 * dot(a,b) = |a||b|*cos(theta)  (direction insensitive)
	 * 
	 * Rotation matrix:
	 *  [ cos(theta) -sin(theta) ]
	 *  [ sin(theta)  cos(theta) ]
	 *  
	 */
	[Serializable]
	public class Line
	{
		public Point a, b;
		public Line() : this(new Point(), new Point()) { }
		public Line(Point a, Point b) { this.a = a; this.b = b; }
		public Line(int x1, int y1, int x2, int y2) : this(new Point(x1,y1), new Point(x2,y2)) { }
		public virtual bool LiesOn(Point p) { return Point.Cross(a,b,p) == 0; }
		public static bool IsParallel(Line a, Line b) { return Point.Det(a.b-a.a, b.b-b.a) == 0; }

		// Returns true if the two line/linesegments intersect (and are not parallel)
		// If the intersection point has integer coordinates, it will be returned in p (otherwise p will be null)
		public static bool Intersect(Line a, Line b, out Point p)
		{
			p = null;
			Point difv = b.a-a.a, av = a.b-a.a, bv = b.a-b.b;
			int d = Point.Det(av,bv), fa = Point.Det(difv, bv), fb = Point.Det(av, difv);
			if (d == 0) return false;
			if (d < 0) { d = -d; fa = -fa; fb = -fb; }
			if ((fa*av.x) % d == 0 && (fa*av.y) % d == 0)
				p = a.a + (av * fa / d);
			if (a is LineSeg && (fa<0 || fa>d)) return false;
			if (b is LineSeg && (fb<0 || fb>d)) return false;
			return true;
		}

		public static bool Intersect(Line a, Line b) 
		{
			Point dummy;
			return Intersect(a,b,out dummy);
		}
		
		public virtual double Distance(Point p)
		{
			Point v = b-a, u = p-a;
			double d = (double)Point.Dot(v,u)/v.NormSqr;
			return Math.Sqrt(Math.Pow(u.x-v.x*d,2) + Math.Pow(u.y-v.y*d,2));
		}

		public override bool Equals(object obj)
		{
			if (this.GetType() != obj.GetType()) return false;
			Line ls = (Line)obj;
			return (a.Equals(ls.a) && b.Equals(ls.b)) || (a.Equals(ls.b) && b.Equals(ls.a));
		}

		public override int GetHashCode()
		{
			return a.GetHashCode() ^ b.GetHashCode();
		}
	}

	[Serializable]
	public class LineSeg : Line
	{
		public LineSeg(Point a, Point b) : base(a,b) { }
		public LineSeg(int x1, int y1, int x2, int y2) : base(x1, y1, x2, y2) { }
		public override bool LiesOn(Point p) { return base.LiesOn(p) && Point.Dot(a-p, p-b)>=0; }
		public double Length { get { return Math.Sqrt((a-b).NormSqr); } }
		
		// If a and b are parallel lines that share at least one point, return the merge of these lines.
		// Otherwise return null. If a LineSeg is returned, it will be a new instance.
		public static LineSeg Merge(LineSeg a, LineSeg b)
		{
			if (!IsParallel(a,b)) return null;
			if (!a.LiesOn(b.a) && !a.LiesOn(b.b) && !b.LiesOn(a.a) && !b.LiesOn(a.b)) return null;
			Point[] p=new Point[] { a.a, a.b, b.a, b.b };
			Array.Sort(p);
			return new LineSeg(p[0], p[3]);
		}

		public override double Distance(Point p)
		{
			Point v = b-a, u = p-a;
			double d = (double)Point.Dot(v,u)/v.NormSqr;
			if (d > 0 && d < 1)
				return Math.Sqrt(Math.Pow(u.x-v.x*d,2) + Math.Pow(u.y-v.y*d,2));
			return Math.Sqrt(Math.Min((a-p).NormSqr,(b-p).NormSqr));
		}
	}

	[Serializable]
	public class Poly
	{
		public Point[] point;

		public Poly(Point[] point) { this.point = point; }
		public double Area() { return Math.Abs(SignedDoubledArea()) / 2.0; }
		public int SignedDoubledArea()
		{
			int sum = 0;
			for (int i = 0; i < point.Length; i++)
				sum += Point.Det(point[i], point[(i+1)%point.Length]);
			return sum;
		}
		
		public LineSeg[] GetLineSegments()
		{
			LineSeg[] segs = new LineSeg[point.Length];
			for (int i = 0; i < point.Length; i++)
				segs[i] = new LineSeg(point[i], point[(i+1) % point.Length]);
			return segs;
		}

		public bool LiesOn(Point p)
		{
			for (int i = 0; i < point.Length; i++)
			{
				Point v1 = point[i]-p, v2 = p-point[(i+1)%point.Length];
				if (Point.Det(v1,v2) == 0 && Point.Dot(v1,v2) >= 0)
					return true;
			}
			return false;
		}

		public bool LiesIn(Point p)
		{
			bool flag = false;
			for (int i = 0; i < point.Length; i++)
			{
				Point a = point[i], b = point[(i+1)%point.Length] - a, q = p - a;
				if (b.y > 0 ? q.y >= 0 && q.y < b.y && Point.Det(q,b) < 0 : b.y <= q.y && q.y < 0 && Point.Det(q,b) > 0)
					flag = !flag;
			}
			return flag;
		}

		public static Poly ConvexHull(Poly poly)
		{
			Array.Sort(poly.point);
			Point adj = new Point(poly.point[0].x, poly.point[0].y);
			for (int i = 0; i < poly.point.Length; i++) poly.point[i] -= adj;
			Array.Sort(poly.point, new PolarPointComparer()); // Vector sort
			ArrayList hull = new ArrayList();
			for (int i = 0; i < poly.point.Length; i++)
			{
				Point cur = poly.point[i] + adj;
				if (hull.Count > 0 && hull[hull.Count-1].Equals(cur)) continue;
				while (hull.Count > 1 && Point.Cross((Point)hull[hull.Count-2], (Point)hull[hull.Count-1], cur) >= 0)
					hull.RemoveAt(hull.Count - 1);
				hull.Add(cur);
			}
			return new Poly((Point[]) hull.ToArray(typeof (Point)));
		}

		private int GCD(int a, int b)
		{
			return b == 0 ? a : GCD(b, a%b);
		}

		public int CountExteriorIntegerCoordinates()
		{
			int cnt = 0;
			for (int i = 0; i < point.Length; i++)
			{
				Point p = point[i] - point[(i + 1) % point.Length];
				cnt += GCD(Math.Abs(p.x), Math.Abs(p.y));
			}
			return cnt;
		}
		
		public int CountInteriorIntegerCoordinates()
		{
			return 1 + (Math.Abs(SignedDoubledArea()) - CountExteriorIntegerCoordinates()) / 2;
		}
	}

	public class Circle
	{
		private Point center;
		private int radius;

		public Point Center
		{
			get { return center; }
			set { center = value; }
		}

		public int Radius
		{
			get { return radius; }
			set { radius = value; }
		}

		public Circle(Point center, int radius)
		{
			Center = center;
			Radius = radius;
		}

		/*
					public static int CheckFourthPoint(Point p1, Point p2, Point p3, Point p4)
					{
						// axay  ax2ay2 1   a-d
						// TODO
						return 0;
					}*/
	}
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


/*
int main(void)
{
	for(int i=0;i<10;i++) {
		TPoint p[3];
		for(int j=0;j<3;j++)
			p[j]=TPoint(rand()%1000,rand()%1000);
		TPoint cent=find_circle(p[0],p[1],p[2]);
		T rad=sqrt(normsq(cent-p[0]));
		if (isnull(sqrt(normsq(cent-p[1]))-rad) && isnull(sqrt(normsq(cent-p[2]))-rad))
			cout << "OK" << endl;
		else
			cout << "Fail" << endl;
	}
	return 0;
}
*/


/*
void show(TPoint p)
{
	printf("(%0.3lf,%0.3lf)",isnull(p.x)?0:p.x,isnull(p.y)?0:p.y);
}

int main(void)
{
	T x1,y1,r1,x2,y2,r2;
	while (cin >> x1 >> y1 >> r1 >> x2 >> y2 >> r2) {
		TPoint p(x1,y1),q(x2,y2);
		if (p==q && r1==r2) {
			if (r1>0)
				cout << "THE CIRCLES ARE THE SAME";
			else
				show(p);
		} else {
			vector<TPoint> ipoint=circle_intersect(p,r1,q,r2);
			if (ipoint.size()==0)
				cout << "NO INTERSECTION";
			else if (ipoint.size()==1)
				show(ipoint[0]);
			else {
				if (ipoint[0].x<ipoint[1].x || (isnull(ipoint[0].x-ipoint[1].x) && ipoint[0].y<ipoint[1].y)) {
					show(ipoint[0]);
					show(ipoint[1]);
				} else {
					show(ipoint[1]);
					show(ipoint[0]);
				}
			}
		}
		cout << endl;
	}
	return 0;
}
*/

