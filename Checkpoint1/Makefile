JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath ./jars/cup.jar
CUP=$(JAVA) $(CLASSPATH) java_cup.Main
#CUP=cup

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: tiny.flex
	$(JFLEX) tiny.flex

parser.java: tiny.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) -expect 3 < tiny.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~

tiny:
	java -classpath /usr/share/java/cup.jar:. Main fac.tiny
