package pl.excellentapp.ekonkursy.article.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlePage {

    private List<Article> data;

    public boolean hasArticles() {
        return data != null && !data.isEmpty();
    }
}