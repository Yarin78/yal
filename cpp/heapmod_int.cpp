#include <iostream>
#include <set>
#include <map>
#include <vector>
#include <string>

using namespace std;

template<typename T>
class Heap
{
private:
	typedef multiset< pair<T,int> >::iterator MI;
	multiset< pair<T,int> > data;
	vector<MI> ptr;
public:
	// n = Number of different data values (0 - n-1)
	Heap(int n)
	{
		ptr.clear();
		ptr.resize(n,data.end());
	}
	
	bool empty(void)
	{
		return data.empty();
	}
	
	pair<int,T> top(void)
	{
		pair<int,T> a(data.begin()->second,data.begin()->first);
		return a;
	}
	
	// v = data value, key = key value
	// If pushing a data value that already is in the heap, it will be replaced!
	void push(int v, T key)
	{		
		if (ptr[v]!=data.end()) data.erase(ptr[v]);
		ptr[v]=data.insert(pair<T,int>(key,v));
	}
	
	void pop(void)
	{
		if (!empty()) {
			ptr[data.begin()->second]=data.end();
			data.erase(data.begin());
		}
	}

	void show(void)
	{
		int j=0;
		for(MI i=data.begin();i!=data.end();i++)
			cout << j++ << ": " << i->second << " " << i->first << endl;
	}
};


int main(void)
{
	return 0;
}
