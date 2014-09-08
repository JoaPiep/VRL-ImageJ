/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import ij.gui.Roi;
import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable{

    private static final long serialVersionUID = 1L;

    private transient Image image;
    private transient Roi roi;

    private transient SerializableRoi serializableRoi;

    public ImageJVRL() {
    }

    /**
     * 
     * @param image image to set
     */
    public ImageJVRL(Image image) {
        this.image = image;
    }

    public ImageJVRL(Image image, Roi roi) throws IOException {
        
        this.image = image;
        
        if (roi != null) {

            this.serializableRoi = new SerializableRoi(roi);
            
        }
    }
    /**
     * 
     * @param roi to encode with SerializableRoi
     */
    public void setSRoi(Roi roi){
        this.serializableRoi = new SerializableRoi(roi);
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
     * 
     * @param serializableRoi the serializable roi to set
     */
    public void setSerializableRoi(SerializableRoi serializableRoi) {
        this.serializableRoi = serializableRoi;
    }

    /**
     * 
     * @return the serializable roi
     */
    public SerializableRoi getSerializableRoi() {
        return serializableRoi;
    }

    
    /*public void encodeROI() throws IOException {

     ByteArrayOutputStream bout = new ByteArrayOutputStream();
     RoiEncoder re = new RoiEncoder(bout);
     System.out.println("encodeROI getRoi() " + getRoi().toString());
     re.write((PolygonRoi) getRoi());
     String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
     setRoiData(byteToString);
     System.out.println("EncodeROI: " + getRoiData());
     }

     public Roi decodeROI() throws IOException {
        
     Roi result = null;

     if (getRoiData() != null) {
     byte[] stringToByte = Base64.decode(getRoiData());
     RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
     result = rd.getRoi();
     System.out.println("result " + result.toString());
     }
     System.out.println("decodeROI null");
     return result;
     }*/

}
