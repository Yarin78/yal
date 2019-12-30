#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/* @JUDGE_ID: 1559ZY F C */

int gcd(int a, int b) {
  int c;
  if (b>a) {
    c=a;
    a=b;
    b=c;
  }
  while (b>=1) {
    c=a%b;
    a=b;
    b=c;
  }
  return a;
}

void dioph(int a, int b, int *q1, int *q2)
{
	int c,a0,b0,a1,b1,a2,b2,k,r,swap=0;
	if (b>a) { c=a; a=b; b=c; swap=1; }
	a0=1; b0=0;
	a1=0; b1=1;
	while (b>=1) {
		k=a/b;
		r=a%b;
		a2=a0-k*a1;
		b2=b0-k*b1;
		a0=a1; b0=b1;
		a1=a2; b1=b2;
		a=b;
		b=r;
	};
	if (swap) { c=a0; a0=b0; b0=c; }
	*q1=a0;
	*q2=b0;
}

int a,b,n,c1,c2,n1,n2,bestx;
int best;

void check(int x)
{
	long long cost;
	cost=c1*(a-x*n2)+c2*(b+x*n1);
	/* printf("x=%d cost=%d\n",x,cost); */
	if ((best<0) || (cost<best)) {
		best=cost;
		bestx=x;
	}
}

int main(void)
{
	int i,min,max;
	

	scanf("%d",&n);
	
	while (n>0) {
		scanf("%d %d %d %d",&c1,&n1,&c2,&n2);
		i=gcd(n1,n2);
		if (n%i)
			printf("failed\n");
		else {
			n1/=i;
			n2/=i;
			n/=i;
			dioph(n1,n2,&a,&b);
			a*=n;
			b*=n;
			/* (a,b) is a specific solution */			
			min=ceil((double)-b/n1);
			max=floor((double)a/n2);
			if (min>max)
				printf("failed\n");
			else {
				/* printf("%0.2lf %d  %0.2lf %d\n",(double)a/n2,max,(double)-b/n1,min); */
				/*
				for(i=min;i<=max;i++) {
					printf("%d %d   cost=%d\n",a-i*n2,b+i*n1,c1*(a-i*n2)+c2*(b+i*n1));
				}
				*/				
				best=-1;
				check(min);
				check(max);
				/*
				i=-c1*n2+c2*n1;
				printf("%d\n",i);
				if ((i>=min) && (i<=max))
					check(i);
					*/
				printf("%d %d\n",a-bestx*n2,b+bestx*n1);
			}
		}
		scanf("%d",&n);
	}
}

/* @END_OF_SOURCE_CODE */
