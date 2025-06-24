/**
 * Classe principal do aplicativo de backup.
 *
 * Responsável por iniciar a aplicação Java, delegando a execução
 * para a interface gráfica implementada em FileBackup.
 *
 * Ponto de entrada padrão para execução via linha de comando ou IDE.
 */
package com.mulato;

public class Main {
    public static void main(String[] args) {
        FileBackup.main(args);
    }
}