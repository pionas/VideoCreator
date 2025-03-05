package pl.excellentapp.ekonkursy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseCategory {

    @JsonProperty("category_name")
    private String categoryName;
    private int id;
    private String slug;
}