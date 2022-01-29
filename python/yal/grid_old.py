import sys
from typing import Iterable, List, Optional, Tuple
from yal.geo2d import Point
from yal.grid import DIRECTIONS, DIRECTIONS_INCL_DIAGONALS

def gridify_sparse_map(map, output_func=None):
    '''Converts a map of dict(Point) or dict((x,y)) into an array of strings. Determines min and max coordinates used.'''
    if isinstance(next(iter(map.keys())), Point):
        get_x = lambda p: p.x
        get_y = lambda p: p.y
        get_p = lambda x, y: Point(x,y)
    else:
        get_x = lambda p: p[0]
        get_y = lambda p: p[1]
        get_p = lambda x, y: (x,y)

    minx = min(get_x(p) for p in map.keys())
    miny = min(get_y(p) for p in map.keys())
    maxx = max(get_x(p) for p in map.keys())
    maxy = max(get_y(p) for p in map.keys())

    res = []
    for y in range(miny, maxy+1):
        s = ''
        for x in range(minx, maxx+1):
            p = get_p(x,y)
            c = map[p] if p in map else None
            if output_func:
                s += output_func(c)
            else:
                s += str(c) if c else '.'
        res.append(s)
    return res


def read_grid(input: Optional[Iterable[str]] = None) -> Tuple[List[str], int, int]:
    '''Reads a grid from stdin (or a line of strings) and returns the tuple (grid, xsize, ysize)'''
    if input is None:
        input = sys.stdin.readlines()
    grid = [line.strip() for line in input]
    ysize = len(grid)
    xsize = len(grid[0])
    for y in range(ysize):
        assert xsize == len(grid[y]), f"Row 0 and row {y} in grid have different length ({xsize} != {len(grid[y])})"
    return (grid, xsize, ysize)

def grid_split(grid: List[str]) -> List[List[str]]:
    '''Splits a grid stored as an array of strings to a 2D array'''
    return [list(row) for row in grid]

def within_grid(grid: List[str], point: Point):
    return point.x >= 0 and point.y >= 0 and point.x < len(grid[0]) and point.y < len(grid)

def grid_get(grid: List[str], p: Point):
    return grid[p.y][p.x]

def grid_get_safe(grid: List[str], p: Point):
    if within_grid(grid, p):
        return grid[p.y][p.x]
    return None

def grid_points(grid: List[str]) -> List[Point]:
    points = []
    for y in range(len(grid)):
        for x in range(len(grid[0])):
            points.append(Point(x,y))
    return points

def grid_neighbors(grid: List[str], p: Point, num_dir=4) -> List[Point]:
    assert num_dir == 4 or num_dir == 8
    dir_list = DIRECTIONS_INCL_DIAGONALS if num_dir == 8 else DIRECTIONS
    neighbors = []
    for d in dir_list:
        q = p + d
        if within_grid(grid, q):
            neighbors.append(q)
    return neighbors

def print_array(array):
    for a in array:
        print(a)
