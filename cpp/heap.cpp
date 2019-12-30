#include <iostream>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>

template<typename T1, typename T2>
struct pair
{
	T1 first;
	T2 second;
	
	pair(void) { }
	pair(const T1 &a, const T2 &b) { first=a; second=b; }
	
	bool operator<(const pair &rhs) const {
		if (first<rhs.first) return true;
		if (rhs.first<first) return false;
		if (second<rhs.second) return true;
		if (rhs.second<second) return false;
		return false;
	}
};

template<typename T, int MAXSIZE>
class Heap
{
	private:
		T data[MAXSIZE];
		int n;
	public:
		inline void swap(T &a, T &b) { T c=a; a=b; b=c; }
		Heap(void) : n(0) {};
		bool isempty(void) { return !n; }

		void trinkle_up(int k) {			
			while (k>0) {				
				int p=(k-1)/2;
				if (!(data[k]<data[p])) return;
				swap(data[k],data[p]);
				k=p;
			}
		}
		
		void trinkle_down(int k) {
			while (1) {
				int lc=k*2+1,rc=k*2+2;
				if (lc>=n) return;
				int c=rc<n?(data[lc]<data[rc]?lc:rc):lc;
				if (!(data[c]<data[k])) return;
				swap(data[k],data[c]);
				k=c;
			}
		}
	
		bool push(T item) {
			if (n>=MAXSIZE) return false;
			data[n++]=item;
			trinkle_up(n-1);
			return true;
		}
		
		T pop(void) {
			T item=data[0];
			data[0]=data[--n];
			trinkle_down(0);
			return item;
		}
		
		void make(T *a, int m)
		{
			for(n=0;n<m;n++) {
				data[n]=a[n];
				trinkle_up(n);
			}
		}
		
		bool isheap(int k)
		{
			int lc=k*2+1,rc=k*2+2;
			if ((lc<n) && (data[lc]<data[k] || !isheap(lc))) return false;
			if ((rc<n) && (data[rc]<data[k] || !isheap(rc))) return false;
			return true;
		}
		
		void show(void)
		{
			for(int i=0;i<n;i++)
				cout << data[i] << " ";
			cout << endl;
		}
};

const int N=100000;

int main(void)
{
	Heap<int,N> h;

	/*
	for(int i=0;i<11;i++) {
		cout << "push " << a[i] << ": ";
		h.push(a[i]);
		h.show();
		assert(h.isheap(0));
	}
	while (!h.isempty()) {
		cout << "pop " << h.pop() << ": ";
		assert(h.isheap(0));
		h.show();
	}
	*/

	int a[N];
	for(int i=0;i<N;i++) a[i]=rand()%10000;
	h.make(a,N);
	printf("%d\n",h.isheap(0));
	exit(0);

	int noop=0;
	while (noop<100000) {
		int n=rand()%100;
		while (n--) {
			h.push(rand()%100);
			assert(h.isheap(0));
			noop++;
		}
		n=rand()%100;
		while (n-- && !h.isempty()) {
			h.pop();
			assert(h.isheap(0));
			noop++;
		}
	}
	
	return 0;
}
