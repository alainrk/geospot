package mobile.system.geospot;

/**
 * Created by giulio on 06/06/16.
 * Classe per la lista in main
 */

public class PoiAdv {
    String name;
    String description;
    boolean poiImage;


    public PoiAdv(String name, String description, boolean poiImage) {
        this.name = name;
        this.description = description;
        this.poiImage = poiImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPoiImage() {
        return poiImage;
    }
}
