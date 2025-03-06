package pl.excellentapp.ekonkursy.video.screens;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.video.FrameProcessor;

import java.io.File;

@RequiredArgsConstructor
public class MovieScreen implements Screen {

    private final String filePath;
    private final int targetWidth;
    private final int targetHeight;

    @Override
    public void record(FFmpegFrameRecorder recorder, FrameProcessor frameProcessor) {
        File welcomeFile = new File(filePath);
        if (!welcomeFile.exists()) {
            System.err.println("Plik powitalny nie istnieje: " + filePath);
            return;
        }
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();
            int originalWidth = grabber.getImageWidth();
            int originalHeight = grabber.getImageHeight();

            if (originalWidth == 0 || originalHeight == 0) {
                System.err.println("Nie można uzyskać rozmiaru obrazu.");
                return;
            }

            double aspectRatio = (double) originalWidth / originalHeight;
            int newWidth, newHeight;

            if (targetWidth / (double) targetHeight > aspectRatio) {
                newHeight = targetHeight;
                newWidth = (int) (targetHeight * aspectRatio);
            } else {
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / aspectRatio);
            }

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    Mat originalMat = frameProcessor.convert(frame);
                    Mat resizedMat = new Mat();
                    opencv_imgproc.resize(originalMat, resizedMat, new Size(newWidth, newHeight), 0, 0, opencv_imgproc.INTER_AREA);

                    Mat finalMat = new Mat(targetHeight, targetWidth, originalMat.type(), MovieConfig.BACKGROUND_COLOR_WHITE);
                    int xOffset = (targetWidth - newWidth) / 2;
                    int yOffset = (targetHeight - newHeight) / 2;
                    resizedMat.copyTo(finalMat.rowRange(yOffset, yOffset + newHeight).colRange(xOffset, xOffset + newWidth));
                    frameProcessor.recordFrame(recorder, finalMat, 1);
                } else {
                    frameProcessor.recordFrame(recorder, frame, 1);
                }
            }
            grabber.stop();
        } catch (Exception e) {
            System.err.println("Błąd przy dodawaniu WelcomeScreen: " + e.getMessage());
        }
    }
}
