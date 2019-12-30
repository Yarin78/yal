#include <stdio.h>

#define MAX 21

typedef int T;

T dp[1<<MAX];
int flag[1<<MAX];

int detr(int a[], int n, int row, int mask)
{
	int i,sign=1,sum=0;
	//if (flag[mask]) return dp[mask];
	if (row==n) return 1;
	for(i=0;i<n;i++) {
		if ((1<<i)&mask) continue;
		sum+=sign*a[row*MAX+i]*detr(a,n,row+1,mask|(1<<i));
		sign=-sign;
	}
	flag[mask]=1;
	return dp[mask]=sum;
}

int det(int a[], int n)
{
	memset(flag,0,sizeof(flag));
	return detr(a,n,0,0);
}

int main(void)
{
	int i,j,n;
	int a[MAX*MAX];

	scanf("%d",&n);
	for(i=0;i<n;i++)
		for(j=0;j<n;j++)
			scanf("%d",a+i*MAX+j);
	
	printf("%d\n",det(a,n));
	return 0;
}
