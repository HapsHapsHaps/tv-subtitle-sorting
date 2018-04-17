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
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Sorter {

    // Local fields
    private Path videoFile;
    private Path workingDirectory;
    private Path positivePath;
    private Path negativePath;
    private File[] frameFiles;
    private Path framesPath;
    private int frameFilesIdx = -1; // Counted one up on next frame.
    private Stack<Path> lastPath;


    // Swing components
    private JPanel topPanel;
    private JButton positiveButton;
    private JButton negativeButton;
    private JButton changeDecisionButton;
    private JLabel imageLabel;
    private SelectVideo selectVideo;

    public Sorter() {
        EventQueue.invokeLater(this::run);
        lastPath = new Stack<>();
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Sorter");

        negativeButton.addActionListener(e -> onNegative());

        changeDecisionButton.addActionListener(e -> onDecisionChange());

        positiveButton.addActionListener(e -> onPositive());

        frame.setContentPane(this.topPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void onPositive() {
        writeFile(positivePath);
        nextFrame();
    }

    private void onNegative() {
        writeFile(negativePath);
        nextFrame();

    }

    private void onDecisionChange() {
        try {
            Files.delete(lastPath.pop());
        } catch (IOException e) {
            e.printStackTrace();
        }
        frameFilesIdx--;
        frameFilesIdx--;
        nextFrame();
    }

    private void writeFile(Path path) {
        try {
            lastPath.push(Paths.get(path.toFile().getAbsolutePath(), frameFiles[frameFilesIdx].getName()));
            ImageIO.write(ImageIO.read(frameFiles[frameFilesIdx].getAbsoluteFile()), "png", lastPath.peek().toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextFrame() {
        frameFilesIdx++;
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(frameFiles[frameFilesIdx]));
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
            positivePath = Paths.get(workingDirectory.toFile().getAbsolutePath(), "positive");
            negativePath = Paths.get(workingDirectory.toFile().getAbsolutePath(), "negative");

            try {
                if (!Files.exists(framesPath))
                    Files.createDirectory(framesPath);

                if (!Files.exists(positivePath))
                    Files.createDirectory(positivePath);

                if (!Files.exists(negativePath))
                    Files.createDirectory(negativePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            videoFile = ((SelectVideo) dlg).getVideoFile().toPath();


            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    IFrameExtractionProcessor frameExtractor = new FrameExtractionProcessor(workingDirectory.toFile());
                    ((FrameExtractionProcessor) frameExtractor).extractVideoFramesToDisk(videoFile.toFile(), 1, framesPath.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).get();

        } catch (NullPointerException ignored) {
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            File[] listOfFiles = framesPath.toFile().listFiles(f -> f.getName().endsWith(".png"));
            Arrays.sort(listOfFiles);
            frameFiles = listOfFiles;
            nextFrame();
        }

    }

}

