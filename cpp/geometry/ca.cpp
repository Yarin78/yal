#include <iostream>
#include <vector>
#include <algorithm>
#include <cstdio>
#include <cmath>

using namespace std;

const double EPS=1e-6;
const double pi=3.1415926535897932384626;

inline bool isnull(double v) { return fabs(v)<EPS; }

struct TPoint {
  double x,y;
 	TPoint(double _x=0,double _y=0) { x=_x; y=_y; }
 	TPoint operator-(const TPoint &rhs) const { return TPoint(x-rhs.x,y-rhs.y); }
 	TPoint operator+(const TPoint &rhs) const { return TPoint(x+rhs.x,y+rhs.y); }
 	TPoint operator*(double f) const { return TPoint(x*f,y*f); }
 	bool operator==(const TPoint &rhs) const { return isnull(x-rhs.x) && isnull(y-rhs.y); }
};

double normsq(const TPoint &p) { return p.x*p.x+p.y*p.y; }
double calcint(double a, double x) { return x/2*sqrt(a*a-x*x)+a*a/2*asin(x/a); }

// Returns a vector with 0, 1 or 2 intersection points.
vector<TPoint> circle_intersect(TPoint c1, double r1, TPoint c2, double r2)
{
	vector<TPoint> ipoints;
	TPoint pq,v;
	double dist,a;
	bool swapflag=false;
	if (c1==c2 && r1==r2) return ipoints;
	if (r1<r2) { swap(c1,c2); swap(r1,r2); swapflag=true; }
	dist=sqrt(normsq(c2-c1));
	pq=(c2-c1)*(1.0/dist);
	if (isnull(dist-r1-r2) || isnull(dist-r1+r2)) {
		ipoints.push_back(c1+pq*r1); // One intersection point
		return ipoints;
	}
	if (dist>r1+r2 || dist<r1-r2) return ipoints; // No intersection points
	a=(r1*r1+dist*dist-r2*r2)/(2*dist);
  v=TPoint(pq.y,-pq.x)*sqrt(r1*r1-a*a);
  ipoints.push_back(c1+pq*a+v);
  ipoints.push_back(c1+pq*a-v);
  if (swapflag) swap(ipoints[0],ipoints[1]);
  return ipoints;
}

bool is_inside(const TPoint &c1, double r1, const TPoint &c2, double r2)
{
	if (r1>r2) return false;
	TPoint diff=c1-c2;
	if (normsq(diff)<(r1-r2)*(r1-r2)+EPS) return true;
	return false;
}

void addcutoff(vector<pair<double,double> > &ival, double a, double b)
{
	if (a<0) a+=2*pi;
	if (b<0) b+=2*pi;
	if (b<a) {
		ival.push_back(make_pair(a,2*pi));
		ival.push_back(make_pair(0,b));
	} else if (a<pi && b>pi) {
		ival.push_back(make_pair(a,pi));
		ival.push_back(make_pair(pi,b));
	} else
		ival.push_back(make_pair(a,b));
}

double calcarea(double a, double b, double rad, double y)
{
	double x1=cos(a)*rad,x2=cos(b)*rad;
	double area=calcint(rad,x1)-calcint(rad,x2);
	if (a<pi) return area+(x1-x2)*y;
	return -area-(x2-x1)*y;
}

int main(void)
{
	int n;
	cin >> n;
	while (n) {
		TPoint c[100];
		double r[100];
		vector<pair<double,double> > ival[100];
		for(int i=0;i<n;i++) {
			double x,y;
			cin >> c[i].x >> c[i].y >> r[i];
			if (isnull(r[i]))
				ival[i].push_back(make_pair(0,2*pi));
			for(int j=0;j<i;j++) {
				if (is_inside(c[j],r[j],c[i],r[i]))
					addcutoff(ival[j],0,2*pi);
				else if (is_inside(c[i],r[i],c[j],r[j]))
					addcutoff(ival[i],0,2*pi);
				else {
					vector<TPoint> p=circle_intersect(c[i],r[i],c[j],r[j]);
					if (p.size()==2) {
						TPoint p1=p[0]-c[i],p2=p[1]-c[i];
						TPoint q1=p[0]-c[j],q2=p[1]-c[j];
						addcutoff(ival[i],atan2(p1.y,p1.x),atan2(p2.y,p2.x));
						addcutoff(ival[j],atan2(q2.y,q2.x),atan2(q1.y,q1.x));
					}
				}
			}
		}
		double totarea=0.0;
		for(int i=0;i<n;i++) {
			sort(ival[i].begin(),ival[i].end());
			double end=0.0;
			for(int j=0;j<ival[i].size();j++) {
				if (ival[i][j].first<=end) {
					if (ival[i][j].second>end) end=ival[i][j].second;
				} else {
					if (end<pi && ival[i][j].first>pi) {
						totarea+=calcarea(end,pi,r[i],c[i].y);
						end=pi;
					}
					totarea+=calcarea(end,ival[i][j].first,r[i],c[i].y);
					end=ival[i][j].second;
				}
			}
			if (end<pi) {
				totarea+=calcarea(end,pi,r[i],c[i].y);
				end=pi;
			}
			if (end<2*pi)
				totarea+=calcarea(end,2*pi,r[i],c[i].y);
		}
		printf("%0.3lf\n",totarea);
		cin >> n;
	}
	return 0;
}
