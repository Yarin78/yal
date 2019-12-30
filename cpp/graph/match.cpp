#include <iostream>
#include <vector>

using namespace std;

// Bipartite matching using augmenting paths
//
// Input:  A vector of pairs containing the edges
//         The first element in a pair is a vertex
//         in one partition, and the second element is
//         a vertex in the other partition.
// Output: A subset of the input, namely those edges
//         that are in a maximum matching.
vector< pair<int,int> > bipartite_matching(vector< pair<int,int> > e)
{
	int n=e.size(),as=0,bs=0,aug=1;
	for(int i=0;i<n;i++) { as>?=e[i].first+1; bs>?=e[i].second+1; }
	vector< vector<int> > a(as);
	vector<int> ba(bs,-1),asat(as,0),q(as);
	for(int i=0;i<n;i++) a[e[i].first].push_back(e[i].second);
	while (aug--) {
		int head=0,tail=0;
		vector<int> aprev(as,-1),bprev(bs,-1);		
		for(int i=0;i<as;i++) if (!asat[i]) q[tail++]=i;
		while (head<tail&&!aug) {
			int x=q[head++];
			for(int i=0;i<a[x].size()&&!aug;i++) {
				int y=a[x][i],w=ba[y];
				if (x==w) continue;
				if (w>=0) {
					if (bprev[y]<0) bprev[y]=x;
					if (aprev[w]<0) aprev[q[tail++]=w]=y;
				} else {
					ba[y]=x;
					while ((y=aprev[x])>=0) ba[y]=x=bprev[y];
					asat[x]=aug=1;
				}
			}
		}
	}
	vector< pair<int,int> > m;
	for(int i=0;i<bs;i++) if (ba[i]>=0) m.push_back(make_pair(ba[i],i));
	return m;
}
