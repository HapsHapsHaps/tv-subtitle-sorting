import javax.swing.*;

public class Sorter {

    private JPanel topPanel;
    private JButton positiveButton;
    private JButton negativeButton;
    private JButton sameButton;
    private JLabel imageLabel;

    public Sorter() {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorter");
        frame.setContentPane(new Sorter().topPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
