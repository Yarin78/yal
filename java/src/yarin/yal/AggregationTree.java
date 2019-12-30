package yarin.yal;

import java.util.ArrayList;
import java.util.List;

public class AggregationTree<T> {
    public interface Aggregator<T> {
        public T aggregate(T first, T second);
    }

    private final Aggregator<T> aggregator;
    private final List<T> tree;
    private final int n;

    private static int getTreeSize(int n) {
        n--;
        while ((n & (n - 1)) > 0) {
            n = n & (n - 1);
        }
        return n*4;
    }

    public AggregationTree(int n, T defaultValue, Aggregator<T> aggregator) {
        this.aggregator = aggregator;
        this.n = n;
        List<T> data = new ArrayList<T>(n);
        for (int i = 0; i < n; i++) {
            data.add(i, defaultValue);
        }
        tree = new ArrayList<T>(getTreeSize(n));
        for (int i = 0; i < getTreeSize(n); i++) {
            tree.add(defaultValue);
        }
        init(1, 0, n - 1, data);
    }

    public AggregationTree(List<T> data, T defaultValue, Aggregator<T> aggregateFunc) {
        this.aggregator = aggregateFunc;
        n = data.size();
        tree = new ArrayList<T>(getTreeSize(n));
        for (int i = 0; i < getTreeSize(n); i++) {
            tree.add(defaultValue);
        }
        init(1, 0, n - 1, data);
    }

    private void init(int node, int b, int e, List<T> data) {
        if (b == e) {
            tree.set(node, data.get(b));
        } else {
            init(2 * node, b, (b + e) / 2, data);
            init(2 * node + 1, (b + e) / 2 + 1, e, data);
            tree.set(node, aggregator.aggregate(tree.get(2*node), tree.get(2*node + 1)));
        }
    }

    public T query(int start, int end) {
        if (start < 0 || end > n || start >= end) {
            throw new IllegalArgumentException();
        }
        return query(1, 0, n - 1, start, end - 1);
    }

    private T query(int node, int b, int e, int i, int j) {
        if (i > e || j < b) {
            throw new IllegalArgumentException();
        }
        if (b >= i && e <= j) {
            return tree.get(node);
        }

        T aggregate = null;
        boolean set = false;

        if (i <= (b+e)/2) {
            aggregate = query(2 * node, b, (b + e) / 2, i, j);
            set = true;
        }
        if (j >= (b+e) / 2 + 1) {
            T p = query(2 * node + 1, (b + e) / 2 + 1, e, i, j);
            aggregate = set ? this.aggregator.aggregate(aggregate, p) : p;
        }
        return aggregate;
    }

    public void set(int index, T value)	{
        set(1, 0, n - 1, index, value);
    }

    private void set(int node, int b, int e, int index, T value) {
        if (b == e) {
            tree.set(node, value);
        } else {
            if (index <= (b+e)/2) {
                set(2 * node, b, (b + e) / 2, index, value);
            } else {
                set(2 * node + 1, (b + e) / 2 + 1, e, index, value);
            }
            tree.set(node, aggregator.aggregate(tree.get(2 * node), tree.get(2 * node + 1)));
        }
    }
}