import sys
from collections import defaultdict, deque

# Port from the C++ version; covered by tests

def bipartite_matching(graph):
    '''Finds the maximum bipartite matching. graph: node -> [edges]'''
    unodes = set(graph.keys())
    vnodes = set(v for edge in graph.values() for v in edge)
    uunsat = set(unodes)
    matched = {}  # v -> u

    while True:
        uprev = {}
        vprev = {}

        def _rec(y):
            x = vprev.get(y)
            if x is None:
                return False
            w = uprev.get(x)
            vprev[y] = uprev[x] = None

            if x not in uunsat and not _rec(w):
                return False
            matched[y] = x
            if x in uunsat:
                uunsat.remove(x)
            return True

        q = deque(uunsat)

        while q:
            x = q.popleft()
            for y in graph[x]:
                if vprev.get(y) is None:
                    vprev[y] = x
                w = matched.get(y)
                if w is not None and uprev.get(w) is None:
                    uprev[w] = y
                    q.append(w)

        # Don't short-circuit by removing the []! Will lower performance
        if not any([_rec(v) for v in vnodes if v not in matched]):
            break

    return {u: v for v, u in matched.items()}
