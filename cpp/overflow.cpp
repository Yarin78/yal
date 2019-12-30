template<class T>
class DetectOverflow {
	typedef DetectOverflow<T> DOT;
	private:		
		const static T m=9223372036854775807LL;
		void overflow(const T v1, const T v2, char op) const
		{
			cerr << v1 << " " << op << " " << v2 << " causes overflow." << endl;
			exit(-1);
		}
	
	public:
		T v;		
		DetectOverflow(const T _v=T(0)) { v=_v; }
		DOT operator=(const DOT &rhs) { v=rhs.v; return *this; }
		DOT operator*(const DOT &rhs) const {			
			T v1=abs(v),v2=abs(rhs.v);
			if (isnull(v1) || isnull(v2)) return T(0);
			if (m/v1<v2) overflow(v,rhs.v,'*');			
			return DOT(v*rhs.v);
		}
		DOT operator/(const DOT &rhs) { return DOT(v/rhs.v); }
		DOT operator+(const DOT &rhs) const {			
			if ((v<0 && rhs.v>0) || (v>0 && rhs.v<0))
				return DOT(v+rhs.v);
			T v1=abs(v),v2=abs(rhs.v);
			if (v1+v2<0) overflow(v,rhs.v,'+');
			return DOT(v+rhs.v);
		}
		DOT operator-() const { return DOT(-v); }
		DOT operator-(const DOT &rhs) const { return *this+(-rhs); }
		DOT operator*=(const DOT &rhs) { return *this=*this*rhs; }			
		DOT operator/=(const DOT &rhs) { return *this=*this/rhs; }			
		DOT operator+=(const DOT &rhs) { return *this=*this+rhs; }			
		DOT operator-=(const DOT &rhs) { return *this=*this-rhs; }			
};

template<class T>
ostream &operator<<(ostream &os, const DetectOverflow<T> &rhs) {
	os << rhs.v; return os;
}

template<class T>
istream &operator>>(istream &is, DetectOverflow<T> &rhs) {
	is >> rhs.v; return is;
}
