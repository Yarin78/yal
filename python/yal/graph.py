from queue import Queue
from collections import defaultdict
import heapq
import functools
from typing import Any, Callable, Dict, List, Optional, Set, Tuple, TypeVar, Union, cast
from yal.geo2d import Point
from yal.grid import DIRECTIONS, DIRECTIONS_INCL_DIAGONALS, Grid
from numbers import Number

Tn = TypeVar("Tn")


def show_graph(
    graph: Dict[Tn, List[Tn | Tuple[Tn, int]]],
    node_colors: Optional[Dict[Tn, str]] = None,
    edge_colors: Optional[Dict[Tuple[Tn, Tn], str]] = None,
    output_name: str = "graph",
    digraph: bool = False
):
    """
    Creates a visualisation of a graph as a file on disk

    Requires graphviz to be installed
        brew install graphviz && pip install graphviz
    """
    import graphviz

    if not edge_colors:
        edge_colors = {}

    edges_shown = set()

    dot = graphviz.Graph() if not digraph else graphviz.Digraph()
    # For more graphing options, see
    # https://pypi.org/project/graphviz/
    for a in graph.keys():
        dot.node(str(a), color=(node_colors or {}).get(a, "black"))
        for v in graph[a]:
            if isinstance(v, tuple):
                b, w = cast(Tuple[Tn, int], v)
            else:
                b = cast(Tn, v)
                w = None

            if digraph:
                edge_color = edge_colors.get((a, b), "black")
            else:
                if (a, b) in edges_shown:
                    continue
                edges_shown.add((a, b))
                edges_shown.add((b, a))
                edge_color = edge_colors.get((a, b), edge_colors.get((b, a), "black"))

            dot.edge(str(a), str(b), label=str(w) if w else None, color=edge_color)

    dot.render(output_name, cleanup=True)


def bfs(
    graph: Dict[Tn, List[Tn]],
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
        for neighbor in graph.get(current, []):
            if neighbor not in dist:
                dist[neighbor] = steps + 1
                q.put(neighbor)
    return dist


def dfs(graph, start, func=None):
    """Performs a DFS search in a graph and returns a set of all nodes visited
    If func is set, calls func(node) when each node is visited.
    graph: {node: [neighbors]}
    """
    seen = set()

    def go(current):
        nonlocal seen, graph, func
        if current not in seen:
            if func:
                func(current)
            seen.add(current)
            for neighbor in graph.get(current, []):
                go(neighbor)

    go(start)
    return seen


def search_all(graph, graph_search_func, func=None):
    """Calls the graph_search_func on an arbitrary node, repeats on a non-visited node,
    repeats until all nodes in graph covered.
    Returns an array of the output from the graph_search_func.
    If func is set, calls func(iteration, node) when each node is visited.
    graph: {node: [neighbors]}
    """
    result = []
    visited = set()
    iteration = 0
    for node in graph.keys():
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


def _add_reverse_edges(new_graph, graph):
    for node, neighbors in graph.items():
        for neighbor in neighbors:
            if isinstance(neighbor, tuple):
                if neighbor[0] not in new_graph:
                    new_graph[neighbor[0]] = []
                new_graph[neighbor[0]].append([node] + list(neighbor[1:]))
            else:
                if neighbor not in new_graph:
                    new_graph[neighbor] = []
                new_graph[neighbor].append(node)


def reverse_graph(graph):
    """Creates a new graph that is the edge-reverse of the given graph.
    Works both with or without distances."""

    new_graph = {}
    _add_reverse_edges(new_graph, graph)
    return new_graph


def symmetric_graph(graph):
    """Adds reverse edges (in-place) to the graph so it becomes symmetric.
    Works both with or without distances."""

    new_graph = {node: list(neighbors) for node, neighbors in graph.items()}
    _add_reverse_edges(new_graph, graph)
    return new_graph


def dijkstra(graph, start, func=None):
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


def dijkstra2(start, neighbors, hash_func=None, approx_func=None, done_func=None):
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


def topological_sort(graph):
    """Performs a topological sort on a graph. Each node in the graph contains
    the dependencies that will be included before the node in the output.
    graph: {node: [neighbors]}
    """

    q = []  # nodes that can be processed; min-heap so they get in lowest-order
    degree = {}  # node -> outdegree
    reverse = reverse_graph(graph)

    seen = set()
    for node, neighbors in graph.items():
        degree[node] = len(neighbors)
        seen.add(node)
        if degree[node] == 0:
            heapq.heappush(q, node)
        for x in neighbors:
            seen.add(x)

    # In case some nodes (with no dependencies) where left out in the input graph
    for node in seen:
        if node not in degree:
            degree[node] = 0
            heapq.heappush(q, node)

    result = []
    while len(result) < len(seen):
        if not len(q):
            raise Exception("Circular graph")
        current = heapq.heappop(q)
        result.append(current)
        if current in reverse:
            for x in reverse[current]:
                degree[x] -= 1
                assert degree[x] >= 0
                if degree[x] == 0:
                    heapq.heappush(q, x)

    return result


def max_flow(graph: Dict[Tn, List[Tuple[Tn, int]]], source: Tn, sink: Tn):
    """
    Determines the maximum flow between source and sink in a directed graph
    graph: {node: [(neighbor, capacity)]}
    """

    # Create a new graph with all edges since we need backedges
    edges: Dict[Tn, List[Tn]] = defaultdict(list)
    capacity: Dict[Tuple[Tn, Tn], int] = defaultdict(int)  # (v1,v2) -> number
    flow: Dict[Tuple[Tn, Tn], int] = defaultdict(int)  # (v1,v2) -> number

    max_edge_capacity = 0
    for node, neighbors in graph.items():
        for v, cap in neighbors:
            if node != sink:
                edges[node].append(v)
            if node != source:
                edges[v].append(node)
            capacity[(node, v)] += cap
            max_edge_capacity = max(max_edge_capacity, cap)

    visited: Set[Tn] = set()

    def go(cur: Tn, current_flow: int) -> int:
        nonlocal visited, flow, capacity, sink
        if cur == sink:
            return current_flow

        if cur in visited or current_flow == 0:
            return 0
        # print(f"at {cur}, cur flow {current_flow}")

        visited.add(cur)
        for v in edges[cur]:
            f = go(
                v,
                min(current_flow, capacity[(cur, v)] - flow[(cur, v)] + flow[(v, cur)]),
            )
            if f > 0:
                flow[(cur, v)] += f
                # print(f"flow {cur}->{v} += {f}")
                return f
        return 0

    total_flow = 0
    added_flow = go(source, max_edge_capacity)
    while added_flow:
        # print(f"Added flow {added_flow}")
        total_flow += added_flow
        visited = set()
        added_flow = go(source, max_edge_capacity)

    return total_flow


def longest_path(
    graph: Dict[Tn, List[Tuple[Tn, int]]], start: Tn, goal: Tn
) -> Tuple[int, List[Tn]]:
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
        for neighbor, d in graph[cur]:
            go(neighbor, distance + d)
        visited.remove(cur)
        path.pop()

    go(start, 0)

    return (best, longest_path)


def compress_paths(
    graph: Dict[Tn, List[Tuple[Tn, int]]], fixed_nodes: Optional[List[Tn]] = None
):
    """
    Removes all nodes in graph with two (bi-directional) edges by connecting
    them directly and setting the distance to the sum of the ege distances.

    Note that this may not work as expected on a pure directional graph.
    """

    def _find(edges: List[Tuple[Tn, int]], search: Tn):
        for i, (v, _) in enumerate(edges):
            if v == search:
                return i
        return -1

    fixed = set(fixed_nodes or [])

    all_nodes = list(graph.keys())
    for node in all_nodes:
        if node in fixed or len(graph[node]) != 2:
            continue

        a, wa = graph[node][0]
        b, wb = graph[node][1]
        ixa = _find(graph[a], node)
        ixb = _find(graph[b], node)

        if ixa >= 0 and ixb >= 0:
            assert graph[a][ixa][1] == wa
            assert graph[b][ixb][1] == wb
            graph[a][ixa] = b, wa + wb
            graph[b][ixb] = a, wa + wb
            del graph[node]


def grid_graph(
    grid: Union[Grid, List[str]],
    is_node: Callable[[Point, str], bool] | str | None = None,
    get_edge: Optional[Callable[[Point, str, Point, str], bool | int]] = None,
    uni_distance=True,
    num_directions=4,
):
    """Converts a grid (line of strings) into a graph given two functions.

    is_node is either a function determining if a position/character in the grid
    is a node, or a string of valid node characters. If not set, all grid elements will become nodes.

    get_edge is a funcion determining if there should be an edge between two node.
    Returns True (or distance) between the nodes.

    If uni_distance is true, no distances will be added, only the edge.

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

    graph = {}
    ysize = len(grid)
    xsize = len(grid[0])

    for y in range(ysize):
        for x in range(xsize):
            p = Point(x, y)
            c = grid[y][x]
            if is_node(p, c):
                neighbors = []
                for d in directions:
                    np = p + d
                    if np.x >= 0 and np.x < xsize and np.y >= 0 and np.y < ysize:
                        nc = grid[np.y][np.x]
                        if is_node(np, nc):
                            e = get_edge(p, c, np, nc) if get_edge else True
                            if e is not None and e is not False:
                                if uni_distance:
                                    neighbors.append(np)
                                else:
                                    neighbors.append((np, e))
                graph[p] = neighbors

    return graph


if __name__ == "__main__":
    g = {
        0: [(1, 3), (2, 8)],
        1: [(2, 4), (3, 6)],
        2: [(4, 7)],
        3: [(5, 5)],
        4: [(1, 5), (3, 2), (5, 8)],
        6: [(7, 3)],
    }

    h = {0: [1, 2, 8], 1: [2, 3], 2: [4], 3: [5], 4: [1, 3, 5], 6: [7]}

    # h = symmetric_graph(h)
    # a = dfs_all(h)

    # h = symmetric_graph(h)
    # print(bfs_all(h, lambda i, x, steps: print(i,x,steps)))

    topg = {
        0: [5],
        1: [5, 2, 2],
        2: [5],
        3: [5],
        4: [],
        5: [4],
    }

    # print(topological_sort(topg))

    grid = ["......#...", "..##..###.", "...#...#..", "#...#....."]

    # g = grid_graph(grid, is_node=lambda p, c: c == '#', get_edge=lambda p1, c1, p2, c2: 1, uni_distance=False)
    # for n, neighbors in g.items():
    #     print(n, neighbors)

    graph = {
        0: [(1, 5), (2, 10), (4, 4)],
        1: [(3, 1), (6, 3)],
        2: [(3, 7), (4, 3), (5, 7)],
        3: [(6, 5)],
        4: [(5, 6)],
        5: [(6, 4)],
    }

    print("max flow", max_flow(graph, 0, 6))
