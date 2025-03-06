package pl.excellentapp.ekonkursy.video.screens;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.video.FrameProcessor;

import java.io.File;

@RequiredArgsConstructor
public class ImageScreen implements Screen {

    private final File file;
    private final int frames;

    @Override
    public void record(FFmpegFrameRecorder recorder, FrameProcessor frameProcessor) {
        if (!file.exists()) {
            System.err.println("Plik powitalny nie istnieje: " + file);
            return;
        }
        Mat img = opencv_imgcodecs.imread(file.getAbsolutePath());
        frameProcessor.recordFrame(recorder, img, frames);
    }
}
