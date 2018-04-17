import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class SelectVideo extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField videoTextField;
    private JButton selectVideoButton;
    private JButton selectWorkingButton;
    private JTextField workingTextField;
    private JCheckBox cleanExistingFilesCheckBox;
    private File videoFile;
    private File workingDirectory;

    public SelectVideo() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

//        buttonCancel.addActionListener(e -> onCancel());

        selectVideoButton.addActionListener(e -> onVideoSelect());
        selectWorkingButton.addActionListener(e -> onWorkSelect());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        this.pack();
        this.setVisible(true);

        // call onCancel() on ESCAPE
        //contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onVideoSelect(){
        JFileChooser jFileChooser = new JFileChooser();
        int response = jFileChooser.showOpenDialog(SelectVideo.this);

        if(response == JFileChooser.APPROVE_OPTION){
            videoTextField.setText(jFileChooser.getSelectedFile().toString());
            videoFile = jFileChooser.getSelectedFile();
        } else {
            dispose();
        }

    }

    private void onWorkSelect(){
        JFileChooser jFileChooser = new JFileChooser();
        int response = jFileChooser.showOpenDialog(SelectVideo.this);

        if(response == JFileChooser.APPROVE_OPTION){
            workingTextField.setText(jFileChooser.getSelectedFile().toString());
            workingDirectory = jFileChooser.getSelectedFile();
        } else {
            dispose();
        }

    }

    private void onOK() {
        dispose();
    }

    public File getVideoFile() {
        return videoFile;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public static void main(String[] args) {
        SelectVideo dialog = new SelectVideo();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
