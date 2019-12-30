#include <stdio.h>

/* @JUDGE_ID: 1559ZY 10294 C */

int gcd(int a, int b)
{
	if (!b) return a;
	return gcd(b,a%b);
}

int phi(int d)
{
	int i,cnt=0;
	for(i=1;i<=d;i++)
		if (gcd(i,d)==1)
			cnt++;
	return cnt;
}

long long pow(int base, int exp)
{
	long long prod=1;
	while (exp--)
		prod*=base;
	return prod;
}

int main(void)
{	
	int d,n,t;
	long long necklace,bracelet;
	
	while (scanf("%d %d",&n,&t)==2) {
		necklace=0; /* Can't be overturned */
		bracelet=0; /* Can be overturned */
		
		/*
		  Cycle index of C_n is
		  
		  1/n * sum(d is a divisor of n | phi(d) * x_d^(n/d))
		*/
		
		for(d=1;d<=n;d++) {
			if (n%d) continue;
			/* printf("%d x%d ^ %d\n",phi(d),d,n/d); */
			necklace+=phi(d)*pow(t,n/d);
		}
		necklace/=n;
		
		/*
		   Cycle index of D_2n is
		
		   1/2 * C_n(x_1,x_2,...,x_n) + 1/4 * (x_2^(n/2) + x_1^2*x_2^(n/2-1))    (n is even)
		   1/2 * C_n(x_1,x_2,...,x_n) + 1/2 * x_1 * x_2^((n-1)/2)                (n is odd)
		*/
		
		if (n&1)
			bracelet=(necklace+t*pow(t,(n-1)/2))/2;
		else
			bracelet=(necklace*2+(pow(t,n/2)+t*t*pow(t,n/2-1)))/4;
		
		printf("%lld %lld\n",necklace,bracelet);
	}	
	return 0;
}

/* @END_OF_SOURCE_CODE */
