package pl.excellentapp.ekonkursy.models;

import lombok.Data;

import java.util.List;

@Data
public class ArticlesLastAdded {

    private List<Article> data;

    public boolean hasArticles() {
        return data != null && !data.isEmpty();
    }
}