#ifndef _RAT_H
#define _RAT_H

using namespace std;

//requires % unary -, /=, * - + *= -= += ==

template<typename T>
class RatNum {
	public:
    typedef RatNum<T> RT;
    T n,d;

    RatNum(T _n=T(0),T _d=T(1)) : n(_n), d(_d) { simpl(); }

    RT &operator=(const RT &rhs) { n=rhs.n; d=rhs.d; return *this; }

    RT operator*(const RT &rhs) const { return RT(n*rhs.n,d*rhs.d); }
    RT operator/(const RT &rhs) const { return RT(n*rhs.d,d*rhs.n); }
    RT operator+(const RT &rhs) const { return RT(n*rhs.d+rhs.n*d,d*rhs.d); }
    RT operator-(const RT &rhs) const { return RT(n*rhs.d-rhs.n*d,d*rhs.d); }
    RT operator-(void) const { return RT(-n,d); }
    bool operator!(void) const { return n==0; }

    RT &operator*=(const RT &rhs) { n*=rhs.n; d*=rhs.d; simpl(); return *this; }
    RT &operator/=(const RT &rhs) { n*=rhs.d; d*=rhs.n; simpl(); return *this; }
    RT &operator+=(const RT &rhs) { n*=rhs.d; n+=rhs.n*d; d*=rhs.d; simpl(); return *this; }
    RT &operator-=(const RT &rhs) { n*=rhs.d; n-=rhs.n*d; d*=rhs.d; simpl(); return *this; }

		bool operator==(const RT &rhs) const { return this->n==rhs.n && this->d==rhs.d; }
		bool operator!=(const RT &rhs) const { return this->n!=rhs.n || this->d!=rhs.d; }
		bool operator<(const RT &rhs) const { return n*rhs.d-rhs.n*d<T(0); }
		bool operator>(const RT &rhs) const { return n*rhs.d-rhs.n*d>T(0); }
		bool operator<=(const RT &rhs) const { return n*rhs.d-rhs.n*d<=T(0); }
		bool operator>=(const RT &rhs) const { return n*rhs.d-rhs.n*d>=T(0); }
		
	RT inverse(void) const { return RT(d,n); }

	string evaluate(void) const {
		char s[100];
		sprintf(s,"%lf",(double)n/d);
		return s;
	}
	
	bool greaterEqualThanOne(void) const {
		return n>=d;
	}
	
	bool isZero(void) const {
		return n==T(0);
	}
				
	private:
    void simpl(void) { 
      T _d=gcd(n,d);
	   	n/=_d;
	   	d/=_d;
      if (d<T(0)) {
        d=-d;
        n=-n;
      }
      if (n==T(0)) d=T(1);
    }
		
		// Returns a positive integer for all integer values on a & b
		T gcd(T a, T b) { return gcd_rec(abs(a),abs(b)); }
		T gcd_rec(T a, T b) { return (b!=T(0))?gcd_rec(b,a%b):(a!=T(0)?a:T(1)); }
};

template<typename T>
double sqrt(const RatNum<T> v) {
	return sqrt((double)v.n/v.d);
}

template<typename T>
RatNum<T> abs(const RatNum<T> v) {
	return RatNum<T>(abs(v.n),v.d);
}

template<typename T>
inline bool isnull(const RatNum<T> v) { return isnull(v.n); }

template<typename T>
ostream &operator<<(ostream &lhs, const RatNum<T> &rhs) {
  if (!(rhs.d==1))
    lhs << rhs.n << "/" << rhs.d;
  else
    lhs << rhs.n;
  return lhs;
}

template<typename T>
istream &operator>>(istream &lhs, RatNum<T> &rhs) {
  lhs >> rhs.n;
  if (lhs.peek()=='/') {
    lhs.get();
    lhs >> rhs.d;
  } else
    rhs.d=1;
  return lhs;
}

#endif
