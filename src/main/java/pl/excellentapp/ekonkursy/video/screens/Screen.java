package pl.excellentapp.ekonkursy.video.screens;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import pl.excellentapp.ekonkursy.video.FrameProcessor;

public interface Screen {

    void record(FFmpegFrameRecorder recorder, FrameProcessor frameProcessor) throws Exception;
}
