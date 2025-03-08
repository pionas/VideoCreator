package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.core.ProjectProperties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class ArticleApiUrlProvider {

    private static final String BASE_URL = ProjectProperties.EKONKURSY_API_URL;

    public String getLastAddedUrl() {
        return BASE_URL + "/api/articles/lastAdded";
    }

    public String getTopUrl(String type) {
        return BASE_URL + "/api/statistics/article/top?type=" + type;
    }

    public String getEndUrl() {
        return BASE_URL + "/api/statistics/article/end-today";
    }

    public String getSearchUrl(String phrase) {
        String encodedPhrase = URLEncoder.encode(phrase, StandardCharsets.UTF_8);
        return BASE_URL + String.format(
                "/api/contestSearch?item=%s&categories[0]=1&categories[1]=8&categories[2]=9&categories[3]=7"
                        + "&categories[4]=6&categories[5]=5&categories[6]=3&categories[7]=13&categories[8]=27"
                        + "&categories[9]=11&categories[10]=14&categories[11]=10&categories[12]=12&categories[13]=15&limit=100",
                encodedPhrase
        );
    }
}
