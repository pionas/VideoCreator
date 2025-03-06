package pl.excellentapp.ekonkursy.video;

class VideoCreator {

    public static void main(String[] args) {

        ApplicationConfig config = new ApplicationConfig();
        VideoCreatorFacade videoCreator = config.getVideoCreatorFacade();
        videoCreator.createVideo(config.getVideoProjectConfig());
    }
}
