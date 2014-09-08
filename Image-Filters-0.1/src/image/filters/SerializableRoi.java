/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.io.Base64;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joanna Pieper
 */
public class SerializableRoi {

    private transient String roiData;

    public SerializableRoi() {
    }

    public SerializableRoi(Roi roi) {

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            RoiEncoder re = new RoiEncoder(bout);
            re.write(roi);
            String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
            setRoiData(byteToString);
            System.out.println("roiEncoder " + byteToString);
        } catch (IOException ex) {
            Logger.getLogger(SerializableRoi.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public Roi roiDecoder() {
        // decoding from string

        Roi result = null;

        try {
            byte[] stringToByte = Base64.decode(getRoiData());
            RoiDecoder rd = new RoiDecoder(stringToByte, "rd");
            result = rd.getRoi();

        } catch (IOException ex) {
            Logger.getLogger(SerializableRoi.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    /**
     * @return the imageData
     */
    public String getRoiData() {
        return roiData;
    }

    /**
     * @param roiData the String data to be set
     */
    public void setRoiData(String roiData) {
        this.roiData = roiData;
    }

}
