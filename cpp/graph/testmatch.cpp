#include <iostream>
#include <vector>
#include "match.cpp"
#include "simple_match.cpp"

using namespace std;

int main()
{
	srand(time(0));
	for(int i=0;i<100;i++) {
		Matching m;
		m.create(100,100);
		int n=rand()%500+10;
		vector< pair<int,int> > e;
		for(int j=0;j<n;j++) {
			int a=rand()%100,b=rand()%100;
			m.add(a,b);
			e.push_back(make_pair(a,b));
		}
		int *matched=m.find_maximum_matching(),cnt1=0;
		for(int i=0;i<100;i++) if (matched[i]>=0) cnt1++;
		
		int cnt2=bipartite_matching(e).size();
		
		cout << cnt1 << " " << cnt2 << endl;		
		if (cnt1!=cnt2) break;
	}
	return 0;
}
