from typing import List, Optional, Sequence, Tuple


EPSILON = 1e-9


def almost_equal(a, b):
    return abs(a-b) < EPSILON


class Point:

    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z

    def __add__(self, other):
        return Point(self.x + other.x, self.y+other.y, self.z+other.z)

    def __sub__(self, other):
        return Point(self.x - other.x, self.y-other.y, self.z-other.z)

    def __mul__(self, scalar):
        return Point(self.x * scalar, self.y * scalar, self.z * scalar)

    def __abs__(self):
        return Point(abs(self.x), abs(self.y), abs(self.z))

    def __eq__(self, other):
        return other and almost_equal(self.x, other.x) and almost_equal(self.y, other.y) and almost_equal(self.z, other.z)

    def __ne__(self, other):
        return not self.__eq__(other)

    def __lt__(self, other):
        if almost_equal(self.z, other.z):
            if almost_equal(self.y, other.y):
                if almost_equal(self.x, other.x):
                    return False
                return self.x < other.x
            return self.y < other.y
        return self.z < other.z

    def __hash__(self):
        return hash((self.x, self.y, self.z))

    def __str__(self):
        return '({0}, {1}, {2})'.format(self.x, self.y, self.z)

    def __repr__(self):
        return self.__str__()

    @staticmethod
    def range(min, max):
        """Returns an iterator of all Points with coordinates <= min and < max."""
        for x in range(min.x, max.x):
            for y in range(min.y, max.y):
                for z in range(min.z, max.z):
                    yield Point(x, y, z)

    def roll(self): return Point(self.x, self.z, -self.y)
    def turn(self): return Point(-self.y, self.x, self.z)

    def rotations(self) -> List["Point"]:
        """Generates all 24 90-degree rotations of this point"""
        rots = []
        cur = self
        for _ in range(2):
            for _ in range(3):
                rots.append(cur)
                cur = cur.roll()
                for _ in range(3):
                    rots.append(cur)
                    cur = cur.turn()
            cur = cur.roll().turn().roll()
        return rots


class Block:
    """Represents a 3d block by its two opposite corners.
    p1 coordinates are inclusive, p2 coordinates exclusive."""
    p1: Point
    p2: Point

    def __init__(self, p1: Point, p2: Point):
        assert p1.x < p2.x and p1.y < p2.y and p1.z < p2.z
        self.p1 = p1
        self.p2 = p2

    def volume(self):
        return (self.p2.x-self.p1.x) * (self.p2.y-self.p1.y) * (self.p2.z-self.p1.z)

    def intersect(self, other: "Block") -> Optional["Block"]:
        x1, x2 = (max(self.p1.x, other.p1.x), min(self.p2.x, other.p2.x))
        y1, y2 = (max(self.p1.y, other.p1.y), min(self.p2.y, other.p2.y))
        z1, z2 = (max(self.p1.z, other.p1.z), min(self.p2.z, other.p2.z))

        return Block(Point(x1,y1,z1), Point(x2,y2,z2)) if x1 < x2 and y1 < y2 and z1 < z2 else None

    def subtract(self, other: "Block") -> List["Block"]:
        x1, x2 = (max(self.p1.x, other.p1.x), min(self.p2.x, other.p2.x))
        y1, y2 = (max(self.p1.y, other.p1.y), min(self.p2.y, other.p2.y))
        z1, z2 = (max(self.p1.z, other.p1.z), min(self.p2.z, other.p2.z))

        if x2 <= x1 or y2 <= y1 or z2 <= z1:
            # The other block does not intersect, so no subtraction happened
            return [self]

        splits = [
            (self.p1, Point(x1, self.p2.y, self.p2.z)),
            (Point(x2, self.p1.y, self.p1.z), self.p2),
            (Point(x1, self.p1.y, self.p1.z), Point(x2, y1, self.p2.z)),
            (Point(x1, y2, self.p1.z), Point(x2, self.p2.y, self.p2.z)),
            (Point(x1, y1, self.p1.z), Point(x2, y2, z1)),
            (Point(x1, y1, z2), Point(x2, y2, self.p2.z)),
        ]

        return [Block(b1, b2) for (b1, b2) in splits if b1.x<b2.x and b1.y<b2.y and b1.z<b2.z]

ORTHOGONAL_DIRECTIONS = [
    Point(1, 0, 0),
    Point(-1, 0, 0),
    Point(0, 1, 0),
    Point(0, -1, 0),
    Point(0, 0, 1),
    Point(0, 0, -1),
]

class Shape:
    '''
    Represents a set of discrete points in 3D space
    '''

    points: List[Point]

    def __init__(self, points: Sequence[Point]):
        self.points = sorted(points)

    def __add__(self, offset: Point):
        return Shape([p+offset for p in self.points])

    def __sub__(self, offset: Point):
        return Shape([p-offset for p in self.points])

    def __mul__(self, scalar):
        return Shape([p * scalar for p in self.points])

    def __eq__(self, other: "Shape"):
        return other and len(self.points) == len(other.points) and all(p1 == p2 for p1, p2 in zip(self.points, other.points))

    def __ne__(self, other: "Shape"):
        return not self.__eq__(other)

    def __hash__(self):
        return hash(tuple(self.points))

    def __str__(self):
        return "[" + ",\n ".join(str(p) for p in self.points) + "]"

    @staticmethod
    def from_inclusive_coords(p1: Point, p2: Point) -> "Shape":
        return Shape([Point(x, y, z) for x in range(min(p1.x, p2.x), max(p1.x, p2.x) + 1) for y in range(min(p1.y, p2.y), max(p1.y, p2.y) + 1) for z in range(min(p1.z, p2.z), max(p1.z, p2.z) + 1)])


    def bounding_range(self) -> Tuple[Point, Point]:
        '''
        Returns two coordinates represents the surrounding range of the shape
        First coordinate is inclusive of shapes min coordinates
        Second coordinate is exclusive of shapes max coordinate
        '''
        minx = min(p.x for p in self.points)
        maxx = max(p.x for p in self.points) + 1
        miny = min(p.y for p in self.points)
        maxy = max(p.y for p in self.points) + 1
        minz = min(p.z for p in self.points)
        maxz = max(p.z for p in self.points) + 1
        return (Point(minx, miny, minz), Point(maxx, maxy, maxz))


    def canonical(self) -> "Shape":
        '''
        Gets a canonical version of this shape by translating the coordinates
        so that min x, y and z are all 0.
        '''

        minx = min(p.x for p in self.points)
        miny = min(p.y for p in self.points)
        minz = min(p.z for p in self.points)
        offset = Point(minx, miny, minz)
        return Shape([p - offset for p in self.points])

    def roll(self):
        return Shape([p.roll() for p in self.points])

    def turn(self):
        return Shape([p.turn() for p in self.points])

    def rotations(self) -> List["Shape"]:
        """Generates all 24 90-degree rotations of this shape"""
        rots = []
        cur = self
        for _ in range(2):
            for _ in range(3):
                rots.append(cur)
                cur = cur.roll()
                for _ in range(3):
                    rots.append(cur)
                    cur = cur.turn()
            cur = cur.roll().turn().roll()
        return rots

if __name__ == "__main__":
    shape = Shape([Point(1,2,1), Point(1,2,2), Point(1,3,2), Point(2,3,2)])

    rotations = set(r.canonical() for r in shape.rotations())
    for r in rotations:
        print(r)
    print(len(rotations))
