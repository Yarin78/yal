#include <stdio.h>

unsigned long long witness(unsigned long long a,	
			   unsigned long long n) {
  int i;
  unsigned long long t, d, pn, x, p;

  p = n-1;
  i=0; t=p; pn=1;
  while (t) {
    t = t >> 1;
    pn = pn << 1;
    i++;
  }
  pn = pn >> 1;

  d = 1;
  while (i--) {
    x = d;
    d = (d*d) % n;
    if ((d==1) && (x != 1) && (x != n-1)) return 1;
    if (p&pn) d=(d*a) % n;
    p = p << 1;
  }
  if (d!=1) return 1;

  return 0;
}


int isprime_MillerRabin(unsigned long long n, 
			unsigned long long s) {
  int i;
  unsigned long long a;

  for (i=0;i<s;i++) {
    a = (rand()%(n-1))+1;
    if (witness(a, n))
      return 0;
  }
  return 1;
}


int main(void) {
  int N;
  unsigned long long n;
  
  scanf("%d", &N);
  while (N--) {
    scanf("%lld", &n);
    printf("%lld is %sa prime\n", n, ((isprime_MillerRabin(n, 50))?"":"not
"));
  }
  return 0;
}
