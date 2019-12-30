#define resid(a,b) (cap[a][b]-fl[a][b]+fl[b][a])

int cookie;

int aug(int s, int t, int mx) {
	int i,j,k=0;
	if (s==t || seen[s]==cookie) return (s==t)*mx;
	seen[s]=cookie;
	for(i=0;i<N && !k;i++) 
	if ((j=min(mx,resid(s,i)))>0 && (j=aug(i,t,j))) fl[s][i]+=k=j;
	return k;
}

int max_flow(int src,int snk) {
	int ret=0,z;
	while(cookie++,z=aug(src,snk,INF)) ret+=z;
	return ret;
}
