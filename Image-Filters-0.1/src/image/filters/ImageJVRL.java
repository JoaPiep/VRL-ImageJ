/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;



import ij.gui.Roi;
import java.awt.Image;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private Image image;
    private Roi roi;
    
    public ImageJVRL(Image image) {
        this.image = image;
    }
 
    
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return the roi
     */
    public Roi getRoi() {
        return roi;
    }

    /**
     * @param roi the roi to set
     */
    public void setRoi(Roi roi) {
        this.roi = roi;
    }
   
}
