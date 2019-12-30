#include <iostream>
#include <string>
#include <vector>

int main(void)
{
	int i,j,m;
	string s,t;
	vector<int> next;
	
	// Read a string s and a search string t and return
	// offset in t where s exist
	while (cin >> s >> t) {
		m=t.size();
		next.resize(m+1);
		next[1]=-1;
		next[2]=0;
		for(i=3;i<=m;i++) {
			j=next[i-1]+1;
			while ((t[i-2]!=t[j-1]) && (j>0))
				j=next[j]+1;
			next[i]=j;
		}
		j=1; i=1;
		int start=0;
		int n=s.size();
		while (start==0 && (i<=n)) {
			if (t[j-1]==s[i-1]) {
				j++;
				i++;
			} else {
				j=next[j]+1;
				if (j==0) {
					j=1;
					i++;
				}
			}
			if (j==m+1)
				start=i-m;
		}
		cout << start << endl;
	}
	return 0;
}

/* @END_OF_SOURCE_CODE */
