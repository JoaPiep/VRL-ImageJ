/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import ij.gui.PolygonRoi;
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
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class ImageJVRL implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roiData;
    private transient Image image;
    private transient PolygonRoi roi; // first element in the roi manager
    private transient ArrayList<PolygonRoi> autoGenerateRoiList;
    private Boolean generateRois;

    private transient RoiManager roiManager;
    private ArrayList<String> roiDataListRM;

    /**
     * empty constructor
     */
    public ImageJVRL() {
        autoGenerateRoiList = new ArrayList<PolygonRoi>();
        roiDataListRM = new ArrayList<String>();
        roiManager = new RoiManager(false);
    }

    /**
     *
     * @param image image to set
     */
    public ImageJVRL(Image image) {
        this.image = image;
        autoGenerateRoiList = new ArrayList<PolygonRoi>();
        roiDataListRM = new ArrayList<String>();
        roiManager = new RoiManager(false);

    }

    public ImageJVRL(Image image, PolygonRoi roi, RoiManager roiManager) {
        this.image = image;
        this.roi = roi;
        this.roiManager = roiManager;
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
     * @return return 'true' if the rois are always created
     */
    public Boolean isGenerateRois() {
        return generateRois;
    }

    /**
     *
     * @param generateRois if 'true' rois will be always automatically created
     */
    public void setGenerateRois(Boolean generateRois) {
        this.generateRois = generateRois;
    }

    /**
     *
     * @return List of Rois from the Roi Manager (encoded as a String)
     */
    public ArrayList<String> getRoiDataListRM() {
        return roiDataListRM;
    }

    /**
     *
     * @param roiDataListRM Stringlist to set (with encoded Rois from the Roi
     * Manager)
     */
    public void setRoiDataListRM(ArrayList<String> roiDataListRM) {
        this.roiDataListRM = roiDataListRM;
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
     * @param roiManager Roi Manager with Rois to encode
     */
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
     * @return Roi Manager with decoded ROIs
     */
    public RoiManager decodeROIListRM() {

        RoiManager rManager = new RoiManager(false);

        for (int i = 0; i < getRoiDataListRM().size(); i++) {
            try {
                byte[] stringToByte = Base64.decode(getRoiDataListRM().get(i));
                RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
                rManager.addRoi((PolygonRoi) rd.getRoi());
            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);
            }

        }
        return rManager;
    }

    /**
     *
     * @param roiFile file to save the encoded Rois
     * @param roiManager relevant Roi Manager
     * @throws FileNotFoundException
     * @throws IOException
     */
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

    /**
     *
     * @param roiFile file to save the encoded Rois
     * @param roiManager relevant Roi Manager
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveSelectedROIsInFileRM(File roiFile, RoiManager roiManager) throws FileNotFoundException, IOException {

        ArrayList<String> tempRoiList = new ArrayList();

        for (int i = 0; i < roiManager.getSelectedIndexes().length; i++) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                RoiEncoder re = new RoiEncoder(bout);
                re.write(roiManager.getRoi(roiManager.getSelectedIndexes()[i]));
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
     * @param roiFile File with Rois(as String)
     * @return Roi Manager with decoded Rois from the file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public RoiManager getROIsfromFileAsRM(File roiFile) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(roiFile));
        ArrayList<String> tempList = (ArrayList<String>) in.readObject();

        RoiManager rManager = new RoiManager();

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

    /**
     *
     * @param roiFile File with Rois(as String)
     * @return List with decoded Rois from the file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ArrayList<PolygonRoi> getROIsfromFileAsList(File roiFile) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(roiFile));
        ArrayList<String> tempList = (ArrayList<String>) in.readObject();
        ArrayList<PolygonRoi> roiList = new ArrayList();

        for (int i = 0; i < tempList.size(); i++) {
            try {
                byte[] stringToByte = Base64.decode(tempList.get(i));
                RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
                roiList.add((PolygonRoi) rd.getRoi());
            } catch (IOException ex) {
                Logger.getLogger(ImageJVRL.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace(System.err);
            }
        }
        return roiList;
    }

}
