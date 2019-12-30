#include <cstdio>
#include <iostream>
#include <cmath>
#include <algorithm>
#include <vector>
#include <set>
#include <map>


// Line intersection

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

// Returns TRUE if the point b lies on the line segment a (including endpoints)
bool on_lineseg(TLine a, TPoint b)
{
	TPoint v1=a.f-b,v2=b-a.t;
	T d0=dot(v1,v2);
	return isnull(det(v1,v2)) && (isnull(d0) || (d0>T(0)));
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


TLine *line;
TPoint sweep_point;

TPoint getvect(const TLine &a)
{
	TPoint u(a.f.x-a.t.x,a.f.y-a.t.y);
	if (u.y<T(0)) { u.x=-u.x; u.y=-u.y; }
	if (isnull(u.y)) u.x=-abs(u.x);
	return u;
}

// Return TRUE if the line is to the left of p at the same y-coordinate
// (undefined if line is horizontal with different y-coordinate than p)
bool cmp_linepoint(TLine &a, TPoint p)
{
	if (a.f.y==a.t.y) return a.f.x<p.x;
	T x1=(a.t.x-a.f.x)*(p.y-a.f.y)/(a.t.y-a.f.y)+a.f.x;
	if (isnull(x1-p.x)) return false;
	return x1<p.x;
}

// Returns TRUE if line a is to the left of line b at the point p
bool cmp_lineline(const TLine &a, const TLine &b, TPoint p)
{
	T x1=p.x,x2=p.x;
	if (!isnull(a.t.y-a.f.y)) x1=(a.t.x-a.f.x)*(p.y-a.f.y)/(a.t.y-a.f.y)+a.f.x;
	if (!isnull(b.t.y-b.f.y)) x2=(b.t.x-b.f.x)*(p.y-b.f.y)/(b.t.y-b.f.y)+b.f.x;
	if (isnull(x1-x2)) return false;
	return x1<x2;
}

// Returns TRUE if line a is to the left of line b below their intersection point
bool cmp_linelinebelow(const int &a, const int &b)
{
	TPoint u=getvect(line[a]),v=getvect(line[b]);
	return det(u,v)>T(0);
}

struct TEvent
{
	TPoint p;
	set<int> upper,lower;

	bool operator<(const TEvent &rhs) const {
		if (!isnull(p.y-rhs.p.y)) return p.y>rhs.p.y;
		if (!isnull(p.x-rhs.p.x)) return p.x<rhs.p.x;
		return 0;
	}
};

struct statsort
{
	bool operator()(const int a, const int b) const
	{
		if (a>=0 && b>=0)
			return cmp_lineline(line[a],line[b],sweep_point);
		if (a<0 && b<0) return false;
		if (a<0) {
			if (on_lineseg(line[b],sweep_point)) return false;
			return !cmp_linepoint(line[b],sweep_point);
		}
		if (on_lineseg(line[a],sweep_point)) return false;
		return cmp_linepoint(line[a],sweep_point);
	}
};

typedef multiset<int,statsort> Tstat;

set<TEvent> eventq;
Tstat stat;

void add_event(TEvent &e)
{
	set<TEvent>::iterator i=eventq.find(e);
	if (i==eventq.end())
		eventq.insert(e);
	else {
		e.upper.insert(i->upper.begin(),i->upper.end());
		e.lower.insert(i->lower.begin(),i->lower.end());
		eventq.erase(i);
		eventq.insert(e);
	}
}

void find_new_event(int i1, int i2, TPoint p)
{
	TEvent e;
	if (lineseg_intersect_inclusive(line[i1],line[i2],&e.p))
		if (e.p>p)
			add_event(e);
}

map<TPoint,set<int> > findall_lineseg_intersections(TLine *_line, int n)
{			
	line=_line;
	
	for(int i=0;i<n;i++) {
		TEvent e1,e2;
		e1.p=line[i].f;
		e2.p=line[i].t;
		(e1<e2?e1:e2).upper.insert(i);
		(e1>e2?e1:e2).lower.insert(i);
		add_event(e1);
		add_event(e2);
	}
	
	map<TPoint,set<int> > isectlist;
	
	while (!eventq.empty()) {
		TEvent e=*eventq.begin();
		eventq.erase(eventq.begin());
		sweep_point=e.p;
		
		pair<Tstat::iterator,Tstat::iterator> r;
		r=stat.equal_range(-1);
		
		Tstat interior;
		for(Tstat::iterator i=r.first;i!=r.second;i++)
			if (e.lower.find(*i)==e.lower.end())
				interior.insert(*i);
		if (e.upper.size()+e.lower.size()+interior.size()>1) {
			set<int> isect(interior.begin(),interior.end());
			isect.insert(e.upper.begin(),e.upper.end());
			isect.insert(e.lower.begin(),e.lower.end());
			isectlist[e.p]=isect;
		}
		
		vector<int> toinsert(e.upper.begin(),e.upper.end());
		toinsert.insert(toinsert.end(),interior.begin(),interior.end());
		sort(toinsert.begin(),toinsert.end(),cmp_linelinebelow);

		Tstat::iterator rn(r.second);
		Tstat::reverse_iterator ln(r.first);
		
		if (!toinsert.size()) {
			if (rn!=stat.end() && ln!=stat.rend())
				find_new_event(*ln,*rn,e.p);
		} else {
			if (ln!=stat.rend())
				find_new_event(*ln,toinsert.front(),e.p);
			if (rn!=stat.end())
				find_new_event(*rn,toinsert.back(),e.p);
		}

		stat.erase(r.first,r.second);
		stat.insert(toinsert.begin(),toinsert.end());
	}
	return isectlist;
};

int main(void)
{
	T x1,y1,x2,y2;
	vector<TLine> lines;
	while (cin >> x1 >> y1 >> x2 >> y2) {
		if (x1==x2 && y1==y2) break;
		TLine l;
		l.f.x=x1; l.f.y=y1;
		l.t.x=x2; l.t.y=y2;
		lines.push_back(l);
	}

	map< TPoint, set<int> > isect;
	isect=findall_lineseg_intersections(lines.begin(),lines.size());
	
	for(map<TPoint,set<int> >::iterator i=isect.begin();i!=isect.end();i++) {
		cout << (double)i->first.x << "," << (double)i->first.y << ":";
		for(set<int>::iterator j=i->second.begin();j!=i->second.end();j++)
			cout << " " << *j;
		cout << endl;
	}
	
	return 0;
}
