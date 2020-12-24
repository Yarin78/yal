EPSILON = 1e-9


def almost_equal(a, b):
    return abs(a-b) < EPSILON


class Point:

    def __init__(self, x, y, z, h):
        self.x = x
        self.y = y
        self.z = z
        self.h = h

    def __add__(self, other):
        return Point(self.x + other.x, self.y+other.y, self.z+other.z, self.h+other.h)


    def __eq__(self, other):
        return other and almost_equal(self.x, other.x) and almost_equal(self.y, other.y) and almost_equal(self.z, other.z) and almost_equal(self.h, other.h)

    def __ne__(self, other):
        return not self.__eq__(other)

    def __lt__(self, other):
        if almost_equal(self.z, other.z):
            if almost_equal(self.y, other.y):
                if almost_equal(self.x, other.x):
                    if almost_equal(self.h, other.h):
                        return 0
                    return self.h < other.h
                return self.x < other.x
            return self.y < other.y
        return self.z < other.z

    def __hash__(self):
        return hash((self.x, self.y, self.z, self.h))

    def __str__(self):
        return '({0}, {1}, {2}, {3})'.format(self.x, self.y, self.z, self.h)

    def __repr__(self):
        return self.__str__()

    @staticmethod
    def range(min, max):
        """Returns an iterator of all Points with coordinates <= min and < max."""
        for x in range(min.x, max.x):
            for y in range(min.y, max.y):
                for z in range(min.z, max.z):
                    for h in range(min.h, max.h):
                        yield Point(x, y, z, h)
