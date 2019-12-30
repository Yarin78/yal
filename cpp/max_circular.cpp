#include <iostream>
#include <vector>

using namespace std;

int max_linear(const vector<int> &a, int &start, int &stop, int mul=1)
{
	int sum=0,best=0,curstart=0;
	start=0; stop=0;
	for(int i=0;i<a.size();i++) {
		sum+=a[i]*mul;
		if (sum<0) { sum=0; curstart=i+1; }
		if (sum>best) {
			best=sum;
			start=curstart;
			stop=i+1;
		}
	}
	return best;
}

int max_circular(const vector<int> &a, int &start, int &stop)
{
	int sum=0,b,e;
	for(int i=0;i<a.size();i++) sum+=a[i];
	int m1=max_linear(a,start,stop);
	int m2=sum+max_linear(a,b,e,-1);
	if (m2>m1) { start=e; stop=b; m1=m2; }
	return m1;
}


int main()
{
	srand(time(0));
	vector<int> a;
	int n,start,stop;
	cin >> n;
	for(int i=0;i<n;i++)
		a.push_back(rand()%1001-500);
//	for(int i=0;i<n;i++) cout << a[i] << " "; cout << endl;
	
	cout << max_circular(a,start,stop);
	cout << " " << start << " " << stop << endl;
	
	int most=0;
	for(int i=0;i<n;i++) {
		int b,e;
		int v=max_linear(a,b,e);
		if (v>most) { most=v; start=(b+i)%n; stop=(e+i)%n; }
		int first=a[0];
		for(int j=0;j<n;j++)
			a[j]=a[j+1];
		a[n-1]=first;
	}
	cout << most << " " << start << " " << stop << endl;
	return 0;
}
