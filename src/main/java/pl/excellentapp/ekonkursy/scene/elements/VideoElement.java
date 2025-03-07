package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.awt.image.BufferedImage;
import java.io.File;

@Getter
public class VideoElement extends SceneElement implements AutoCloseable {

    private final File videoFilePath;
    private final boolean loop;
    private final boolean keepLastFrame;
    private FFmpegFrameGrabber grabber;
    private int totalFrames;
    private boolean initialized = false;

    public VideoElement(File videoFilePath, ElementPosition position, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame) {
        super(position, displayDuration, delay, fps);
        this.videoFilePath = videoFilePath;
        this.loop = loop;
        this.keepLastFrame = keepLastFrame;
        initGrabber();
    }

    VideoElement(File videoFilePath, ElementPosition position, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame, FFmpegFrameGrabber grabber) {
        super(position, displayDuration, delay, fps);
        this.videoFilePath = videoFilePath;
        this.loop = loop;
        this.keepLastFrame = keepLastFrame;
        this.grabber = grabber;
        this.totalFrames = grabber.getLengthInFrames();
        this.initialized = true;
    }


    @Override
    public void render(Mat frame, int currentFrame) {
        if (!initialized || currentFrame < frameStart) {
            return;
        }
        if (currentFrame > frameEnd && !loop && !keepLastFrame) {
            return;
        }
        int frameIndex = currentFrame - frameStart;
        if (frameIndex > totalFrames) {
            if (loop) {
                frameIndex %= totalFrames;
            } else if (!keepLastFrame) {
                return;
            } else {
                frameIndex = totalFrames - 1;
            }
        }

        try {
            grabber.setFrameNumber(frameIndex);
            Frame videoFrame = grabber.grabImage();
            if (videoFrame != null) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage img = converter.getBufferedImage(videoFrame);
                    Mat videoMat = new Mat(img.getHeight(), img.getWidth(), opencv_core.CV_8UC3);
                    opencv_imgproc.resize(videoMat, videoMat, new Size(frame.cols(), frame.rows()));
                    videoMat.copyTo(frame);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas odtwarzania klatki wideo: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.release();
            }
        } catch (Exception e) {
            System.err.println("Błąd przy zamykaniu grabbera: " + e.getMessage());
        }
    }

    private void initGrabber() {
        try {
            grabber = new FFmpegFrameGrabber(videoFilePath);
            grabber.setFormat("mp4");
            grabber.start();
            extracted();
        } catch (Exception e) {
            System.err.println("Nie udało się załadować wideo: " + e.getMessage());
            initialized = false;
        }
    }

    private void extracted() {
        totalFrames = grabber.getLengthInFrames();
        initialized = true;
    }
}