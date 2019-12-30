#include <stdio.h>
#include <iostream>
#include <vector>
#include <cmath>
#include <cassert>

using namespace std;

inline bool isnull(double v) { return fabs(v)<1e-8; }

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

struct TLine {
  TPoint f,t;
};

inline T det(TPoint a, TPoint b) {  // Calculates the determinant of a vector
  return a.x*b.y-a.y*b.x;
}

inline T normsq(TPoint p) {  // Calculates the norm (squared)
	return p.x*p.x+p.y*p.y;
}

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

ostream& operator<<(ostream &os, TPoint p)
{
	os << "{" << p.x << "," << p.y << "}";
	return os;
}

ostream& operator<<(ostream &os, vector<TPoint> vp)
{
	os << "{";
	for(int i=0;i<vp.size();i++) {
		if (i) os << ",";
		os << vp[i];
	}
	os << "}";
	return os;
}

TPoint p[3];

const int dx[4]={0,1,0,-1};
const int dy[4]={1,0,-1,0};

double calcdist(TPoint q)
{
	double tot=0.0;
	int i;
	for(int i=0;i<3;i++) {
		double dx=q.x-p[i].x;
		double dy=q.y-p[i].y;
		tot+=sqrt(dx*dx+dy*dy);
	}
	return tot;
}

TPoint find_point(double r)
{
	int i,j;
	vector<TPoint> ip[3];
	for(i=0;i<3;i++) {
		ip[i]=circle_intersect(p[i],r,p[(i+1)%3],r);
		assert(ip[i].size());
	}
	for(i=0;i<8;i++) {
		int k[3];
		for(j=0;j<3;j++) {
			k[j]=!(i&(1<<j));
			if (k[j] && (ip[j].size()==1))
				break;
		}		
		if (j<3) continue;
		if ((ip[0][k[0]]==ip[1][k[1]]) &&
			(ip[1][k[1]]==ip[2][k[2]]) &&
			(ip[2][k[2]]==ip[0][k[0]]))
				return ip[0][k[0]];
	}
	assert(0);
}

int main(void)
{
	int N;
	scanf("%d",&N);
	while (N--) {
		double a,b,c,tmp;
		vector<TPoint> ip,ip1,ip2,ip3;
		scanf("%lf %lf %lf",&a,&b,&c);
		if (b>a) { tmp=a; a=b; b=tmp; }
		if (c>a) { tmp=a; a=c; c=tmp; }
		assert(a>0 && b>0 && c>0);
		assert(isnull(a-b-c) || (a<b+c));
		p[0]=TPoint(0,0);
		p[1]=TPoint(a,0);
		ip=circle_intersect(p[0],b,p[1],c);
		assert(ip.size());
		p[2]=ip[0];
		p[2].y=fabs(p[2].y);
		TPoint v=p[0]+p[1]+p[2];
		TPoint centroid(v.x/3,v.y/3); // Center of gravity

		double gdist=calcdist(centroid),icdist=-1,ccdist=-1;
		
		// Only calculate incenter & circumcenter for a real triangle!
		if (!isnull(a-b-c)) {		
			double inradius=sqrt((b+c-a)*(c+a-b)*(a+b-c)/(a+b+c))/2.0;
			double circumradius=a*b*c/sqrt((a+b+c)*(b+c-a)*(c+a-b)*(a+b-c));
		
			TPoint incenter;
			TPoint circumcenter=find_point(circumradius);

			double alpha=atan(p[2].y/p[2].x);
			double beta=atan(p[2].y/(p[1].x-p[2].x));
			assert(alpha>0 && alpha<pi && beta>0 && beta<pi);
			TLine l1,l2;
			l1.f=p[0]; l1.t=p[0]+TPoint(cos(alpha/2),sin(alpha/2));
			l2.f=p[1]; l2.t=p[1]+TPoint(-cos(beta/2),sin(beta/2));
			assert(line_intersect(l1,l2,&incenter));
		
			icdist=calcdist(incenter);
			ccdist=calcdist(circumcenter);
		}
		// Calculate closest point
		TPoint cur=centroid,last=TPoint(-5,-5);
		double curdist=calcdist(cur),step=a/10;
		while (step>1e-5) {
			TPoint best=cur;
			double bestdist=curdist;
			for(int i=0;i<4;i++) {
				TPoint np=cur+TPoint(dx[i]*step,dy[i]*step);
				double ndist=calcdist(np);
				if (ndist<bestdist) {
					bestdist=ndist;
					best=np;
				}
			}
			cur=best;
			curdist=bestdist;
			step*=0.90;
		}
		// If colinear, adjust incenter distance
		if (icdist<0) icdist=curdist;
		printf("%0.3lf %0.3lf %0.3lf %0.3lf\n",curdist,icdist,gdist,ccdist);
	}
	return 0;
}

/* @END_OF_SOURCE_CODE */
