from typing import List, Tuple

# NOT DONE, use https://pypi.org/project/intervaltree/ instead

class Intervals:
    '''Represents a set of non-overlapping intervals in a discrete space
    Merging intervals [0,3) and [3,9) yields [0,9]
    '''

    # Start point is inclusive, end point exclusive
    ivals: List[Tuple[int, int]]

    def __init__(self):
        self.ivals = []

    def add(self, ival: Tuple[int, int]):
        assert ival[0] < ival[1]

        (start, end) = ival

        if not self.ivals:
            self.ivals.append(ival)
            return

        # ivals before ileft are completely to the left of ival
        ileft = 0
        while self.ivals[ileft][1] < start:
            ileft += 1
        # ivals after iright
        iright = len(self.ivals)
        while self.ivals[iright][0] > stop:
            iright -= 1

        # The first ileft ivals are completely to the left of ival
        for i in range(self.ivals):
            if ival[0]