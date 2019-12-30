#include <stdio.h>
#include <memory.h>

int e[500][500],deg[500];
int circuit[1025],cpos;

void search(int cur)
{
	int i;
	for(i=0;i<500;i++)
		if (e[cur][i]) {			
			e[cur][i]--;
			e[i][cur]--;
			search(i);
			i--;
		}
	circuit[cpos++]=cur;
}

int main(void)
{
	int i,x,y,N;
	
	freopen("fence.in","r",stdin);
	freopen("fence.out","w",stdout);
	memset(e,0,sizeof(e));
	memset(deg,0,sizeof(deg));
	scanf("%d",&N);
	for(i=0;i<N;i++) {
		scanf("%d %d",&x,&y);
		x--; y--;
		e[y][x]++;
		e[x][y]++;
		deg[x]++;
		deg[y]++;
	}
	cpos=0;
	for(i=0;i<500;i++)
		if (deg[i]&1)
			break;
	if (i==500)
		for(i=0;i<500;i++)
			if (deg[i]) break;
	search(i);	
		
	for(i=cpos-1;i>=0;i--)
		printf("%d\n",circuit[i]+1);
	
	return 0;	
}
