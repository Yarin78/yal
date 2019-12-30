#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <list>
#include <cmath>

/* @JUDGE_ID: 1559ZY 10095 C++ */

template <int d>
class Wrapped_array {
	private:
		double coord[d];
	public:
		// default
		Wrapped_array() {}

		// copy from Wrapped_array
		Wrapped_array (const Wrapped_array& p) {
			for (int i=0;i<d;++i)
				coord[i]=p.coord[i];
		}

		// copy from double*
		Wrapped_array (const double* p) {
			for(int i=0;i<d;++i)
				coord[i]=p[i];
		}

		// assignment
		Wrapped_array& operator = (const Wrapped_array& p) {
			for (int i=0; i<d; ++i)
				coord[i] = p.coord[i];
			return *this;
		}

		// coordinate access
		double& operator[](int i) { return coord[i]; }		
		const double& operator [] (int i) const { return coord[i]; }
		const double* begin() const { return coord; }
		const double* end() const { return coord+d; }
};

template<int d> class Miniball;
template<int d> class Basis;

// Miniball
// --------

template <int d>
class Miniball {
	public:
		// types
		typedef Wrapped_array<d> Point;
		typedef typename list<Point>::iterator It;
	private:
		// data members
		list<Point> L;              // STL list keeping the points
		Basis<d>    B;              // basis keeping the current ball
		It          support_end;    // past-the-end iterator of support set

		// private methods
		void        mtf_mb(It k);
		void        pivot_mb(It k);
		void        move_to_front(It j);
		double      max_excess(It t, It i, It& pivot) const;
		double      sqr(double r) const {return r*r;}

	public:
		Miniball() {}
		void        check_in(const Point& p) { L.push_back(p); }
		void        build(bool pivoting = true);
		Point       center() const { return Point(B.center()); }
		double      squared_radius() const { return B.squared_radius(); }
};

// Basis
// -----

template <int d>
class Basis {
	private:
		typedef Wrapped_array<d> Point;
		int m,s;
		double q0[d],z[d+1],f[d+1],v[d+1][d],a[d+1][d],c[d+1][d],sqr_r[d+1];
		double *current_c,current_sqr_r;
		double sqr(double r) const {return r*r;}
	public:
		Basis() { reset(); }
		const double* center() const { return current_c; }
		double squared_radius() const { return current_sqr_r; }
		int size() const { return m; }
		int support_size() const { return s; }
		double excess (const Point& p) const;
		void reset();
		bool push (const Point& p);
		void pop() { --m; }
};

// Miniball
// --------

template <int d>
void Miniball<d>::build(bool pivoting) {
	B.reset();
	support_end=L.begin();
	if (pivoting)
		pivot_mb(L.end());
	else
		mtf_mb(L.end());
}

template <int d>
void Miniball<d>::mtf_mb (It i)
{
	support_end=L.begin();
	if((B.size())==d+1) return;
	for(It k=L.begin(); k!=i;) {
		It j=k++;
		if (B.excess(*j)>0) {
			if (B.push(*j)) {
				mtf_mb(j);
				B.pop();
				move_to_front(j);
			}
		}
	}
}

template <int d>
void Miniball<d>::move_to_front(It j)
{
	if (support_end==j)
		support_end++;
	L.splice(L.begin(),L,j);
}

template <int d>
void Miniball<d>::pivot_mb (It i)
{
	It t=++L.begin();
	mtf_mb(t);
	double max_e,old_sqr_r;
	do {
		It pivot;
		max_e=max_excess(t,i,pivot);
		if (max_e>0) {
			t=support_end;
			if (t==pivot) ++t;
			old_sqr_r=B.squared_radius();
			B.push(*pivot);
			mtf_mb(support_end);
			B.pop();
			move_to_front(pivot);
		}
	} while ((max_e>0) && (B.squared_radius()>old_sqr_r));
}


template <int d>
double Miniball<d>::max_excess (It t, It i, It& pivot) const
{
	const double *c=B.center(), sqr_r=B.squared_radius();
	double e,max_e=0;
	for (It k=t;k!=i;++k) {
		const double *p=(*k).begin();
		e=-sqr_r;
		for(int j=0;j<d;++j)
			 e+=sqr(p[j]-c[j]);
		if (e>max_e) {
			max_e=e;
			pivot=k;
		}
	}
	return max_e;
}


// Basis
// -----

template <int d>
double Basis<d>::excess (const Point& p) const
{
	double e=-current_sqr_r;
	for(int k=0;k<d;++k)
		e+=sqr(p[k]-current_c[k]);
	return e;
}

template <int d>
void Basis<d>::reset ()
{
	m=s=0;
	// we misuse c[0] for the center of the empty sphere
	for(int j=0;j<d;++j)
		c[0][j]=0;
	current_c=c[0];
	current_sqr_r=-1;
}

template <int d>
bool Basis<d>::push (const Point& p)
{
	int i,j;
	double eps=1e-32;
	if (m==0) {
		for (i=0; i<d; ++i)
			q0[i] = p[i];
		for (i=0; i<d; ++i)
			c[0][i] = q0[i];
		sqr_r[0] = 0;
	} else {
		// set v_m to Q_m
		for(i=0;i<d;++i)
			v[m][i]=p[i]-q0[i];

		// compute the a_{m,i}, i< m
		for(i=1;i<m;++i) {
			a[m][i]=0;
			for (j=0;j<d;++j)
				a[m][i]+=v[i][j]*v[m][j];
			a[m][i]*=(2/z[i]);
		}

		// update v_m to Q_m-\bar{Q}_m
		for(i=1;i<m;++i)
			for(j=0;j<d;++j)
				v[m][j]-=a[m][i]*v[i][j];

		// compute z_m
		z[m]=0;
		for(j=0;j<d;++j)
			z[m]+=sqr(v[m][j]);
		z[m]*=2;

		// reject push if z_m too small
		if (z[m]<eps*current_sqr_r)
			return false;

		// update c, sqr_r
		double e=-sqr_r[m-1];
		for(i=0;i<d;++i)
			e+=sqr(p[i]-c[m-1][i]);
		f[m]=e/z[m];

		for(i=0;i<d;++i)
			c[m][i]=c[m-1][i]+f[m]*v[m][i];
		sqr_r[m]=sqr_r[m-1]+e*f[m]/2;
	}
	current_c=c[m];
	current_sqr_r=sqr_r[m];
	s=++m;
	return true;
}

int main () {
	const int d=3;
	int n;
		
	Miniball<d>::Point p;
	
	cin >> n;
	while (n>0) {
		Miniball<d> mb;
		for(int i=0;i<n;i++) {
			for(int j=0;j<d;j++)
				cin >> p[j];
			mb.check_in(p);
		}

		mb.build();
		
		const Wrapped_array<d>& p=mb.center();
		printf("%0.4lf",sqrt(mb.squared_radius()));
		for(int i=0;i<d;i++)
			printf(" %0.4lf",p[i]);
		printf("\n");

		cin >> n;
	}

	return 0;
}

/* @END_OF_SOURCE_CODE */
