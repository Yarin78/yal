#include <iostream>
#include <cstdio>
#include <cmath>
#include <vector>

using namespace std;

const double EPS=1e-6;

inline bool isnull(double v) { return fabs(v)<EPS; }

typedef double T;

const T pi=3.1415926535897932384626;

struct TPoint {
  T x,y;
 	TPoint(T _x=0,T _y=0) { x=_x; y=_y; }
 	
 	TPoint operator-(const TPoint &rhs) const {
 		return TPoint(this->x-rhs.x,this->y-rhs.y);
 	}
 	TPoint operator+(const TPoint &rhs) const {
 		return TPoint(this->x+rhs.x,this->y+rhs.y);
 	}
 	TPoint operator*(T f) const {
 		return TPoint(this->x*f,this->y*f);
 	}
 	bool operator==(const TPoint &rhs) const {
 		return isnull(this->x-rhs.x) && isnull(this->y-rhs.y);
 	}
};

struct TCircle
{
	TPoint c;
	double r;
};

inline T normsq(const TPoint &p) {  // Calculates the norm (squared)
	return p.x*p.x+p.y*p.y;
}

// Returns a vector with 0, 1 or 2 intersection points.
// If the circles are the same, an empty vector will be returned.
vector<TPoint> circle_intersect(TCircle c1, TCircle c2) //TPoint p, T r1, TPoint q, T r2)
{
	vector<TPoint> ipoints;
	TPoint pq,v;
	T dist,a;
	bool swapflag=false;
	if (c1.c==c2.c && c1.r==c2.r)
		return ipoints; // Infinite number of intersection point
	if (c1.r<c2.r) {
		swap(c1,c2); // Make sure circle p is bigger
		swapflag=true;
	}
	dist=sqrt(normsq(c2.c-c1.c));
	pq=(c2.c-c1.c)*(T(1)/dist);
	if (isnull(dist-c1.r-c2.r) || isnull(dist-c1.r+c2.r)) {
		ipoints.push_back(c1.c+pq*c1.r); // One intersection point
		return ipoints;
	}
	if (dist>c1.r+c2.r || dist<c1.r-c2.r)
		return ipoints; // No intersection points
	a=(c1.r*c1.r+dist*dist-c2.r*c2.r)/(2*dist);
  v=TPoint(pq.y,-pq.x)*sqrt(c1.r*c1.r-a*a);
  ipoints.push_back(c1.c+pq*a+v);
  ipoints.push_back(c1.c+pq*a-v);
  if (swapflag)
  	swap(ipoints[0],ipoints[1]);
  return ipoints;
}

T calcint(T a, T x)
{
	return x/2*sqrt(a*a-x*x)+a*a/2*asin(x/a);
}

bool is_inside(const TCircle &c1, const TCircle &c2)
{
	// Return true if circle c1 is completely inside c2 _or_ c1==c2
	if (c1.r>c2.r) return false;
	TPoint diff=c1.c-c2.c;
	if (normsq(diff)<(c1.r-c2.r)*(c1.r-c2.r)+EPS) return true;
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

int main(void)
{
	int n;
	cin >> n;
	TCircle circle[100];
	vector<pair<double,double> > ival[100];
	while (n) {		
		for(int i=0;i<n;i++) {		
			double x,y;
			ival[i].clear();
			cin >> x >> y >> circle[i].r;
			circle[i].c=TPoint(x,y);
			for(int j=0;j<i;j++) {
				if (is_inside(circle[j],circle[i])) {
					ival[j].push_back(make_pair(0,2*pi));
				} else if (is_inside(circle[i],circle[j])) {
					ival[i].push_back(make_pair(0,2*pi));
				} else {
					vector<TPoint> p=circle_intersect(circle[i],circle[j]);
					if (p.size()==2) {						
						TPoint p1=p[0]-circle[i].c,p2=p[1]-circle[i].c;
						TPoint q1=p[0]-circle[j].c,q2=p[1]-circle[j].c;
						addcutoff(ival[i],atan2(p1.y,p1.x),atan2(p2.y,p2.x));
						addcutoff(ival[j],atan2(q2.y,q2.x),atan2(q1.y,q1.x));
					}
				}
			}
		}
		double totarea=0.0, totperimeter=0.0;
		for(int i=0;i<n;i++) {		
			//cout << "Circle " << i << ":" << endl;
			vector<double> arc;			

			sort(ival[i].begin(),ival[i].end());
			//for(int j=0;j<ival[i].size();j++)
				//printf("(%0.2lf,%0.2lf)\n",ival[i][j].first,ival[i][j].second);
							
			double end=0.0;
			for(int j=0;j<ival[i].size();j++) {
				if (ival[i][j].first<=end) {
					end>?=ival[i][j].second;
				} else {
					arc.push_back(end);
					if (end<pi && ival[i][j].first>pi) {
						arc.push_back(pi); // Split upper and lower arcs
						arc.push_back(pi);
					}
					arc.push_back(ival[i][j].first);
					end=ival[i][j].second;
				}
			}
			if (end<2*pi) {
				arc.push_back(end);
				if (end<pi) {
					arc.push_back(pi); // Split upper and lower arcs
					arc.push_back(pi);
				}
				arc.push_back(2*pi);
			}
			
			for(int j=0;j<arc.size();j+=2) {				
				double a=arc[j],b=arc[j+1],r=circle[i].r;
				//printf("%6.2lf - %6.2lf: ",a*180/pi,b*180/pi);

				double dif=b-a;
				if (dif<0) dif+=2*pi;
				totperimeter+=dif*r;
				double x1=cos(a)*r,x2=cos(b)*r;
				
				double area=calcint(r,x1)-calcint(r,x2);
				if (a<pi) {
					area+=(x1-x2)*circle[i].c.y;
					totarea+=area;
					//printf("%+6.2lf\n",area);
				} else {
					area+=(x2-x1)*circle[i].c.y;
					totarea-=area;
					//printf("%+6.2lf\n",-area);
				}
			}
		}
		printf("Real area      = %6.2lf\n",totarea);
		printf("Perimeter      = %6.2lf\n",totperimeter);
/*
		double minx=1000,maxx=-1000,miny=1000,maxy=-1000;
		const int granularity=1000;
		for(int i=0;i<n;i++) {
			minx<?=circle[i].c.x-circle[i].r;
			maxx>?=circle[i].c.x+circle[i].r;
			miny<?=circle[i].c.y-circle[i].r;
			maxy>?=circle[i].c.y+circle[i].r;
		}
		double stepx=(maxx-minx)/granularity;
		double stepy=(maxy-miny)/granularity;
		int cnt=0;
		for(int y=0;y<granularity;y++)
			for(int x=0;x<granularity;x++) {
				double xc=minx+x*stepx;
				double yc=miny+y*stepy;
				bool inside=false;
				for(int i=0;i<n;i++) {
					double dx=circle[i].c.x-xc,dy=circle[i].c.y-yc;
					if (dx*dx+dy*dy<circle[i].r*circle[i].r) {
						inside=true;
						break;
					}
				}
				if (inside) cnt++;									
			}
		printf("Estimated area = %6.2lf\n",
			double(cnt)/(granularity*granularity)*(maxx-minx)*(maxy-miny));
		*/
		cin >> n;			
	}
	return 0;
}
