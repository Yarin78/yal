import sys

# Port from the C++ version; compared with the output for the Java version for random input
# TODO: Make more pythonesque

class BipartiteMatching:
    def __init__(self, n, m):
        self.usize = n
        self.uprev = [0] * self.usize
        self.vsize = m
        self.vprev = [0] * self.vsize
        self.edge = [list() for u in range(self.usize)]
        self.uunsat = [1] * self.usize
        self.q = [0] * self.usize
        self.matched = [-1] * self.vsize

    def add(self, x, y):
        self.edge[x].append(y)

    def rec(self, y):
        if self.vprev[y] < 0:
            return False
        x = self.vprev[y]
        w = self.uprev[x]
        self.vprev[y] = -1
        self.uprev[x] = -1
        if not self.uunsat[x] and (w < 0 or not self.rec(w)):
            return False
        self.matched[y] = x
        self.uunsat[x] = 0
        return True

    def find_augmenting_path(self):
        self.uprev = [-1] * self.usize
        self.vprev = [-1] * self.vsize
        head = 0
        tail = 0
        found = False
        for i in range(self.usize):
            if self.uunsat[i]:
                self.q[tail] = i
                tail += 1

        while head < tail:
            x = self.q[head]
            head += 1
            for j in range(len(self.edge[x])):
                y = self.edge[x][j]
                w = self.matched[y]
                if self.vprev[y]<0:
                    self.vprev[y] = x
                if w >= 0 and self.uprev[w] < 0:
                    self.uprev[w] = y
                    self.q[tail] = w
                    tail += 1
        for i in range(self.vsize):
            if self.matched[i] < 0:
                found |= self.rec(i)
        return found

    def find_maximum_matching(self):
        while (self.find_augmenting_path()):
            pass
        return self.matched


def show_matching(matched, vsize):
    cnt = 0
    #print("Matching:")
    for i in range(vsize):
        if matched[i] >= 0:
            # if cnt < 30:
            #     print(f" {matched[i]}, {i}")
            # elif cnt==30:
            #     print("...")
            cnt += 1
    #print()
    #print(f"Matching size: {cnt}")
    print(cnt)

if __name__ == "__main__":
    n = int(input())
    for _ in range(n):
        usize, vsize, num_edges = map(int, input().split(' '))
        m = BipartiteMatching(usize, vsize)
        for _ in range(num_edges):
            x, y = map(int, input().split(' '))
            m.add(x, y)

        matching = m.find_maximum_matching()
        show_matching(matching, vsize)
