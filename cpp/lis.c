#include <stdio.h>

#define MAX 1000000
#define INF 999999999

int a[MAX],slast[MAX];

int main(void)
{
	int i,m,n,p,lo,hi;
	n=0;
	printf("reading input\n");
	while (scanf("%d",&i)==1)
		a[n++]=i;
	printf("processing\n");
	
	slast[m=0]=-INF;
	for(i=0;i<n;i++) {
		lo=0; hi=m+1;
		while (lo+1<hi)
			slast[p]>=a[i]?hi:lo=p=(lo+hi)/2;
		slast[lo+1]=a[i];
		m+=!(lo-m);
	}
	printf("%d\n",m);
	for(i=1;i<=m;i++)
		printf("%d ",slast[i]);
	return 0;
}
