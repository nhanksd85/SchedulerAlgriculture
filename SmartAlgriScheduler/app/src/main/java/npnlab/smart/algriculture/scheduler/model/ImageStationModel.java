package npnlab.smart.algriculture.scheduler.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ImageStationModel {

    @SerializedName("images")
    private List<ImageModel> images;

    public void ImageStationModel(){
        setImages(new ArrayList<>());
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }
}
