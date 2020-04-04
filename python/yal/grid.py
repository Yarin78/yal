from yal.geo2d import Point


NORTH = UP = Point(0, -1)
NORTH_EAST = UP_RIGHT = Point(1, -1)
EAST = RIGHT = Point(1, 0)
SOUTH_EAST = DOWN_RIGHT = Point(1,1)
SOUTH = DOWN = Point(0, 1)
SOUTH_WEST = DOWN_LEFT = Point(-1,1)
WEST = LEFT = Point(-1, 0)
NORTH_WEST = UP_LEFT = Point(-1, -1)

DIRECTIONS = [NORTH, EAST, SOUTH, WEST]
DIRECTIONS_INCL_DIAGONALS = [NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST]


DIRECTION_MAP = {
    'U': NORTH,
    'D': SOUTH,
    'R': EAST,
    'L': WEST,

    'N': NORTH,
    'S': SOUTH,
    'E': EAST,
    'W': WEST,

    'NW': NORTH_WEST,
    'SW': SOUTH_WEST,
    'NE': NORTH_WEST,
    'SE': SOUTH_EAST
}


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


def print_array(array):
    for a in array:
        print(a)


if __name__ == "__main__":
    print_array(gridify_sparse_map({Point(0,0): '#'}))
    print()
    print_array(gridify_sparse_map({(0,0): '#', (1,1): '#', (-1,1): '_'}))
