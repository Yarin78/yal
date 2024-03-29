from typing import Dict, Generic, List, Optional, Set, Tuple, TypeVar

T = TypeVar("T")

class Tree(Generic[T]):

    parents: Dict[T, Optional[T]]
    children: Dict[T, List[T]]
    depth: Dict[T, int]
    root: T
    all_nodes: Set[T]


    def __init__(self, parent_child: List[Tuple[T, T]]):
        self.parents = {}   # parents[child] = parent
        self.children = {}  # children[parent] = [child1, child2, ...]
        self.depth = {}     # depth[root] = 0, depth[children[root][0]] = 1, ...
        root = None
        self.all_nodes = set()

        for r in parent_child:
            parent = r[0]
            child = r[1]
            self.all_nodes.add(parent)
            self.all_nodes.add(child)
            self.parents[child] = parent
            if parent not in self.children:
                self.children[parent] = [child]
            else:
                self.children[parent].append(child)

        for node in self.all_nodes:
            if node not in self.parents:
                assert root is None
                root = node
                self.parents[node] = None
            if node not in self.children:
                self.children[node] = []

        assert root
        self.root = root

        def _set_depth(node, d):
            self.depth[node] = d
            for c in self.children[node]:
                _set_depth(c, d+1)

        _set_depth(self.root, 0)


    def all_ancestors(self, node: T) -> List[T]:
        ancestors: List[T] = []
        cur = self.parents.get(node)
        while cur is not None:
            ancestors.append(cur)
            cur = self.parents.get(cur)
        return ancestors
