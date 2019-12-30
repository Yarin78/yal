#ifndef _MATRIX_H
#define _MATRIX_H

#include <string>
#include <math.h>

//T shoud have +,-,+=,-=,*, /=, == and constructor that takes one int
//and have function bool nless(T a,T b) that return true if abs(a)<abs(b)
//and have isnull true if value is 0 (including tolerance)

inline bool nless(float a,float b) { return fabs(a)<fabs(b); }
inline bool isnull(float f) { return fabs(f)<1e-8; }
inline bool nless(double a,double b) { return fabs(a)<fabs(b); }
inline bool isnull(double d) { return fabs(d)<1e-12; }

//to throws rang errors
struct RankNotEnough {
    RankNotEnough(int _r):r(_r) {}
    int r;
};

struct NoSolution { };

template<class T>
class Matrix {
public:
    Matrix(const Matrix<T> &m):_data(0) { (*this)=m; }
    Matrix():_w(0),_h(0),_data(0) {}
    Matrix(int w,int h):_data(new T[w*h]),_w(w),_h(h) { }
    ~Matrix() { del(); }

    const Matrix<T> &operator=(const Matrix<T> &rhs);

    //Nothing of this is realy neaded!!!
    void zero() { // Creates the zero matrix
			int i=_w*_h; 
			while(i--)
				_data[i]=T(0); 
		}
		
    void id() { // Creates the identity matrix
    	int i=_w*_h;
    	while(i--)
    		_data[i]=(i%(_w+1)||i>_w*_w)?T(0):T(1);
    }

    T* operator[] (int rhs) { return _data+rhs*_w; }
    const T* operator[] (int rhs) const { return _data+rhs*_w; }
    
    Matrix<T> operator*(const Matrix<T> &rhs) const;

    Matrix<T> solve(const Matrix<T> &m) const;

    int width() { return _w; }
    int height() { return _h; }

private:
    void del() { if (_data) delete []_data; }

    T *_data;
    int _w,_h;
};

template<class T>
const Matrix<T> &Matrix<T>::operator=(const Matrix<T> &rhs) {
    if (this==&rhs) return *this;
    del();
    _w=rhs._w; 
    _h=rhs._h;
    int s=_w*_h;
    _data=new T[s];
    while(s--) _data[s]=rhs._data[s]; 
    return *this;
}

template<class T>
Matrix<T> Matrix<T>::operator*(const Matrix<T> &rhs) const {
    int i,j,k;
    if (_w!=rhs._h) throw std::string("Dimensions must agree");
    Matrix<T> ret(_h,rhs._w);

    for(k=0;k<ret._w;++k)
        for(j=0;j<_h;++j) {
            T t(0);
            for(i=0;i<_w;++i)
                t+=_data[i+j*_w]*rhs._data[k+i*rhs._w];
            ret._data[k+j*rhs._w]=t;
        }
    return ret;
}


//May throw NoRankEnough error
template<class T>
Matrix<T> Matrix<T>::solve(const Matrix<T> &m) const {
    Matrix<T> ret(m),w(*this);
    //if (_w!=_h) throw std::string("Must be square matrix");
    if (_h!=m._h) throw std::string("Dimensions must agree");

    //to keep track on p
    int *p=new int[_h],i,j,k,r=0;
    for(i=0;i<_h;++i) p[i]=i;
    for(k=0;k<((_h<_w)?_h:_w);++k) {
        //find largest value
        T m(w._data[k+p[i=r]*_w]);
        for(j=r+1;j<_h;++j)
            if (nless(m,w._data[k+p[j]*_w]))
                m=w._data[k+p[i=j]*_w];

        if (i!=r) p[r]^=p[i]^=p[r]^=p[i];

        if (isnull(m)) continue;

        for(i=k;i<_w;++i) w._data[i+p[r]*_w]/=m;
        for(i=0;i<ret._w;++i) ret._data[i+p[r]*ret._w]/=m;

        for(j=r+1;j<_h;++j) {
            m=w._data[k+p[j]*_w];
            for(i=k;i<_w;++i) w._data[i+p[j]*_w]-=m*w._data[i+p[r]*_w];
            for(i=0;i<ret._w;++i) ret._data[i+p[j]*ret._w]-=m*ret._data[i+p[r]*ret._w];
        }
        ++r;
    }

    for(k=0;k<m._w;++k)
        for(j=r;j<_h;++j)
            if (!isnull(ret._data[k+p[j]*m._w]))
                throw NoSolution();

    if (r!=_w) throw RankNotEnough(r);

    for(j=0;j<ret._w;++j)
        for(k=_w-1;k>=0;--k)
            for(i=k+1;i<_w;++i)
                ret._data[j+p[k]*ret._w]-=w._data[i+p[k]*_w]*ret._data[j+p[i]*ret._w];

    Matrix<T> res(ret._w,_w);
    for(j=0;j<_w;++j)
        for(i=0;i<ret._w;++i)
            res._data[i+j*ret._w]=ret._data[i+p[j]*ret._w];

    delete []p;
    return res;
}

#endif
