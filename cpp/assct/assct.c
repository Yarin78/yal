#include <stdio.h>

#define SIZE 514
#define MAXINT 255*255*255*127

static int n,kslc,kslr,cost,np1;
static int a[SIZE+1][SIZE],c[SIZE];

static int u[SIZE+1];
static int lc[SIZE],lr[SIZE];
static int slc[SIZE],slr[SIZE];

static int rh[SIZE+1],ch[SIZE];

static void improve(void);

static int reduce(void)
{
	int i,j,k,l,h;
	
	h=MAXINT;
	for(j=1;j<=n;j++)
		if (!lc[j-1])
			for(k=1;k<=kslr;k++) {
				i=slr[k-1];
				if (a[j][i]<h)
					h=a[j][i];
			}
	cost+=h;
	for(j=1;j<=n;j++)
		if (!lc[j-1])
			for(k=1;k<=kslr;k++) {
				i=slr[k-1];
				a[j][i]-=h;
				if (!a[j][i]) {
					if (!rh[i-1]) {
						rh[i-1]=rh[np1-1];
						ch[i-1]=j;
						rh[np1-1]=i;
					}
					l=np1;
					while (-a[l][i])
						l=-a[l][i];
					a[l][i]=-j;
				}
			}
	
	if (kslc!=0)
		for(i=1;i<=n;i++)
			if (lr[i-1]==0)
				for(k=1;k<=kslc;k++) {
					j=slc[k-1];
					if (a[j][i]>0)
						a[j][i]+=h;
					else {
						l=np1;
						while (-a[l][i]!=j)
							l=-a[l][i];
						a[l][i]=a[j][i];
						a[j][i]=h;
					}
				}
	return rh[np1-1];
}

static void assign(int l, int r)
{
	int m;
	while (1) {
		c[l-1]=r;
		m=np1;
		while (-a[m][r]!=l)
			m=-a[m][r];
		a[m][r]=a[l][r];
		a[l][r]=0;
		if (lr[r-1]<0) {
			u[np1-1]=u[r-1];
			u[r-1]=0;
			return;
		}
		l=lr[r-1];
		a[l][r]=a[np1][r];
		a[np1][r]=-l;
		r=lc[l-1];
	}
}

int assct(int size)
{
	int i,j,k,l,m,r,lj,lm;
	int lz[SIZE],nz[SIZE];
	
	n=size;
	np1=n+1;
	cost=0;
	
	for(i=0;i<n;i++)
		c[i]=lz[i]=nz[i]=u[i]=0;
	
	u[np1-1]=0;
	
	for(i=1;i<=n;i++) {
		k=a[i][1];
		for(j=2;j<=n;j++)
			if (a[i][j]<k)
				k=a[i][j];
		cost+=k;
		for(j=1;j<=n;j++)
			a[i][j]-=k;
	}
	
	for(i=1;i<=n;i++) {
		k=a[1][i];
		for(l=2;l<=n;l++)
			if (a[l][i]<k)
				k=a[l][i];
		cost+=k;
		l=np1;
		for(j=1;j<=n;j++) {
			a[j][i]-=k;
			if (a[j][i]==0) {
				a[l][i]=-j;
				l=j;
			}
		}
	}
		
	k=np1;
	for(i=1;i<=n;i++) {
		j=-a[lj=np1][i];
		
		while ((j>0) && (c[j-1]!=0))
			j=-a[lj=j][i];
		if (!j) {
			j=np1;
			do {
				j=-a[lj=j][i];
				r=c[j-1];
				lm=lz[r-1];
				m=nz[r-1];
				while ((m>0) && (c[m-1]!=0))
					m=-a[lm=m][r];
			} while ((-a[j][i]>0) && (!m || c[m-1]));
			if (m && !c[m-1]) {
				nz[r-1]=-a[m][r];
				lz[r-1]=j;
				a[lm][r]=-j;
				a[j][r]=a[m][r];
				a[m][r]=0;
				c[m-1]=r;
			} else {
				u[k-1]=i;
				k=i;
				j=0;
			}
		}
		if (j>0) {
			c[j-1]=i;
			a[lj][i]=a[j][i];
			nz[i-1]=-a[j][i];
			lz[i-1]=lj;
			a[j][i]=0;
		}
	}	
	
	while (u[np1-1])
		improve();
	return cost;
}

void improve(void)
{
	int i,j,l,r;
	int mode=170;
	
	for(i=0;i<n;i++)
		ch[i]=lc[i]=lr[i]=rh[i]=0;
	
	rh[np1-1]=-1;
	kslc=0;
	kslr=1;
	r=u[np1-1];
	lr[r-1]=-1;
	slr[0]=r;
	
	while (1)
		switch (mode) {
			case 170:
				mode=190;
				if (a[np1][r]==0)
					r=reduce();
				else {
					l=-a[np1][r];
					if (a[l][r] && !rh[r-1]) {
						rh[r-1]=rh[np1-1];
						ch[r-1]=-a[l][r];
						rh[np1-1]=r;
					}
					if (lc[l-1]==0)
						mode=200;
					else if (rh[r-1]==0)
						mode=210;
				}
				break;
			case 190 :
				mode=200;
				do {
					l=ch[r-1];
					ch[r-1]=-a[l][r];
					if (a[l][r]==0) {
						rh[np1-1]=rh[r-1];
						rh[r-1]=0;
					}
				} while (lc[l-1] && rh[r-1]);
				break;
			case 200 :
				mode=210;
				if (lc[l-1]==0) {
					lc[l-1]=r;
					if (c[l-1]==0) {
						assign(l,r);
						return;
					}
					kslc++;
					slc[kslc-1]=l;
					r=c[l-1];
					lr[r-1]=l;
					kslr++;
					slr[kslr-1]=r;
					if (a[np1][r]!=0)
						mode=170;
				}
				break;
			case 210:
				mode=190;
				if (rh[np1-1]>0)
					r=rh[np1-1];
				else
					r=reduce();
				break;
		}
}

int main(void)
{
	int i,j,n,t;
	
	n=500;
	for(i=1;i<=n;i++) {
		for(j=1;j<=n;j++) {
			a[i][j]=random()%1000; /* Matrix a is indexed from 1! */
		}
	}
	
	/* Returns the MINIMUM COST
	   c[i] = The object select in row i (NOTE: 0<=i<n, 0<c[i]<=n)
	   Matrix a is changed, so the elements a[i+1][c[i]] are all 0!
	*/

	t=assct(n);
	printf("Cost = %d\n",t);
	for(i=0;i<n;i++)
		printf("%d ",c[i]);
	printf("\n");
	return 0;
}
