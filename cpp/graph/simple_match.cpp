#include <iostream>
#include <set>
#include <queue>

using namespace std;

// Bipartite matching, complexity O(|V|*(|V|+|E|))
class Matching
{
	private:
		set<int> *edge;
		int usize,vsize;
		int *uprev,*vprev,*matched,*uunsat;

	public:
		void create(int n, int m)
		{
			uprev=new int[usize=n];
			vprev=new int[vsize=m];
			edge=new set<int>[usize];
			matched=new int[vsize];
			uunsat=new int[usize];
			for(int i=0;i<usize;i++) uunsat[i]=1;
			for(int i=0;i<vsize;i++) matched[i]=-1;
		}

		void add(int x, int y) { edge[x].insert(y); }

		bool find_augmenting_path(void)
		{
			queue<int> s;
			
			fill_n(uprev,usize,-1);
			fill_n(vprev,vsize,-1);

			for(int i=0;i<usize;i++)
				if (uunsat[i]) s.push(i);
			
			while (!s.empty()) {
				int x=s.front();
				s.pop();
				for(set<int>::iterator j=edge[x].begin();j!=edge[x].end();j++) {
					int y=*j,w=matched[y];
					if (x==w) continue; // Edge already in matching
					if (w>=0) { // y is saturated
						if (vprev[y]<0) vprev[y]=x;
						if (uprev[w]<0) { uprev[w]=y; s.push(w); }
					} else { // Augmenting path found
						matched[y]=x;
						while (uprev[x]>=0) {
							y=uprev[x];
							x=vprev[y];
							matched[y]=x;
						}
						uunsat[x]=0;
						return true;
					}
				}
			}
			return false;
		}

		// Redundant, increases speed quite a lot though
		void greedy(void)
		{
			for(int x=0;x<usize;x++)
				for(set<int>::iterator j=edge[x].begin();j!=edge[x].end();j++) {
					if (matched[*j]>=0) continue;
					matched[*j]=x;
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
/*

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

void output_matching(int *matched, int vsize)
{
	int cnt=0;
	for(int i=0;i<vsize;i++) if (matched[i]>=0) cnt++;
	cout << cnt << endl;
	for(int i=0;i<vsize;i++) if (matched[i]>=0) cout << matched[i] << " " << i << endl;
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
	//show_matching(matched,vsize);
	output_matching(matched,vsize);
	return 0;
}
*/
