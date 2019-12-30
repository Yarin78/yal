package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestRootedTree {

    @Test
    public void testTreeLCA() {
        Random r = new Random(0);
        for (int i = 0; i < 10; i++) {
            int N = 30000 + r.nextInt(10000);
            int[] perm = new int[N];
            for (int j = 0; j < N; j++) {
                perm[j] = j;
            }

            for (int j = 0; j < N; j++) {
                int swap = j + r.nextInt(N-j);
                int tmp = perm[j];
                perm[j] = perm[swap];
                perm[swap] = tmp;
            }

            Graph g = new Graph(N);
            for (int j = 1; j < N; j++) {
                int a = r.nextInt(j);
                g.addEdge(perm[a], perm[j]);
            }
            RootedTree tree = new RootedTree(g.getNode(perm[0]));

            for (int j = 0; j < 100; j++) {
                int a = r.nextInt(N), b = r.nextInt(N);
                RootedTree.Node p = tree.getNode(a), q = tree.getNode(b);

                // Naive solution
                RootedTree.Node pp = p, qq = q;
                List<RootedTree.Node> ptrail = new ArrayList<RootedTree.Node>(), qtrail = new ArrayList<RootedTree.Node>();
                while (pp != null) {
                    ptrail.add(pp);
                    pp = pp.getParent();
                }
                while (qq != null) {
                    qtrail.add(qq);
                    qq = qq.getParent();
                }
                Collections.reverse(ptrail);
                Collections.reverse(qtrail);
                int k = 0;
                while (k+1 < ptrail.size() && k+1 < qtrail.size() && ptrail.get(k+1) == qtrail.get(k+1)) {
                    k++;
                }
                RootedTree.Node expected = ptrail.get(k);

                RootedTree.Node lca = tree.getLowestCommonAncestor(p, q);

                Assert.assertSame(lca, expected);
            }
        }
    }
}
