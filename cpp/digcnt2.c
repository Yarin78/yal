#include <stdio.h>

typedef long long s64;

s64 pow10(int exp)
{
	s64 pow=1;
	if (exp<0) return 0;
	while (exp--)
		pow*=10;
	return pow;
}
	
int nodig(int num)
{
	int nodig=0;
	do {
		num/=10;
		nodig++;
	} while (num);
	return nodig;
}
	
void check(int n, s64 cnt[])
{		
	int d,i,j,k,cur;
	
	n++;
	d=nodig(n);
	for(i=0;i<10;i++)
		cnt[i]=0;
	for(i=1;i<d;i++) {
		for(j=1;j<10;j++) {
			cnt[j]+=pow10(i-1);
			for(k=0;k<10;k++)
				cnt[k]+=pow10(i-2)*(i-1);
		}			
	}
		
	for(i=0;i<d;i++) {
		cur=n%10;
		n/=10;			
		for(j=n?0:1;j<cur;j++) {
			k=n;
			while (k) {
				cnt[k%10]+=pow10(i);
				k/=10;
			}
			cnt[j]+=pow10(i);
			for(k=0;k<10;k++)
				cnt[k]+=i*pow10(i-1);
		}
	}
}	
	
int numbering(int num) {
	int i,ok,x;
	s64 lo=1,hi=2000000000;
	s64 cnt[10];
	while (lo+1<hi) {
		x=(lo+hi)/2;
		check(x,cnt);
		for(i=0,ok=1;(i<10) && ok;i++)
			ok=cnt[i]<=num*2;
		if (ok)
			lo=x;
		else
			hi=x;
	}
	return lo;
}

int main(void)
{
	int i,n;
	s64 cnt[10];
	
	while (scanf("%d",&n)==1) {
		check(n,cnt);
		for(i=0;i<10;i++)
			printf("%lld ",cnt[i]);		
		printf("\n");
	}
	return 0;
}
