#include <iostream>
#include <cstdlib>
#include <vector>
#include <algorithm>

int main(void)
{
	int usize,vsize,noedges;
	cin >> usize >> vsize >> noedges;
	vector< pair<int,int> > edge;
	cout << usize << " " << vsize << endl;
	for(int i=0;i<noedges;i++) {
		int x=rand()%usize;
		int y=rand()%vsize;
		edge.push_back(pair<int,int>(x,y));
	}
	sort(edge.begin(),edge.end());
	for(int i=0;i<noedges;i++)
		cout << edge[i].first << " " << edge[i].second << endl;	
	cout << "-1 -1" << endl;
	return 0;
}
