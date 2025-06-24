# Plano de Teste – Aplicação Java Backup de Arquivos

## Objetivo

Validar as principais funcionalidades da aplicação de backup de arquivos após a compilação, garantindo que o sistema atenda aos requisitos funcionais e opere corretamente em ambiente real.

---

## Funcionalidades a serem testadas

1. **Seleção de Diretório de Origem**
2. **Seleção de Diretório de Destino**
3. **Cópia de Arquivos para Diretório Temporário**
4. **Compactação em Arquivos ZIP**
5. **Divisão de ZIP por tamanho**
6. **Geração de Logs**
7. **Barra de Progresso e Mensagens**
8. **Execução via Script (Windows e Linux/macOS)**
9. **Execução do método main sem exceções**
10. **Cobertura de código com JaCoCo**

---

## Casos de Teste

### 1. Seleção de Diretório de Origem

- **Passos:** Abrir a aplicação, clicar em "Selecionar Pasta", escolher um diretório válido.
- **Resultado Esperado:** O caminho do diretório aparece no campo correspondente.

### 2. Seleção de Diretório de Destino

- **Passos:** Clicar em "Selecionar Pasta" para destino, escolher um diretório válido.
- **Resultado Esperado:** O caminho do diretório aparece no campo correspondente.

### 3. Cópia de Arquivos para Diretório Temporário

- **Passos:** Selecionar origem e destino, clicar em "Copiar Arquivos".
- **Resultado Esperado:** Arquivos são copiados para o diretório temporário, barra de progresso é atualizada.

### 4. Compactação em Arquivos ZIP

- **Passos:** Após a cópia, clicar em "Compactar".
- **Resultado Esperado:** Arquivos ZIP são criados no destino, respeitando o tamanho máximo configurado.

### 5. Divisão de ZIP por tamanho

- **Passos:** Configurar tamanho máximo pequeno, realizar backup de muitos arquivos.
- **Resultado Esperado:** Mais de um arquivo ZIP é gerado, nenhum arquivo ZIP excede o tamanho máximo.

### 6. Geração de Logs

- **Passos:** Realizar um backup completo.
- **Resultado Esperado:** Um arquivo de log é criado no destino, contendo data, arquivos processados, nomes dos ZIPs e eventuais erros.
- **Validação:** Comparar o conteúdo do log gerado com o log esperado para garantir que todas as operações foram registradas corretamente.

### 7. Barra de Progresso e Mensagens

- **Passos:** Executar operações de cópia e compactação.
- **Resultado Esperado:** Barra de progresso e mensagens refletem o andamento e conclusão das operações.

### 8. Execução via Script

- **Passos:** Rodar `executar-backup.bat` (Windows) ou `executar-backup.sh` (Linux/macOS).
- **Resultado Esperado:** Aplicação inicia normalmente e permite uso completo.

### 9. Execução do método main

- **Passos:** Executar a aplicação via linha de comando: `java -jar arquivo.jar`
- **Resultado Esperado:** Interface gráfica é exibida sem exceções.

### 10. Cobertura de código com JaCoCo

- **Passos:** Executar `mvn clean test` e abrir `target/site/jacoco/index.html`.
- **Resultado Esperado:** Relatório mostra cobertura dos testes unitários, com destaque para métodos principais.

---

## Critérios de Aceitação

- Todas as funcionalidades devem operar conforme descrito nos casos de teste.
- Não devem ocorrer erros não tratados durante a execução.
- Logs e arquivos ZIP devem ser gerados corretamente.
- Cobertura de código mínima conforme política do projeto (ex: 80%).

---

## Observações

- Testar em diferentes sistemas operacionais (Windows, Linux/macOS).
- Testar com diferentes tamanhos e quantidades de arquivos.
- Validar mensagens de erro para casos de diretórios inválidos ou falta de permissão.