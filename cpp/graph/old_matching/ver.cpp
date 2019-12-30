#include <iostream>
#include <cstdio>
#include <set>

set<int> *edge;
int *usat,*vsat;

int main(int argc, char *argv[])
{
	int usize,vsize,x,y;
	FILE *f;
	
	if (argc<3) return -1;
	f=fopen(argv[1],"rt");
	fscanf(f,"%d %d",&usize,&vsize);
	edge=new set<int>[usize];
	usat=new int[usize];
	vsat=new int[vsize];
	for(int i=0;i<usize;i++) usat[i]=0;
	for(int j=0;j<vsize;j++) vsat[j]=0;
	fscanf(f,"%d %d",&x,&y);
	while (x>=0) {
		edge[x].insert(y);
		fscanf(f,"%d %d",&x,&y);
	}
	fclose(f);
	
	int size;
	
	f=fopen(argv[2],"rt");
	fscanf(f,"%d",&size);
	for(int i=0;i<size;i++) {
		fscanf(f,"%d %d",&x,&y);
		if (x<0 || y<0 || x>=usize || y>=usize) {
			printf("Invalid edge range!\n");
			exit(-1);
		}
		if (edge[x].find(y)==edge[x].end()) {
			printf("Edge doesn't exist!\n");
			exit(-1);
		}
		if (usat[x] || vsat[y]) {
			printf("Vertex already saturated!\n");
			exit(-1);
		}
		usat[x]++;
		vsat[y]++;
	}
	printf("Matching of size %d is valid!\n",size);
	return 0;
}
