import re
import itertools
import string
from typing import Any, List, Optional, Tuple, TypeVar, Union

T = TypeVar("T")

_integer_pattern = re.compile(r"-?[0-9]+")
_non_neg_integer_pattern = re.compile(r"[0-9]+")
_token_pattern = re.compile(r"[A-Za-z0-9]+")
_token_pattern_with_dash = re.compile(r"[\-A-Za-z0-9]+")


def get_ints(line: str) -> List[int]:
    '''Detects all integers in a line an returns a list of them. Non-integer tokens are ignored.'''
    return [int(m) for m in _integer_pattern.findall(line)]

def get_non_negative_ints(line: str) -> List[int]:
    '''Detects all non-negative integers in a line an returns a list of them. Non-integer tokens are ignored.'''
    return [int(m) for m in _non_neg_integer_pattern.findall(line)]


def tokenize(line: str) -> List[str]:
    '''Splits a line into tokens made up of alpha numerical characters'''
    return [s for s in _token_pattern.findall(line)]


def is_int(s: str) -> bool:
    '''Determines if a string is an int.'''
    return s.isdigit() or (len(s) > 0 and s[0] == '-' and s[1:].isdigit())


def intify(lines: List[str]) -> List[Union[int, str]]:
    '''Converts a list of strings into a list of mixed int/strings depending on if a string can be parsed as an int.
    If something looks like an int, it probably is.'''
    return [int(s) if is_int(s) else s for s in lines]


def tokenize_minus(line: str) -> List[str]:
    '''Same as tokenize but in addition - is not a separator (negative numbers)'''
    return [s for s in _token_pattern_with_dash.findall(line)]


def pair_up(data: List[T], pair_size=2) -> List[Tuple[T, T]]:
    '''Transform [a,b,c,d,e,f] => [(a,b),(c,d),(e,f)]'''
    return [tuple(data[i:i+pair_size]) for i in range(0, len(data), pair_size)]


def multi_split(s: str, delimeters: List[str]):
    # Split a string by a delimter, then split all parts of that by the next delimeter, and so on
    # Returns a multidimension array, same number of dimensions as elements in delimeters
    if not delimeters:
        return s
    parts = s.split(delimeters[0])
    ans = []
    for p in parts:
        sp = multi_split(p, delimeters[1:])
        if sp:
            ans.append(sp)
    return ans

def chunk(s: List[str], chunk_size: int) -> List[str]:
    '''Splits a string or list into chunks given the specified chunk size'''
    res = []
    i = 0
    while i < len(s):
        res.append(s[i:min(i+chunk_size, len(s))])
        i += chunk_size
    return res

def split_lines(lines: List[str], delimeter: str = "") -> List[List[str]]:
    '''Splits a list of strings into groups based on a delimeter string.'''
    # Note: An empty input list will result in [[]] as output
    res = []
    cur = []
    for line in lines:
        if line == delimeter:
            res.append(cur)
            cur = []
        else:
            cur.append(line)
    res.append(cur)
    return res

def init_matrix(ysize: int, xsize: int, init=0) -> List[List]:
    '''Initialized an empty matrix.'''
    return [[init] * xsize for _ in range(ysize)]

def matrix_filter(m, condition):
    '''Gets a list of tuples (col,row) for all elements in a matrix matching a condition.'''
    res=[]
    for row in range(len(m)):
        for col in range(len(m[row])):
            if condition(m[row][col]):
                res.append((col, row))
    return res

def lower_letters(num:int=26) -> str:
    '''Returns a string of the first num lowercase letters'''
    return string.ascii_lowercase[:num]

def upper_letters(num:int=26) -> str:
    '''Returns a string of the first num uppercase letters'''
    return string.ascii_uppercase[:num]

def letter_value(c: str) -> int:
    '''Returns 0-25 for a-z, 26-51 for A-Z'''
    assert len(c) == 1
    if c[0] >= 'a' and c[0] <= 'z':
        return ord(c[0])-ord('a')
    assert c[0] >= 'A' and c[0] <= 'Z'
    return ord(c[0])-ord('A')+26

def string_to_mask(s: str) -> int:
    '''Converts a string of letters a-z, A-Z into a corresponding bitmask (52 bits)'''
    mask = 0
    for c in s:
        if c.islower():
            mask |= 1 << (ord(c) - ord('a'))
        if c.isupper():
            mask |= 1 << (ord(c) - ord('A') + 26)
    return mask

def bits_set(v: int) -> List[int]:
    '''Returns a list of all set bits in the input integer'''
    res = []
    i = 0
    j = 1
    while v > 0:
        if v & j:
            v -= j
            res.append(i)
        i += 1
        j *= 2
    return res

def count_bits(i: int) -> int:
    '''Count number of bits set in an int'''
    cnt = 0
    while i:
        cnt += i&1
        i //= 2
    return cnt


def eval_expr(expr: str):
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


def get_matrix(line_stream) -> Optional[List[List[int]]]:
    '''Returns a matrix of ints by reading lines,
    stopping at the next empty line and ignoring initial empty lines.
    Returns None at end if input.'''
    try:
        s = next(line_stream)
        while s == '':
            s = next(line_stream)
    except StopIteration:
        return None

    rows = []
    while s:
        rows.append(list(map(int, tokenize(s))))
        try:
            s = next(line_stream)
        except StopIteration:
            break
    return rows

def sign(x: int)->int:
    if x < 0:
        return -1
    if x > 0:
        return 1
    return 0

def dict_min_value(dict):
    '''Gets a tuple (value, key) from the dict with the min value'''
    return min((v, k) for k, v in dict.items())

def dict_max_value(dict):
    '''Gets a tuple (value, key) from the dict with the max value'''
    return max((v, k) for k, v in dict.items())

def transpose_matrix(matrix: List[List[Any]]) -> List[List[Any]]:
    result = [[] for _ in range(len(matrix[0]))]
    for row in matrix:
        for col_ix, col in enumerate(row):
            result[col_ix].append(col)
    return result

def replace_wildcards(pattern: str, wildcard: str, replacements: List[str]):
    '''
    Generates all possible replacement of a specific wildcard string
    in a pattern, by replacing them with any of the values in replacements.
    '''

    def _generate_rec(cur: str, ix: int):
        if ix == len(pattern):
            yield cur
        else:
            if pattern[ix] != wildcard:
                yield from _generate_rec(cur + pattern[ix], ix+1)
            else:
                for repl in replacements:
                    yield from _generate_rec(cur + repl, ix+1)

    yield from _generate_rec("", 0)
