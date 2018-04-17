import dk.kb.tvsubtitleocr.lib.frameextraction.FrameExtractionProcessor;
import dk.kb.tvsubtitleocr.lib.frameextraction.IFrameExtractionProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Sorter {

    // Local fields
    private Path videoFile;
    private Path workingDirectory;
    private File[] frameFiles;
    private Path framesPath;


    // Swing components
    private JPanel topPanel;
    private JButton positiveButton;
    private JButton negativeButton;
    private JButton sameButton;
    private JLabel imageLabel;
    private SelectVideo selectVideo;

    public Sorter() {
        EventQueue.invokeLater(this::run);
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Sorter");

        negativeButton.addActionListener(e -> nextFrame());

        sameButton.addActionListener(e -> nextFrame());

        positiveButton.addActionListener(e -> nextFrame());

        frame.setContentPane(this.topPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void onNegative() {

        nextFrame();

    }

    private void onSame() {

        nextFrame();
    }

    private void onPositive() {

        nextFrame();
    }

    private void nextFrame() {
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(frameFiles[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageLabel.setText("");
        imageLabel.setIcon(icon);
    }

    private void run() {
        createAndShow();
        Dialog dlg = new SelectVideo();

        try {
            workingDirectory = ((SelectVideo) dlg).getWorkingDirectory().toPath();
        } catch (NullPointerException e) {
            workingDirectory = Paths.get(System.getProperty("user.dir"));
            try {
                workingDirectory = Paths.get(workingDirectory.toFile().getAbsolutePath(), "working");

                if (!Files.exists(workingDirectory))
                    Files.createDirectory(workingDirectory);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            framesPath = Paths.get(workingDirectory.toFile().getAbsolutePath(), "frames");
        }

        try {
            videoFile = ((SelectVideo) dlg).getVideoFile().toPath();


            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    if (!Files.exists(framesPath)) {
                        Files.createDirectory(framesPath);
                    }

                    IFrameExtractionProcessor frameExtractor = new FrameExtractionProcessor(workingDirectory.toFile());
                    ((FrameExtractionProcessor) frameExtractor).extractVideoFramesToDisk(videoFile.toFile(), 1, framesPath.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).get();

        } catch (NullPointerException ignored) {
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        finally {
            File[] listOfFiles = framesPath.toFile().listFiles(f -> f.getName().endsWith(".png"));
            Arrays.sort(listOfFiles);
            frameFiles = listOfFiles;
        }

    }

}

