package yarin.yal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SegmentTree<T> {
    private final Comparator<T> comparison;
    private final List<T> data;
    private final int[] tree;
    private final int n;

    private static int getTreeSize(int n) {
        n--;
        while ((n & (n - 1)) > 0) {
            n = n & (n - 1);
        }
        return n*4;
    }

    public SegmentTree(int n, Comparator<T> comparison) {
        this.n = n;
        this.comparison = comparison;
        this.data = new ArrayList<T>(n);
        for (int i = 0; i < n; i++) {
            this.data.add(null);
        }
        this.tree = new int[getTreeSize(n)];
        init(1, 0, n - 1);
    }

    public SegmentTree(Collection<T> data, Comparator<T> comparison) {
        this.data = new ArrayList<T>(data);
        this.comparison = comparison;
        n = this.data.size();
        tree = new int[getTreeSize(n)];
        init(1, 0, n - 1);
    }

    private void init(int node, int b, int e) {
        if (b == e) {
            tree[node] = b;
        } else {
            init(2 * node, b, (b + e) / 2);
            init(2 * node + 1, (b + e) / 2 + 1, e);
            tree[node] = comparison.compare(data.get(tree[2*node]), data.get(tree[2*node + 1])) <= 0
                            ? tree[2*node] : tree[2*node + 1];
        }
    }

    public int query(int start, int end) {
        if (start < 0 || end > n || start >= end) {
            throw new IllegalArgumentException();
        }
        return query(1, 0, n - 1, start, end - 1);
    }

    private int query(int node, int b, int e, int i, int j) {
        if (i > e || j < b) {
            return -1;
        }
        if (b >= i && e <= j) {
            return tree[node];
        }

        int p1 = query(2 * node, b, (b + e) / 2, i, j);
        int p2 = query(2 * node + 1, (b + e) / 2 + 1, e, i, j);
        if (p1 == -1) {
            return p2;
        }
        if (p2 == -1) {
            return p1;
        }
        return comparison.compare(this.data.get(p1), this.data.get(p2)) <= 0 ? p1 : p2;
    }

    public void set(int index, T value) {
        set(1, 0, n - 1, index, value);
    }

    public T get(int index) {
        return this.data.get(index);
    }

    private void set(int node, int b, int e, int index, T value) {
        if (b == e)	{
            this.data.set(index, value);
            this.tree[node] = b;
        } else {
            if (index <= (b+e)/2) {
                set(2 * node, b, (b + e) / 2, index, value);
            } else {
                set(2 * node + 1, (b + e) / 2 + 1, e, index, value);
            }
            tree[node] = comparison.compare(this.data.get(tree[2 * node]), this.data.get(tree[2 * node + 1])) <= 0
                            ? tree[2 * node] : tree[2 * node + 1];
        }
    }
}