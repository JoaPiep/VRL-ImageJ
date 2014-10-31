/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import ij.gui.PolygonRoi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roiData;
    private transient Image image;
    private transient PolygonRoi roi;

    public ImageJVRL() {
    }

    /**
     *
     * @param image image to set
     */
    public ImageJVRL(Image image) {
        this.image = image;
    }

    /**
     *
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /**
     *
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return the roi
     */
    public PolygonRoi getRoi() {
        return roi;
    }

    /**
     * @param roi the roi to set
     */
    public void setRoi(PolygonRoi roi) {
        this.roi = roi;
    }

    /**
     *
     * @param roi roi to encode (as String)
     */
    public void encodeROI(PolygonRoi roi) {

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            RoiEncoder re = new RoiEncoder(bout);
            re.write(roi);
            String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
            setRoiData(byteToString);
            bout.close();

        } catch (IOException ex) {
            Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);

        }
    }

    /**
     *
     * @return Roi decoded from a string
     */
    public PolygonRoi decodeROI() {

        PolygonRoi result = null;

        try {
            byte[] stringToByte = Base64.decode(getRoiData());
            RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
            result = (PolygonRoi) rd.getRoi();
        } catch (IOException ex) {
            Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
        }

        return result;
    }

    /**
     *
     * @return encoded Roi (as String)
     */
    public String getRoiData() {
        return roiData;
    }

    /**
     *
     * @param roiData roiData to set
     */
    public void setRoiData(String roiData) {
        this.roiData = roiData;
    }

}
