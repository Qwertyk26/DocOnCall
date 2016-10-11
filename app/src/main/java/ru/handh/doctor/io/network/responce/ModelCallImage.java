package ru.handh.doctor.io.network.responce;

/**
 * Created by drybochkin on 08.10.2016.
 */

public class ModelCallImage extends ParentModel {
    public ModeImageData data;

    public class ModeImageData {
        private String id;
        private String image;
        private int idImage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getIdImage() {
            return idImage;
        }

        public void setIdImage(int idImage) {
            this.idImage = idImage;
        }
    }
}
