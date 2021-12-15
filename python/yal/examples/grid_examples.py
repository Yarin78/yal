import sys
from collections import defaultdict

from yal.util import *
from yal.geo2d import *
from yal.grid import *
from yal.graph import *

def grid_connected_components(valid_chars='.#'):
    (grid, xsize, ysize) = read_grid()

    # This code snippet finds all connected components in a grid,
    # assigns each component a letter, and outputs the marking
    # of each component in the grid and the size of each component

    def _is_node(p, c): return c in valid_chars
    def _is_edge(p1, c1, p2, c2): return c1==c2 and c1 in valid_chars
    g = grid_graph(grid, _is_node, _is_edge)
    m = init_matrix(ysize, xsize, '.')

    def _assign_component(iteration, p, steps):
        m[p.y][p.x]=chr(65+iteration)

    res = bfs_all(g, _assign_component)
    for y in range(ysize):
        print("".join(map(str, m[y])))

    print("Number of components", len(res))
    for comp_ix, comp in enumerate(res):
        print(f"  {chr(65+comp_ix)}: {len(comp)}")

def grid_can_see():
    (grid, xsize, ysize) = read_grid()

    # For each open position in the grid, print longest distance that can be seen in each direction

    for y in range(ysize):
        for x in range(xsize):
            if grid[y][x] == '.':
                s = f"{x},{y}  "
                for dir_name in 'UDRL':
                    delta = DIRECTION_MAP[dir_name]
                    cur = Point(x,y) + delta
                    cnt = 0
                    while within_grid(grid, cur) and grid[cur.y][cur.x] == '.':
                        cur += delta
                        cnt += 1
                    s += f"{dir_name}: {cnt}  "
                print(s)

def grid_longest_distance():
    (grid, xsize, ysize) = read_grid()

    # For each open connected component, print longest distance between two nodes in that component
    def _is_node(p, c): return c=='.'
    def _is_edge(p1, c1, p2, c2): return c1=='.' and c2=='.'
    g = grid_graph(grid, _is_node, _is_edge)
    res = bfs_all(g)
    for component in res:
        print(max(max(bfs(g, start).values()) for start in component))



if __name__ == "__main__":
    #grid_connected_components(".")
    #grid_can_see())
    grid_longest_distance()

