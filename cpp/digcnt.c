

int digposcnt(int n, int d, int pos)
{
  /* How many times does the digit d appear at position pos
     (0=ones, 1=tens etc) in the sequence 0 - n-1 ?         */
  
  int rest=0,exp10=1,dcmp;
  
  for(;pos>0;pos--) {
    rest+=(n%10)*exp10;
    exp10*=10;
    n/=10;
  }
  dcmp=n%10;
  n=(n/10)*exp10;
  return n+(d<dcmp?exp10:(d==dcmp)?rest:0);
}

int digcnt(int n, int d)
{
	/* How many times does the digit d appear in the sequence [0,n) ? */
	
	int i=0,cnt=0,m=n-1;
	do {
		cnt+=digposcnt(n,d,i);
		m/=10;
		i++;
	} while (m);
	if (!d) {
		/* Remove count for leading zeros */
		
	}
	return cnt;
}


int main(void)
{
	int i,n;
	for(n=1;n<105;n++) {
		printf("0 - %3d  ",n-1);
		for(i=0;i<10;i++)
			printf("%3d",digcnt(n,i));
		printf("\n");
	}
	return 0;
}
