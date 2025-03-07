package pl.excellentapp.ekonkursy.scene.builder;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.scene.SceneConfig;

import java.awt.Color;
import java.util.List;

@RequiredArgsConstructor
public class HybridSceneBuilder implements SceneBuilderStrategy {

    private final ArticleFetcher articleFetcher;
    private final String directoryPath;

    @Override
    public SceneConfig buildScene() {
//        List<TextElement> textElements = articleFetcher.fetchData().stream()
//                .map(text -> new TextElement(text, new ElementPosition(50, 50 + text.hashCode() % 500)))
//                .toList();
//
//        File dir = new File(directoryPath);
//        List<ImageElement> imageElements = new ArrayList<>();
//        if (dir.exists() && dir.isDirectory()) {
//            File[] files = dir.listFiles((d, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
//            if (files != null) {
//                int yOffset = 50;
//                for (File file : files) {
//                    imageElements.add(new ImageElement(file.getAbsolutePath(), new ElementPosition(400, yOffset)));
//                    yOffset += 150;
//                }
//            }
//        }
//
//        List<Object> allElements = new ArrayList<>();
//        allElements.addAll(textElements);
//        allElements.addAll(imageElements);

        return new SceneConfig(1920, 1080, Color.DARK_GRAY, Color.WHITE, 10, List.of());
    }
}
