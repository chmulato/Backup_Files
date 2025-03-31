@echo off
title Mulato Backup Files Runner
echo ===== Mulato Backup Files =====
echo Iniciando aplicacao...

rem Verifica se o arquivo JAR existe
if not exist "target\mulato-backup-files-1.0-SNAPSHOT.jar" (
    echo Compilando o projeto...
    call mvn clean package
) else (
    echo JAR encontrado, pulando compilacao.
)

rem Define o tamanho maximo da memoria para a JVM
set JAVA_OPTS=-Xmx512m

rem Executa o programa
echo Executando aplicacao de backup...
java %JAVA_OPTS% -jar target\mulato-backup-files-1.0-SNAPSHOT.jar

echo Programa encerrado.
pause