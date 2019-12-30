#ifndef _BIGNUM_H
#define _BIGNUM_H

#include <iostream>
#include <string>

using namespace std;

class Bignum
{
	private:
		string data;
		int len,sign;
	public:
		Bignum(int n=0) { set(n); };
		
		int nodigits() { return len; }
		string digits() { return data; }
		int digsum() { int sum=0; for(int i=0;i<data.size();i++) sum+=data[i]-'0'; return sum; }

		bool operator!(void) const { return data[0]=='0'; }
	 	bool operator==(const Bignum &rhs) const { return data==rhs.data && sign==rhs.sign; }
	 	bool operator!=(const Bignum &rhs) const { return !(*this==rhs); }
	 	bool operator<=(const Bignum &rhs) const { return *this==rhs || *this<rhs; }
	 	bool operator>=(const Bignum &rhs) const { return *this==rhs || *this>rhs; }
	 	bool operator>(const Bignum &rhs) const { return rhs<*this; }
	 	bool operator<(const Bignum &rhs) const {
	 		return sign!=rhs.sign?sign>rhs.sign:
	 			operator==(rhs)?false:sign^(len!=rhs.len?len<rhs.len:data<rhs.data);
	 	}

	 	Bignum operator+=(const Bignum &rhs) { return *this=*this+rhs; }
	 	Bignum operator-=(const Bignum &rhs) { return *this=*this-rhs; }
	 	Bignum operator*=(const Bignum &rhs) { return *this=*this*rhs; }
	 	Bignum operator/=(const Bignum &rhs) { return *this=*this/rhs; }
	 	Bignum operator%=(const Bignum &rhs) { return *this=*this%rhs; }

		Bignum operator*(const Bignum &rhs) const { return mul(rhs); }
	 	Bignum operator/(const Bignum &rhs) const { return div(rhs); }
	 	Bignum operator%(const Bignum &rhs) const { return div(rhs,true); }
	 	Bignum operator-() const { Bignum ans(*this); ans.sign^=1; return ans; }
	 	Bignum operator-(const Bignum &rhs) const { Bignum neg(-rhs); return *this+neg; }
	 	Bignum operator+(const Bignum &rhs) const {
	 		if (lessthan(rhs)) return rhs+*this;
	 		if (sign^rhs.sign) { // Subtract
 				Bignum dif(*this);
 				dif.sub(rhs,len-rhs.len);
 				dif.fix();
 				return dif;
 			} else { // Add
 				Bignum sum(*this);
 				if (sum.add(rhs,len-rhs.len)) {
 					sum.data='1'+sum.data;
 					sum.len++;
 				}
 				return sum;
 			}
	 	}

		// Unsigned less than
		bool lessthan(const Bignum &t) const { return len!=t.len?len<t.len:data<t.data; }

		bool sub(const Bignum &t, int pos) {
			int cf=0;
			if (pos+t.len>len) return true;
			for(int i=pos+t.len-1;i>=0;i--) {
				data[i]-=(pos>i?0:t.data[i-pos]-'0')+cf;
				cf=data[i]<'0'?data[i]+=10,1:0;
				if (!cf && i<pos) break;
			}
			return cf;
		}

		bool add(const Bignum &t, int pos, int mul=1) {
			int cf=0;
			for(int i=pos+t.len-1;i>=0;i--) {
				int d=(data[i]-'0')+(pos>i?0:(t.data[i-pos]-'0')*mul)+cf;
				data[i]=d%10+'0';
				cf=d/10;				
				if (!cf && i<pos) break;
			}
			return cf;
		}
		
		Bignum mul(const Bignum &t) const {
			Bignum f;
			f.data.resize(f.len=len+t.len);
			for(int i=0;i<f.len;i++) f.data[i]='0';
			for(int i=0;i<t.len;i++)
				f.add(*this,i+1,t.data[i]-'0');
			f.sign=sign^t.sign;
			f.fix();
			return f;
		}

		Bignum div(const Bignum &t, bool modulo=false) const {
			int pos=0;
			Bignum s(*this),ans;
			while (pos+t.len<=s.len) {
				int cnt=0;
				while (!s.sub(t,pos)) cnt++;
				s.add(t,pos);
				ans.push_back(cnt);
				pos++;
			}
			if (modulo) {
				s.fix();
				return s;
			}
			if (ans.data[0]>'0')
				ans.sign=s.sign^t.sign;
			return ans;
		}
				
		void fix(void) { // Remove leading zeros
			int i;
			for(i=0;i<len && data[i]=='0';i++);
			if (i==len) { i--; sign=0; }
			data.erase(0,i);
			len-=i;
		}

		void push_back(int d) {
			if (data[0]=='0')
				data[0]+=d;
			else {
				data+=char('0'+d);
				len++;
			}
		}

		void set(int n) {
			data="";
			len=0;
			sign=n<0;
			n=abs(n);
			do {
				data=char(n%10+'0')+data;
				len++;
				n/=10;
			} while (n);
		}

	 	Bignum qdiv(int n) {
	 		int v=0;
	 		if (n<0) { sign^=1; n=-n; }
	 		for(int i=0;i<len;i++) {
	 			v=v*10+data[i]-'0';
	 			data[i]='0'+v/n;
	 			v=v%n;
	 		}
	 		fix();
	 		return *this;
	 	}

		friend istream &operator>>(istream &is, Bignum &rhs);
		friend ostream &operator<<(ostream &os, const Bignum &rhs);
		friend Bignum abs(const Bignum &b);
		friend bool isnull(const Bignum &b);
};

istream &operator>>(istream &is, Bignum &rhs)
{
	char c;
	rhs.set(0);
	while (is.get(c)) if (c>' ') break;
	if (c=='-') { rhs.sign=1; is >> c; }
	for(;is && isdigit(c);is.get(c)) rhs.push_back(c-'0');
	is.putback(c);
	return is;
}

ostream &operator<<(ostream &os, const Bignum &rhs)
{
	if (rhs.sign) os << "-";
	os << rhs.data;
	return os;
}

Bignum abs(const Bignum &t)
{
	Bignum ans(t);
	ans.sign=0;
	return ans;
}

bool isnull(const Bignum &t)
{
	return !t;
}

Bignum sqrt(const Bignum &t)
{
  Bignum lo=0,hi=t+1;
  while (lo+1<hi) {
  	Bignum x=lo+hi;
  	x.qdiv(2);
  	if (x*x>t) hi=x; else lo=x;
  }
  return lo;
}

#endif
