#include <ext/hash_map>

using namespace std;
using namespace __gnu_cxx;

namespace __gnu_cxx {
	template<> struct hash<std::string> {
		size_t operator()(const std::string& x) const
			{ return hash<const char*>()(x.c_str()); }
	};
	template<> struct hash<long long int> {
		size_t operator()(long long int x) const { return x; }
	};
	template<> struct hash<unsigned long long int> {
		size_t operator()(unsigned long long int x) const { return x; }
	};
}
