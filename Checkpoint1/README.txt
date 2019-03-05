Joshua Lange
CIS*4650
Checkpoint #1
Date of Last Modification: Monday, March 4th, 2019

===================
Program Description
===================

This program takes in a C-minus program as input. The program should be reasonably well
formatted, but some error checking has been completed. A scanner reads in the input, and 
a parser generates a syntax tree. The code has been designed based off of the code provided
to us by Dr. Song, and has implemented the visitor pattern using the AST tree template provided
in class. The final output is either a) a syntax tree of the input file, or 2) an error report
from a fatal error. Some errors, we believe, were not recoverable from, given the scope and
time available for this assignment. 

======================================
Building Instructions and Testing Plan
======================================

This program uses both jflex and CUP as part of the application. These must be installed on
your system for the build to be successful. These build instructions also assume that javac is
a usable command on your computer, and that you are building on a Linux environment.

This program uses a makefile to build the program. Type 'make' in the top directory of this 
program's file structure to build the program. To remove the executable and intermediate files 
generated from compilation, type "make clean".

Testing has been automated into separate test files to test different components of the code. There
are five test files used for this program, as this was the maximum number of test files that we
were allow to submit. These tests, therefore, attempt to show both the functionality and the error
recovery abilities of the program to the best of their ability, while also being limited to the
number of errors that can be shown (max. 3 per file). The fifth file has no restrictions, and has been
filled with as many errors as possible. These tests include both positive and negative test cases to 
show off both correct and incorrect code structures. The files are listed below with a short description
of what is contained inside of them:

   1) <Insert descriptions here>
   2) <Insert descriptions here>
   3) <Insert descriptions here>
   4) <Insert descriptions here>
   5) <Insert descriptions here>


  
these files are unit tests, such that they test specific components of the scanner (hyphenated words,
for example). These tests include both positive and negative test cases for each of the regular expressions. 
These files are listed below with a short description:

sort.cm
three modified prof files that are error
one that is just a mess.

    1) apostropheWordTest.txt - Tests the apostrophe tag. 
    2) balancedOPENCLOSETagChecker.txt - Checks for valid and balanced open and close tags.
    3) balancedTagsRealTags.txt - Extension of 2
    4) hyphenWords.txt - tests only hyphenated word cases.
    5) realNumbersTest.txt - ensures that both real and integer numbers are properly tagged.
    6) secondHyphenWords.txt - tests combinations of hyphens and apostrophes, along with other tests.
    7) simpleOPENCLOSETagChecker.txt - deprecated test, used prior to irrelevant tag filtering.
    8) wordsTesterNoNumbers.txt - tests standard words with no numbers included.
    9) wordsTesterWithNumbers.txt - tests words that include numbers, and makes sure they do not overlap
        with the numbers regular expression.
    10) smallerNewsData.txt - test file to compare with the sample.out file


-Batch Tests-

These test files can be run through the makefile through a batch processing in three ways:

1)  Type 'make test' to run the scanner on all of the test files. A large amount of output is generated,
    all of which is sent to the console. 

2)  Type 'make testToFile' to run the scanner on all of the test files. A large amount of output is generated,
    with the output of each test being stored in its corresponding .out file in the testOutput/ directory. 

3)  Type 'make newsdata' to un the scanner on the newsdata.txt file provided by the professor and send the 
    stdout to testOutput/newsdata.out. This can then be checked manually for consistency.


-Single Tests-

If you would like to run a single test and output to the console, the general command to run a test is as follows: 

    java Scanner <  testCases/nameOfTestFile.txt

If you would like to run a single test and output to a file, the general command to run a test is as follows:

    java Scanner < testCases/nameOfTestCase.txt > testOutput/nameOfTestCase.out



===========================
Assumptions and Limitations
===========================

1. Testing has been completed using that standard English alphabet, and will 
not run properly if symbols outside of those alphanumeric characters are given. 
    -> Punctuation is the default case of this program, which captures anything 
    that is not recognized by the previous regular expressions. Any non-English
    alphanumeric characters would be treated as punctuation.


===================
Future Improvements
===================

1. Expanding the alphabet to include other language characters would greatly improve the usability
of this scanner.
2. The Open tag regular expression is very complicated, and a more simple version is likely. A future 
improvement would be to find this simpler version and implement it.
3. Additional SGML test files would likely be useful for finding additional future improvements, 
especially if these SGML files were from various industries. Currently, the running of this code 
is based off of unit testing and a correct runtime of a single correct case. Additional correct cases
could help make this code more robust.
4. Currently, this scanner skips input that causes an error. A more sophisticated scanner would implement
some sort of backtracking to see if a possible alternative path could lead to a solution. The scanner 
could also maintain a list of currently known tags to attempt to correct for typos. This is. however, 
still a very immature field in scanning, so it would likely be too costly to do this much work for a 
simple SGML scanner.
