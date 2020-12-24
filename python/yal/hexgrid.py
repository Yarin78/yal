from yal.geo2d import Point

# https://www.redblobgames.com/grids/hexagons/

HEX_DIRS_EW = {
    'e': Point(1, 0),
    'w': Point(-1, 0),
    'se': Point(0, 1),
    'sw': Point(-1, 1),
    'nw': Point(0, -1),
    'ne': Point(1, -1),
}
