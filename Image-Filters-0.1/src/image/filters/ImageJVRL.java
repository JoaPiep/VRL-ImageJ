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

    private transient Image image;
    private transient Roi roi;
    private SerializableRoi sRoi = new SerializableRoi();
  //  private transient String roiData = null;

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
        sRoi.roiEncoder(roi);
    }

    public void setsRoi(SerializableRoi sRoi) {
        this.sRoi = sRoi;
    }

    public SerializableRoi getsRoi() {
        return sRoi;
    }

    /*
     public String getRoiData() {
     return roiData;
     }

 
     public void setRoiData(String roiData) {
     this.roiData = roiData;
     }

     public void encodeROI() throws IOException {

     ByteArrayOutputStream bout = new ByteArrayOutputStream();
     RoiEncoder re = new RoiEncoder(bout);
     System.out.println("encodeROI getRoi() " +getRoi().toString());
     re.write((PolygonRoi) getRoi());
     String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
     setRoiData(byteToString);
     System.out.println("EncodeROI: " + getRoiData());
     }

     public Roi decodeROI() throws IOException {
     // System.out.println("decode getRoiData " + getRoiData());
     Roi result = null;

     if (getRoiData() != null){
     byte[] stringToByte = Base64.decode(getRoiData());
     RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
     result = rd.getRoi();
     System.out.println("result "+ result.toString());
     }
     System.out.println("decodeROI null");
     return result;
     }*/
}
