from typing import List, Tuple
from intervaltree import IntervalTree, Interval

# NOT DONE, use https://pypi.org/project/intervaltree/ instead
# Example usage below, more examples on page. Looks very powerful!
# Can merge intervals and merge data, but not seemingly not sum data
# in overlapping intervals; use Fenwick tree for that

if __name__ == "__main__":
    t = IntervalTree()
    t.addi(3,8)
    t.addi(15,18)
    print(t)  # IntervalTree([Interval(3, 8), Interval(15, 18)])
    t.addi(8, 17)
    print(t)  # IntervalTree([Interval(3, 8), Interval(8, 17), Interval(15, 18)])

    print(t.envelop(6, 17))  # {Interval(8, 17)}

    t.merge_overlaps() 
    print(t) # IntervalTree([Interval(3, 8), Interval(8, 18)])
    t.chop(6, 12)
    print(t)  # IntervalTree([Interval(3, 6), Interval(12, 18)])

    t = IntervalTree()
    t.add(Interval(2, 5, "foo"))
    t.add(Interval(4, 9, "bar"))
    print(t)  # IntervalTree([Interval(2, 5, 'foo'), Interval(4, 9, 'bar')])
    t.merge_overlaps(data_reducer=lambda cur_data, new_data: cur_data + new_data)
    print(t) # IntervalTree([Interval(2, 9, 'foobar')])

    t = IntervalTree()
    t.add(Interval(3, 10, 7))
    t.add(Interval(8, 15, 4))
    t.add(Interval(14, 20, 2))
    print(t)  # IntervalTree([Interval(3, 10, 7), Interval(8, 15, 4), Interval(14, 20, 2)])
    t.merge_overlaps()

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
        #for i in range(self.ivals):
        #    if ival[0]

