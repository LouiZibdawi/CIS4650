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
were allowed to submit. These tests, therefore, attempt to show both the functionality and the error
recovery abilities of the program to the best of their ability, while also being limited to the
number of errors that can be shown (max. 3 per file). The fifth file has no restrictions on the
number of errors that can be contained in it, and has been filled with as many errors as possible.
These tests include both positive and negative test cases to show off both correct and incorrect
code structures. The files are listed below with a short description of what is contained inside of them:

   1) 1.cm: This is a modified version of the fac.cm that was provided for use by the professor. The
      errors included in this are as follows:
        - Unrecognized type for variables are handled by converting to integer (default)
        - Unrecognized type for arrays are handled by converting to integer (default)
      We decided that the best way to handle an invalid type would be to set it to a default value and
      continue parsing the file. Obviously, this is likely not the best option for a full parser, but
      for the current scale of the project it makes sense. There are only two variable types, one of 
      which is void, so converting it to integer is the most logical.

   2) 2.cm: This is another modified version of the fac.cm that was provided to us for use by the professor.
      The errors included in this are as follows:
        - function declaraction with no parameters and without a body
        - variable declaration without a recognized type 
   3) 3.cm: This is another modified version of the fac.cm that was provided to us for use by the professor. 
      The errors included in this are as follows:
        - function declaration with parameters, without a body
        - array declaraction without a recognized type

   4) 4.cm: This is a modified version for gcd.cm that was provided to us by the professor. The
      errors included are as follows:
        - function declaraction with parameters and an unrecognized type
        - function declaration without parameters and an unrecognized type

   5) 5.cm: This is a modified version of sort.cm that was provided to us for use by the professor.
      The errors included in this file are as follows:
        - function declaration with parameters and an unrecognized type
        - function declaration without parameters and an unrecognized type
        - variable declaraction without a recognized type
        - array declaration without a recognized type
        - function declaration with parameters, without a body


-Makefile to Run Tests-

These test files can be run through the makefile through using the following commands:

1)  make test1: runs the first test case
2)  make test2: runs the second test case
3)  make test3: runs the third test case
4)  make test4: runs the fourth test case
5)  make test5: runs the fifth test case


===========================
Assumptions and Limitations
===========================

1. We assume that the files provided will be in the .cm style, and have reasonable errors. Some
   error handling has been implemented, but definitely not enough to cover all possible error cases.
2. We assume that nothing outside of the assigment specification will be provided as input into the
   program, unless it is a reasonable test case for the scanner/parser.
3. This program will run perfectly on a program that is properly formatted.
4. This program does not handle cases where a variable not declared, but is then used in the program.
5. The program will not recognize a variable input when it is expecting a number. Rather, it will just
   crash. This is because we use ParseInt.
===================
Future Improvements
===================

Given additional work hours for this assignment, the following changes would have been implemented:

1. Additional error handling could be implemented, given more time. This includes a wider range of 
   error handling, as well as better handling of all errors when they occur. Some of the unrecoverable
   errors could be handled in a different way, or could be skipped, or even some basic spelling modifications
   could be reported, once the symbol table has been set up.
2. Additional simplification of the program could be completed using associations and combining
   grammar rules.  
3. Fixed the fifth limitation listed above by finding a way to create the grammar without needing ParseInt.
