#include <iostream>
#include <vector>
#include <cmath>

using namespace std;

typedef long long s64;

// Solves x^2-D*y^2=1
template<class T>
void pell(int D, T &x, T &y)
{
	vector<T> a,p,q,P,Q;
	a.push_back((T)floor(sqrt(double(D))));
	p.push_back(a[0]);	
	q.push_back(1);
	P.push_back(0);
	Q.push_back(1);
	
	int n=0;
	do {
		n++;
		P.push_back(n==1 ? a[0] : a[n-1]*Q[n-1]-P[n-1]);
		Q.push_back(n==1 ? D-a[0]*a[0] : (T)floor((D-P[n]*P[n])/Q[n-1]));
		a.push_back((T)floor((a[0]+P[n])/Q[n]));
		p.push_back(n==1 ? a[0]*a[1]+1 : a[n]*p[n-1]+p[n-2]);
		q.push_back(n==1 ? a[1] : a[n]*q[n-1]+q[n-2]);		
	} while (a[n]!=a[0]*2 || (n%2));
	
	x=p[n-1];
	y=q[n-1];
}

int main()
{
	double largest=0,best=0;
	for(int d=2;d<=1000;d++) {
		int ds=(int)sqrt(double(d));
		if (ds*ds==d) continue;
		
		double x,y;
		pell<double>(d,x,y);
		if (x>largest) {
			largest=x;
			best=d;
		}		
		printf("%4d: x = %20.0lf  y = %20.0lf\n",d,x,y);
	}
	cout << "d = " << best << endl;
	return 0;
}
