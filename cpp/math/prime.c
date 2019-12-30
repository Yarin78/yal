#include <stdio.h>
#include <memory.h>

/*
  Even faster prime generation:

#define MAXSIEVE 100000000   // All prime numbers up to this
#define MAXSIEVEHALF (MAXSIEVE/2)
#define MAXSQRT 5000 // sqrt(MAXSIEVE)/2
char a[MAXSIEVE/16+2];
#define isprime(n) (a[(n)>>4]&(1<<(((n)>>1)&7)))  // Works when n is odd

	int i,j;
	memset(a,255,sizeof(a));
  a[0]=0xFE;
  for(i=1;i<MAXSQRT;i++)
    if (a[i>>3]&(1<<(i&7)))
      for(j=i+i+i+1;j<MAXSIEVEHALF;j+=i+i+1)
        a[j>>3]&=~(1<<(j&7));

*/

#define MAX 10005

char a[MAX];
int primes[MAX],np;

int isprime(int n)
{
	int i;
	if (n==1) return 0;
	for(i=0;(i<np) && (primes[i]*primes[i]<=n);i++)
		if (!(n%primes[i]))
			return 0;
	return 1;
}

int main(void)
{
	int i,j;
	
	np=0;
	memset(a,1,sizeof(a));
	for(i=2;i<MAX;i++)
		if (a[i]) {
			primes[np++]=i;
			for(j=i;j<MAX;j+=i)
				a[j]=0;
		}
	for(i=0;i<10;i++)
		printf("%d\n",primes[i]);
	return 0;
}
