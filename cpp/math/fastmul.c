#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct TBig {
	int *dig;
	int len;
};

#define MAXSIZE 1000000
#define RADIX 10000

#define min(a,b) ((a)<(b)?(a):(b))
#define max(a,b) ((a)>(b)?(a):(b))

struct TBig readbig(void)
{
	int i;
	char s[MAXSIZE];
	struct TBig f;
	scanf("%s",s);
	f.len=strlen(s);
	f.dig=malloc(f.len*sizeof(int));
	for(i=0;i<f.len;i++)
		f.dig[f.len-i-1]=s[i]-'0';
	return f;
}

struct TBig randbig(int n)
{
	int i;
	struct TBig f;
	f.len=n;
	f.dig=malloc(f.len);
	for(i=0;i<f.len;i++)
		f.dig[i]=rand()%RADIX;
	return f;
}


void showbig(struct TBig f)
{
	int i,first=1;
	if (!f.len) {
		printf("0\n");
		return;
	}
	for(i=f.len-1;i>=0;i--)
		if (!i || (!first || f.dig[i])) {
			printf("%c",f.dig[i]+'0');
			first=0;
		}
	printf("\n");
}

struct TBig add(struct TBig t1, struct TBig t2, int shift)
{
	int i,j,cf,z=0;
	struct TBig sum;
	sum.len=max(t1.len,t2.len)+1+shift;
	sum.dig=malloc(sum.len);
	cf=0;
	for(i=0;i<sum.len;i++) {
		cf+=(i<t1.len)?t1.dig[i]:0;
		j=i-shift;
		cf+=((j>=0) && (j<t2.len))?t2.dig[j]:0;
		sum.dig[i]=cf%RADIX;
		if (i && !sum.dig[i]) z++; else z=0;
		cf/=RADIX;
	}
	sum.len-=z;
	return sum;
}

void subfrom(struct TBig *t1, struct TBig t2)
{
	int i,j,cf=0,z=0;
	for(i=0;i<t1->len;i++) {
		j=t1->dig[i]-((i<t2.len)?t2.dig[i]:0)-cf;
		if (j<0) {
			cf=1;
			j+=RADIX;
		} else
			cf=0;		
		t1->dig[i]=j;
		if (i && !t1->dig[i]) z++; else z=0;
	}
	t1->len-=z;
}

struct TBig mul(struct TBig f1, struct TBig f2)
{
	int i,split;
	long long v1=0,v2=0;
	struct TBig prod,a,b,c,d,bd,ac,ab,cd,tmp,tmp2;
	prod.len=0;
	/*
	if (f1.len+f2.len<=4) {
		for(i=f1.len-1;i>=0;i--) v1=v1*RADIX+f1.dig[i];
		for(i=f2.len-1;i>=0;i--) v2=v2*RADIX+f2.dig[i];
		prod.dig=malloc(18);		
		v1=v1*v2;
		while (v1) {
			prod.dig[prod.len++]=v1%RADIX;
			v1/=RADIX;
		}
		return prod;			
	}
	*/
	
	split=max(f1.len,f2.len)/2;
	a.len=max(f1.len-split,0);
	b.len=min(split,f1.len);
	c.len=max(f2.len-split,0);
	d.len=min(split,f2.len);
	a.dig=f1.dig+split;
	b.dig=f1.dig;
	c.dig=f2.dig+split;
	d.dig=f2.dig;

	bd=mul(b,d);
	ac=mul(a,c);
	ab=add(a,b,0);
	cd=add(c,d,0);
	tmp=mul(ab,cd);
	subfrom(&tmp,ac);
	subfrom(&tmp,bd);
	tmp2=add(bd,tmp,split);
	prod=add(tmp2,ac,split+split);

	free(tmp.dig);
	free(tmp2.dig);
	free(bd.dig);
	free(ac.dig);
	free(ab.dig);
	free(cd.dig);	
	return prod;
}

int main(void)
{
	struct TBig f1,f2,p;

//	while (1) {
		//f1=readbig();
		//f2=readbig();
		f1=randbig(100000/4);
		f2=randbig(100000/4);
		//if (feof(stdin)) break;	
		p=mul(f1,f2);
		//showbig(p);
	//}
	return 0;
}
