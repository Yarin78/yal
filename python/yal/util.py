import re
import itertools

_integer_pattern = re.compile(r"-?[0-9]+")
_token_pattern = re.compile(r"[A-Za-z0-9]+")
_token_pattern_with_dash = re.compile(r"[\-A-Za-z0-9]+")


def get_ints(line):
    return [int(m) for m in _integer_pattern.findall(line)]


def tokenize(line):
    return [s for s in _token_pattern.findall(line)]


def is_int(s):
    return s.isdigit() or (len(s) and s[0] == '-' and s[1:].isdigit())


def intify(line):
    '''If something looks like an int, it probably is'''
    return [int(s) if is_int(s) else s for s in line]


def tokenize_minus(line):
    '''Same as tokenize but in addition - is not a separator (negative numbers)'''
    return [s for s in _token_pattern_with_dash.findall(line)]


def pair_up(data, pair_size=2):
    '''Transform [a,b,c,d,e,f] => [(a,b),(c,d),(e,f)]'''
    return [tuple(data[i:i+pair_size]) for i in range(0, len(data), pair_size)]


def chunk(s, chunk_size):
    '''Splits a string into chunks given the specified chunk size'''
    res = []
    i = 0
    while i < len(s):
        res.append(s[i:min(i+chunk_size, len(s))])
        i += chunk_size
    return res

