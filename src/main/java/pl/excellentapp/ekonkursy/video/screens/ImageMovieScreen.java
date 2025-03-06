package pl.excellentapp.ekonkursy.video.screens;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;
import pl.excellentapp.ekonkursy.video.FrameProcessor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ImageMovieScreen implements Screen {

    private final int targetWidth;
    private final int targetHeight;
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
            } catch (FrameGrabber.Exception e) {
                System.out.println("############ ERROR ##############" + e.getMessage());
            }
        }

        int index = 0;
        while (hasAnyVideoMoreFrame() || hasAnyImageMoreFrame(index)) {
            Mat outputFrame = new Mat(targetHeight, targetWidth, CvType.CV_8UC3, background);
            loopVideo(frameProcessor, grabbers, outputFrame);
            loopImage(index, outputFrame);
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
                overlayImage(outputFrame, imageMat, image.getPosition());
            }
        }
    }

    private void loopVideo(FrameProcessor frameProcessor, List<FFmpegFrameGrabber> grabbers, Mat outputFrame) {
        for (int i = 0; i < grabbers.size(); i++) {
            FFmpegFrameGrabber grabber = grabbers.get(i);
            try {
                Frame frame = grabber.grab();
                if (frame == null) {
                    if (videos.get(i).isLoop()) {
                        grabber.restart();
                        frame = grabber.grab();
                    }
                }
                Mat videoMat = frameProcessor.convert(frame);
                if (videoMat != null) {
                    overlayImage(outputFrame, videoMat, videos.get(i).getPosition());
                }
            } catch (FrameGrabber.Exception e) {
                System.out.println("############ ERROR ##############" + e.getMessage());
            }
        }
    }

    private boolean hasAnyImageMoreFrame(int index) {
        return images.stream().anyMatch(image -> image.isBetween(index));
    }

    private boolean hasAnyVideoMoreFrame() {
        return videos.stream().anyMatch(VideoConfig::isLoop);
    }

    private void overlayImage(Mat background, Mat foreground, Position position) {
        int width = foreground.cols();
        int height = foreground.rows();
        int positionTop = position.getTop() - (height / 2);
        int positionLeft = position.getLeft() - (width / 2);
        foreground.copyTo(background.apply(
                new Rect(positionLeft, positionTop, width, height)
        ));
    }

}
