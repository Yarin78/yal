#include <iostream>
#include <vector>

using namespace std;

// Network Flow
// Using Edmonds-Karp algorithm (Ford-Fulkersson with BFS)
// Complexity: O(V*E^2)

struct TEdge
{
	int src,dest,flow,cap;
	TEdge(int a, int b, int c) : src(a),dest(b),flow(0),cap(c) {};
};

struct TVertex
{
	vector<TEdge*> e;
	int slack;
	TEdge *used;
};

vector<TVertex> vert;

void addedge(int src, int dest, int cap)
{
	if (src==dest) return;
	TEdge* e=new TEdge(src,dest,cap);
	vert[src].e.push_back(e);
	vert[dest].e.push_back(e);
}

bool find_augmenting_path(int source, int sink)
{
	int q[vert.size()],head=0,tail=0;
	for(int i=0;i<vert.size();i++)
		vert[i].used=0;
	vert[source].slack=1<<30;
	q[tail++]=source;
	while ((head<tail) && !vert[sink].used) {
		int x=q[head++];
		for(int i=0;i<vert[x].e.size();i++) {
			TEdge *e=vert[x].e[i];
			int y=e->src+e->dest-x;
			if (vert[y].used) continue;
			int s=e->src==x?(e->cap-e->flow):e->flow;
			if (s) {
				vert[y].slack=min(vert[x].slack,s);
				vert[y].used=e;
				q[tail++]=y;
			}
		}
	}
	return vert[sink].used;
};

void use_path(int source, int sink)
{
	int x=sink,slack=vert[sink].slack;
	while (x!=source) {
		//cout << x << " <- ";
		if (vert[x].used->dest==x) {
			vert[x].used->flow+=slack;
			x=vert[x].used->src;
		} else {
			vert[x].used->flow-=slack;
			x=vert[x].used->dest;
		}
	}
	//cout << x << "   slack = " << slack << endl;
}

int find_max_flow(int source, int sink)
{
	while (find_augmenting_path(source,sink))
		use_path(source,sink);
	int flow=0;
	for(int i=0;i<vert[source].e.size();i++)
		if (vert[source].e[i]->src==source)
			flow+=vert[source].e[i]->flow;
	return flow;
}

vector<TEdge*> find_min_cut(void)
{
	vector<TEdge*> mincut;
	for(int i=0;i<vert.size();i++) {
		if (!vert[i].used) continue;
		for(int j=0;j<vert[i].e.size();j++) {
			TEdge *e=vert[i].e[j];
			if (e->src!=i || vert[e->dest].used) continue;
			mincut.push_back(e);
		}
	}
	return mincut;
}

/*
void showgraph(void)
{
	for(int i=0;i<vert.size();i++) {
		cout << "Vertex " << i << ":" << endl;
		for(int j=0;j<vert[i].e.size();j++) {
			TEdge *e=vert[i].e[j];
			if (e->src!=i) continue;
			cout << " " << e->src << " -> " << e->dest << ": "
				<< e->flow << " (" << e->cap << ")" << endl;
		}
	}
}
*/

int main(void)
{
	int n,source,sink,a,b,c;

	cin >> n >> source >> sink;
	vert.resize(n);
	while (cin >> a >> b >> c)
		addedge(a,b,c);

	int maxflow=find_max_flow(source,sink);
	cout << "Maximum flow: " << maxflow << endl;

	vector<TEdge*> cut=find_min_cut();
	cout << "Minimum cut: " << endl;
	for(int i=0;i<cut.size();i++)
		cout << " " << cut[i]->src << " -> " << cut[i]->dest << endl;

	//showgraph();
	return 0;
}

/*

The following graph

11 0 4
0 1 3
0 5 7
0 8 4
1 2 6
1 8 5
2 3 7
3 4 4
5 6 6
6 7 9
7 4 2
7 2 5
8 9 9
9 10 7
10 4 8

should yield output

4 <- 3 <- 2 <- 1 <- 0   slack = 3
4 <- 7 <- 6 <- 5 <- 0   slack = 2
4 <- 10 <- 9 <- 8 <- 0   slack = 4
4 <- 3 <- 2 <- 7 <- 6 <- 5 <- 0   slack = 1
4 <- 10 <- 9 <- 8 <- 1 <- 2 <- 7 <- 6 <- 5 <- 0   slack = 3
Maximum flow: 13
Minimum cut:
 0 -> 1
 0 -> 8
 5 -> 6

*/
