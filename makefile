# Variáveis de compilação
JAVAC = javac
JAVA_FLAGS = -cp .  # Define o classpath para o diretório atual

# Lista de arquivos fonte
SOURCES = Pong.java GameLib.java MyFrame.java MyKeyAdapter.java Wall.java Ball.java Player.java Score.java

# Lista de arquivos objeto (arquivos .class)
CLASSES = $(SOURCES:.java=.class)

# Regra de compilação
all: $(CLASSES)

%.class: %.java
    rm -f *.class::
