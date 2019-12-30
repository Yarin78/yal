#include <iostream>
#include <string>
#include <set>

using namespace std;

// POKER HAND EVALUATOR  (by Jimmy Mårdell, 2004-09-18)
//
// Input:  An array of 5 integers representing the cards.
//         0-12 is 2,3,..,King,Ace in one suit, 13-25 in
//         another, and so on. The input array is not
//         modified.
//
// Output: A score denoting the evaluation of the hand,
//         according to the following formula:
//
//           <typeScore> * 10e6 + sum(k=1..5, <cardK> * 13^(5-k))
//
//         where hand type is any in the following table:
//
//           0 - high card           5 - flush
//           1 - pair                6 - full house
//           2 - two pair            7 - four of a kind
//           3 - three of a kind     8 - straight flush
//           4 - straight            9 - royal straight flush
//
//         and card1, card2... is the value of the cards
//         in importance order (ie for full house, card 1-3
//         denotes the value of the three of a kind etc)

int pokerEval(int c[])  // Sum of all poker evals: 1290285728
{
	int i,j,s=0,f=1,vc[13],sc[5],sv[5],sw[5];
	for(i=0;i<13;i++) vc[i]=0;
	for(i=0;i<5;i++) { sc[i]=sv[i]=0; f&=c[i]/13==c[0]/13; vc[c[i]%13]++; }
	for(i=12;i>=0;i--) { sc[j=vc[i]]++; sw[j]=sv[j]; sv[j]=i; if (j==1) s=s*13+i; }
	if (sc[4]) return 7000000+sv[4]*30940+sv[1];
	if (sc[2]&&sc[3]) return 6000000+sv[3]*30927+sv[2]*14;
	if (sc[3]) return 3000000+sv[3]*30927+s;
	if (sc[2]) return sc[2]*1000000+(sv[2]>?sw[2])*30758+(sv[2]<?sw[2])*182+s;
	if (s==349674) return 90258+1000000*(f?8:4);
	if (s==368714&&f) return 9000000+s;
	int st=(s-121186)%30941==0;
	return s+(st&&f?8:st?4:f?5:0)*1000000;
}

// Conversion functions

int cardToInt(string card)
{
	char v=toupper(card[0]),s=toupper(card[1]);
	int val=v-'A'?v-'K'?v-'Q'?v-'J'?v-'T'?v-'2':8:9:10:11:12;
	int suit=s-'S'?s-'H'?s-'D'?0:1:2:3;
	return suit*13+val;
}

string intToCard(int card)
{		
	string c="  ";
	int v=card%13,s=card/13;
	c[0]=v-12?v-11?v-10?v-9?v-8?v+'2':'T':'J':'Q':'K':'A';
	c[1]=s-3?s-2?s-1?'C':'D':'H':'S';
	return c;
}


// Test

int go(int cards[], int n, int cur)
{
	if (n==5) {
		int eval=pokerEval(cards);
		
		/*
		int e=eval%1000000,c[5];
		for(int i=0;i<5;i++) {
			c[i]=e%13;
			e/=13;
		}
		for(int i=4;i>=0;i--) cout << c[i] << (i?" ":"\n");
		*/
		
		return eval;
	}
	if (cur==52) return 0;
	cards[n]=cur;
	return go(cards,n+1,cur+1) + go(cards,n,cur+1);
}

int main()
{
	int cards[7];
	for(int i=0;i<5;i++) cin >> cards[i];
	set<int> u;
	
	for(int x=0;x<52;x++)
		for(int y=x+1;y<52;y++) {
			bool ok=true;
			for(int i=0;i<5;i++)
				if (cards[i]==x || cards[i]==y)
					ok=false;
			if (ok) {
				int best=0;
				cards[5]=x; cards[6]=y;
				int selected[5];
				for(int i=0;i<7;i++)
					for(int j=i+1;j<7;j++) {
						for(int k=0,t=0;k<7;k++)
							if (k!=i && k!=j)
								selected[t++]=cards[k];
						int val=pokerEval(selected);
						if (val>best)
							best=val;						
					}
				u.insert(best);
			}
		}
	cout << u.size() << endl;
	
	/*			
	int eval=pokerEval(cards);
	cout << eval/1000000 << endl;
	
	eval%=1000000;
	for(int i=0;i<5;i++) {
		cards[i]=eval%13;
		eval/=13;
	}
	for(int i=4;i>=0;i--) cout << cards[i] << " ";
	cout << endl;
	*/
	
	return 0;
}
