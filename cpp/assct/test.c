#include <stdio.h>
#include <stdlib.h>

#define MAX 20

int a[100][100],precalc[MAX][1<<MAX];

int calc(int n, int mask, int row)
{
	int i,j,best,first=1;
	if (precalc[row][mask]>=0)
		return precalc[row][mask];
	if (n==row) return 0;
	for(i=0;i<n;i++)
		if ((1<<i)&mask) {
			j=a[row+1][i+1]+calc(n,mask-(1<<i),row+1);
			if (first || (j<best)) {
				best=j;
				first=0;
			}
		}
	return precalc[row][mask]=best;
}

int assct(int n)
{
	memset(precalc,-1,sizeof(precalc));
	return calc(n,(1<<n)-1,0);
}


int main(void)
{
	int i,j,n,t;
	n=20;
	for(i=1;i<=n;i++) {
		for(j=1;j<=n;j++) {
			a[i][j]=random()%1000;
		}
	}
	t=assct(n);
	printf("Cost = %d\n",t);
//	for(i=0;i<n;i++)
	//	printf("%d ",c[i]);
	//printf("\n");
	return 0;
}
