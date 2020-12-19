import sys
import random
from collections import defaultdict
from yal import matching

def test_trivial():
    g = {
        'FOO' : ['bar'],
        'X': ['a', 'b'],
        'Y': ['b', 'c'],
        'Z': ['c'],
        'BLAH': []
    }
    m = matching.bipartite_matching(g)
    # Unique solution
    assert(m == {'Z': 'c', 'X': 'a', 'FOO': 'bar', 'Y': 'b'})

def test_input_file():
    with open("tests/matching.in", "r") as f:
        num_cases = int(f.readline())
        with open("tests/matching.ans", "r") as fans:
            for _ in range(num_cases):
                n, m, num_edges = map(int, f.readline().split(' '))
                expected = int(fans.readline())
                #print(n, m, num_edges)
                edges = defaultdict(lambda: list())
                for _ in range(num_edges):
                    x, y = map(int, f.readline().split(' '))
                    edges[x].append(y)

                m = matching.bipartite_matching(edges)
                assert len(m) == expected





if __name__ == "__main__":
    # Generates matching input

    # num_cases, nodes_mean, nodes_stdev, edges_per_node_mean, edges_per_node_stdev
    num_cases = int(sys.argv[1])
    nodes_mean = float(sys.argv[2])
    nodes_stdev = float(sys.argv[3])
    edges_per_node_mean = float(sys.argv[4])
    edges_per_node_stdev = float(sys.argv[5])

    def rand_int(mean, stdev, min_value, max_value=9999999):
        v = int(random.gauss(mean, stdev))
        return max(v, min_value)

    print(num_cases)
    for _ in range(num_cases):
        n = rand_int(nodes_mean, nodes_stdev, 1)
        m = rand_int(nodes_mean, nodes_stdev, 1)
        u = rand_int(edges_per_node_mean * nodes_mean, edges_per_node_stdev * nodes_mean, 1, n*m)

        edges = [(i, j) for i in range(n) for j in range(m)]
        random.shuffle(edges)

        print(n, m, u)
        [print(edge[0], edge[1]) for edge in edges[:u]]

