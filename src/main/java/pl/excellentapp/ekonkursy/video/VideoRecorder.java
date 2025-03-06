package pl.excellentapp.ekonkursy.video;

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
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.ThankYouScreenGenerator;
import pl.excellentapp.ekonkursy.VideoConfig;
import pl.excellentapp.ekonkursy.article.models.Article;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VideoRecorder {

    private final FrameProcessor frameProcessor = new FrameProcessor();

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
                    frameProcessor.recordFrame(recorder, finalMat, 1);
                } else {
                    frameProcessor.recordFrame(recorder, frame, 1);
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
            int sideMargin = 10;
            int availableWidth = effectWidth - (2 * sideMargin);
            int availableHeight = effectHeight - (topMargin + bottomMargin);
            int index = 0;
            int delayFrames = 20;
            for (Article article : articles) {
                Mat articleImg = opencv_imgcodecs.imread(article.getImageFile().getAbsolutePath());

                double scale = Math.min((double) availableWidth / articleImg.cols(), (double) availableHeight / articleImg.rows());
                int newWidth = (int) (articleImg.cols() * scale);
                int newHeight = (int) (articleImg.rows() * scale);

                Mat resizedArticleImg = new Mat();
                opencv_imgproc.resize(articleImg, resizedArticleImg, new Size(newWidth, newHeight));

                for (int j = 0; j < delayFrames; j++) {
                    Mat effectOnlyFrame;
                    if (effectMats.size() > index) {
                        effectOnlyFrame = effectMats.get(index).clone();
                    } else {
                        effectOnlyFrame = lastEffectImg.clone();
                    }
                    frameProcessor.recordFrame(recorder, effectOnlyFrame, 1);
                    index++;
                }

                for (int j = 0; j < frameRate; j++) {
                    Mat combined;
                    if (effectMats.size() > index) {
                        combined = effectMats.get(index).clone();
                    } else {
                        combined = lastEffectImg.clone();
                    }

                    int xOffset = sideMargin + (availableWidth - newWidth) / 2;
                    int yOffset = topMargin + (availableHeight - newHeight) / 2;

                    resizedArticleImg.copyTo(combined.rowRange(yOffset, yOffset + newHeight).colRange(xOffset, xOffset + newWidth));

                    frameProcessor.recordFrame(recorder, combined, 1);
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

                    frameProcessor.recordFrame(recorder, combinedMat, 1);

                    subscribeFrameIndex++;
                }
            }
        }
    }

    private void addWelcomeScreen(FFmpegFrameRecorder recorder, int frameRate) throws IOException {
        File logo = new File(VideoConfig.WELCOME_IMAGE_FILE);
        if (logo.exists()) {
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

            String text = "Ostatnia szansa";
            int fontFace = opencv_imgproc.FONT_HERSHEY_TRIPLEX;
            double fontScale = 1.0;
            int thickness = 2;
            Scalar textColor = new Scalar(0, 0, 0, 255); // Czarny kolor

            // Pobranie rozmiaru tekstu - poprawiona wersja
            IntBuffer baseline = IntBuffer.allocate(1);
            Size textSize = opencv_imgproc.getTextSize(text, fontFace, fontScale, thickness, baseline);
            int textWidth = textSize.width();
            int textHeight = textSize.height();

            // Wyliczenie pozycji tekstu 50px poniżej logo
            int textX = xOffset + (newWidth - textWidth) / 2;
            int textY = yOffset + newHeight - 50 + textHeight;

            // Sprawdzenie czy tekst nie wychodzi poza obraz
            if (textY + textHeight > videoHeight) {
                textY = videoHeight - 10; // Zapobiega wychodzeniu poza ramkę
            }

            // Naniesienie tekstu na obraz
            opencv_imgproc.putText(frameMat, text, new Point(textX, textY), fontFace, fontScale, textColor);

            // Konwersja i zapisanie do wideo
            frameProcessor.recordFrame(recorder, frameMat, ((int) (frameRate * 1.5)));
        }
    }
}
