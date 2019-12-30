#include <stdio.h>

class UnionFind
{
	private:
		int *p,*size;

	public:
		UnionFind(int n) {
			p=new int[n];
			size=new int[n];
			for(int i=0;i<n;i++) {
				p[i]=i;
				size[i]=1;
			}
		}
		
		int find_set(int e) {
			int v=e;
			while (p[v]!=v) v=p[v];
			int root=v;
			while (p[e]!=root) { // Path compression
				int t=e;
				e=p[e];
				p[t]=root;
			}
			return root;
		}
		
		void union_set(int a, int b) {
			a=find_set(a);
			b=find_set(b);
			if (a==b) return;
			if (size[b]>size[a]) {
				int c=a; a=b; b=c;
			}			
			p[b]=a;
			size[a]+=size[b];
		}
};

int main(void)
{
	int n,a,b;
	
	scanf("%d",&n);
	
	UnionFind uf(n);

	while (scanf("%d %d",&a,&b)==2) {
		uf.union_set(a,b);
		for(int i=0;i<n;i++) {
			int j=uf.find_set(i);
			printf("%d ",j);
		}
		printf("\n");
	}
	
	return 0;
}
