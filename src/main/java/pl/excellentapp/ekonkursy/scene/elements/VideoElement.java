package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
@Getter
public class VideoElement implements SceneElement {

    private final String videoFilePath;
    private final ElementPosition position;

    @Override
    public void render(Mat frame) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            grabber.setFormat("mp4");
            grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            grabber.start();

            Frame videoFrame = grabber.grab();
            if (videoFrame != null) {
                try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                    BufferedImage img = converter.getBufferedImage(videoFrame);
                    Mat videoMat = new Mat(img.getHeight(), img.getWidth(), opencv_core.CV_8UC3);
                    opencv_imgproc.resize(videoMat, videoMat, new Size(frame.cols(), frame.rows()));
                    videoMat.copyTo(frame);
                }
            }
            grabber.stop();
        } catch (Exception e) {
            System.err.println("Nie udało się załadować wideo: " + e.getMessage());
        }
    }
}
