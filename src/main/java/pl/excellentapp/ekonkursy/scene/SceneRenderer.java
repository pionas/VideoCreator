package pl.excellentapp.ekonkursy.scene;

import lombok.RequiredArgsConstructor;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.util.List;

@RequiredArgsConstructor
public class SceneRenderer {

    private final int fps;
    private final int width;
    private final int height;

    public void renderScenes(List<SceneConfig> scenes, String outputPath) {
        FFmpegLogCallback.set();
        int i = 0;
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, width, height)) {
            setupRecorder(recorder, fps);
            try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {
                for (SceneConfig scene : scenes) {
                    int totalFrames = scene.getDurationInSeconds() * fps;
                    List<SceneElement> elements = scene.getElements();

                    System.out.println("Renderowanie sceny: " + scene);

                    for (int frameNumber = 0; frameNumber < totalFrames; frameNumber++) {
                        Mat frame = new Mat(new Size(width, height), opencv_core.CV_8UC3);
                        Scalar bgColor = new Scalar(
                                scene.getBackgroundColor().getBlue(),
                                scene.getBackgroundColor().getGreen(),
                                scene.getBackgroundColor().getRed(),
                                0
                        );
                        opencv_imgproc.rectangle(
                                frame,
                                new Point(0, 0),
                                new Point(width, height),
                                bgColor
                        );
                        opencv_imgproc.rectangle(frame, new Point(0, 0), new Point(width, height), bgColor, opencv_imgproc.FILLED, opencv_imgproc.LINE_AA, 0);


                        for (SceneElement element : elements) {
                            element.render(scene.getMargin(), frame, frameNumber);
                            opencv_imgcodecs.imwrite("./tescik/" + i + ".png", frame);
                            i++;
                        }
                        Frame convertedFrame = converter.convert(frame);
                        recorder.record(convertedFrame);
                    }
                }
            }
            recorder.stop();
            System.out.println("Montaż zakończony: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
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
}
