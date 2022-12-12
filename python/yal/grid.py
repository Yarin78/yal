import sys
from typing import Callable, Iterable, List, Optional, Tuple, Union
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

class Grid:
    cells: List[List[str]]
    xsize: int
    ysize: int
    xofs: int  # The x coordinate of the first element in a cell row
    yofs: int  # The y coordinate of the first element in a cells

    @staticmethod
    def empty(xsize: int, ysize: int, ch: str='.', border='', xofs=0, yofs=0) -> "Grid":
        '''Creates an empty Grid of a given size'''
        return Grid([ch * xsize for _ in range(ysize)], border, xofs, yofs)

    @staticmethod
    def read(input: Optional[Iterable[str]] = None, border='', xofs=0, yofs=0) -> "Grid":
        '''Reads a grid from stdin (or a line of strings) and returns a new Grid'''
        return Grid(input or sys.stdin.readlines(), border, xofs, yofs)

    @staticmethod
    def from_sparse_map(map, output_func=None):
        '''
        Converts a map of dict(Point) or dict((x,y)) into a Grid.
        '''
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
        return Grid(res, '', minx, miny)

    def __init__(self, grid: Iterable[str], border: str='', xofs=0, yofs=0):
        '''
        Creates a new grid from input, surrounding it with the border characters.
        The first character is the outermost border character.
        By default, the 0,0 coordinate will correspond to the first character
        in the input grid, even when a border is added.
        To adjust this, set xofs/yofs. They correspond what coordinate the
        first character in the input grid should be.
        '''

        mid_cells = [[c for c in border + row.rstrip('\n') + border[::-1]] for row in grid]

        self.ysize = len(mid_cells) + len(border) * 2
        self.xsize = len(mid_cells[0])
        self.xofs = -len(border) + xofs
        self.yofs = -len(border) + yofs

        internal_xsize = self.xsize - len(border) * 2

        self.cells = []
        for y in range(len(border)):
            s = ""
            for x in range(len(border)):
                s += border[min(x,y)]
            s += border[y] * internal_xsize
            for x in range(len(border)-1, -1, -1):
                s += border[min(x,y)]
            self.cells.append([c for c in s])

        self.cells.extend(mid_cells)

        for y in range(len(border)-1, -1, -1):
            s = ""
            for x in range(len(border)):
                s += border[min(x,y)]
            s += border[y] * internal_xsize
            for x in range(len(border)-1, -1, -1):
                s += border[min(x,y)]
            self.cells.append([c for c in s])

        assert len(self.cells) == self.ysize
        assert len(self.cells[0]) == self.xsize
        for y in range(self.ysize):
            assert self.xsize == len(self.cells[y]), f"Row 0 and row {y} in grid have different length ({self.xsize-2*len(border)} != {len(self.cells[y])-2*len(border)})"

    def __getitem__(self, coord: Union[Tuple[int, int], Point]):
        '''Gets an element in the grid. Coordinates are y,x'''
        if isinstance(coord, Point):
            coord = (coord.y, coord.x)
        x = coord[1]-self.xofs
        y = coord[0]-self.yofs
        # assert y >= 0 and y < len(self.cells) and x >= 0 and x < len(self.cells[0])
        return self.cells[y][x]

    def __setitem__(self, coord: Union[Tuple[int, int], Point], ch: str):
        '''Sets an element in the grid. Coordinates are y,x'''
        if isinstance(coord, Point):
            coord = (coord.y, coord.x)
        x = coord[1]-self.xofs
        y = coord[0]-self.yofs
        # assert y >= 0 and y < self.ysize and x >= 0 and x < self.xsize
        self.cells[y][x] = ch

    def is_within(self, point: Point):
        x = point.x-self.xofs
        y = point.y-self.yofs
        return y >= 0 and y < self.ysize and x >= 0 and x < self.xsize

    def set(self, p: Point, c: str):
        self[p.y, p.x] = c

    def get(self, p: Point):
        return self[p.y, p.x]

    def get_safe(self, p: Point):
        if self.is_within(p):
            return self[p.y, p.x]
        return None

    def find(self, ch: str) -> List[Point]:
        return [
            Point(x+self.xofs,y+self.yofs) for y in range(self.ysize) for x in range(self.xsize)
            if self.cells[y][x] in ch
        ]

    def find_and_replace(self, ch: str, repl_ch: str) -> List[Point]:
        res = self.find(ch)
        for p in res:
            self.cells[p.y][p.x] = repl_ch
        return res

    def find_first(self, ch: str) -> Optional[Point]:
        find_all = self.find(ch)
        return find_all[0] if find_all else None

    def to_ints(self) -> List[List[int]]:
        return [[int(cell) for cell in row] for row in self.cells]

    def all_points(self) -> List[Point]:
        return [Point(x+self.xofs,y+self.yofs) for y in range(self.ysize) for x in range(self.xsize)]

    def neighbors(self, p: Point, num_dir=4) -> List[Point]:
        '''
        Returns a list of all neighboring points to another point.
        num_dir specifies what counts as a neighbor:
        - 4: up/down/left/right
        - 5: same as 4 but including point itself
        - 8: as 4 plus diagonals
        - 9: as 8 but including point itself
        '''
        assert num_dir in (4,5,8,9)
        neighbors = []
        if num_dir in (5,9):
            if self.is_within(p):
                neighbors.append(p)
            num_dir -= 1
        dir_list = DIRECTIONS_INCL_DIAGONALS if num_dir == 8 else DIRECTIONS
        for d in dir_list:
            q = p + d
            if self.is_within(q):
                neighbors.append(q)
        return neighbors

    def clone(self):
        '''
        Returns a new copy of this grid.
        '''
        return self.transform(lambda grid, x, y: grid[y,x])

    def transform(self, func: Callable[["Grid", int, int], str], extend=0) -> "Grid":
        '''
        Performs a transformation of the grid by applying the given function
        on all cells simultaneously and returning a new Grid.
        If extend is set, the grid will be expanded this much in all direction:
        the transform function will be called for the new cells, but it's up
        to the function to handle how to deal with the newly added cells.
        '''
        data = [''.join(func(self, x+self.xofs, y+self.yofs) for x in range(-extend, self.xsize+extend)) for y in range(-extend, self.ysize+extend)]
        return Grid(data, '', self.xofs+extend, self.yofs+extend)

    def to_list(self) -> List[str]:
        return [''.join(row) for row in self.cells]

    def show(self):
        for row in self.cells:
            print(''.join(row))

if __name__ == "__main__":
    # print_array(gridify_sparse_map({Point(0,0): '#'}))
    # print()
    # print_array(gridify_sparse_map({(0,0): '#', (1,1): '#', (-1,1): '_'}))

    grid = Grid([
        "##.",
        "#.#",
        "###"
    ], border="."*3)

    def game_of_life(grid: Grid, x: int, y: int) -> str:
        cnt = 0
        for p in grid.neighbors(Point(x,y), 8):
            cnt += grid.get(p) == '#'
        if grid[y,x] == '.':
            return '#' if cnt == 3 else '.'
        return '#' if cnt in (2,3) else '.'

    grid.show()
    for i in range(10):
        grid = grid.transform(game_of_life)
        print()
        grid.show()
