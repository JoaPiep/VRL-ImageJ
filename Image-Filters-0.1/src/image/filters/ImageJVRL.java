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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
     * empty constructor
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
     * @return Roi decoded from the string
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

    /**
     *
     * @param roiList List of ROIs to set
     */
    public void setRoiList(ArrayList<PolygonRoi> roiList) {
        this.roiList = roiList;
    }

    /**
     *
     * @return list of ROIs
     */
    public ArrayList<PolygonRoi> getRoiList() {
        return roiList;
    }

    /**
     *
     * @return list of strings - encoded ROIs
     */
    public ArrayList<String> getRoiDataList() {
        return roiDataList;
    }

    /**
     *
     * @param roiDataList roiDataList to set
     */
    public void setRoiDataList(ArrayList<String> roiDataList) {
        this.roiDataList = roiDataList;
    }

    /**
     *
     * @param roiList list of ROIs to encode
     */
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

    /**
     *
     * @return list of decoded ROIs
     */
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

    /**
     *
     * @param roiFile file
     * @throws IOException
     */
    public void saveRoiInFile(File roiFile) throws IOException {

        if (getRoi() != null) {
            RoiEncoder re = new RoiEncoder(roiFile.getAbsolutePath());
            re.write(getRoi());
        }
    }

    /**
     *
     * @param roiFile file with encoded Roi
     * @return decoded polygon Roi
     * @throws IOException
     */
    public PolygonRoi getRoifromFile(File roiFile) throws IOException {

        RoiDecoder rd = new RoiDecoder(roiFile.getAbsolutePath());
        PolygonRoi pRoi = (PolygonRoi) rd.getRoi();
        setRoi(pRoi);

        if (roiList.contains(pRoi) == false) {
            roiList.add(pRoi);
        }

        return pRoi;
    }
    
    /**
     * 
     * @param roiFile save File
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void saveROIsInFile(File roiFile) throws FileNotFoundException, IOException {

        encodeROIList(getRoiList());
        ObjectOutputStream aus = new ObjectOutputStream(new FileOutputStream(roiFile));
        aus.writeObject(getRoiDataList());
    }
    
    /**
     * 
     * @param roiFile load file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public ArrayList<PolygonRoi> getROIsfromFile(File roiFile) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(roiFile));
        ArrayList<String> tempList = (ArrayList<String>) in.readObject();
        setRoiDataList(tempList);
       
        return decodeROIList();
    }

}
