package com.mulato;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class FileBackup {
    private static JTextField sourcePathField;
    private static JTextField destinationPathField;
    private static JComboBox<String> zipSizeComboBox;
    private static JProgressBar progressBar;
    private static JButton copyButton;
    private static JButton zipButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Backup de Arquivos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setResizable(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Configurações");
        JMenuItem sourceMenuItem = new JMenuItem("Selecionar Origem");
        JMenuItem destinationMenuItem = new JMenuItem("Selecionar Destino");
        menu.add(sourceMenuItem);
        menu.add(destinationMenuItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        sourcePathField = new JTextField(20);
        destinationPathField = new JTextField(20);
        sourcePathField.setEditable(false);
        destinationPathField.setEditable(false);

        JPanel pathsPanel = new JPanel();
        pathsPanel.setLayout(new GridLayout(4, 1));
        pathsPanel.add(new JLabel("Caminho de Origem:"));
        pathsPanel.add(sourcePathField);
        pathsPanel.add(new JLabel("Caminho de Destino:"));
        pathsPanel.add(destinationPathField);

        sourceMenuItem.addActionListener(e -> selectDirectory(sourcePathField));
        destinationMenuItem.addActionListener(e -> selectDirectory(destinationPathField));

        JPanel zipSizePanel = new JPanel();
        zipSizePanel.setLayout(new FlowLayout());
        JLabel zipSizeLabel = new JLabel("Tamanho do pacote ZIP:");
        String[] zipSizes = {"100 MB", "200 MB", "500 MB"};
        zipSizeComboBox = new JComboBox<>(zipSizes);
        zipSizePanel.add(zipSizeLabel);
        zipSizePanel.add(zipSizeComboBox);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel actionPanel = new JPanel();
        copyButton = new JButton("Copiar Arquivos");
        zipButton = new JButton("Compactar Pasta");
        zipButton.setEnabled(false); // Inicialmente desativado
        actionPanel.add(copyButton);
        actionPanel.add(zipButton);

        mainPanel.add(pathsPanel);
        mainPanel.add(zipSizePanel);
        mainPanel.add(actionPanel);
        mainPanel.add(progressBar);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        copyButton.addActionListener(e -> new Thread(() -> copyFiles()).start());
        zipButton.addActionListener(e -> new Thread(() -> zipFiles()).start());

        // Verifica se existe pasta temporária ao iniciar
        checkTempFolder();
    }

    private static void selectDirectory(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Selecione o diretório");
        fileChooser.setApproveButtonText("Selecionar");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            checkTempFolder(); // Verifica se a pasta temporária existe após selecionar diretório
        }
    }

    // Método para verificar a existência da pasta temporária e ajustar os botões
    private static void checkTempFolder() {
        String destDir = destinationPathField.getText();
        if (!destDir.isEmpty()) {
            File tempFolder = new File(destDir + "/backup_temp");
            boolean tempFolderExists = tempFolder.exists() && tempFolder.isDirectory() && tempFolder.list().length > 0;

            SwingUtilities.invokeLater(() -> {
                zipButton.setEnabled(tempFolderExists);
                copyButton.setEnabled(!tempFolderExists);
            });
        }
    }

    private static void zipFiles() {
        String destDir = destinationPathField.getText();
        if (destDir.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione o diretório de destino.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        progressBar.setValue(0);

        // Desativa os botões durante a operação
        SwingUtilities.invokeLater(() -> {
            zipButton.setEnabled(false);
            copyButton.setEnabled(false);
        });

        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(destDir + "/backup.log", true))) {
            File destFolder = new File(destDir + "/backup_temp");
            if (!destFolder.exists()) {
                JOptionPane.showMessageDialog(null, "A pasta temporária não existe. Primeiro copie os arquivos.", "Erro", JOptionPane.ERROR_MESSAGE);
                checkTempFolder();
                return;
            }

            int totalFiles = countFiles(destFolder);
            progressBar.setMaximum(totalFiles);
            AtomicInteger filesProcessed = new AtomicInteger(0);

            // Compactar arquivos copiados em pacotes ZIP
            int zipSizeMB = Integer.parseInt(zipSizeComboBox.getSelectedItem().toString().split(" ")[0]);
            int zipSizeBytes = zipSizeMB * 1024 * 1024;
            zipDirectory(destFolder, destFolder.getName(), destDir, zipSizeBytes, 1, logWriter, filesProcessed);

            // Remover a pasta temporária
            deleteDirectory(destFolder);

            // Após compactação, habilita o botão de cópia
            SwingUtilities.invokeLater(() -> {
                copyButton.setEnabled(true);
                zipButton.setEnabled(false);
            });

            JOptionPane.showMessageDialog(null, "Compactação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            checkTempFolder();
        }
    }

    private static void copyFiles() {
        String sourceDir = sourcePathField.getText();
        String destDir = destinationPathField.getText();
        if (sourceDir.isEmpty() || destDir.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione os diretórios de origem e destino.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        progressBar.setValue(0);

        // Desativa os botões durante a operação
        SwingUtilities.invokeLater(() -> {
            copyButton.setEnabled(false);
            zipButton.setEnabled(false);
        });

        try {
            File sourceFolder = new File(sourceDir);
            File destFolder = new File(destDir + "/backup_temp");
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            int totalFiles = countFiles(sourceFolder);
            progressBar.setMaximum(totalFiles);
            AtomicInteger filesProcessed = new AtomicInteger(0);

            // Copiar arquivos da origem para o destino temporário
            copyDirectory(sourceFolder, destFolder, filesProcessed);
            progressBar.setValue(totalFiles);

            // Após cópia, habilita o botão de compactação
            SwingUtilities.invokeLater(() -> {
                copyButton.setEnabled(false);
                zipButton.setEnabled(true);
            });

            JOptionPane.showMessageDialog(null, "Cópia concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            checkTempFolder();
        }
    }

    // Os outros métodos continuam iguais...
    private static void deleteDirectory(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    private static void copyDirectory(File sourceFolder, File destFolder, AtomicInteger filesProcessed) throws IOException {
        File[] files = sourceFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            File destFile = new File(destFolder, file.getName());
            if (file.isDirectory()) {
                destFile.mkdirs();
                copyDirectory(file, destFile, filesProcessed);
            } else {
                try (InputStream in = new FileInputStream(file);
                     OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                filesProcessed.incrementAndGet();
                final int progress = filesProcessed.get();
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        }
    }

    private static int countFiles(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return 0;
        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                count += countFiles(file);
            } else {
                count++;
            }
        }
        return count;
    }

    private static void zipDirectory(File folder, String basePath, String destDir, int zipSizeBytes, int zipIndex, BufferedWriter logWriter, AtomicInteger filesProcessed) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;

        // Lista para armazenar todos os arquivos a serem processados
        List<FileEntry> allFiles = new ArrayList<>();
        collectFiles(folder, basePath, allFiles);

        // Processa os arquivos sequencialmente
        int currentZipIndex = zipIndex;
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(
                new FileOutputStream(destDir + "/backup_" + currentZipIndex + ".zip"));
        zos.setLevel(9); // Nível máximo de compressão

        logWriter.write("Backup realizado em: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
        logWriter.write("Arquivo ZIP: backup_" + currentZipIndex + ".zip\n");

        long currentSize = 0;

        for (FileEntry entry : allFiles) {
            // Verifica se precisa criar um novo arquivo ZIP
            if (currentSize + entry.file.length() > zipSizeBytes && currentSize > 0) {
                zos.finish();
                zos.close();
                currentZipIndex++;
                zos = new ZipArchiveOutputStream(
                        new FileOutputStream(destDir + "/backup_" + currentZipIndex + ".zip"));
                zos.setLevel(9);
                logWriter.write("Arquivo ZIP: backup_" + currentZipIndex + ".zip\n");
                currentSize = 0;
            }

            // Adiciona o arquivo ao ZIP
            try {
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.path);
                zipEntry.setSize(entry.file.length());
                zos.putArchiveEntry(zipEntry);

                try (FileInputStream fis = new FileInputStream(entry.file)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }

                zos.closeArchiveEntry();
                currentSize += entry.file.length();

                // Registra o caminho completo do arquivo no log
                logWriter.write(" - " + entry.path + "\n");
                logWriter.flush();

                filesProcessed.incrementAndGet();
                final int progress = filesProcessed.get();
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            } catch (IOException e) {
                logWriter.write("Erro ao compactar " + entry.path + ": " + e.getMessage() + "\n");
            }
        }

        zos.finish();
        zos.close();
    }

    // Classe auxiliar para armazenar informações do arquivo
    private static class FileEntry {
        File file;
        String path;

        FileEntry(File file, String path) {
            this.file = file;
            this.path = path;
        }
    }

    // Método para coletar recursivamente todos os arquivos
    private static void collectFiles(File folder, String basePath, List<FileEntry> fileList) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectFiles(file, basePath + "/" + file.getName(), fileList);
            } else {
                fileList.add(new FileEntry(file, basePath + "/" + file.getName()));
            }
        }
    }
}