#!/bin/bash
echo "===== Mulato Backup Files ====="
echo "Iniciando aplicação..."

# Verifica se o arquivo JAR existe
if [ ! -f "target/mulato-backup-files-1.0-SNAPSHOT.jar" ]; then
    echo "Compilando o projeto..."
    mvn clean package
else
    echo "JAR encontrado, pulando compilação."
fi

# Define o tamanho máximo da memória para a JVM
JAVA_OPTS="-Xmx512m"

# Executa o programa
echo "Executando aplicação de backup..."
java $JAVA_OPTS -jar target/mulato-backup-files-1.0-SNAPSHOT.jar

echo "Programa encerrado."