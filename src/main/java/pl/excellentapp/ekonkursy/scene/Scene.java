package pl.excellentapp.ekonkursy.scene;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import pl.excellentapp.ekonkursy.FrameProcessor;

public interface Scene {

    void record(FFmpegFrameRecorder recorder, FrameProcessor frameProcessor) throws Exception;
}
