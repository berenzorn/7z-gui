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

    Win() {

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
//        setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA2.name(), cmdline.getThreadNum());

        updateModeBox.addItem("Add & replace files");
        pathModeBox.addItem("Relative pathnames");
        encryptBox.addItem("AES-256");

        formatBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    || (Objects.equals(formatBox.getSelectedItem(), Format.zip.name())))
                formatBoxInit(Objects.requireNonNull(formatBox.getSelectedItem()).toString(),
                        Level.normal.name(), cmdline.getThreadNum());

            if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name())) {
                levelBox.removeAllItems();
                levelBox.setEnabled(false);
                methodBox.removeAllItems();
                methodBox.setEnabled(false);
                threadsBox.removeAllItems();
                threadsBox.setEnabled(false);
//                setTarFields();
            }
        });

        levelBox.addActionListener(actionEvent -> {
/*
            int threads;
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    && methodBox.getSelectedIndex() == 1)
                threads = 2;
            else
                threads = cmdline.getThreadNum();
            setMemoryFields(formatBox.getSelectedItem().toString(), levelBox.getSelectedItem().toString(),
                    methodBox.getSelectedItem().toString(), threads);
*/
        });

        // for 7z:lzma 2 threads, for any other threads = cpu cores
        methodBox.addActionListener(actionEvent -> {
            if (Objects.equals(formatBox.getSelectedItem(), z7name.toString())
                    && methodBox.getSelectedIndex() == 1) {   // LZMA. don't touch
                setThreadsBox(false);
//                setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA.name(), 2);
            }
            else {
                setThreadsBox(true);
/*
                if (Objects.equals(formatBox.getSelectedItem(), z7name.toString()))
                    setMemoryFields(z7name.toString(), Level.normal.name(), Method.LZMA2.name(), cmdline.getThreadNum());
                if (Objects.equals(formatBox.getSelectedItem(), Format.zip.name()))
                    setMemoryFields(Format.zip.name(), Level.normal.name(), Method.Deflate.name(), cmdline.getThreadNum());
                if (Objects.equals(formatBox.getSelectedItem(), Format.tar.name()))
                    setTarFields();
*/
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

    private void formatBoxInit(String format, String level, int threads) {
        methodBox.setEnabled(true);
        methodBox.removeAllItems();
        if (format.equals(z7name.toString())) {
            methodBox.addItem(Method.LZMA2.name());
//            setMemoryFields(format, level, Method.LZMA2.name(), threads);
        }
        if (format.equals(Format.zip.name())) {
            methodBox.addItem(Method.Deflate.name());
//            setMemoryFields(format, level, Method.Deflate.name(), threads);
        }
        methodBox.addItem(Method.LZMA.name());
//        if (methodBox.getItemCount() > 2)
//            while (methodBox.getItemCount() != 2)
//                methodBox.remove(2);
        setLevelBox();
        setThreadsBox(true);
    }

    private void setLevelBox() {
        levelBox.setEnabled(true);
        levelBox.removeAllItems();
        levelBox.addItem(Level.fast.name());
        levelBox.addItem(Level.normal.name());
        levelBox.addItem(Level.maximum.name());
        levelBox.setSelectedItem(Level.normal.name());
//        if (levelBox.getItemCount() > 3)
//            while (levelBox.getItemCount() != 3)
//                levelBox.remove(3);
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
//        if (threadsBox.getItemCount() > 8)
//            while (threadsBox.getItemCount() != 8)
//                threadsBox.remove(8);
    }

    private void setMemoryFields(String format, String level, String method, int threads) {
        int memory = 0;
        int dememory = 0;
        if (format.equals(Format.zip.name())) {
            memory = (int) Math.round(33.3 * threads);
            dememory = 2;
        }
        if (format.equals(Format.z7.name())) {
            if (method.equals(Method.LZMA.name()))
                memory = 12 * cmdline.getDictionarySize(level) * threads;
            if (method.equals(Method.LZMA2.name()))
                memory = 11 * cmdline.getDictionarySize(level) * threads;
            dememory = cmdline.getDictionarySize(level) + 2;
        }
        memField.setHorizontalAlignment(4);
        memField.setText(String.valueOf(memory) + " MB");
        dememField.setHorizontalAlignment(4);
        dememField.setText(String.valueOf(dememory) + " MB");
    }

    private void setTarFields() {
        memField.setHorizontalAlignment(4);
        memField.setText(1 + " MB");
        dememField.setHorizontalAlignment(4);
        dememField.setText(1 + " MB");
    }

    void construct() {
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
