package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

public class ImageModel {
    @SerializedName("image_id")
    private int image_id;
    @SerializedName("data")
    private String data;

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
