package yuanren.tvsamrtwatch.smartwatchinteractions.models;

import java.io.Serializable;

public class XRayItem implements Serializable {
    private long movieId;
    private long itemId;
    private String type;
    private String name;
    private String imageUrl;
    private String description;
    private String merchandise;

    public XRayItem() {
    }

    public XRayItem(long movieId, long itemId, String type, String name, String imageUrl, String description, String merchandise) {
        this.movieId = movieId;
        this.itemId = itemId;
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.merchandise = merchandise;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchandise() {
        return merchandise;
    }

    public void setMerchandise(String merchandise) {
        this.merchandise = merchandise;
    }
}
