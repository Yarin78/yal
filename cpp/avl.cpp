#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <set>
#include <map>

template<class T1, class T2>
struct Pair {
	T1 first;
	T2 second;
	Pair(void) {}
	Pair(const T1 &a, const T2 &b) : first(a), second(b) {}
	bool operator<(const Pair &rhs) const {
		return first<rhs.first?true:rhs.first<first?false:second<rhs.second;
	}
};

template<class T1, class T2>
struct MapPair : public Pair<T1,T2> {
	MapPair(void) {}
	MapPair(const T1 &a, const T2 &b) : Pair<T1,T2>(a,b) {}
	bool operator<(const MapPair &rhs) const { return first<rhs.first; }
};

template<class T=int, bool multi=false>
class AVLNode {
	void swap(T &a, T &b) { T c(a); a=b; b=c; }
	void check() {
		height=(c[0]->height>?c[1]->height)+1;
		c[0]->par=c[1]->par=this;
	}
	void rotate(int i) { // 0=>rotate left, 1=>rotate right		
  	AVLNode *u=c[1-i];
    c[1-i]=u->c[1-i]; u->c[1-i]=u->c[i]; u->c[i]=c[i]; c[i]=u;
    swap(key,u->key); u->check(); check();
  }
  void baldir(int i) {
  	if (c[i]->height==c[1-i]->height+2) {
  		if (c[i]->c[i]->height>=c[i]->c[1-i]->height) rotate(1-i);
  		else { c[i]->rotate(i); rotate(1-i); }
  	}
  }
  void balance() { check(); baldir(0); baldir(1);	}

public:
	T key;
	signed char height;
	AVLNode *c[2],*par;

	/* BEGIN of iterator stuff */
	struct iterator {
		AVLNode *p;
		iterator(AVLNode *pt=0) : p(pt) {}
		const T& operator*() { return p->key; }		
		T* operator->() { return &p->key; }
		bool operator==(const iterator &r) const { return this->p==r.p; }
		iterator &operator++() {
			if (p->c[1]->height!=-1) return *this=p->c[1]->begin();
			while (p->par && p->par->c[1]==p) p=p->par;
			return *this=p->par;
		}
		iterator operator++(int) {iterator tmp=*this; ++*this; return tmp;}
	};
	iterator begin() {
		if (height==-1) return 0;
		return (c[0]->height==-1)?iterator(this):c[0]->begin();
	}
	iterator end() { return 0; }
	int erase(iterator it) { return it.p?it.p->erase(*it):0; }
	/* END of iterator stuff */

	AVLNode *find(const T &k) {
		if (height==-1) return 0;
		return (k<key?c[0]->find(k):key<k?c[1]->find(k):this);
	}
	int insert(const T &k) {
		if (height==-1) {
			key=k; c[0]=new AVLNode(); c[1]=new AVLNode(); check();
			for(AVLNode *u=par;u;u=u->par) u->balance();
			return 1;
		} else if (k<key) return c[0]->insert(k);		
		else return (multi || key<k)?c[1]->insert(k):0;
	}
	int erase(const T &k) {
		if (height==-1) return 0;
		if (!(key<k || k<key)) {
			if (!height) {
				delete c[0]; delete c[1]; c[0]=c[1]=0; height=-1;
				for (AVLNode *u=par;u;u=u->par) u->balance();
				return 1;
			} else {
				AVLNode *u;
				int i=c[0]->height>c[1]->height;
				for(u=c[1-i];u->c[i]->height!=-1;u=u->c[i]);
				swap(key,u->key); return u->erase(u->key);
			}
		} else
			return c[key<k]->erase(k);
	}
	~AVLNode() { delete c[0]; delete c[1]; }
	AVLNode() { height=-1; c[0]=c[1]=par=0; }
};

template<class T>
class Set : public AVLNode<T> {
	private:
		int n;
	public:
		Set(void) : n(0) {}
		int size(void) { return n; }
		int insert(T item) { int i=AVLNode<T>::insert(item); n+=i; return i; }
		int erase(T item) { int i=AVLNode<T>::erase(item); n-=i; return i; }
		int erase(iterator it) { int i=AVLNode<T>::erase(it); n-=i; return i; }
};

template<class T>
class MultiSet : public AVLNode<T,true> {
	private:
		int n;
	public:
		MultiSet(void) : n(0) {}
		int size(void) { return n; }
		int insert(T item) { int i=AVLNode<T,true>::insert(item); n+=i; return i; }
		int erase(T item) { int i=n; while (AVLNode<T,true>::erase(item)) n--; return i-n; }
		int erase(iterator it) { int i=AVLNode<T,true>::erase(it); n-=i; return i; }
};

template<class T1, class T2>
class Map : public AVLNode< MapPair<T1,T2> > {	
	private:		
		typedef MapPair<T1,T2> MP;
		int n;
	public:
		Map(void) : n(0) {}
		int size(void) { return n; }
		int insert(MP item) { int i=AVLNode<MP>::insert(item); n+=i; return i; }
		iterator find(T1 item) { return AVLNode<MP>::find(MP(item,T2())); }
		int erase(T1 item) { int i=AVLNode<MP>::erase(MP(item,T2())); n-=i; return i; }		
		int erase(iterator it) { int i=AVLNode<MP>::erase(it); n-=i; return i; }
		T2& operator[](const T1 &k) {	insert(MP(k,T2())); return find(k)->second; }
};


/* Test functions */

template<class T1, class T2> ostream& operator<<(ostream &os, const Pair<T1,T2> &p)
{
	os << "(" << p.first << "," << p.second << ") ";
	return os;
}

template<class T1, class T2> ostream& operator<<(ostream &os, const pair<T1,T2> &p)
{
	os << "(" << p.first << "," << p.second << ") ";
	return os;
}

template<class T1, class T2> ostream& operator<<(ostream &os, const MapPair<T1,T2> &p)
{
	os << "(" << p.first << "," << p.second << ") ";
	return os;
}

template<class T> ostream& operator<<(ostream &os, Set<T> &a) {
	os << a.size() << "=> ";
	for(Set<T>::iterator it=a.begin();it!=a.end();it++) os << ' ' << *it;
	return os;
}

template<class T> ostream& operator<<(ostream &os, MultiSet<T> &a) {
	os << a.size() << "=> ";
	for(MultiSet<T>::iterator it=a.begin();it!=a.end();it++) os << ' ' << *it;
	return os;
}

template<class T1,class T2> ostream& operator<<(ostream &os, Map<T1,T2> &a) {
	os << a.size() << "=> ";
	for(Map<T1,T2>::iterator it=a.begin();it!=a.end();it++)
		os << '(' << it->first << ',' << it->second << ") ";
		//os << '(' << (*it).first << ',' << (*it).second << ") ";
	return os;
}

template<class T1,class T2> ostream& operator<<(ostream &os, map<T1,T2> &a) {
	os << a.size() << "=> ";
	for(map<T1,T2>::iterator it=a.begin();it!=a.end();it++)
		os << '(' << it->first << ',' << it->second << ") ";
	return os;
}

template<class T> ostream& operator<<(ostream &os, set<T> &a) {
	os << a.size() << "=> ";
	for(set<T>::iterator it=a.begin();it!=a.end();it++) os << ' ' << *it;
	return os;
}

template<class T> ostream& operator<<(ostream &os, multiset<T> &a) {
	os << a.size() << "=> ";
	for(multiset<T>::iterator it=a.begin();it!=a.end();it++) os << ' ' << *it;
	return os;
}

void error(void) {
	printf("Mismatch!\n");
	exit(-1);
}

template<class T>
void verify(Set<T> &a, set<T> &b)
{
	Set<T>::iterator ai=a.begin();
	set<T>::iterator bi=b.begin();
	while (ai!=a.end()) {
		if (bi==b.end() || *ai!=*bi) error();
		ai++; bi++;
	}
	if (bi!=b.end()) error();
}

template<class T>
void verify(MultiSet<T> &a, multiset<T> &b)
{
	MultiSet<T>::iterator ai=a.begin();
	multiset<T>::iterator bi=b.begin();
	while (ai!=a.end()) {
		if (bi==b.end() || *ai!=*bi) error();
		ai++; bi++;
	}
	if (bi!=b.end()) error();
}

template<class T1, class T2>
void verify(Map<T1,T2> &a, map<T1,T2> &b)
{
	Map<T1,T2>::iterator ai=a.begin();
	map<T1,T2>::iterator bi=b.begin();
	while (ai!=a.end()) {
		if (bi==b.end() || ai->first!=bi->first || ai->second!=bi->second) error();
		ai++; bi++;
	}
	if (bi!=b.end()) error();
}

int main(void) {
	Map<int,int> a;
	map<int,int> b;
	
	int i,j,n,noop=0;
	srand(time(0));
	while (noop<10000) {
		n=rand()%20;
		while (n--) {
			i=rand()%100;
			j=rand()%1000000;
			//a.insert(MapPair<int,int>(i,j));
			//b.insert(pair<int,int>(i,j));
			a[i]=j;
			b[i]=j;
			verify(a,b);
			noop++;
		}
		n=rand()%20;
		while (n--) {
			i=rand()%100;
			a.erase(i);
			b.erase(i);
			verify(a,b);
			noop++;
		}
	}

	return 0;
}
