/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private Image image;
    private Roi roi;
    private File file = new File(System.getProperty("user.dir") + "/roi");

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

    public void encodeROI() throws IOException {
        RoiEncoder re = new RoiEncoder(file.getAbsolutePath());
        re.write(roi);
    }

    public Roi decodeROI() throws IOException {
        RoiDecoder rd = new RoiDecoder(file.getAbsolutePath());
        return rd.getRoi();
    }

}
