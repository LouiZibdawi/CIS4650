JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./jars/cup.jar
CUP=$(JAVA) $(CLASSPATH) java_cup.Main
#CUP=cup

all: CM.class

CM.class: absyn/*.java parser.java sym.java SymItem.java Lexer.java AssemblyCodeCreator.java SemanticAnalyzer.java ShowTreeVisitor.java Scanner.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 < cminus.cup

test0:
	-rm input/0.tm
	java -classpath ./jars/cup.jar: CM input/0.cm
	cat input/0.tm
test1:
	-rm input/1.tm
	java -classpath ./jars/cup.jar: CM input/1.cm
	cat input/1.tm
test2:
	-rm input/2.tm
	java -classpath ./jars/cup.jar: CM input/2.cm
	cat input/2.tm
test3:
	-rm input/3.tm
	java -classpath ./jars/cup.jar: CM input/3.cm
	cat input/3.tm
test4:
	-rm input/4.tm
	java -classpath ./jars/cup.jar: CM input/4.cm
	cat input/4.tm
test5:
	-rm input/5.tm
	java -classpath ./jars/cup.jar: CM input/5.cm
	cat input/5.tm

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

C2Test1:
	java -classpath ./jars/cup.jar: CM input/C2-1.cm
C2Test2:
	java -classpath ./jars/cup.jar: CM input/C2-2.cm
C2Test3:
	java -classpath ./jars/cup.jar: CM input/C2-3.cm
C2Test4:
	java -classpath ./jars/cup.jar: CM input/C2-4.cm
C2Test5:
	java -classpath ./jars/cup.jar: CM input/C2-5.cm


clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~

tiny:
	java -classpath /usr/share/java/cup.jar:. CM fac.tiny
