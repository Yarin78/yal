#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>

using namespace std;

/* @JUDGE_ID: 1559ZY 10600 C++ */

struct Edge
{
	int x,y,cost;	
};

bool operator<(const Edge &e1, const Edge &e2)
{
	return e1.cost<e2.cost;
}

class UnionFind
{
	private:
		int *p,*size,m;

	public:
		UnionFind(int n) {
			m=n;
			p=new int[n];
			size=new int[n];
			for(int i=0;i<n;i++) {
				p[i]=i;
				size[i]=1;
			}
		}
		
		UnionFind(const UnionFind &v) {
			m=v.m;
			p=new int[v.m];
			size=new int[v.m];			
			for(int i=0;i<m;i++) {
				p[i]=v.p[i];
				size[i]=v.size[i];
			}
		}
		
		int find_set(int e) {
			int v=e;
			while (p[v]!=v) v=p[v];
			int root=v;
			while (p[e]!=root) { // Path compression
				int t=e;
				e=p[e];
				p[t]=root;
			}
			return root;
		}
		
		void union_set(int a, int b) {
			a=find_set(a);
			b=find_set(b);
			if (a==b) return;
			if (size[b]>size[a]) {
				int c=a; a=b; b=c;
			}			
			p[b]=a;
			size[a]+=size[b];
		}
};

int main(void)
{
	int T,N,M;
			
	cin >> T;
	for(int t=0;t<T;t++) {
		cin >> N >> M;
		vector<Edge> v(M);
		for(int i=0;i<M;i++)
			cin >> v[i].x >> v[i].y >> v[i].cost;
		sort(v.begin(),v.end());				
		
		vector<UnionFind> u;
		vector<int> added,cost;
		u.push_back(UnionFind(N));
		added.push_back(0);
		cost.push_back(0);
		int done=0;		
		
		for(int i=0;i<M && done<N;i++) {
			v[i].x--; v[i].y--;
			int n=added.size();
			for(int j=0;j<n;j++) {
				int x=u[j].find_set(v[i].x);
				int y=u[j].find_set(v[i].y);
				if (x!=y) {
					if (j==0) {
						u.push_back(UnionFind(u[0]));
						added.push_back(added[j]);
						cost.push_back(cost[j]);
					}
					cost[j]+=v[i].cost;
					added[j]++;
					if (added[j]==N-1) done++;
					u[j].union_set(x,y);
				}
			}
		}
		int s2=999999999;
		for(int i=1;i<N;i++)
			if (cost[i]<s2 && added[i]==N-1) s2=cost[i];
		cout << cost[0]  << " " << s2 << endl;
	}
	return 0;
}

/* @END_OF_SOURCE_CODE */
