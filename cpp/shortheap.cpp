#include <cstdio>
#include <cstdlib>
#include <ctime>

using namespace std;

template<class T>
int swap(T &a, T &b) { T t(a); a=b; b=t; }

template<class T>
int up(T *d, int p) {
    int u=(p-1)/2;
    return p?d[p]<d[u]?swap(d[u],d[p]),up(d,u):0:0;
}
/*
template<class T>
int down(T *d,int p,int N) {
    int u=p*2+2;
    if (u<=N) {
        int n=u==N?u-1:d[u]>d[u-1]?u-1:u;
        return d[p]>d[n]?swap(d[n],d[p]),down(d,u,N):0;
    }
    return 0;
}
*/

template<class T>
int down(T *d, int p, int N) {
  int u=p*2+2;
  if (u<=N) {
  	int n=u==N?u-1:d[u-1]<d[u]?u-1:u;
    return d[n]<d[p]?swap(d[n],d[p]),down(d,u,N):0;
  }
  return 0;
}

template<class T>
bool isheap(T *d, int p, int n) {
	int lc=p*2+1,rc=p*2+2;
	if ((lc<n) && (d[lc]<d[p] || !isheap(d,lc,n))) return false;
	if ((rc<n) && (d[rc]<d[p] || !isheap(d,rc,n))) return false;
	return true;
}

int main(void)
{
	const int N=10;
	
	int a[N];

	srand(time(0));
	for(int i=0;i<N;i++) {
		a[i]=rand()%10000;
		up<int>(a,i);
		for(int j=0;j<=i;j++) printf("%d ",a[j]);
		printf("=> %d\n",isheap<int>(a,0,i+1));
	}
	
	int n=N;
	while (n) {
		a[0]=a[--n];
		down<int>(a,0,n);
		for(int j=0;j<n;j++) printf("%d ",a[j]);
		printf("=> %d\n",isheap<int>(a,0,n));
	}
	
	return 0;
}
