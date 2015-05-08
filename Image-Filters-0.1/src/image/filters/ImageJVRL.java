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
import ij.plugin.frame.RoiManager;
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
 * @author Joanna Pieper <joanna.pieper1@gmail.com>
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roiData;
    private transient Image image;
    private transient PolygonRoi roi; // last element in the roi list
    private transient ArrayList<PolygonRoi> roiList;
    private transient ArrayList<PolygonRoi> autoGenerateRoiList;
    private ArrayList<String> roiDataList;

    private transient RoiManager roiManager = new RoiManager(false);
    private ArrayList<String> roiDataListRM;
    private Roi [] autoGenerateArray;

    /**
     * empty constructor
     */
    public ImageJVRL() {
        roiList = new ArrayList<PolygonRoi>();
        autoGenerateRoiList = new ArrayList<PolygonRoi>();
        roiDataList = new ArrayList<String>();
        roiDataListRM = new ArrayList<String>();
    }

    /**
     *
     * @param image image to set
     */
    public ImageJVRL(Image image) {
        this.image = image;
        roiList = new ArrayList<PolygonRoi>();
        autoGenerateRoiList = new ArrayList<PolygonRoi>();
        roiDataList = new ArrayList<String>();
        roiDataListRM = new ArrayList<String>();

    }

    /**
     *
     * @param imageJVRL ImageJVRL-Object to copy the attributes
     */
    public void copyAttributes(ImageJVRL imageJVRL) {
        if (imageJVRL.getImage() != null) {
            setImage(imageJVRL.getImage());
        }
        if (imageJVRL.getRoi() != null) {
            setRoi(imageJVRL.getRoi());
        }
        if (imageJVRL.getRoiData() != null) {
            setRoiData(imageJVRL.getRoiData());
        }
        if (imageJVRL.getRoiList() != null) {
            setRoiList(imageJVRL.getRoiList());
        }
        if (imageJVRL.getRoiDataList() != null) {
            setRoiDataList(imageJVRL.getRoiDataList());
        }

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
     * @return list of ROIs
     */
    public ArrayList<PolygonRoi> getRoiList() {
        return roiList;
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
     * @param autoGenerateRoiList to set
     */
    public void setAutoGenerateRoiList(ArrayList<PolygonRoi> autoGenerateRoiList) {
        this.autoGenerateRoiList = autoGenerateRoiList;
    }

    /**
     *
     * @return automatically generated list with ROIs
     */
    public ArrayList<PolygonRoi> getAutoGenerateRoiList() {
        return autoGenerateRoiList;
    }

    /**
     *
     * @return roi manager
     */
    public RoiManager getRoiManager() {
        return roiManager;
    }

    /**
     *
     * @param roiManager roi manager to set
     */
    public void setRoiManager(RoiManager roiManager) {
        this.roiManager = roiManager;
    }

    /**
     *
     * @param roi encode the given ROI
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
     * @param roiList encode the given ROI list
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
     * @param roiList list of ROIs to save
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveROIsInFile(File roiFile, ArrayList<PolygonRoi> roiList) throws FileNotFoundException, IOException {

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

        ObjectOutputStream aus = new ObjectOutputStream(new FileOutputStream(roiFile));
        aus.writeObject(tempRoiList);
    }

    /**
     *
     * @param roiFile load file
     * @return ROIs from the file
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

    //**************************************************************************
    public void encodeROIListRM(RoiManager roiManager) {

        ArrayList<String> tempRoiList = new ArrayList();

        for (int i = 0; i < roiManager.getCount(); i++) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                RoiEncoder re = new RoiEncoder(bout);
                re.write(roiManager.getRoi(i));
                String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
                tempRoiList.add(i, byteToString);
                bout.close();

            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);

            }
        }
        setRoiDataListRM(tempRoiList);
    }

    /**
     *
     * @return list of decoded ROIs
     */
    public RoiManager decodeROIListRM() {

        RoiManager rManager = new RoiManager(false);

        for (int i = 0; i < getRoiDataListRM().size(); i++) {
            try {
                byte[] stringToByte = Base64.decode(getRoiDataList().get(i));
                RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
                rManager.addRoi((PolygonRoi) rd.getRoi());
            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);
            }
        }
        return rManager;
    }

    public ArrayList<String> getRoiDataListRM() {
        return roiDataListRM;
    }

    public void setRoiDataListRM(ArrayList<String> roiDataListRM) {
        this.roiDataListRM = roiDataListRM;
    }

    public void saveROIsInFileRM(File roiFile, RoiManager roiManager) throws FileNotFoundException, IOException {

        ArrayList<String> tempRoiList = new ArrayList();

        for (int i = 0; i < roiManager.getCount(); i++) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                RoiEncoder re = new RoiEncoder(bout);
                re.write(roiManager.getRoi(i));
                String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
                tempRoiList.add(i, byteToString);
                bout.close();

            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);

            }
        }

        ObjectOutputStream aus = new ObjectOutputStream(new FileOutputStream(roiFile));
        aus.writeObject(tempRoiList);
    }
    public RoiManager getROIsfromFileRM(File roiFile) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(roiFile));
        ArrayList<String> tempList = (ArrayList<String>) in.readObject();
        
        RoiManager rManager = new RoiManager(false);

        for (int i = 0; i < tempList.size(); i++) {
            try {
                byte[] stringToByte = Base64.decode(tempList.get(i));
                RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
                rManager.addRoi((PolygonRoi) rd.getRoi());
            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);
            }
        }
        return rManager;
    }

    public Roi[] getAutoGenerateArray() {
        return autoGenerateArray;
    }

    public void setAutoGenerateArray(Roi[] autoGenerateArray) {
        this.autoGenerateArray = autoGenerateArray;
    }
}
