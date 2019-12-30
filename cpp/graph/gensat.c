#include <stdio.h>
#include <stdlib.h>

#define SAT 3

int a[100000];

char *varname(int i)
{
	static char s[100];
	s[0]='A'+i;
	s[1]=0;
	return s;
}

int main(void)
{
	int i,j,n,m,k[3],v[3];
	srand(time(0));
	scanf("%d %d",&n,&m);
	for(i=0;i<n;i++) {
		a[i]=rand()%2;
		fprintf(stderr,"%d",a[i]);
	}
	fprintf(stderr,"\n");
	printf("%d\n",n);
	for(i=0;i<m;i++) {
		for(j=0;j<SAT;j++) {
			k[j]=rand()%n;
			v[j]=a[k[j]];
		}
	 	j=rand()%SAT;
	 	while (j--)
	 		v[rand()%SAT]^=1;
	 	for(j=0;j<SAT;j++) {
	 		if (j) printf(" ");
	 		//printf("%s",varname(k[j]));
	 		//if (!v[j]) printf("-");
	 		if (v[j]) printf("%d",k[j]+1); else printf("-%d",k[j]+1);
	 	}
	 	printf("\n");
	}
	return 0;
}
