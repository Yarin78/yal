from typing import List


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
                    return 0
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
