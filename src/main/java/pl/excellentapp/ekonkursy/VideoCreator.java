package pl.excellentapp.ekonkursy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.image.ImageProcessor;

class VideoCreator {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FileDownloader fileDownloader = new FileDownloader();
        JsonDownloader jsonDownloader = new JsonDownloader(objectMapper);
        VideoRecorder videoRecorder = new VideoRecorder();
        ImageProcessor imageProcessor = new ImageProcessor();

        VideoCreatorFacade videoCreator = new VideoCreatorFacade(fileDownloader, jsonDownloader, imageProcessor, videoRecorder);
        videoCreator.createVideo();
    }
}
