# Backup de Arquivos

Uma ferramenta simples para realizar backup de arquivos e compactá-los em vários arquivos ZIP.

## Visão Geral

Este aplicativo Java permite:
- Selecionar um diretório fonte para backup
- Copiar arquivos para um diretório temporário
- Compactar os arquivos em múltiplos arquivos ZIP com tamanho limitado
- Gerar logs detalhados das operações de backup

## Requisitos

- Java 8 ou superior
- Apache Commons Compress 1.21
- Maven (opcional, para compilação)

## Como Usar

### Usando os scripts

O aplicativo inclui scripts de execução para Windows e Linux/macOS:

**Windows:**
executar-backup.bat

**Linux/macOS:**
chmod +x executar-backup.sh
./executar-backup.sh

### Interface do Aplicativo

1. Clique em **Selecionar Pasta** para escolher o diretório que deseja fazer backup
2. Clique em **Copiar Arquivos** para iniciar a cópia dos arquivos para o diretório temporário
3. Após a conclusão da cópia, clique em **Compactar** para gerar os arquivos ZIP
4. Os arquivos de backup serão salvos no diretório de destino

## Características

- **Divisão de arquivos**: Gera múltiplos arquivos ZIP com tamanho máximo definido
- **Compressão de alta performance**: Utiliza nível máximo de compressão (nível 9)
- **Registro detalhado**: Cria logs com informações de todos os arquivos processados, incluindo caminho completo
- **Barra de progresso**: Mostra o andamento da operação em tempo real

## Compilação Manual

### Via Maven:

mvn clean package

Via linha de comando:
javac -cp ".;C:/caminho/para/commons-compress-1.21.jar" -d target/classes src/main/java/com/mulato/*.java

Criando JAR executável:
echo Main-Class: com.mulato.FileBackup > MANIFEST.MF
jar cfm backup-all.jar MANIFEST.MF -C target/classes .

Estrutura do Projeto

src/main/java/com/mulato/: Código-fonte da aplicação
FileBackup.java: Classe principal com interface gráfica e lógica de backup
executar-backup.bat: Script para Windows
executar-backup.sh: Script para Linux/macOS

Logs de Backup

Os logs são gerados na pasta de destino e contêm:

Data e hora do backup

Nome dos arquivos ZIP gerados
Caminho completo de cada arquivo incluído no backup
Mensagens de erro (caso ocorram)

Aviso

Este aplicativo foi desenvolvido para uso pessoal e educacional. Para ambientes de produção, considere recursos adicionais como:

Criptografia dos arquivos de backup
Verificação de integridade
Backup incremental
Agendamento automático

