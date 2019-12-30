#include <iostream>
#include <set>
#include <map>
#include <string>

using namespace std;

template<typename T1, typename T2>
class Heap
{
private:
	typedef multiset< pair<T2,T1> >::iterator MI;
	multiset< pair<T2,T1> > data;
	map<T1,MI> ptr;	
public:
	Heap(void) {}
	
	bool empty(void)
	{
		return data.empty();
	}
	
	pair<T1,T2> top(void)
	{
		pair<T1,T2> a(data.begin()->second,data.begin()->first);
		return a;
	}
	
	// v = data value, key = key value
	// If pushing a data value that already is in the heap, it will be replaced!
	void push(T1 v, T2 key)
	{		
		map<T1,MI>::iterator p=ptr.find(v);
		if (p!=ptr.end()) data.erase(p->second);
		ptr[v]=data.insert(pair<T2,T1>(key,v));
	}
	
	void pop(void)
	{
		if (!empty()) {
			ptr.erase(ptr.find(data.begin()->second));
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
	Heap<string,int> heap;

	cout << "Empty: " << heap.empty() << endl;

	heap.push("Sju",7);
	heap.push("Tre",3);
	heap.push("Tio",10);
	heap.push("Tolv",12);
	heap.push("Fem",5);
	heap.push("Nio",9);
	heap.push("Två",2);

	cout << "Empty: " << heap.empty() << endl;

	heap.show();

	cout << heap.top().first << endl;

	heap.pop();
	cout << heap.top().first << endl;
	heap.pop();

	cout << "Changing element \"tolv\" to value 8" << endl;
	heap.push("Tolv",8);

	heap.push("Två",2);
	
	while (!heap.empty()) {
		cout << heap.top().first << endl;
		heap.pop();
	}

	return 0;
}
