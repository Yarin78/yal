#include <iostream>
#include <vector>

class SAT3
{
	private:		
		struct TTriple {
			int a,b,c;
			TTriple(int _a, int _b, int _c) : a(_a),b(_b),c(_c) {};
		};
		int literals;
		vector<TTriple> g;
	public:
		vector<int> value;
		
		SAT3(int n) : literals(n) { value.resize(n*2); }
		
		void add(int x, int y, int z) {
			g.push_back(TTriple(x,y,z));
		}
		
		bool isSAT(void)
		{
			int k=g.size();
			vector<int> err(k);
			while (1) {
				for(int i=0;i<literals;i++) {
					value[i*2]=rand()%2;
					value[i*2+1]=value[i*2]^1;
				}
				for(int i=0;i<3*literals;i++) {
					int cnt=0;
					for(int j=0;j<k;j++)
						if (!(value[g[j].a]+value[g[j].b]+value[g[j].c]))
							err[cnt++]=j;
					if (!cnt) return true;
					int j=err[rand()%cnt];
					int m=rand()%3;
					switch (m) {
						case 0 : j=g[j].a; break;
						case 1 : j=g[j].b; break;
						case 2 : j=g[j].c; break;
					}
					value[j]^=1;
					value[j^1]^=1;
				}
			}
			return false;
		}
};

int main(void)
{
	int n,x,y,z;
	
	cin >> n;
	SAT3 sat3(n);
	while (cin >> x >> y >> z) {
		if (x>0) x=(x-1)*2; else x=(-x-1)*2+1;
		if (y>0) y=(y-1)*2; else y=(-y-1)*2+1;
		if (z>0) z=(z-1)*2; else z=(-z-1)*2+1;
		sat3.add(x,y,z);
	}
	bool ok=sat3.isSAT();
	cout << ok << endl;
	if (ok)
		for(int i=0;i<n;i++)
			//cout << (char)(i+'A') << ": " << sat3.value[i*2] << endl;
			cout << sat3.value[i*2];
	return 0;
}
