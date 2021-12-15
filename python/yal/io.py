import queue
import sys
from typing import Iterable, Iterator


class Scanner:
    lines: Iterator[str]
    buffer: queue.Queue
    eof: bool

    def __init__(self, lines: Iterable[str]=sys.stdin, tokenizer=lambda s: s.split()):
        self.lines = iter(lines)
        self.eof = False
        self.buffer = queue.Queue()
        self.tokenizer = tokenizer

    def _read(self):
        '''Ensures buffer contains at least one element _or_ eof is set to True.'''
        if self.eof:
            return
        try:
            while self.buffer.empty():
                line = next(self.lines)
                for token in self.tokenizer(line):
                    self.buffer.put(token)
        except StopIteration:
            self.eof = True

    def has_next_token(self):
        self._read()
        return not self.eof

    def next_token(self):
        self._read()
        return self.buffer.get()
