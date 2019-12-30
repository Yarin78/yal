#include <iostream>
#include <set>
#include <queue>

using namespace std;

// Bipartite matching using customized Hopcroft-Karp,
// complexity should be about O(sqrt(|V|)*(|V|+|E|))...
class Matching
{
	private:
		vector<int> *edge;
		int usize,vsize;
		int *uprev,*vprev,*matched,*uunsat;

	public:
		void create(int n, int m)
		{
			uprev=new int[usize=n];
			vprev=new int[vsize=m];
			edge=new vector<int>[usize];
			matched=new int[vsize];
			uunsat=new int[usize];
			fill_n(uunsat,usize,1);
			fill_n(matched,vsize,-1);
		}

		void add(int x, int y) { edge[x].push_back(y); }

		int rec(int y)
		{
			if (vprev[y]<0) return 0;
			int x=vprev[y],w=uprev[x];
			vprev[y]=-1;
			uprev[x]=-1;
			if (uunsat[x] || (w>=0 && rec(w))) {
				matched[y]=x;
				uunsat[x]=0;
				return 1;
			}
			return 0;
		}

		bool find_augmenting_path(void)
		{
			fill_n(uprev,usize,-1);
			fill_n(vprev,vsize,-1);

			queue<int> q;
			for(int i=0;i<usize;i++)
				if (uunsat[i]) q.push(i);
			
			while (!q.empty()) {				
				int x=q.front();
				q.pop();
				for(int j=0;j<edge[x].size();j++) {
					int y=edge[x][j],w=matched[y];
					/*
					if (x==w) continue; // Edge already in matching
					if (w>=0) { // y is saturated
						if (vprev[y]<0) vprev[y]=x;
						if (uprev[w]<0) { uprev[w]=y; q.push(w); }
					} else // y is unsaturated
						vprev[y]=x;
					*/
					if (vprev[y]<0) vprev[y]=x;
					if (uprev[w]<0) { uprev[w]=y; q.push(w); }
				}
			}
			// Extract a maximal set of augmenting paths
			int found=0;
			for(int i=0;i<vsize;i++)
				if (matched[i]<0)
					found|=rec(i);
			return found;
		}

		// Redundant, increases speed a bit though
		void greedy(void)
		{
			for(int x=0;x<usize;x++)
				for(int i=0;i<edge[x].size();i++) {
					if (matched[edge[x][i]]>=0) continue;
					matched[edge[x][i]]=x;
					uunsat[x]=0;
					break;
				}
		}

		int *find_maximum_matching(void)
		{
			greedy();
			while (find_augmenting_path());
			return matched;
		}
};

void show_matching(int *matched, int vsize)
{
	int cnt=0;
	cout << "Matching:";
	for(int i=0;i<vsize;i++)
		if (matched[i]>=0) {
			if (cnt<30)
				cout << " {" << matched[i] << "," << i << "}";
			else if (cnt==30)
				cout << " ...";
			cnt++;
		}
	cout << endl;
	cout << "Matching size: " << cnt << endl;
}

int main(void)
{
	Matching m;
	int usize,vsize,x,y;

	cin >> usize >> vsize;
	m.create(usize,vsize);
	cin >> x >> y;
	while (x>=0) {
		m.add(x,y);
		cin >> x >> y;
	}

	int *matched=m.find_maximum_matching();
	show_matching(matched,vsize);
	return 0;
}
