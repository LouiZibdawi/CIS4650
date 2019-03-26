JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./jars/cup.jar
CUP=$(JAVA) $(CLASSPATH) java_cup.Main
#CUP=cup

all: CM.class

CM.class: absyn/*.java parser.java sym.java SymItem.java Lexer.java SemanticAnalyzer.java ShowTreeVisitor.java Scanner.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 < cminus.cup

test1:
	java -classpath ./jars/cup.jar: CM input/1.cm

test2:
	java -classpath ./jars/cup.jar: CM input/2.cm

test3:
	java -classpath ./jars/cup.jar: CM input/3.cm

test4:
	java -classpath ./jars/cup.jar: CM input/4.cm

test5:
	java -classpath ./jars/cup.jar: CM input/5.cm

C1Test1:
	java -classpath ./jars/cup.jar: CM input/C1-1.cm
C1Test2:
	java -classpath ./jars/cup.jar: CM input/C1-2.cm
C1Test3:
	java -classpath ./jars/cup.jar: CM input/C1-3.cm
C1Test4:
	java -classpath ./jars/cup.jar: CM input/C1-4.cm
C1Test5:
	java -classpath ./jars/cup.jar: CM input/C1-5.cm

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~

tiny:
	java -classpath /usr/share/java/cup.jar:. CM fac.tiny
