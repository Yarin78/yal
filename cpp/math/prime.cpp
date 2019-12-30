#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <algorithm>
#include <memory.h>

using namespace std;

// -- BEGIN PRIME

int maxPrime;
vector<int> primes;
char *primeMask;

void generatePrimes(int max)
{	
	maxPrime=max+(max&1);
	primes.push_back(2);
	primeMask=new char[maxPrime/16+2];
	memset(primeMask,255,maxPrime/16+2);
	primeMask[0]=0xFE;
  	for(int i=1;i<maxPrime/2;i++)
    	if (primeMask[i>>3]&(1<<(i&7))) {
    		primes.push_back(i*2+1);
      		for(int j=i+i+i+1;j<maxPrime/2;j+=i+i+1)
        		primeMask[j>>3]&=~(1<<(j&7));
        }
}

bool isPrime(int value)
{
	if (value==2) return true;
	if (value<2 || !(value&1)) return false;	
	if (value<=maxPrime)
		return primeMask[value>>4]&(1<<((value>>1)&7));
	if (value>(long long)maxPrime*maxPrime) {
		cerr << value << " is too high" << endl;
		abort();
	}
	for(int i=0;i<primes.size();i++) {
		int p=primes[i];
		if (p*p>value) break;
		if (value%p==0) return false;
	}
	return true;
}

vector<int> primeFactorize(int n, bool onlyUnique=false)
{
	vector<int> f;	
	if (n<2) return f;
	for(int i=0;i<primes.size() && n>1;i++) {
		int p=primes[i];
		while (n%p==0) {
			f.push_back(p);
			n/=p;
			if (onlyUnique)
				while (n%p==0) n/=p;
		}		
	}
	if (n<=(long long)maxPrime*maxPrime) {
		if (n>1) f.push_back(n);		
		return f;
	}
	cerr << "Can't prime factorize " << n << endl;
	abort();		
}

// -- END PRIME

int main(void)
{	
	generatePrimes(100);
	int n;
	while (cin >> n) {
		vector<int> p=primeFactorize(n);
		for(int i=0;i<p.size();i++)
			cout << p[i] << " ";
		cout << endl;
	}
	
	return 0;
}
