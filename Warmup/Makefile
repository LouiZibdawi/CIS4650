JAVAC=javac
JFLEX=jflex
#JFLEX=/home/fsong/jflex/bin/jflex

all: Token.class Lexer.class Scanner.class

%.class: %.java
	$(JAVAC) $^

Lexer.java: warmup.flex
	$(JFLEX) warmup.flex

clean:
	rm -f Lexer.java *.class *~
test:
	java Scanner < testCases/apostropheWordTest.txt
	java Scanner < testCases/balancedOPENCLOSETagChecker.txt
	java Scanner < testCases/balancedTagsRealTags.txt
	java Scanner < testCases/hyphenWords.txt
	java Scanner < testCases/realNumbersTest.txt
	java Scanner < testCases/secondHyphenWords.txt
	java Scanner < testCases/simpleOPENCLOSETagChecker.txt
	java Scanner < testCases/smallerNewsData.txt
	java Scanner < testCases/wordsTesterNoNumbers.txt
	java Scanner < testCases/wordsTesterWithNumbers.txt

testToFile:
	java Scanner < testCases/apostropheWordTest.txt > testOutput/apostropheWordTest.out
	java Scanner < testCases/balancedOPENCLOSETagChecker.txt > testOutput/balancedOPENCLOSETagChecker.out
	java Scanner < testCases/balancedTagsRealTags.txt > testOutput/balancedTagsRealTags.out
	java Scanner < testCases/hyphenWords.txt > testOutput/hyphenWords.out
	java Scanner < testCases/realNumbersTest.txt > testOutput/realNumbersTest.out
	java Scanner < testCases/secondHyphenWords.txt > testOutput/secondHyphenWords.out
	java Scanner < testCases/simpleOPENCLOSETagChecker.txt > testOutput/simpleOPENCLOSETagChecker.out
	java Scanner < testCases/smallerNewsData.txt > testOutput/smallerNewsData.out
	java Scanner < testCases/wordsTesterNoNumbers.txt > testOutput/wordsTesterNoNumbers.out
	java Scanner < testCases/wordsTesterWithNumbers.txt	 > testOutput/wordsTesterWithNumbers.out

newsdata:
	java Scanner <  testCases/newsdata.txt > testOutput/newsdata.out
