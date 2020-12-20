from yal.geo2d import Point

class Tile:
    """Representation of a 2D tile of ASCII characters. Immutable."""

    def __init__(self, data, id=0, rot_flip=0):
        self.data = data
        self.ysize = len(data)
        self.xsize = len(data[0])
        self.id = id
        self.rot_flip = rot_flip
        for row in data:
            assert len(row) == self.xsize

    def show(self):
        for y in range(self.ysize):
            print(self.data[y])

    def rotate_ccw(self):
        res = []
        for x in range(self.xsize):
            s = ""
            for y in range(self.ysize):
                s += self.data[y][self.xsize-1-x]
            res.append(s)
        return Tile(res, self.id, (self.rot_flip%4+1)%4 + (self.rot_flip//4*4))

    def rotate_cw(self):
        res = []
        for x in range(self.xsize):
            s = ""
            for y in range(self.ysize):
                s += self.data[self.ysize-1-y][x]
            res.append(s)
        return Tile(res, self.id, (self.rot_flip%4+3)%4 + (self.rot_flip//4*4))

    def all_rotations(self):
        cur = self
        res = []
        for i in range(4):
            res.append(cur)
            cur = cur.rotate_cw()
        return res

    def all_rotations_and_flips(self):
        return [*self.all_rotations(), *self.flip_vert().all_rotations()]

    def flip_vert(self):
        return Tile([self.data[self.ysize-1-y] for y in range(self.ysize)], self.id, (self.rot_flip+4) % 8)

    def flip_horz(self):
        return Tile([s[::-1] for s in self.data], self.id, (self.rot_flip+6) % 8)

    def points(self, matching_chars: str):
        """Gets a list of all coordinates as Point where the character is in matching_chars"""
        return [Point(x,y) for y in range(self.ysize) for x in range(self.xsize) if self.data[y][x] in matching_chars]



def tiles_match_left_right(tile_left, tile_right):
    """Returns true if the right edge of tile_left has the same characters as the left edge of tile_right."""
    assert tile_left.ysize == tile_right.ysize
    return all(tile_left.data[y][tile_left.xsize-1] == tile_right.data[y][0] for y in range(tile_left.ysize))

def tiles_match_up_down(tile_up, tile_down):
    """Returns true if the bottom edge of tile_up has the same characters as the top edge of tile_down."""
    assert tile_up.xsize == tile_down.xsize
    return all(tile_up.data[tile_up.ysize-1][x] == tile_down.data[0][x] for x in range(tile_up.xsize))
