/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import java.awt.Image;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL {

    public Image image;
    

    public ImageJVRL(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}