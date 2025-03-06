package pl.excellentapp.ekonkursy.video;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;

@RequiredArgsConstructor
public class VideoCreatorFacade {

    private final VideoRecorder videoRecorder;
    private final DirectoryCleaner imageDirectoryCleaner;


    public void createVideo(VideoProjectConfig videoProjectConfig) {
        if (videoProjectConfig.isValid()) {
            videoRecorder.recordVideo(videoProjectConfig.getOutputFile(), videoProjectConfig.getScreens(), videoProjectConfig.getWidth(), videoProjectConfig.getHeight(), videoProjectConfig.getFrameRate());
        }
        imageDirectoryCleaner.clean();
    }

}
