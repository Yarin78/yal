from setuptools import setup, find_packages
from os import path

setup(
    name='yal',
    version='1.0.0',
    description='Algorithms and data structures',
    url='https://github.com/yarin/yal',
    author='Jimmy Mårdell',
    author_email='jimmy.mardell@gmail.com',
    classifiers=[
        'Development Status :: 4 - Beta',

        'Intended Audience :: Developers',
        'Topic :: Software Development :: Libraries',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
        'Programming Language :: Python :: 3.7',
        'Programming Language :: Python :: 3.8',
    ],
    keywords='algorithms data structures',

    packages=find_packages(exclude=['tests*']),
    python_requires='>=3.5',

    install_requires=[]
)
