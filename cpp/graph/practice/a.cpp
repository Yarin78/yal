#include <iostream>
#include <vector>
#include <set>
#include <string>

using namespace std;

// Find strong components in a directed graph

typedef vector<int> VI;
typedef vector<VI> VVI;

struct TGraph {
  VVI vin,vout;
  VI vcomp;
  TGraph(int n) : vin(n),vout(n),vcomp(n) {};
  void addedge(int x, int y) {
    vout[x].push_back(y);
    vin[y].push_back(x);
  }
};

class StrongComponents
{
  private:
    vector<int> vis,sorted,comp;
    TGraph *g;
  public:
    int no_comp;

    StrongComponents(int n, TGraph *_g) : g(_g),vis(n,0),comp(n),no_comp(0) {
      for(int i=0;i<n;i++) dfs(i,g->vout);
      for(int i=0;i<n;i++) vis[i]=0;
      for(int i=n-1;i>=0;i--)
        if (!vis[sorted[i]]) {
          dfs(sorted[i],g->vin);
          no_comp++;
        }
      g->vcomp=comp;
    };

    void dfs(int v, VVI &edges) {
      if (!vis[v]) {
        vis[v]=1;
        comp[v]=no_comp;
        for(VI::iterator i=edges[v].begin();i!=edges[v].end();i++)
          dfs(*i,edges);
        sorted.push_back(v);
      }
    }

    VVI createGraph(void)
    {
      vector< set<int> > h(no_comp);
      int n=g->vout.size();
      for(int i=0;i<n;i++)
        for(VI::iterator j=g->vout[i].begin();j!=g->vout[i].end();j++)
          h[comp[i]].insert(comp[*j]);
      VVI h2;
      for(int i=0;i<no_comp;i++) {
        h[i].erase(i);
        h2.push_back(VI(h[i].begin(),h[i].end()));
      }
      return h2;
    }
};

class SAT2
{
  private:
    TGraph g; // Odd vertices are negations
    int literals;
    VI vis,conflict;
    VVI e,litmap;
  public:
    VI value;

    SAT2(int n) : literals(n),g(n*2) {}

    void add(int x, int y)
    {
      g.addedge(x^1,y);
      g.addedge(y^1,x);
    }

    bool isSAT(void)
    {
      StrongComponents sc(literals*2,&g);
      value.resize(literals*2);
      for(int i=0;i<literals;i++)
        if (g.vcomp[i*2]==g.vcomp[i*2+1])
          return false;
      litmap.resize(sc.no_comp);
      for(int i=0;i<literals*2;i++)
        litmap[g.vcomp[i]].push_back(i);
      e=sc.createGraph();
      int n=e.size();
      vis.resize(n);
      conflict.resize(n);
      for(int i=0;i<n;i++) dfs(i);
      for(int i=0;i<literals;i++)
        if (!value[i*2] && !value[i*2+1])
          return false;
      return true;
    }

    bool dfs(int v)
    {
      bool flag=conflict[v];
      if (!vis[v]) {
        vis[v]=1;
        for(VI::iterator i=e[v].begin();i!=e[v].end();i++)
          flag|=dfs(*i);
        if (!flag) {
          for(VI::iterator i=litmap[v].begin();i!=litmap[v].end();i++)
            if (value[*i^1]) flag=true;
          if (!flag)
            for(VI::iterator i=litmap[v].begin();i!=litmap[v].end();i++)
              value[*i]=1;
        }
      }
      return conflict[v]=flag;
    }
};

int main(void)
{
  int n,m,x,y;
  cin >> n >> m;
  SAT2 sat2(m);
  while (n--) {
    cin >> x >> y;
    if (x>0) x=(x-1)*2; else x=(-x-1)*2+1;
    if (y>0) y=(y-1)*2; else y=(-y-1)*2+1;
    sat2.add(x,y);
  }
  bool ok=sat2.isSAT();
  if (!ok)
    cout << "NO" << endl;
  else {
    int cnt=0;
    for(int i=0;i<m;i++)
      if (sat2.value[i*2]) cnt++;
    cout << cnt << endl;
    for(int i=0;i<m;i++)
      if (sat2.value[i*2])
        cout << i+1 << " ";
    cout << endl;
  }
  return 0;
}
