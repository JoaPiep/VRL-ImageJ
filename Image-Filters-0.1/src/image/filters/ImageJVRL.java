/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import java.awt.Image;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL {

    public Image image;
    public PolygonRoi roi;

    public ImageJVRL(Image image) {
        this.image = image;
    }
    
    public ImageJVRL(PolygonRoi roi) {
        this.roi = roi;
    }
    

    public Image getImage() {
        return image;
    }
    
    public PolygonRoi getRoi(){
        return roi;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    public void setRoi(PolygonRoi roi) {
        this.roi = roi;
    }
}
