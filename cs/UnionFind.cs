namespace Algorithms
{
    public class UnionFind
    {
        private readonly int[] p;
        private readonly int[] size;

        public UnionFind(int n)
        {
            p = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++)
            {
                p[i] = i;
                size[i] = 1;
            }
        }

        public int FindSet(int e)
        {
            int v = e;
            while (p[v] != v) v = p[v];
            int root = v;
            while (p[e] != root)
            {
                // Path compression
                int t = e;
                e = p[e];
                p[t] = root;
            }
            return root;
        }

        public void UnionSet(int a, int b)
        {
            a = FindSet(a);
            b = FindSet(b);
            if (a == b) return;
            if (size[b] > size[a])
            {
                int c = a;
                a = b;
                b = c;
            }
            p[b] = a;
            size[a] += size[b];
        }
    }
}
