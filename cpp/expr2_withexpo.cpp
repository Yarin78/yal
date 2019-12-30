#include <iostream>
#include <string>
#include <map>

using namespace std;

enum { PLUS, MINUS, MULT, DIV, CONS, VAR, EXPO};
const int prec[4]={0,0,1,1};

map<string,long long> varvalue;

template<class T>
class Expr
{
	private:
		int _op;
		Expr *_left, *_right;
		T _value;
		string _var;
		const char *_p;

	public:	
		Expr<T>* get_term(void)
		{
			Expr *e=get_factor();
			if (!e) return 0; // Syntax error			
			while (1) {
				int op;
				switch (*_p) {
					case 0 : case ')' : return e;
					case '+' : op=PLUS; break;
					case '-' : op=MINUS; break;
					default : return 0; // Syntax error
				}				
				_p++;
				Expr *tmp=new Expr;
				tmp->_left=e;
				e=tmp;
				e->_op=op;
				e->_right=get_factor();
				if (!e->_right) return 0; // Syntax error
			}
		}

		Expr<T>* get_factor(void)
		{
			Expr *e=get_expo();
			if (!e) return e;
			while (1) {
				int op;			
				switch (*_p) {
					case 0 : case '+' : case '-' : case ')' : return e;
					case '*' : op=MULT; break;
					case '/' : op=DIV; break;
					default : return 0; // Syntax error
				}
				_p++;
				Expr *tmp=new Expr;
				tmp->_left=e;
				e=tmp;
				e->_op=op;
				e->_right=get_expo();
				if (!e->_right) return 0; // Syntax error
			}
		}
		
		Expr<T>* get_expo(void)
		{
			Expr *e=get_var();
			if (!e) return e;
			while (1) {
				int op;			
				switch (*_p) {
					case 0 : case '+' : case '-' : case '*' : case '/' : case ')' : return e;
					case '^' : op=EXPO; break;
					default : return 0; // Syntax error
				}
				_p++;
				Expr *tmp=new Expr;
				tmp->_left=e;
				e=tmp;
				e->_op=op;
				e->_right=get_var();
				if (!e->_right) return 0; // Syntax error
			}
		}

		Expr<T>* get_var(void)
		{
			if (isdigit(*_p) || *_p=='-') {
				// Constant				
				Expr *e=new Expr;
				e->_op=CONS;
				e->_value=0;
				int sign=1;
				if (*_p=='-') { // Unary minus
					sign=-1;
					_p++;
					if (!isdigit(*_p)) return 0;
				}
				while (isdigit(*_p)) {
					e->_value=e->_value*10+*_p-'0';
					_p++;
				}
				e->_value*=sign;
				return e;
			}
			if (isalpha(*_p) || *_p=='_') {
				// Variable
				Expr *e=new Expr;
				e->_op=VAR;
				const char *q=_p;
				while (isalpha(*_p) || isdigit(*_p) || *_p=='_') _p++;
				e->_var=string(q,_p);
				// LOOKUP variable value
				e->_op=CONS;
				e->_value=varvalue[e->_var];
				return e;
			}
			if (*_p=='(') {
				_p++;
				Expr *e=get_term();
				if (*_p!=')') return 0;
				_p++;
				return e;
			}			
			return 0;
		}
		
		bool correct_order(Expr *a, Expr *b)
		{
			if (a->_op==CONS) return true;
			if (b->_op==CONS) return false;
			if (a->_op==VAR && b->_op==VAR) return a->_var<=b->_var;
			if (a->_op==VAR) return true;
			if (b->_op==VAR) return false;
			return true;
		}				
		
		void normalize(void)
		{
			if (_op==CONS || _op==VAR) return;			
			_left->normalize();
			_right->normalize();
			if (_op==MINUS || _op==DIV) return;			
			// Transform A*(B*C) => (A*B)*C
			if (_right->_op==_op) {
				Expr *a=_left,*b=_right->_left,*c=_right->_right;
				_left=_right;
				_right=c;
				_left->_left=a;
				_left->_right=b;
				normalize();
			}
			// Move constants forward in the expression			
			if (_op==_left->_op) {
				if (!correct_order(_left->_right,_right)) {
					swap(_left->_right,_right);
					normalize();
				}
			} else {
				if (!correct_order(_left,_right)) {
					swap(_left,_right);
					normalize();
				}
			}
		}
		
		bool simplify(void)
		{
			if (_op==CONS || _op==VAR) return false;
			bool flag=_left->simplify()|_right->simplify();
			if (_left->_op==CONS && _right->_op==CONS) {
				T v1=_left->_value,v2=_right->_value;				
				delete _left;
				delete _right;				
				switch (_op) {
					case PLUS  : _value=v1+v2; break;
					case MINUS : _value=v1-v2; break;
					case MULT  : _value=v1*v2; break;
					case DIV   : _value=v1/v2; break;
					case EXPO  :
						_value=1;
						while (v2--) _value*=v1;
						break;
				}
				_op=CONS;
				return true;
			}
			// 0*(..), (..)*0 => 0
			if (_op==MULT) {
				if ((_left->_op==CONS && _left->_value==0) || (_right->_op==CONS && _right->_value==0)) {
					delete _left;
					delete _right;
					_op=CONS;
					_value=0;
					return true;
				}
			}
			// 1*(..),0+(..) => (..)
			if (_left->_op==CONS && ((_left->_value==1 && _op==MULT) || (_left->_value==0 && _op==PLUS))) {
				Expr *oleft=_left,*oright=_right;
				*this=*_right;
				delete oleft;
				delete oright;
				return true;
			}
			// (..)*1 => (..)
			if (_right->_op==CONS && ((_right->_value==1 && _op==MULT) || (_right->_value==0 && _op==PLUS))) {
				Expr *oleft=_left,*oright=_right;
				*this=*_left;
				delete oleft;
				delete oright;
				return true;
			}
			return flag;
		}
		
		void show(int lvl=0)
		{
			if (_op==CONS) {
				cout << _value;
				return;
			}
			if (_op==VAR) {
				cout << _var;
				return;
			}
			if (_op<lvl) cout << "(";
			_left->show(prec[_op]);
			switch (_op) {
				case PLUS  : cout << "+"; break;
				case MINUS : cout << "-"; break;
				case MULT  : cout << "*"; break;
				case DIV   : cout << "/"; break;
			}
			_right->show(prec[_op]);
			if (_op<lvl) cout << ")";
		}

		void show_tree(void)
		{
			if (_op==CONS) {
				cout << _value << " ";
				return;
			}
			if (_op==VAR) {
				cout << _var << " ";
				return;
			}
			cout << "( ";
			_left->show_tree();
			_right->show_tree();
			switch (_op) {
				case PLUS  : cout << "+ "; break;
				case MINUS : cout << "- "; break;
				case MULT  : cout << "* "; break;
				case DIV   : cout << "/ "; break;
			}
			cout << ") ";
		}
		
		Expr<T> *read_expression(string s)
		{
			// Remove whitespace
			for(int i=0;i<s.size();i++)
				if (isspace(s[i])) {
					s.erase(i,1);
					i--;
				}
			_p=s.c_str();
			Expr *e=get_term();
			return *_p?0:e;
		}
};

int main(void)
{
	Expr<int> expr,*e;
	string s;
	
	while (getline(cin,s)) {	
		varvalue["x"]=1;
		varvalue["y"]=11;
		e=expr.read_expression(s);
		if (e) {			
			e->simplify();
			//do {
//				e->normalize();
			//} while (e->simplify());
			e->show(); cout << endl;		
		} else
			cout << "Syntax error!" << endl;
	}
	return 0;
}
