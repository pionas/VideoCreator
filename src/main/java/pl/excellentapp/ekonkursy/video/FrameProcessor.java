package pl.excellentapp.ekonkursy.video;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

public class FrameProcessor {

    private final OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    public void recordFrame(FFmpegFrameRecorder recorder, Mat mat, int frames) {
        try {
            Frame frame = converter.convert(mat);
            for (int i = 0; i < frames; i++) {
                recorder.record(frame);
            }
        } catch (Exception e) {
            System.err.println("Błąd nagrywania klatki: " + e.getMessage());
        }
    }

    public void recordFrame(FFmpegFrameRecorder recorder, Frame frame, int frames) {
        try {
            for (int i = 0; i < frames; i++) {
                recorder.record(frame);
            }
        } catch (Exception e) {
            System.err.println("Błąd nagrywania klatki: " + e.getMessage());
        }
    }

    public Mat convert(Frame frame) {
        return converter.convert(frame);
    }
}
