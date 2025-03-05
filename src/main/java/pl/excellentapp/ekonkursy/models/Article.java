package pl.excellentapp.ekonkursy.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static pl.excellentapp.ekonkursy.VideoCreatorFacade.IMAGE_DIRECTORY;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {

    private int id;
    private String title;
    private String city;
    private String description;
    @JsonProperty("user_accepted")
    private Object userAccepted;
    private String content;
    private int promo;
    private List<String> urls;
    private String terms;
    private String prices;
    private String slug;
    @JsonProperty("start_days")
    private int startDays;
    private String image;
    private String buybox;
    private String website;
    @JsonProperty("guest_name")
    private String guestName;
    @JsonProperty("prices_html")
    private String pricesHtml;
    @JsonProperty("end_days")
    private int endDays;
    @JsonProperty("date_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEnd;
    @JsonProperty("base_category_id")
    private int baseCategoryId;
    private List<TagsItem> tags;
    @JsonProperty("date_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String dateStart;
    @JsonProperty("image_path")
    private String imagePath;
    private List<PrizesItem> prizes;
    @JsonProperty("base_category")
    private BaseCategory baseCategory;
    private User user;
    @JsonProperty("content_html")
    private String contentHtml;
    private int homepage;
    private int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private String createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private String updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private String publishedAt;
    private File imageFile;

    public boolean hasImage() {
        return image != null && !image.isEmpty();
    }

    public String getImageUrl(String url) {
        return String.format("%s/uploads/articles/org%s/%s", url, imagePath, image);
    }

    public void addFile(File file) {
        this.imageFile = file;
    }

    public String getFileName() {
        return String.format("./%s/%d_%s", IMAGE_DIRECTORY, id, image);
    }

    public String getUserName() {
        if (user != null && user.getName() != null) {
            return user.getName();
        }
        if (guestName != null) {
            return guestName;
        }
        return "Gość";
    }
}