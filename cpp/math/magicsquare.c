#include <stdio.h>

#define MAX 100

int a[MAX][MAX];

/* Generate a magic square of size n using integers 1-n^2 */
void genMagicSquare(int n)
{
  int i,j,k,x,y,flag,v[2];
  if (n%2) {
    /* n = 2k+1 */
    for(i=0;i<n;i++)
      for(j=0;j<n;j++) {
        x=((n/2)+j-i+n)%n;
        y=(-j+2*i+n)%n;
        a[y][x]=i*n+j+1;
      }
  } else if (!(n%4)) {
    /* n = 4k */
    for(i=0;i<n;i++)
      for(j=0;j<n;j++) {
        x=j%4<2?j%4:3-j%4;
        y=i%4<2?i%4:3-i%4;
        k=i*n+j+1;
        a[i][j]=(x==y)?k:n*n+1-k;
      }
  } else {
    /* n = 4k+2 */
    for(i=flag=0;i<n/2;i++) {
      for(j=0;j<n;j++) {
        k=(n/2+i-1)%(n/2);
        x=(k+n/2-1)%(n/2);
        v[0]=((j==x) || (j==n-x-1))?i*n+(n-j-1):
           (i*n+j+(j==k?n*(1+2*(n/2-1-i)):0));
        v[1]=n*n-i*n-j-1-(j==n-k-1?n*(1+2*(n/2-1-i)):0);
        a[i][j]=v[flag]+1;
        a[n-i-1][n-j-1]=v[flag^1]+1;
        if ((j!=n/2-1) && (j!=k-1) && (j!=n-k-1)) flag^=1;
      }
    }
  }
}

int main(void)
{
	int i,j,n;
	while (scanf("%d",&n)==1) {
		genMagicSquare(n);
		for(i=0;i<n;i++) {
			for(j=0;j<n;j++)
				printf("%5d",a[i][j]);
			printf("\n");
		}
		printf("\n");
	}
}
