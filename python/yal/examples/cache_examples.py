from functools import lru_cache

# Example of how to use the built in cache in Python

@lru_cache(maxsize=None,)
def fib(n):
    return fib(n-1)+fib(n-2) if n >= 2 else 1

@lru_cache(maxsize=None)
def rec(s:str) -> float:
    # Silly function doing nothing except recursing across all substrings
    n = len(s)
    if n == 1:
        return 5.23
    best = 0
    for i in range(n):
        for j in range(i+1, n+1):
            if j-i < n:
                best = max(best, rec(s[i:j]))
    return best

if __name__ == "__main__":
    #for i in range(50):
    #   print(i, fib(i))
    rec("AFHSFASOIDFAIOFERFAFHSFASOIDFAIOFERFAFHSFASO")

    (hits, misses, maxsize, currsize) =  rec.cache_info()
    print(hits, misses, maxsize, currsize)
