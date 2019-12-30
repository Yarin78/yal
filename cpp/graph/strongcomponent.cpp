#include <iostream>
#include <vector>
#include <set>

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

int main(void)
{
	int n,x,y;
	cin >> n;
	TGraph g(n);
	while (cin >> x >> y)
		g.addedge(x,y);
	StrongComponents sc(n,&g);
	for(int i=0;i<sc.no_comp;i++) {
		cout << "Component " << i << ":";
		for(int j=0;j<n;j++)
			if (g.vcomp[j]==i)
				cout << " " << j;
		cout << endl;
	}
	VVI h=sc.createGraph();
	for(int i=0;i<h.size();i++) {
		cout << i << " ->";
		for(int j=0;j<h[i].size();j++)
			cout << " " << h[i][j];
		cout << endl;
	}
	return 0;
}

/*

Input:

 9
 0 1
 0 5
 1 2
 2 1
 3 2
 3 6
 3 8
 4 0
 5 4
 6 2
 6 5
 7 3
 7 6
 8 7
 
Should yield:

 Component 0: 3 7 8
 Component 1: 6
 Component 2: 0 4 5
 Component 3: 1 2
 0 -> 1 3
 1 -> 2 3
 2 -> 3
 3 ->

*/
