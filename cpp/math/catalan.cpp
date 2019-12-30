#include <stdio.h>
#include <iostream>
#include <assert.h>

using namespace std;

//#include "marcus/bignum.h"

/* CATALAN NUMBERS 

   In how many ways can a sum of n terms be bracketed so that it can
   be calculated by adding two terms at a time? Answer: C(n)
   
   How many different binary search tree with n nodes are there? Answer: C(n+1)
*/

#define MAXSIEVE 2000   /* All prime numbers up to this */
#define MAXPRIMES 2000  /* Maximum number of primes in 1..MAXSIEVE */
#define MAXSIEVEHALF (MAXSIEVE/2)
#define isprime(n) (tmp[(n)>>4]&(1<<(((n)>>1)&7)))  /* Works when n is odd */

char tmp[MAXSIEVE/16+2];
int primes[MAXPRIMES],no_primes=0;

int cnt[MAXPRIMES];

typedef long long T;
//typedef Bignum T;

/* 70 choose 35 == largest to fit in a long long */
/* catalan(34)  == largest to fit in a long long */

void choose_addfactor(int n, int sign)
{
	int i=0;
	while (n>1) {
		assert(i<no_primes);
		while (!(n%primes[i])) {
			n/=primes[i];
			cnt[i]+=sign;
		}
		i++;
	}
}

void choose_add(int n, int k)
{
	int i;
	if ((k<0) || (k>n)) return;
	if (k>n/2) k=n-k;
	for(i=n-k+1;i<=n;i++)
		choose_addfactor(i,1);
	for(i=2;i<=k;i++)
		choose_addfactor(i,-1);
}

T choose_get(void)
{
	int i;
	T sum=1;
	long long prod=1;
	long long overflow=(((long long)1<<63)-1)/2000;
	for(i=0;i<no_primes;i++)
		while (cnt[i]--) {
			if (prod>overflow) {
				sum*=prod;
				prod=1;
			}
			prod*=primes[i];
		}
	return sum*prod;
}

/*
T choose_get(void)
{
	int i;
	T sum=1;
	for(i=0;i<no_primes;i++)
		while (cnt[i]--)
			sum*=primes[i];
	return sum;
}
*/

/* Calculate n choose k - if overflow, return -1 */
T choose(int n, int k)
{
	if ((k<0) || (k>n)) return 0;
	memset(cnt,0,sizeof(cnt));
	choose_add(n,k);
	return choose_get();
}

/* Calculate the n'th Catalan number - if overflow, return -1 */
T catalan(int n)
{
	T c;
	c=choose(2*n-2,n-1);
	if (c<0) return -1;
	return c/n;
}

int main(void)
{
	int i,j;
	int n,k;

	memset(tmp,255,sizeof(tmp));
  tmp[0]=0xFE;
  primes[no_primes++]=2;
  for(i=1;i<MAXSIEVEHALF;i++)
    if (tmp[i>>3]&(1<<(i&7))) {
    	primes[no_primes++]=i*2+1;
      for(j=i+i+i+1;j<MAXSIEVEHALF;j+=i+i+1)
        tmp[j>>3]&=~(1<<(j&7));
		}
	while (scanf("%d",&n)==1)
		cout << catalan(n) << endl;
	return 0;
}
