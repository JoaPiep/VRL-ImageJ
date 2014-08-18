/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import eu.mihosoft.vrl.io.IOUtil;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Image image;
    private transient Roi roi;
    private String roiData;

    public ImageJVRL() {
    }
    
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
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        RoiEncoder re = new RoiEncoder(bout);
        re.write(roi);
        roiData = Base64.encodeBytes(bout.toByteArray()); // byte to string
        System.out.println("EncodeROI: "+roiData);
    }

    public Roi decodeROI() throws IOException {
      
        byte[] stringToByte = Base64.decode(roiData);
        RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
        
        Roi result = rd.getRoi();
        System.out.println("DecodeROI "+ rd.getRoi());
        return result;
    }

}
