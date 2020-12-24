import re
import itertools
import string

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

def init_matrix(ysize, xsize, init=0):
    return [[init] * xsize for _ in range(ysize)]

def matrix_filter(m, condition):
    res=[]
    for row in range(len(m)):
        for col in range(len(m[row])):
            if condition(m[row][col]):
                res.append((col, row))
    return res

def lower_letters(num=26):
    return string.ascii_lowercase[:num]

def upper_letters(num=26):
    return string.ascii_uppercase[:num]

def string_to_mask(s):
    mask = 0
    for c in s:
        if c.islower():
            mask |= 1 << (ord(c) - ord('a'))
        if c.isupper():
            mask |= 1 << (ord(c) - ord('A') + 26)
    return mask

def count_bits(i):
    cnt = 0
    while i:
        cnt += i&1
        i //= 2
    return cnt


def eval_expr(expr):
    # This is an example from AoC 2020 day 18 that applies + before * on an integer expression

    # Recursively replace the innermost ( ) expression with its evaluation
    while '(' in expr:
        expr = re.sub(r'\(([^\(^\)]+)\)', lambda m: eval_expr(m.groups(1)[0]), expr, count=1)

    # + has higher precedence than *
    while '+' in expr:
        expr = re.sub(r'(([0-9]+)\s*\+\s*([0-9]+))', lambda m: str(int(m.groups()[1]) + int(m.groups()[2])), expr, count=1)
    while '*' in expr:
        expr = re.sub(r'(([0-9]+)\s*\*\s*([0-9]+))', lambda m: str(int(m.groups()[1]) * int(m.groups()[2])), expr, count=1)

    # left-to right eval of + and *
    # def _sub_eval(m):
    #     t1 = int(m.groups()[1])
    #     t2 = int(m.groups()[3])
    #     if m.groups()[2] == '+':
    #         return str(t1+t2)
    #     if m.groups()[2] == '*':
    #         return str(t1*t2)
    #     assert False

    # while '+' in expr or '*' in expr:
    #     expr = re.sub(r'(([0-9]+)\s*(\+|\*)\s*([0-9]+))', _sub_eval, expr, count=1)
    return expr
