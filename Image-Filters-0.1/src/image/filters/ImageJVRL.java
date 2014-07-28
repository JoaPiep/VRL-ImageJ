/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private Image image;
    private Image image1;
    private Roi roi;
    private OutputStream fos = null;
    private InputStream fis = null;

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

    public void imageToOutputStream(String filename) {

        try {
            fos = new FileOutputStream(filename);
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(image);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }

    }

    public void getImagefromOutputStream(String filename) {

        try {
            fis = new FileInputStream(filename);
            ObjectInputStream o = new ObjectInputStream(fis);
            image = (Image) o.readObject();
            
        } catch (IOException e) {
            System.err.println(e);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
    }
    
     public ImagePlus getImagePlus() {
        return new ImagePlus("image 1", image1);
    }

}
