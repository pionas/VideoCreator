package pl.excellentapp.ekonkursy;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.models.Article;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VideoRecorder {

    public void recordVideo(List<Article> articles, String outputFile, int width, int height, int frameRate, Set<String> thankYouNames) {
        FFmpegLogCallback.set();

        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height)) {
            setupRecorder(recorder, frameRate);

            addWelcomeScreen(recorder, frameRate);
//            addWelcomeScreen(recorder, VideoConfig.WELCOME_FILE, width, height);

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

    private void addWelcomeScreen(FFmpegFrameRecorder recorder, String welcomeFilePath, int targetWidth, int targetHeight) {
        File welcomeFile = new File(welcomeFilePath);
        if (!welcomeFile.exists()) {
            System.err.println("Plik powitalny nie istnieje: " + welcomeFilePath);
            return;
        }
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(welcomeFilePath);
             OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {

            grabber.start();
            int originalWidth = grabber.getImageWidth();
            int originalHeight = grabber.getImageHeight();

            if (originalWidth == 0 || originalHeight == 0) {
                System.err.println("Nie można uzyskać rozmiaru obrazu.");
                return;
            }

            double aspectRatio = (double) originalWidth / originalHeight;
            int newWidth, newHeight;

            if (targetWidth / (double) targetHeight > aspectRatio) {
                newHeight = targetHeight;
                newWidth = (int) (targetHeight * aspectRatio);
            } else {
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / aspectRatio);
            }

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    Mat originalMat = converter.convert(frame);
                    Mat resizedMat = new Mat();
                    opencv_imgproc.resize(originalMat, resizedMat, new Size(newWidth, newHeight), 0, 0, opencv_imgproc.INTER_AREA);

                    Mat finalMat = new Mat(targetHeight, targetWidth, originalMat.type(), VideoConfig.BACKGROUND_COLOR_WHITE);
                    int xOffset = (targetWidth - newWidth) / 2;
                    int yOffset = (targetHeight - newHeight) / 2;
                    resizedMat.copyTo(finalMat.rowRange(yOffset, yOffset + newHeight).colRange(xOffset, xOffset + newWidth));

                    Frame resizedFrame = converter.convert(finalMat);
                    recorder.record(resizedFrame);
                } else {
                    recorder.record(frame);
                }
            }
            grabber.stop();
        } catch (Exception e) {
            System.err.println("Błąd przy dodawaniu WelcomeScreen: " + e.getMessage());
        }
    }

    private void recordFrames(FFmpegFrameRecorder recorder, List<Article> articles, int frameRate) throws IOException {
        try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage()) {
            Mat lastEffectImg = null;
            List<Mat> effectMats = new ArrayList<>();
            try (FFmpegFrameGrabber effectGrabber = new FFmpegFrameGrabber(VideoConfig.EFFECT_FILE)) {
                effectGrabber.start();
                Frame effectFrame;
                while ((effectFrame = effectGrabber.grabImage()) != null) {
                    lastEffectImg = converter.convertToMat(effectFrame).clone();
                    effectMats.add(lastEffectImg);
                }
                effectGrabber.stop();
            }

            if (lastEffectImg == null) {
                throw new IOException("Nie udało się odczytać efektu wideo.");
            }

            int effectWidth = lastEffectImg.cols();
            int effectHeight = lastEffectImg.rows();
            int topMargin = VideoConfig.MARGIN_TOP;
            int bottomMargin = VideoConfig.MARGIN_BOTTOM;
            int availableHeight = effectHeight - (topMargin + bottomMargin);
            int index = 0;
            for (Article article : articles) {
                Mat articleImg = opencv_imgcodecs.imread(article.getImageFile().getAbsolutePath());

                double scale = Math.min((double) effectWidth / articleImg.cols(), (double) availableHeight / articleImg.rows());
                int newWidth = (int) (articleImg.cols() * scale);
                int newHeight = (int) (articleImg.rows() * scale);

                Mat resizedArticleImg = new Mat();
                opencv_imgproc.resize(articleImg, resizedArticleImg, new Size(newWidth, newHeight));

                for (int j = 0; j < frameRate; j++) {
                    Mat combined;
                    if (effectMats.size() > index) {
                        combined = effectMats.get(index).clone();
                    } else {
                        combined = lastEffectImg.clone();
                    }

                    int xOffset = (effectWidth - newWidth) / 2;
                    int yOffset = topMargin + (availableHeight - newHeight) / 2;

                    resizedArticleImg.copyTo(combined.rowRange(yOffset, yOffset + newHeight).colRange(xOffset, xOffset + newWidth));

                    Frame finalFrame = converter.convert(combined);
                    recorder.record(finalFrame);
                    index++;
                }
            }
        }
    }

    private void addThankYouScreen(FFmpegFrameRecorder recorder, int frameRate, Set<String> thankYouNames) throws IOException {
        ThankYouScreenGenerator generator = new ThankYouScreenGenerator();
        File thankYouImage = generator.generateThankYouScreen(thankYouNames);
        if (!thankYouImage.exists()) {
            return;
        }
        try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
             FFmpegFrameGrabber subscribeGrabber = new FFmpegFrameGrabber(VideoConfig.SUBSCRIBE_FILE)) {
            subscribeGrabber.start();

            try (Mat thankYouMat = opencv_imgcodecs.imread(thankYouImage.getAbsolutePath())) {
                int thankYouWidth = thankYouMat.cols();
                int thankYouHeight = thankYouMat.rows();

                Frame subscribeFrame;
                Mat lastSubscribeFrame = null;
                List<Mat> subscribeFrames = new ArrayList<>();

                while ((subscribeFrame = subscribeGrabber.grabImage()) != null) {
                    lastSubscribeFrame = converter.convertToMat(subscribeFrame).clone();
                    subscribeFrames.add(lastSubscribeFrame);
                }
                subscribeGrabber.stop();

                if (lastSubscribeFrame == null) {
                    throw new IOException("Nie udało się odczytać filmu subscribe.mp4.");
                }

                double scale = (double) thankYouWidth / lastSubscribeFrame.cols();
                int subscribeHeight = (int) (lastSubscribeFrame.rows() * scale);

                int yOffset = thankYouHeight - subscribeHeight;

                int subscribeFrameIndex = 0;
                int totalFrames = frameRate * 3;

                for (int j = 0; j < totalFrames; j++) {
                    Mat combinedMat = thankYouMat.clone();

                    Mat subscribeMat;
                    if (!subscribeFrames.isEmpty()) {
                        subscribeMat = subscribeFrames.get(subscribeFrameIndex % subscribeFrames.size()).clone();
                    } else {
                        subscribeMat = lastSubscribeFrame.clone();
                    }

                    Mat resizedSubscribeMat = new Mat();
                    opencv_imgproc.resize(subscribeMat, resizedSubscribeMat, new Size(thankYouWidth, subscribeHeight));

                    resizedSubscribeMat.copyTo(combinedMat.rowRange(yOffset, yOffset + subscribeHeight).colRange(0, thankYouWidth));

                    Frame finalFrame = converter.convert(combinedMat);
                    recorder.record(finalFrame);

                    subscribeFrameIndex++;
                }
            }
        }
    }

    private void addWelcomeScreen(FFmpegFrameRecorder recorder, int frameRate) throws IOException {
        File logo = new File(VideoConfig.WELCOME_IMAGE_FILE);
        if (logo.exists()) {
            try (OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage()) {
                Mat img = opencv_imgcodecs.imread(logo.getAbsolutePath());

                int imgWidth = img.cols();
                int imgHeight = img.rows();

                int videoWidth = recorder.getImageWidth();
                int videoHeight = recorder.getImageHeight();

                double scale = Math.min((double) videoWidth / imgWidth, (double) videoHeight / imgHeight);
                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);

                Mat resizedImg = new Mat();
                opencv_imgproc.resize(img, resizedImg, new Size(newWidth, newHeight));

                Mat frameMat = new Mat(videoHeight, videoWidth, img.type(), new Scalar(255, 255, 255, 255));

                int xOffset = (videoWidth - newWidth) / 2;
                int yOffset = (videoHeight - newHeight) / 2;

                Mat roi = frameMat.apply(new Rect(xOffset, yOffset, newWidth, newHeight));
                resizedImg.copyTo(roi);

                Frame frame = converter.convert(frameMat);
                for (int j = 0; j < frameRate * 1.5; j++) {
                    recorder.record(frame);
                }
            }
        }
    }

}
