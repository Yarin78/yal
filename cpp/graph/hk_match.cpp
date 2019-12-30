#include <iostream>
#include <vector>

using namespace std;

// Bipartite matching using customized Hopcroft-Karp,
// complexity should be about O(sqrt(|V|)*(|V|+|E|))...
// _Very_ few calls to find_augmenting_path needed.
class BipartiteMatching
{
	private:
		vector<int> *edge;
		int usize,vsize;
		int *uprev,*vprev,*matched,*uunsat,*q;
	public:
		void create(int n, int m)
		{
			uprev=new int[usize=n];
			vprev=new int[vsize=m];
			edge=new vector<int>[usize];
			uunsat=new int[usize];
			q=new int[usize];
			matched=new int[vsize];
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
			if (!uunsat[x] && (w<0 || !rec(w))) return 0;
			matched[y]=x;
			uunsat[x]=0;
			return 1;
		}

		int find_augmenting_path(void)
		{
			fill_n(uprev,usize,-1);
			fill_n(vprev,vsize,-1);
			int head=0,tail=0,found=0;
			for(int i=0;i<usize;i++)
				if (uunsat[i]) q[tail++]=i;
			while (head<tail) {
				int x=q[head++];
				for(int j=0;j<edge[x].size();j++) {
					int y=edge[x][j],w=matched[y];
					if (vprev[y]<0) vprev[y]=x;
					if (w>=0 && uprev[w]<0) { uprev[w]=y; q[tail++]=w; }
				}
			}
			for(int i=0;i<vsize;i++)
				if (matched[i]<0)
					found|=rec(i);
			return found;
		}

		int *find_maximum_matching(void)
		{
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
	BipartiteMatching m;
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
