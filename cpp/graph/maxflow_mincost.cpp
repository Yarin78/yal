#include<cstdio>
#include<iostream>
#include<vector>
#include<algorithm>

using namespace std;

const int infty = 2000000000;

struct Edge {
  Edge *other;
  int from, to;
  int flow;
  const int cap;
  double w;
  
  Edge (int f, int t, double wt, int c=0)
    : other(0), from(f), to(t), flow(0), cap(c), w(wt) {};

  void couple (Edge *e) { other = e; e->other = this; };
};


typedef vector<Edge *> Edges;

// returns -1 or a vertex in a negative cycle
int bellman_ford(int n, int s, 
		 Edges &edges,
		 Edges &p, 
		 vector<double> &d)
{

  int nedges = edges.size();

  p.clear();
  p.resize(n);
  d.clear();
  d.resize(n);

  for (int i = 0; i < n; ++i) {
    p[i] = 0;
    d[i] = infty;
  }
  d[s] = 0.0;
  
  bool change = true;

  for (int m = 1; change && m < n-1; ++m) {
    change = false;
    
    for (int i = 0; i < nedges; ++i)
      if (edges[i]->cap > edges[i]->flow) {
	int a = edges[i]->from;
	int b = edges[i]->to;
	double wab = edges[i]->w;
	if (d[a] != infty && d[b] > d[a] + wab) {
	  change = true;
	  
	  d[b] = d[a] + wab;
	  p[b] = edges[i];
	}
      }
  }
  
  for (int i = 0; i < nedges; ++i)
    if (edges[i]->cap > edges[i]->flow) {
      int a = edges[i]->from;
      int b = edges[i]->to;
      double wab = edges[i]->w;
      if (d[a] != infty && d[b] > d[a] + wab) {
	/*
	cerr << "d[" << a << "]=" << d[a] << "  "
	     << "d[" << b << "]=" << d[b] << "  w="
	     << wab << endl;
	*/
	p[b] = edges[i];
	return b;  // found a negative cycle
      }
    }
  return -1;
}


void adjust_cycle(const int u, vector<Edge *> &p)
{
  //  cerr << "> " << u << endl;
  double dst = p[u]->w;
  int minc = p[u]->cap - p[u]->flow;
  for (int v = p[u]->from; v != u; v = p[v]->from) {
    //    cerr << "> " << v << endl;
    minc = min(minc, p[v]->cap - p[v]->flow);
    dst += p[v]->w;
  }
  /*
  cerr << " cycle weight " << dst << endl;
  if (minc == 0) cerr << "cannot adjust cycle!!" << endl;
  else cerr << "adjusting by " << minc << endl;
  */
  p[u]->flow += minc;
  p[u]->other->flow -= minc;
  //  cerr << p[u]->from << " to " << u << " has " << p[u]->flow << endl;
  for (int v = p[u]->from; v != u; v = p[v]->from) {
    p[v]->flow += minc;
    p[v]->other->flow -= minc;
  }
}

void increment_flow(int s, int t, Edges &e, vector<Edge *> &p)
{

  int minc = infty;
  for (int u = t; u != s; u = p[u]->from) {
    //    cerr << "> " << u << endl;
    minc = min(minc, p[u]->cap - p[u]->flow);
  }
  //  cerr << "> " << s << endl;

  //  cerr << "incrementing by " << minc << endl;
  for (int u = t; u != s; u = p[u]->from) {
    p[u]->flow += minc;
    p[u]->other->flow -= minc;
  }
}


void mincost_maxflow(int n, int s, int t, Edges &edges)
{
  Edges p;
  int nedges = edges.size();
  for (int i = 0; i < nedges; ++i) edges[i]->flow = 0;
  vector<double> d;
  int cnt = 0;
  while (true) {
    ++ cnt;
    //    cerr << cnt << endl;
    int u = bellman_ford(n, s, edges, p, d);
    if ( u == -1 ) { // no negative cycle, but augmenting path
      //      cerr << "distance to t is " << d[t] << endl;
      if (p[t] == 0) break;
      else increment_flow(s, t, edges, p);
    }
    else { // negative cycle
      //      cerr << "Negative cycle!!" << endl;
      adjust_cycle(u, p);
      
      //    break;
    }
  }
  
}


void solve() 
{
  int nu, nt;
  cin >> nu >> nt;
  vector< pair<int,int> > unit(nu);
  vector< pair<int,int> > target(nt);
  vector< int > m(nt);

  for (int i = 0; i < nu; ++i) 
    cin >> unit[i].first >> unit[i].second;
  for (int i = 0; i < nt; ++i) 
    cin >> target[i].first >> target[i].second >> m[i];

  int nverts = nu + nt + 2;
  int s = nu + nt;
  int t = s+1;
  
  Edges es;
  for (int u = 0; u < nu; ++u) {
    Edge *e1 = new Edge(s, u, 0.0, 8);
    Edge *e2 = new Edge(u, s, 0.0);
    e1->couple(e2);
    es.push_back(e1);
    es.push_back(e2);
  }

  for (int u = 0; u < nt; ++u) {
    Edge *e1 = new Edge(u+nu, t, 0.0, m[u]);
    Edge *e2 = new Edge(t,u+nu, 0.0);
    e1->couple(e2);
    es.push_back(e1);
    es.push_back(e2);
  }

  for (int u = 0; u < nu; ++u) {
    for (int v = 0; v < nt; ++v) {
      double dx = (unit[u].first - target[v].first);
      double dy = (unit[u].second - target[v].second);
      //      cerr << "("<< u <<"," << nu+v << ") " << dx << " " << dy << endl;  
      double dst = (int)(sqrt(dx*dx+dy*dy));  // round to integer!!
      Edge *e1 = new Edge(u, nu+v,  dst, 8);
      Edge *e2 = new Edge(nu+v, u, -dst, 0);
      e1->couple(e2);
      es.push_back(e1);
      es.push_back(e2);
    }
  }

  /*
  for (Edges::iterator e = es.begin(); e != es.end(); ++e) {
    cerr << "edge (" << (*e)->from << "," << (*e)->to
	 << "  w=" << (*e)->w << "  c=" << (*e)->cap << endl;
    if ((*e)->w + (*e)->other->w != 0) cerr << "Warning!!" << endl;
  }
  */
  mincost_maxflow(nverts, s, t, es);
  int totdist = 0;
  for (Edges::iterator e = es.begin(); e != es.end(); ++e) {
    int u = (*e)->from;
    int v = (*e)->to;
    if (u < nu && v >= nu && v < nu+nt )
      totdist += (*e)->flow * (*e)->w;
  }
  
  printf("%d\n", totdist);

  for (Edges::iterator e = es.begin(); e != es.end(); ++e)
    delete (*e);
  
}




int main() 
{
  int T;
  for (cin >> T; T ; --T) solve();
  return 0;
}
