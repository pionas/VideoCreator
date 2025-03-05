package pl.excellentapp.ekonkursy;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.models.Article;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class VideoRecorder {

    public void recordVideo(List<Article> articles, String outputFile, int width, int height, int frameRate, Set<String> thankYouNames) {
        FFmpegLogCallback.set();

        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height)) {
            setupRecorder(recorder, frameRate);
            recordFrames(recorder, articles, frameRate);

            addThankYouScreen(recorder, frameRate, thankYouNames);
            System.out.println("Film zapisany jako " + outputFile);
        } catch (Exception e) {
            System.err.println("Błąd podczas zapisu wideo: " + e.getMessage());
        }
    }

    private void setupRecorder(FFmpegFrameRecorder recorder, int frameRate) throws org.bytedeco.javacv.FrameRecorder.Exception {
        recorder.setFrameRate(frameRate);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setVideoBitrate(4000000);

        recorder.setVideoOption("level", "3.1");

        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("tune", "zerolatency");

        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

        recorder.start();
    }

    private void recordFrames(FFmpegFrameRecorder recorder, List<Article> articles, int frameRate) throws IOException {
        try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage()) {
            for (Article article : articles) {
                Mat img = opencv_imgcodecs.imread(article.getImageFile().getAbsolutePath());
                Frame frame = converter.convert(img);
                for (int j = 0; j < frameRate; j++) {
                    recorder.record(frame);
                }
            }
        }
    }

    private void addThankYouScreen(FFmpegFrameRecorder recorder, int frameRate, Set<String> thankYouNames) throws IOException {
        ThankYouScreenGenerator generator = new ThankYouScreenGenerator();
        File thankYouImage = generator.generateThankYouScreen(thankYouNames);
        if (thankYouImage.exists()) {
            try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage()) {
                Mat img = opencv_imgcodecs.imread(thankYouImage.getAbsolutePath());
                Frame frame = converter.convert(img);
                for (int j = 0; j < frameRate * 3; j++) {
                    recorder.record(frame);
                }
            }
        }
    }
}
