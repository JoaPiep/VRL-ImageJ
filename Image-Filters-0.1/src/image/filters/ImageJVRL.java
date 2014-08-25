/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import ij.gui.Roi;
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

    private transient Image image;
    private transient Roi roi;
    private String roiData;

   public ImageJVRL() {
   }
    
   /* public ImageJVRL() {
        // encoding in base64
       
       try {
             
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            RoiEncoder re = new RoiEncoder(bout);
            re.write(roi);
            System.out.println("33333333333333333333");
            String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
         //  System.out.println("wwwwwwwwwwwww " + byteToString);
            setRoiData(byteToString);

        } catch (IOException ex) {
            Logger.getLogger(ImageJVRL.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public Roi toRoi() {
        // decoding from string
        
        Roi result  = null;

        try {
            byte[] stringToByte = Base64.decode(getRoiData());
            RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
            result = rd.getRoi();

        } catch (IOException ex) {
            Logger.getLogger(ImageJVRL.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }*/
    
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
     /**
     * @return the roi
     */
    public String getRoiData() {
        return roiData;
    }

    /**
     * @param roiData
     */
    public void setRoiData(String roiData) {
        this.roiData = roiData;
    }

   public void encodeROI() throws IOException {
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        RoiEncoder re = new RoiEncoder(bout);
        re.write(roi);
        roiData = Base64.encodeBytes(bout.toByteArray()); // byte to string
        System.out.println("EncodeROI: "+ roiData);
    }

    public Roi decodeROI() throws IOException {
        System.out.println("decode roi RoiData " + roiData);
        byte[] stringToByte = Base64.decode(roiData);
        RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
        
        Roi result = rd.getRoi();
        System.out.println("DecodeROI "+ rd.getRoi());
        return result;
    }

}
