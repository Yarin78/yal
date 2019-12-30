#include <stdio.h>
#include <stdlib.h>

int main()
{
	int i,k,n;
	srand(time(0));
	for(k=0;k<10;k++) {
		n=rand()%50+1;
		printf("%d\n",n);
		for(i=0;i<n;i++)
			printf("%d %d %d\n",rand()%100,rand()%100,rand()%30+1);
	}
	printf("0\n");
	return 0;
}
