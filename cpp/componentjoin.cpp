#include <iostream>
#include <vector>
#include <map>

using namespace std;

// Input:
//   prevLine = list of component numbers on previous line (at most 32 elements)
//   prevParity = parity of first component in previous line,
//     false if it's "white", true if "black"
//   curLineMask = bitmask of current line. Bit 0 correspond
//     to the first element in prev. 0 = white, 1 = black.
//   curLine = empty vector, will be set to the component
//     of the current line
//   curParity = will be set to the parity of this line
// Output:
//   number of components in prevLine that were lost
int joinLine(const vector<int> &prevLine, bool prevParity, int curMask, vector<int> &curLine, bool &curParity)
{
	int n=prevLine.size(),q[32],prevMask=0,comp=0,savedComp=0,lost=0;
	for(int i=0,b;i<n;i++) {
		curLine[i]=-1; // Component not deduced yet
		if (i==0) b=prevParity?1:0;
		if (i>0 && prevLine[i]!=prevLine[i-1]) b^=1;
		if (b>0) prevMask|=(1<<i);		
	}
	curParity=(curMask&1)==1;
	for(int i=0;i<n;i++) {
		if (curLine[i]>=0) continue;		
		int head=0,tail=0; // Do a BFS search
		curLine[q[tail++]=i]=comp; // Add cell in this row		
		while (head<tail) {			
			int x=q[head++];
			if (((curMask^prevMask)&(1<<x))==0) { // Can we go back to previous line?
				savedComp|=(1<<prevLine[x]); // This component (from the previous line) will not be lost
				for(int j=0;j<n;j++) // Then add all cells below the matching component from line above					
					if (prevLine[x]==prevLine[j] && ((curMask^prevMask)&(1<<j))==0 && curLine[j]<0)
						curLine[q[tail++]=j]=comp;							
			}			
			if (x>0 && curLine[x-1]<0 && (((3<<(x-1))&curMask)==0 || ((3<<(x-1))&curMask)==(3<<(x-1)))) // Can we go left?
				curLine[q[tail++]=x-1]=comp;
			if (x+1<n && curLine[x+1]<0 && (((3<<x)&curMask)==0 || ((3<<x)&curMask)==(3<<x)))  // Can we go right?
				curLine[q[tail++]=x+1]=comp;
		}
		comp++;
	}
	for(int i=0;i<n;i++)
		if (((1<<prevLine[i])&savedComp)==0) {
			savedComp|=(1<<prevLine[i]);
			lost++;
		}
	return lost;
}

int firstLine(int n, int curMask, vector<int> &curLine, bool &curParity)
{
	curParity=(curMask&1)==1;
	for(int i=0,comp=0;i<n;i++) {
		int m=(3<<(i-1))&curMask;
		if (i>0 && (m==1 || m==2)) comp++;
		curLine[i]=comp;
	}
	return 0;		
}

map<int,int> memo;

int go(int rowsLeft, bool blackOnEdge, const vector<int> &prevLine, bool prevParity)
{
	int a=prevLine.size(),state=0;
	// Calc state
	for(int i=0;i<a;i++) state=state*10+prevLine[i];
	state=((state*10+rowsLeft)*2+(prevParity?1:0))*2+(blackOnEdge?1:0);	
	if (memo.find(state)!=memo.end()) return memo[state];
	
	int &sum=memo[state];
	
	for(int i=0;i<(1<<a);i++) {		
		vector<int> curLine(a);
		bool curParity;		
		int lost=joinLine(prevLine,prevParity,i,curLine,curParity);		
		if (lost>1) continue;
		int noComp=0;
		for(int j=0;j<a;j++)
			if (curLine[j]+1>noComp) noComp=curLine[j]+1;
		if (lost==1) {
			if (noComp>1) continue;
			if (!curParity && !blackOnEdge) continue; // The black part never touches the edge
			sum++;
			continue;
		}
		if (rowsLeft==1) {
			// Last line is special
			if (noComp>2) continue;			
			sum++;
		} else
			sum+=go(rowsLeft-1,blackOnEdge || (i&1)>0 || i>=(1<<(a-1)),curLine,curParity);
	}
	return sum;
}

int solve(int a, int b)
{
	vector<int> curLine(a,0);
	// First line complete white, but don't count complete white filling (hence -1)
	int sum=go(b-1,false,curLine,false)-1;
	
	for(int i=1;i<a;i++)
		for(int j=1;i+j<=a;j++) {		
			for(int k=0;k<a;k++)
				curLine[k]=k<i?0:(k<i+j?1:2);
			sum+=go(b-1,true,curLine,false);
		}
	return sum;
}

int main()
{	
	int a,b;
	cin >> a >> b;
	int n=solve(a,b);
	cout << n << endl;
	cout << memo.size() << endl;
	return 0;
	
}
