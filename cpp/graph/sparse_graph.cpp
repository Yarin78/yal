#include <iostream>
#include <algorithm>
#include <vector>

using namespace std;

int n, m;
struct edge {
   int u, v;
   edge( int U, int V ) { u = U; v = V; }
};
bool operator < ( const edge &A, const edge &B ) { return A.u < B.u; }

struct sparse_graph {
   vector<edge> E;
   vector< vector<edge>::iterator > V;

   void insert_edge( const edge &e ) {
      E.push_back( e );
   }

   void init() {
      V.resize(n+1);
      sort( E.begin(), E.end() );
      V[0] = E.begin();
      for( int i = 1; i <= n; ++i )
         for( V[i] = V[i-1]; V[i] != E.end() && V[i]->u < i; ++V[i] );
   }

   inline vector<edge>::iterator begin( int u ) { return V[u]; }
   inline vector<edge>::iterator end( int u ) { return V[u+1]; }
} graph;

int main( void ) {
   scanf( "%d%d", &n, &m );
   
   for( int i = 0; i < m; ++i ) {
      int u, v;
      scanf( "%d%d", &u, &v ); --u; --v;
      graph.insert_edge( edge( u, v ) );
      graph.insert_edge( edge( v, u ) );
   }
   graph.init();

   for( vector<edge>::iterator it = graph.begin(u); it != graph.end(u); ++it ) {
   }
   return 0;
}
