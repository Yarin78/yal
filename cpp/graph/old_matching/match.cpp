#include <iostream>
#include <cstdio>
#include <set>
#include <queue>
#include <vector>

//#define DBG
#define STRICT
#define GREEDY

using namespace std;

// Bipartite matching using "simplified" Hopcroft-Karp,
// complexity should be about O(sqrt(|V|)*(|V|+|E|))...
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
			int found=0;
			queue<int> s;
			
			fill_n(uprev,usize,-1);
			fill_n(vprev,vsize,-1);

#ifdef DBG	
			int alen=0;
#endif
			
			s.push(-1);
			for(int i=0;i<usize;i++)
				if (uunsat[i]) s.push(i);
			
			while (1) {
				int x=s.front();
				s.pop();
				if (x==-1) {
#ifdef STRICT				
					if (found) break;
					if (s.empty()) return false;
#else					
					if (s.empty()) break;
#endif					
#ifdef DBG					
					alen++;			
#endif					
					s.push(-1);
					continue;
				}
					
				for(set<int>::iterator j=edge[x].begin();j!=edge[x].end();j++) {
					int y=*j,w=matched[y];
					if (x==w) continue; // Edge already in matching
					if (w>=0) { // y is saturated
						if (vprev[y]<0) vprev[y]=x;
						if (uprev[w]<0) { uprev[w]=y; s.push(w); }
					} else { // y is unsaturated
						vprev[y]=x;
						found=1;
					}
				}
			}
#ifndef STRICT			
			if (!found) return false;
#endif
#ifdef DBG				
			printf("Finding augmenting paths of len %d:",alen);
#endif			
			// Extract a maximal set of augmenting path
			for(int i=0;i<vsize;i++)
				if (matched[i]<0)
					rec(i);
#ifdef DBG
			printf("\n");
#endif
			return true;
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
#ifdef GREEDY			
			greedy();
#endif			
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

#ifdef DBG
	cout << "Reading graph..." << endl;
#endif
	cin >> usize >> vsize;
	m.create(usize,vsize);
	cin >> x >> y;
	while (x>=0) {
		m.add(x,y);
		cin >> x >> y;
	}

#ifdef DBG
	cout << "Finding maximum matching..." << endl;
#endif
	int *matched=m.find_maximum_matching();
#ifdef DBG
	show_matching(matched,vsize);
#endif
	output_matching(matched,vsize);
	return 0;
}
