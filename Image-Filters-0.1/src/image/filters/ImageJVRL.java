/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.process.FloatPolygon;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joanna Pieper
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roiData;
    private ArrayList<String> roiDataList = new ArrayList();
    private transient Image image;
    private transient PolygonRoi roi; // is last elemnt in the roi list
    private transient ArrayList<PolygonRoi> roiList;

    /**
     *
     */
    public ImageJVRL() {

    }

    /**
     *
     * @param image image to set
     */
    public ImageJVRL(Image image) {
        this.image = image;
        roiList = new ArrayList();
    }

    public void addElemInRoiList(PolygonRoi roi) {
        roiList.add(roi);
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

    public void setRoiList(ArrayList<PolygonRoi> roiList) {
        this.roiList = roiList;
    }

    public ArrayList<PolygonRoi> getRoiList() {
        return roiList;
    }

    public void removeLastElemFromRoiList() {
        roiList.remove(roiList.size() - 1);
    }

    public void removeFirstElem() {
        roiList.remove(0);
    }

    public ArrayList<String> getRoiDataList() {
        return roiDataList;
    }

    public void setRoiDataList(ArrayList<String> roiDataList) {
        this.roiDataList = roiDataList;
    }

    public void encodeROIList(ArrayList<PolygonRoi> roiList) {

        ArrayList<String> tempRoiList = new ArrayList();

        for (int i = 0; i < roiList.size(); i++) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                RoiEncoder re = new RoiEncoder(bout);
                re.write(roiList.get(i));
                String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
                tempRoiList.add(i, byteToString);
                bout.close();

            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);

            }
        }
        setRoiDataList(tempRoiList);
    }

    public ArrayList<PolygonRoi> decodeROIList() {

        ArrayList<PolygonRoi> result = new ArrayList();
        for (int i = 0; i < getRoiDataList().size(); i++) {
            try {
                byte[] stringToByte = Base64.decode(getRoiDataList().get(i));
                RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
                result.add(i, (PolygonRoi) rd.getRoi());
            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);
            }
        }
        return result;
    }
}
