package pl.excellentapp.ekonkursy.video;

import lombok.RequiredArgsConstructor;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import pl.excellentapp.ekonkursy.video.screens.Screen;

import java.util.List;

@RequiredArgsConstructor
public class VideoRecorder {

    private final FrameProcessor frameProcessor;

    public void recordVideo(String outputFile, List<Screen> screens, int width, int height, int frameRate) {
        FFmpegLogCallback.set();
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height)) {
            setupRecorder(recorder, frameRate);

            for (Screen screen : screens) {
                screen.record(recorder, frameProcessor);
            }
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

}
