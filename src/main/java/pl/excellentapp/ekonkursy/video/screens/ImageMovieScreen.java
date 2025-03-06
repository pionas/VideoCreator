package pl.excellentapp.ekonkursy.video.screens;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;
import pl.excellentapp.ekonkursy.video.FrameProcessor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ImageMovieScreen implements Screen {

    private final int targetWidth;
    private final int targetHeight;
    private final int maxFrames;
    private final Scalar background;

    @Builder.Default
    private final List<VideoConfig> videos = new ArrayList<>();
    @Builder.Default
    private final List<ImageConfig> images = new ArrayList<>();

    @Override
    public void record(FFmpegFrameRecorder recorder, FrameProcessor frameProcessor) {
        List<FFmpegFrameGrabber> grabbers = new ArrayList<>();
        for (VideoConfig video : videos) {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video.getFile());
            try {
                grabber.start();
                grabbers.add(grabber);
                Frame effectFrame;
                while ((effectFrame = grabber.grabImage()) != null) {
                    video.setLastEffect(effectFrame.clone());
                }
                grabber.stop();
                grabber.restart();
            } catch (FrameGrabber.Exception e) {
                System.out.println("############ ERROR ##############" + e.getMessage());
            }
        }

        int index = 0;
        while ((hasAnyVideoMoreFrame() || hasAnyImageMoreFrame(index)) && index < maxFrames) {
            Mat outputFrame = new Mat(targetHeight, targetWidth, CvType.CV_8UC3, background);
            loopVideoBeforeImage(index, frameProcessor, grabbers, outputFrame);
            loopImage(index, outputFrame);
            loopVideoAfterImage(index, frameProcessor, grabbers, outputFrame);
            frameProcessor.recordFrame(recorder, outputFrame, 1);
            index++;
        }

        for (FFmpegFrameGrabber grabber : grabbers) {
            try {
                grabber.stop();
                grabber.release();
            } catch (FrameGrabber.Exception e) {
                System.out.println("############ ERROR ##############" + e.getMessage());
            }
        }
    }

    private void loopImage(int index, Mat outputFrame) {
        for (ImageConfig image : images) {
            if (image.isBetween(index)) {
                Mat imageMat = opencv_imgcodecs.imread(image.getFile().getAbsolutePath());
                overlayImage(outputFrame, imageMat, image.getPosition(), image.getSize());
            }
        }
    }

    private void loopVideoBeforeImage(int index, FrameProcessor frameProcessor, List<FFmpegFrameGrabber> grabbers, Mat outputFrame) {
        for (int i = 0; i < grabbers.size(); i++) {
            if (videos.get(i).isDelay(index) || !videos.get(i).isBefore()) {
                continue;
            }
            overlayVideo(frameProcessor, grabbers, outputFrame, i);
        }
    }

    private void overlayVideo(FrameProcessor frameProcessor, List<FFmpegFrameGrabber> grabbers, Mat outputFrame, int i) {
        FFmpegFrameGrabber grabber = grabbers.get(i);
        try {
            Frame frame = grabber.grabImage();
            VideoConfig videoConfig = videos.get(i);
            if (frame == null && videoConfig.isLoop()) {
                grabber.restart();
                frame = grabber.grabImage();
            } else if (frame == null && videoConfig.hasEffect()) {
                frame = videoConfig.getLastEffect().clone();
            }
            if (frame != null && frame.image != null) {
                overlayImage(outputFrame, frameProcessor.convert(frame), videoConfig.getPosition(), videoConfig.getSize());
            }
        } catch (FrameGrabber.Exception e) {
            System.out.println("############ ERROR ##############" + e.getMessage());
        }
    }

    private void loopVideoAfterImage(int index, FrameProcessor frameProcessor, List<FFmpegFrameGrabber> grabbers, Mat outputFrame) {
        for (int i = 0; i < grabbers.size(); i++) {
            if (videos.get(i).isDelay(index) || videos.get(i).isBefore()) {
                continue;
            }
            overlayVideo(frameProcessor, grabbers, outputFrame, i);
        }
    }

    private boolean hasAnyImageMoreFrame(int index) {
        return images.stream().anyMatch(image -> image.isBetween(index));
    }

    private boolean hasAnyVideoMoreFrame() {
        return videos.stream().anyMatch(VideoConfig::isLoop);
    }

    private void overlayImage(Mat background, Mat foreground, Position position, ElementSize elementSize) {
        int width = foreground.cols();
        int height = foreground.rows();

        if (elementSize != null && (width > elementSize.getMaxWidth() || height > elementSize.getMaxHeight())) {
            double scaleX = (double) elementSize.getMaxWidth() / width;
            double scaleY = (double) elementSize.getMaxHeight() / height;
            double scale = Math.min(scaleX, scaleY);

            Size newSize = new Size((int) (width * scale), (int) (height * scale));
            Mat resizedForeground = new Mat();
            opencv_imgproc.resize(foreground, resizedForeground, newSize);
            foreground = resizedForeground;
            width = foreground.cols();
            height = foreground.rows();
        }

        int positionTop = position.getTop() - (height / 2);
        int positionLeft = position.getLeft() - (width / 2);

        if (positionLeft < 0 || positionTop < 0 || positionLeft + width > background.cols() || positionTop + height > background.rows()) {
            throw new IllegalArgumentException("Pozycja wykracza poza granice obrazu tła.");
        }

        foreground.clone().copyTo(background.apply(
                new Rect(positionLeft, positionTop, width, height)
        ));
    }

}
