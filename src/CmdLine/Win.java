package CmdLine;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Win {
    private final JFrame frame = new JFrame("7z GUI");
    private JPanel panel;
    private JTextField fileTextField;
    private JButton chooseButton;
    private JComboBox volumeBox;
    private JComboBox<String> updateModeBox;
    private JComboBox<String> pathModeBox;
    private JTextField passwordField;
    private JCheckBox SFXArchiveCheckBox;
    private JCheckBox compressSharedFilesCheckBox;
    private JCheckBox deleteAfterCompressionCheckBox;
    private JComboBox<String> encryptBox;
    private JCheckBox encryptCheckBox;
    private JComboBox<String> formatBox;
    private JComboBox<String> levelBox;
    private JComboBox<String> methodBox;
    private JComboBox<String> threadsBox;
    private JTextField memField;
    private JButton OKButton;
    private JButton cancelButton;
    private JTextField dememField;
    private JTextField cmdlineField;
    private JTextField optionsField;
    private JButton checkButton;
    private JTextField maxThreadsField;
    private final Cmdline cmdline = new Cmdline();
    private StringBuilder z7name = new StringBuilder();

    public Win() {

        z7name.append(Format.z7.name()).reverse();
        frame.setResizable(false);

        cmdline.setThreadNum(Runtime.getRuntime().availableProcessors());
        maxThreadsField.setHorizontalAlignment(4);
        maxThreadsField.setText(" / " + cmdline.getThreadNum());

        formatBox.addItem(z7name.toString());
        formatBox.addItem(Format.zip.name());
        formatBox.addItem(Format.tar.name());

        methodBox.setEnabled(true);
        methodBox.removeAllItems();
        methodBox.addItem(Method.LZMA2.name());
        methodBox.addItem(Method.LZMA.name());

        setLevelBox();
        setThreadsBox(true);
        setMemField(z7name.toString(), Level.normal, Method.LZMA2, cmdline.getThreadNum());
        setDememField(z7name.toString(), Level.normal);

        updateModeBox.addItem("Add & replace files");
        pathModeBox.addItem("Relative pathnames");
        encryptBox.addItem("AES-256");

        formatBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())) {
                methodBox.setEnabled(true);
                methodBox.removeAllItems();
                methodBox.addItem(Method.LZMA2.name());
                methodBox.addItem(Method.LZMA.name());
                setLevelBox();
                setThreadsBox(true);
                setMemField(z7name.toString(), Level.normal, Method.LZMA2, cmdline.getThreadNum());
                setDememField(z7name.toString(), Level.normal);
            }
            if (Objects.equals(formatBox.getSelectedItem(), Format.zip.name())) {
                methodBox.setEnabled(true);
                methodBox.removeAllItems();
                methodBox.addItem(Method.Deflate.name());
                methodBox.addItem(Method.LZMA.name());
                setLevelBox();
                setThreadsBox(true);
                setMemField(Format.zip.name(), Level.normal, Method.Deflate, cmdline.getThreadNum());
                setDememField(Format.zip.name(), Level.normal);
            }
            if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name())) {
                levelBox.removeAllItems();
                levelBox.setEnabled(false);
                methodBox.removeAllItems();
                methodBox.setEnabled(false);
                threadsBox.removeAllItems();
                threadsBox.setEnabled(false);
                setMemField(Format.tar.name());
                setDememField(Format.tar.name());
            }
        });

        levelBox.addActionListener(actionEvent -> {
        });

        // for 7z:lzma 2 threads, for any other threads = cpu cores
        methodBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    && methodBox.getSelectedIndex() == 1) {   // LZMA. don't touch
                setThreadsBox(false);
                setMemField(z7name.toString(), Level.normal, Method.LZMA, 2);
                setDememField(z7name.toString(), Level.normal);
            }
            else {
                setThreadsBox(true);
                if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())) {
                    setMemField(z7name.toString(), Level.normal, Method.LZMA2, cmdline.getThreadNum());
                    setDememField(z7name.toString(), Level.normal);
                }
                if (Objects.equals(formatBox.getSelectedItem(), Format.zip.name())) {
                    setMemField(Format.zip.name(), Level.normal, Method.Deflate, cmdline.getThreadNum());
                    setDememField(Format.zip.name(), Level.normal);
                }
                if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name())) {
                    setMemField(Format.tar.name());
                    setDememField(Format.tar.name());
                }
            }
        });

        chooseButton.addActionListener(actionEvent -> {
            FileDialog fileDialog = new FileDialog(frame, "Choose file", FileDialog.LOAD);
            fileDialog.setFile("*.*");
            fileDialog.setVisible(true);
            String fileName = fileDialog.getDirectory() + fileDialog.getFile();
            cmdline.setName(fileName);
            if (!fileName.equals("nullnull"))
                fileTextField.setText(fileName);
            else
                fileTextField.setText("");
        });

        OKButton.addActionListener(actionEvent -> {
//            String fileName = cmdline.getName();
        });

        cancelButton.addActionListener(actionEvent -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    private void setLevelBox() {
        levelBox.setEnabled(true);
        levelBox.removeAllItems();
        levelBox.addItem(Level.fast.name());
        levelBox.addItem(Level.normal.name());
        levelBox.addItem(Level.maximum.name());
        levelBox.setSelectedItem(Level.normal.name());
    }

    // true: threads = cpu cores, false: threads = 2 for lzma
    private void setThreadsBox(boolean longList) {
        threadsBox.setEnabled(true);
        threadsBox.removeAllItems();
        if (longList) {
            for (int i = 2; i <= 16; i += 2)
                threadsBox.addItem(Integer.toString(i));
            threadsBox.setSelectedIndex(cmdline.getThreadNumKey(cmdline.getThreadNum()));
        }
        else {
            threadsBox.addItem("2");
            threadsBox.setSelectedIndex(cmdline.getThreadNumKey(2));
        }
    }

    private void setMemField(String format) {
        int memory = 0;
        if (format.equals(Format.tar.name()))
            memory = 1;
        memField.setHorizontalAlignment(4);
        memField.setText(String.valueOf(memory) + " MB");
    }

    private void setDememField(String format) {
        int memory = 0;
        if (format.equals(Format.tar.name()))
            memory = 1;
        dememField.setHorizontalAlignment(4);
        dememField.setText(String.valueOf(memory) + " MB");
    }

    private void setMemField(String format, Level level, Method method, int threads) {
        int memory = 0;
        if (format.equals(Format.zip.name()))
            memory = (int) Math.round(33.3 * threads);

        if (format.equals(z7name.toString())) {
            if (method.equals(Method.LZMA))
                memory = 12 * cmdline.getDictionarySize(level) * threads;
            if (method.equals(Method.LZMA2))
                memory = 11 * cmdline.getDictionarySize(level) * threads;
        }
        memField.setHorizontalAlignment(4);
        memField.setText(String.valueOf(memory) + " MB");
    }

    private void setDememField(String format, Level level) {
        int memory = 0;
        if (format.equals(Format.zip.name()))
            memory = 2;
        if (format.equals(z7name.toString()))
            memory = cmdline.getDictionarySize(level) + 2;
        dememField.setHorizontalAlignment(4);
        dememField.setText(String.valueOf(memory) + " MB");
    }

    void construct() {
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
