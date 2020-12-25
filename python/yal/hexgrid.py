from yal.geo2d import Point

# https://www.redblobgames.com/grids/hexagons/


# Cube coordinates, using x,z
HEX_DIRS_EW = {
    'e': Point(1, 0),
    'w': Point(-1, 0),
    'se': Point(0, 1),
    'sw': Point(-1, 1),
    'nw': Point(0, -1),
    'ne': Point(1, -1),
}

# Cube coordinate; as above rotate 90 degrees ccw, using z, x (Not tested!)
HEX_DIRS_NS = {
    'n': Point(0, -1),
    's': Point(0, 1),
    'ne': Point(-1, 0),
    'nw': Point(1, -1),
    'sw': Point(1, 0),
    'se': Point(-1, 1),
}
