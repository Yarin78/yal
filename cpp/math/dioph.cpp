#include <iostream>

using namespace std;

/*
  If d=dioph(A,B,C,x,y) returns d>0, then the equation
  
     Ax + By = C
  
  is a valid Diophantine Equation and we have
  
     specific solution: x,y
     general solution:  x+n*B/d, y-n*A/d  
*/

int dioph(int A, int B, int C, int &x, int &y, int p=0, int d=0) {
	return p?B?d=dioph(B,A%B,1,p,x,1),y=p-A/B*x,d:(x=1,y=0,A):
	(d=dioph(abs(A),abs(B),1,x,y,1),p=abs(C)/(d?d:1),
	x*=A*C>0?p:-p,y*=B*C>0?p:-p,!d||C%d?0:d);
} 


/* Test code */

void test()
{
	int cnt=0;
	for(int i=0;i<1000000;i++) {
		int a=rand()%101-50;
		int b=rand()%101-50;
		int c=rand()%51-25;
		int x,y,d;
		d=dioph(a,b,c,x,y);
		if (!d) continue;
		int n=rand()%1001-500;
		x+=n*b/d;
		y-=n*a/d;
		if (x*a+y*b!=c) {
			printf("Error! A=%d, B=%d, C=%d\n",a,b,c);
			return;
		}
		cnt++;		
	}
	printf("%d succeded\n",cnt);
}
		
int main()
{
	test(); return 0;
	int A,B,C;
	cout << "Ax + By = C" << endl;
	cin >> A >> B >> C;

	int x,y,d;
	d=dioph(A,B,C,x,y);
	if (!d)
		printf("No solution\n");
	else
	{
		printf("x=%d, y=%d is a specific solution\n",x,y);
		printf("x=%d + n*%d, y=%d + n*%d is the general solution\n",x,A/d,y,-B/d);
	}
	
	return 0;
}


/*  Clean, non-obfuscated, version (iteration 1)
int euclid(int a, int b, int &q, int &r, int p=0, int d=0)
{	
	if (b==0) { q=1; r=0; return a; }
	int p,d=euclid(b,a%b,p,q);
	r=p-a/b*q;
	return d;
}

int dioph(int A, int B, int C, int &x, int &y)
{	
	int d=euclid(abs(A),abs(B),x,y);
	if (!d || C%d) return 0;
	x*=abs(C)/d*(A*C>0?1:-1);
	y*=abs(C)/d*(B*C>0?1:-1);
	return d;
}
*/

/* Iteration 2
int euclid(int a, int b, int &q, int &r, int p=0, int d=0) {	
	return b?d=euclid(b,a%b,p,q),r=p-a/b*q,d:(q=1,r=0,a);
}

int dioph(int A, int B, int C, int &x, int &y) {	
	int d=euclid(abs(A),abs(B),x,y),e=abs(C)/(d?d:1);
	return x*=A*C>0?e:-e,y*=B*C>0?e:-e,!d||C%d?0:d;
}
*/