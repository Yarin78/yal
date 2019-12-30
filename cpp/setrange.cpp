#include <iostream>
#include <memory.h>

using namespace std;

const int EXP=10;

// 0..(2^EXP)-1 is max range
int t[1<<EXP+1];
int query(int x,int i=EXP) { return x?(x&1)*t[(1<<i)+x-1]+query(x/2,i-1):0; }
int insert(int x,int v,int i=EXP) {	return (t[(1<<i)+x]+=v)+(i&&insert(x/2,v,i-1)); }


int main()
{
	memset(t,0,sizeof(t));
	/*
	int x,v;
	while (1) {
		cout << "x & v (v=0 for query)" << endl;
		cin >> x >> v;
		if (v!=0)
			insert(x,v);
		else
			cout << query(x) << endl;
	}
	*/
	
	insert(5, 10); // a[5] = 10
	insert(6, 6);  // a[6] = 6;
	insert(10, 2); // a[10] = 2;
	cout << query(5) << endl; // sum(a[0..4]) = 0
	cout << query(6) << endl; // sum(a[0..5]) = 10
	cout << query(7) << endl; // sum(a[0..6]) = 16
	cout << query(15) - query(6) << endl; // sum(a[6..14]) = 8
	
	insert(6, -6); // a[6] -= 6 (dvs 0)
	cout << query(7) << endl; // sum(a[0..6]) = 10
	
	return 0;
	
}
