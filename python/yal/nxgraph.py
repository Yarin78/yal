import functools
import heapq
import networkx as nx
from collections import defaultdict
from queue import Queue
from typing import (
    Any,
    Callable,
    Dict,
    Hashable,
    List,
    Optional,
    Set,
    Tuple,
    TypeVar,
    Union,
    cast,
)
from yal.grid import DIRECTIONS, DIRECTIONS_INCL_DIAGONALS, Grid
from yal.geo2d import Point

Tn = TypeVar("Tn")

"""
Create a graph:

>>> G=nx.Graph()
>>> G.add_edge("foo", "bar", weight=0.9)
>>> G.add_edges_from([("foo", "baz"), (1, 7)])
>>> G.add_node("single")
>>> G.add_node("a", {"color": "red"})
>>> H=nx.Graph({1: [3, 5], 2: [3,1]})
>>> DH=nx.DiGraph(H)

Mutate it:

>>> nx.remove_node(1)
>>> nx.remove_edge(1, 2)
>>> nx.contracted_nodes(G, a, b, self_loop=False, copy=False)  # treat a and b as same node

Inspect it: (the functions return a read-only view)

>>> list(G.nodes)
>>> list(G.edges)
>>> list(G["foo"])  # lists neighboring nodes of foo
>>> list(G.edges("foo")) # lists edges with foo
>>> list(DG.in_edges("foo")) # lists incoming edges of foo in a digraph

Transform it:

>>> H = nx.convert_node_labels_to_integers(G)  # relabels nodes to 0, 1, 2 (can speed up run time)

In general, a graph is stored as a dict-of-dict-of-dicts (node->node->attribute) and the structure
is "node-centric", which is why G["foo"] gets the neighboring nodes (not edges).

More fundamentals: https://networkx.org/documentation/stable/tutorial.html
Also good introduction: https://networkx.org/documentation/stable/reference/introduction.html
Algorithms: https://networkx.org/documentation/stable/reference/algorithms/index.html

Many of the algorithms return a generator of the output.
Below are some more common ones, but there are many, many more,
including minor alternatives to the ones listed below

Components / Connectivity:
    connected_components(G)
    strongly_connected_components(DG)
    biconnected_components(G)
    articulation_points(G)
    bridges(G)
    maximum_flow(G)
    minimum_cut(G)
    minimum_edge_cut(G)

Shortest paths / Traversals:
    shortest_path(G)  [Dijkstra or Bellman-Ford]
    all_shortest_paths(G)
    floyd_warshall(G)
    dijkstra_path(G)
    astar_path(G)
      (more advanced interfaces here: https://networkx.org/documentation/stable/reference/algorithms/shortest_paths.html#module-networkx.algorithms.shortest_paths.unweighted)
    dfs_preorder_nodes(G)
    dfs_edges(G)  # visits all nodes if no source specified
    bfs_edges(G)
    bfs_layers(G)  # Multiple starts possible

Cycles:
    find_cycle(G)
    eulerian_circuit(G)

Bipartite / Matching / Covering
    bipartite.maximum_matching(G)
    min_edge_cover(G)
    max_weight_matching(G)  # Generic matching, O(N^3)

DAG/Tree algorithms:
    topological_sort(G)  # if u->v exists, u will be before v in the sort
    lexicographical_topological_sort(G)
    dag_longest_path(G)
    lowest_common_ancestor(G)
    minimum_spanning_tree(G)

Hashing / Isomorphishm / Similarity:
    weisfeiler_lehman_graph_hash(G)  # isometric graphs will get same hash
    is_isomorphic(G1, G2)
    graph_edit_distance(G1, G2)
"""


def show_graph(
    graph: nx.Graph | nx.DiGraph,
    node_colors: Optional[Dict[Tn, str]] = None,
    edge_colors: Optional[Dict[Tuple[Tn, Tn], str]] = None,
    output_name: str = "graph",
    engine: Optional[str] = "neato",
):
    """
    Creates a visualisation of a graph as a file on disk

    Different layout engines: https://graphviz.org/docs/layouts/
    "neato" is a good default spring model one.
    "dot" for hierarchal graphs

    Requires graphviz to be installed
        brew install graphviz && pip install graphviz
    """
    import graphviz

    if not edge_colors:
        edge_colors = {}

    edges_shown = set()
    is_digraph = isinstance(graph, nx.DiGraph)

    dot = graphviz.Graph() if not is_digraph else graphviz.Digraph()
    # For more graphing options, see
    # https://pypi.org/project/graphviz/
    for a in graph:
        dot.node(str(a), color=(node_colors or {}).get(a, "black"))
        for v in graph[a]:
            if isinstance(v, tuple):
                b, w = cast(Tuple[Tn, int], v)
            else:
                b = cast(Tn, v)
                w = None

            if is_digraph:
                edge_color = edge_colors.get((a, b), "black")
            else:
                if (a, b) in edges_shown:
                    continue
                edges_shown.add((a, b))
                edges_shown.add((b, a))
                edge_color = edge_colors.get((a, b), edge_colors.get((b, a), "black"))

            dot.edge(str(a), str(b), label=str(w) if w else None, color=edge_color)

    dot.render(output_name, engine=engine, cleanup=True)


# nx-equivalent: nx.shortest_path_length(G, source=start)
#   or
# [node for layer in nx.bfs_layers(G, start) for node in layer]
def bfs(
    graph: nx.Graph | nx.DiGraph,
    start: Union[Tn, List[Tn]],
    func: Optional[Callable[[Tn, int], None]] = None,
) -> Dict[Any, int]:
    """Performs a BFS search in a graph and returns the distans to all nodes visited.
    Start can either be a list of nodes or a single node.
    If func is set, calls func(node, dist) when each node is visited.
    graph: {node: [neighbors]}
    """
    dist: Dict[Tn, int] = {}  # node -> distance
    q = Queue[Tn]()
    if isinstance(start, list):
        for p in start:
            q.put(p)
            dist[p] = 0
    else:
        q.put(start)
        dist[start] = 0
    while not q.empty():
        current = q.get()
        steps = dist[current]
        if func:
            func(current, steps)
        for neighbor in graph[current]:
            if neighbor not in dist:
                dist[neighbor] = steps + 1
                q.put(neighbor)
    return dist


# nx-equivalent: list(nx.dfs_preorder_nodes(G, start))
# or
# for node in nx.dfs_preorder_nodes(G, start):
#    func(node)
def dfs(
    graph: nx.Graph | nx.DiGraph,
    start: Union[Tn, List[Tn]],
    func: Optional[Callable[[Tn], None]] = None,
):
    """
    Performs a DFS search in a graph and returns a set of all nodes visited
    If func is set, calls func(node) when each node is visited.
    graph: {node: [neighbors]}
    """
    seen_in_order = list()
    seen = set()

    def go(current):
        nonlocal seen, graph, func
        if current not in seen:
            if func:
                func(current)
            seen_in_order.append(current)
            seen.add(current)
            for neighbor in graph[current]:
                go(neighbor)

    go(start)
    return seen_in_order


def search_all(graph: nx.Graph | nx.DiGraph, graph_search_func, func=None):
    """Calls the graph_search_func on an arbitrary node, repeats on a non-visited node,
    repeats until all nodes in graph covered.
    Returns an array of the output from the graph_search_func.
    If func is set, calls func(iteration, node) when each node is visited.
    graph: {node: [neighbors]}
    """
    result = []
    visited = set()
    iteration = 0
    for node in graph.nodes:
        if node not in visited:
            res = graph_search_func(
                graph, node, functools.partial(func, iteration) if func else None
            )
            result.append(res)
            visited = visited.union(res.keys() if isinstance(res, dict) else res)
            iteration += 1
    return result


def dfs_all(graph, func=None):
    return search_all(graph, dfs, func)


def bfs_all(graph, func=None):
    return search_all(graph, bfs, func)


def reverse_graph(graph: nx.DiGraph) -> nx.DiGraph:
    """Creates a new graph that is the edge-reverse of the given graph.
    Works both with or without distances."""

    return graph.reverse(True)


def symmetric_graph(graph: nx.DiGraph) -> nx.DiGraph:
    """Returns a new graph containing all edges of the original
    graph and corresponding reverse edges so to the graph so it becomes symmetric.
    Works both with or without distances."""

    return graph.to_undirected().to_directed()


# Use nx.shortest_path_length(graph, start) instead
def dijkstra(
    graph: nx.Graph | nx.DiGraph,
    start: Tn,
    func: Optional[Callable[[Tn, int], None]] = None,
):
    """Performs a shortest-path in a graph and returns the distance to all nodes visited.
    If func is set, calls func(node, dist) when each node is visited.
    graph: {node: [(neighbor, distance)]}
    """
    dist = {}
    q = []

    def add(node, d):
        nonlocal dist, q
        if node not in dist or d < dist[node]:
            dist[node] = d
            heapq.heappush(q, (d, node))

    add(start, 0)
    while len(q):
        (cur_dist, cur) = heapq.heappop(q)
        if cur_dist == dist[cur]:
            if func:
                func(cur, cur_dist)
            if cur in graph:
                for x, d in graph[cur]:
                    add(x, cur_dist + d)

    return dist


# Useful if graph can't be represented explicitly
def dynamic_dijkstra(
    start: Tn,
    neighbors: Callable[[Tn], List[Tuple[Tn, float]]],
    hash_func: Optional[Callable[[Tn], Any]] = None,
    approx_func: Optional[Callable[[Tn], float]] = None,
    done_func: Optional[Callable[[Tn], bool]] = None,
):
    """Performs a shortest-path in a graph and returns the distance to all nodes visited.
    neighbors is a function that takes a state and return a list of tuples containing
    neighboring nodes and the distance.
    If the state is not hashable, set hash_func to something that maps the state to a unique hashable value
    If approx_func is set, the search becomes an A* search. approx_func takes a state an returns
    a minimum cost required to reach the target end state
    If done_func is set, the search is aborted if it's evaluated to true and the returned value is instead
    the dist (or None if the end is never reached)
    """
    dist = {}
    q = []

    def add(node, d):
        nonlocal dist, q
        hashable_node = hash_func(node) if hash_func else node
        if hashable_node not in dist or d < dist[hashable_node]:
            dist[hashable_node] = d
            approx_cost = d + approx_func(node) if approx_func else d
            heapq.heappush(q, (approx_cost, d, node))

    add(start, 0)
    while len(q):
        (_, cur_dist, cur) = heapq.heappop(q)
        if done_func and done_func(cur):
            return cur_dist
        cur_hash = hash_func(cur) if hash_func else cur
        if cur_dist == dist[cur_hash]:
            for x, d in neighbors(cur):
                add(x, cur_dist + d)

    return None if done_func else dist


def longest_path(
    graph: nx.Graph | nx.DiGraph, start: Tn, goal: Tn
) -> Tuple[float, List[Tn]]:
    """
    Finds the longest path in a graph from start to goal without visiting the same
    node twice. Returns an empty path if no path from start to goal is found.
    """
    visited: Set[Tn] = set()
    path: List[Tn] = []
    best = 0
    longest_path: List[Tn] = []

    assert start != goal

    def go(cur, distance):
        nonlocal visited, path, best, longest_path
        if cur == goal:
            if distance > best:
                best = distance
                longest_path = list(path)
            return

        if cur in visited:
            return

        visited.add(cur)
        path.append(cur)
        for neighbor, datadict in graph[cur].items():
            go(neighbor, distance + datadict.get("weight", 1))
        visited.remove(cur)
        path.pop()

    go(start, 0)

    return (best, longest_path)


def compress_paths(graph: nx.Graph|nx.DiGraph, fixed_nodes: Optional[List] = None):
    """
    Removes all nodes in graph with two (bi-directional) edges by connecting
    them directly and setting the distance to the sum of the edge distances.

    Note that this may not work as expected on a pure directional graph.
    """

    fixed = set(fixed_nodes or [])

    all_nodes = list(graph.nodes)
    for node in all_nodes:
        if node in fixed or len(graph[node]) != 2:
            continue

        (a, b) = graph[node]
        if node in graph[a] and node in graph[b]:
            graph.add_edge(a, b, weight=graph[a][node]["weight"] + graph[node][b]["weight"])
            if graph.is_directed():
                graph.add_edge(b, a, weight=graph[b][node]["weight"] + graph[node][a]["weight"])
            graph.remove_node(node)


def grid_graph(
    grid: Union[Grid, List[str]],
    is_node: Callable[[Point, str], bool] | str | None = None,
    get_edge: Optional[Callable[[Point, str, Point, str], bool | int]] = None,
    graph=nx.Graph,
    uni_distance=True,
    num_directions=4,
):
    """Converts a grid (line of strings) into a graph given two functions.

    is_node is either a function determining if a position/character in the grid
    is a node, or a string of valid node characters. If not set, all grid elements will become nodes.

    get_edge is a funcion determining if there should be an edge between two node.
    Returns True (or distance) between the nodes.

    If uni_distance is true, no weights data will be added

    num_directions should either be 4 (not diagonals) or 8 (with diagonals)."""

    if num_directions == 4:
        directions = DIRECTIONS
    else:
        assert num_directions == 8
        directions = DIRECTIONS_INCL_DIAGONALS

    if is_node is None:
        is_node = lambda p, c: True
    elif isinstance(is_node, str):
        node_chars = is_node
        is_node = lambda p, c: c in node_chars

    if isinstance(grid, Grid):
        grid = grid.to_list()

    G = graph()
    ysize = len(grid)
    xsize = len(grid[0])

    for y in range(ysize):
        for x in range(xsize):
            p = Point(x, y)
            c = grid[y][x]
            if is_node(p, c):
                for d in directions:
                    np = p + d
                    if graph == nx.Graph and np < p:
                        continue
                    if np.x >= 0 and np.x < xsize and np.y >= 0 and np.y < ysize:
                        nc = grid[np.y][np.x]
                        if is_node(np, nc):
                            e = get_edge(p, c, np, nc) if get_edge else True
                            if e is not None and e is not False:
                                if uni_distance:
                                    G.add_edge(p, np)
                                else:
                                    G.add_edge(p, np, weight=e)

    return G


if __name__ == "__main__":
    g = nx.fast_gnp_random_graph(25, 0.05, 0, False)
