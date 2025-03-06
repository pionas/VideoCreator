package pl.excellentapp.ekonkursy.article.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrizesItem {

    private String image;
    @JsonProperty("file_path")
    private String filePath;
    private String name;
    private int id;
}