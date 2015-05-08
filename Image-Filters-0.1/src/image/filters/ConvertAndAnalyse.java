/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.Base64;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Joanna Pieper <joanna.pieper1@gmail.com>
 */
@ComponentInfo(name = "Analyse Tools",
        category = "ImageJ-VRL",
        description = "Tools to analyse an image. See: ImageJ API -> ParticleAnalyser. ")
public class ConvertAndAnalyse implements Serializable {

    public ConvertAndAnalyse() {
    }

    /**
     *
     * @param image Image image
     * @return ImageJVRL ijVRL
     */
    public ImageJVRL imageToImageJVRL(Image image) {

        return new ImageJVRL(image);
    }

    /**
     *
     * @param imageJVRL imageJVRL
     * @return Image image
     */
    public Image imageJVRLToImage(@ParamInfo(name = "ImageJVRL") ImageJVRL imageJVRL) {

        return imageJVRL.getImage();
    }

    /**
     *
     * @param image image to convert
     * @return in gray32 converted image
     */
    public ImageJVRL convertImgToGray32(@ParamInfo(name = "ImageJVRL", options = "saveRoi=false") ImageJVRL image) {

        ImagePlus imagePlus = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imagePlus);
        ic.convertToGray32();

        Image im = imagePlus.getBufferedImage();

        return new ImageJVRL(im);
    }

    /**
     *
     * @param image image to analyse
     * @param minSize minimal size of the area (pixel)
     * @param maxSize maximal size of the area (pixel)
     * @param minCirc minimal size of the circle
     * @param maxCirc maximal size of the circle
     * @param includeHolesBool If true, interior holes will be included.
     * @param excludeEdgeParticlesBool If true, particles touching the edge of
     * the image (or selection) will be ignored.
     * @param clearWorksheetBool If true, any previous measurements listed in
     * the Results Table↑ will be cleared
     * @param showResultsBool If true, the measurements for each particle will
     * be displayed in the Log-shell from VRL-Studio
     * @param showSummaryBool If true, the particle count, total particle area,
     * average particle size, area fraction and the mean of all parameters
     * listed in the Set Measurements dialog box will be displayed in a separate
     * Summary table.
     * @param addToManagerBool If true, the measured particles will be added to
     * the ROI Manager
     * @param recordStartsBool This option allows plugins and macros to recreate
     * particle outlines using the doWand(x,y) macro function.
     * @param show choosen option
     * @param inSituShowBool If checked, the original image will be replaced by
     * the binary mask specified in the Show drop-down menu. Note that this
     * option does not apply to Overlay Outlines and Overlay Masks that are
     * always displayed as non-destructive image Overlays on the measured image.
     * @return edited or original image
     */
    public ImageJVRL imageAnalyse(@ParamInfo(name = "ImageJVRL", options = "saveRoi=true") ImageJVRL image,
            @ParamInfo(name = "Min size   ") double minSize,
            @ParamInfo(name = "Max size   ") double maxSize,
            @ParamInfo(name = "Min circle ") double minCirc,
            @ParamInfo(name = "Max circle ") double maxCirc,
            @ParamInfo(name = "Show: ", style = "selection", options = "value=[\"Nothing\", \"Outlines\", \"Four connected\","
                    + " \"Masks\", \"Rois Masks\", \"Overlay Outlines\", \"Overlay Masks\"]") String show,
            @ParamGroupInfo(group = "Options|false|no description") @ParamInfo(name = "Display results") boolean showResultsBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Clear results") boolean clearWorksheetBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Summarize") boolean showSummaryBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Add to manager") boolean addToManagerBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Exclude on edges") boolean excludeEdgeParticlesBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Incluce holes") boolean includeHolesBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Record starts") boolean recordStartsBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "In situ show") boolean inSituShowBool) {

        if (maxSize == 0.0) {
            maxSize = Double.MAX_VALUE;
        }

        final ImagePlus imp = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray32();

        IJ.setAutoThreshold(imp, "Default"); //TODO choose method with "selection"

        int showNothing = ParticleAnalyzer.SHOW_NONE;
        int showOutlines = ParticleAnalyzer.SHOW_OUTLINES;
        int fourConnected = ParticleAnalyzer.FOUR_CONNECTED;
        int showMasks = ParticleAnalyzer.SHOW_MASKS;
        int showRoiMasks = ParticleAnalyzer.SHOW_ROI_MASKS;
        int showOverlayOutlines = ParticleAnalyzer.SHOW_OVERLAY_OUTLINES;
        int showOverlayMasks = ParticleAnalyzer.SHOW_OVERLAY_MASKS;

        int showResults = ParticleAnalyzer.SHOW_RESULTS;
        int clearWorksheet = ParticleAnalyzer.CLEAR_WORKSHEET;
        int showSummary = ParticleAnalyzer.SHOW_SUMMARY;
        int addToManager = ParticleAnalyzer.ADD_TO_MANAGER;
        int excludeEdgeParticles = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
        int includeHoles = ParticleAnalyzer.INCLUDE_HOLES;
        int recordStarts = ParticleAnalyzer.RECORD_STARTS;
        int inSituShow = ParticleAnalyzer.IN_SITU_SHOW;

        int options = 0;

        if (show.equals("Nothing")) {
            options = options + showNothing;
        } else if (show.equals("Outlines")) {
            options = options + showOutlines;
        } else if (show.equals("Four connected")) {
            options = options + fourConnected;
        } else if (show.equals("Masks")) {
            options = options + showMasks;
        } else if (show.equals("Rois Masks")) {
            options = options + showRoiMasks;
        } else if (show.equals("Overlay Outlines")) {
            options = options + showOverlayOutlines;
        } else if (show.equals("Overlay Masks")) {
            options = options + showOverlayMasks;
        }

        if (showResultsBool) {
            options = options + showResults;
        }
        if (clearWorksheetBool) {
            options = options + clearWorksheet;
        }
        if (showSummaryBool) {
            options = options + showSummary;
        }
        if (addToManagerBool) {
            options = options + addToManager;
        }
        if (excludeEdgeParticlesBool) {
            options = options + excludeEdgeParticles;
        }
        if (includeHolesBool) {
            options = options + includeHoles;
        }
        if (recordStartsBool) {
            options = options + recordStarts;
        }
        if (inSituShowBool) {
            options = options + inSituShow;
        }

        int measurements = 0;

        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, new ResultsTable(), minSize, maxSize, minCirc, maxCirc);

        if (!addToManagerBool) {
            pa.setHideOutputImage(true);
        }

        pa.analyze(imp);

        if (addToManagerBool) {
            final RoiManager manager = RoiManager.getInstance();

            if (pa.getOutputImage() != null) {
                final ImagePlus outputImage = pa.getOutputImage();

                manager.setVisible(true);
                manager.setLocation(0, 0);
                manager.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent ew) {
                        outputImage.close();
                    }
                });

                outputImage.getWindow().addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent ew) {
                        manager.close();
                    }
                });
            } else {
                manager.setVisible(false);
            }
        }

        if (pa.getOutputImage() != null) {
            return new ImageJVRL(pa.getOutputImage().getImage());
        } else {
            return new ImageJVRL(imp.getImage());
        }
    }

    /**
     *
     * @param imgFile file
     * @param image image to analyse and find ROIs
     * @param minSize minimal size of the area (pixel)
     * @param maxSize maximal size of the area (pixel)
     * @param minCirc minimal size of the circle
     * @param maxCirc maximal size of the circle
     * @param includeHolesBool If true, interior holes will be included.
     * @param excludeEdgeParticlesBool If true, particles touching the edge of
     * the image (or selection) will be ignored.
     * @return image with ROIs
     */
    public Image autoGenerateROIsToFile(
            @ParamInfo(name = "", style = "save-dialog", options = "endings=[\"txt\"]; description=\"Textfile\"") File imgFile,
            @ParamInfo(name = "ImageJVRL") ImageJVRL image,
            @ParamInfo(name = "Min size   ") double minSize,
            @ParamInfo(name = "Max size   ") double maxSize,
            @ParamInfo(name = "Min circle ") double minCirc,
            @ParamInfo(name = "Max circle ") double maxCirc,
            @ParamGroupInfo(group = "Options|true|no description") @ParamInfo(name = "Exclude on edges") boolean excludeEdgeParticlesBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Incluce holes") boolean includeHolesBool) throws IOException {

        if (maxSize == 0.0) {
            maxSize = Double.MAX_VALUE;
        }
        ImagePlus imp = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray32();

        IJ.setAutoThreshold(imp, "Default"); //TODO choose method with "selection"

        RoiManager manager = new RoiManager(true);
        ParticleAnalyzer.setRoiManager(manager);

        int includeHoles = ParticleAnalyzer.INCLUDE_HOLES;
        int excludeEdgeParticles = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;

        int options = 0;
        int measurements = 0;

        if (includeHolesBool) {
            options = options + includeHoles;
        }

        if (excludeEdgeParticlesBool) {
            options = options + excludeEdgeParticles;
        }

        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, new ResultsTable(), minSize, maxSize, minCirc, maxCirc);
        pa.analyze(imp);

        Roi[] rois = manager.getRoisAsArray();
        ArrayList<PolygonRoi> roiList = new ArrayList<PolygonRoi>();

        for (Roi roi : rois) {
            roiList.add((PolygonRoi) roi);
        }

        ImageJVRL imageJVRL = new ImageJVRL(image.getImage());
        ImagePlus ip = new ImagePlus("", image.getImage());
        if (!roiList.isEmpty()) {
            imageJVRL.setRoi(roiList.get(roiList.size() - 1));
            imageJVRL.setRoiList(roiList);
            saveRoisInFile(imgFile, imageJVRL);

            ImageProcessor imProcessor = ip.getProcessor();

            for (int i = 0; i < roiList.size(); i++) {
                imProcessor.setColor(Color.red);
                roiList.get(i).setStrokeWidth(1);
                roiList.get(i).drawPixels(imProcessor);

            }
        }
        return ip.getImage();
    }

    /**
     *
     * @param imgFile file to save the ROIs
     * @param image ImageJVRL with ROIs
     * @throws IOException
     */
    private void saveRoisInFile(@ParamInfo(name = "", style = "save-dialog", options = "endings=[\"txt\"]; description=\"Textfile\"") File imgFile,
            @ParamInfo(name = "ImageJVRL") ImageJVRL image) throws IOException {

        ArrayList<String> tempRoiList = new ArrayList();

        if (image.getRoiList() != null) {
            for (int i = 0; i < image.getRoiList().size(); i++) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                RoiEncoder re = new RoiEncoder(bout);
                re.write(image.getRoiList().get(i));
                String byteToString = Base64.encodeBytes(bout.toByteArray()); // byte to string
                tempRoiList.add(i, byteToString);
                bout.close();
            }

            ObjectOutputStream aus = new ObjectOutputStream(new FileOutputStream(imgFile));
            aus.writeObject(tempRoiList);
        }

    }

    /**
     *
     * @param image image to analyse and find ROIs
     * @param minSize minimal size of the area (pixel)
     * @param maxSize maximal size of the area (pixel)
     * @param minCirc minimal size of the circle
     * @param maxCirc maximal size of the circle
     * @param includeHolesBool If true, interior holes will be included.
     * @param excludeEdgeParticlesBool If true, particles touching the edge of
     * the image (or selection) will be ignored.
     * @return image with ROIs
     */
    public ImageJVRL autoGenerateROIs(
            @ParamInfo(name = "ImageJVRL") ImageJVRL image,
            @ParamInfo(name = "Min size   ") double minSize,
            @ParamInfo(name = "Max size   ") double maxSize,
            @ParamInfo(name = "Min circle ") double minCirc,
            @ParamInfo(name = "Max circle ") double maxCirc,
            @ParamGroupInfo(group = "Options|true|no description") @ParamInfo(name = "Exclude on edges") boolean excludeEdgeParticlesBool,
            @ParamGroupInfo(group = "Options") @ParamInfo(name = "Incluce holes") boolean includeHolesBool) {

        if (maxSize == 0.0) {
            maxSize = Double.MAX_VALUE;
        }
        ImagePlus imp = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray32();

        IJ.setAutoThreshold(imp, "Default"); //TODO choose method with "selection"

        RoiManager manager = new RoiManager(true);
        ParticleAnalyzer.setRoiManager(manager);

        int includeHoles = ParticleAnalyzer.INCLUDE_HOLES;
        int excludeEdgeParticles = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;

        int options = 0;
        int measurements = 0;

        if (includeHolesBool) {
            options = options + includeHoles;
        }

        if (excludeEdgeParticlesBool) {
            options = options + excludeEdgeParticles;
        }

        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, new ResultsTable(), minSize, maxSize, minCirc, maxCirc);
        pa.analyze(imp);

        Roi[] rois = manager.getRoisAsArray();
        ImageJVRL imageJVRL = new ImageJVRL(image.getImage());
        imageJVRL.setAutoGenerateArray(rois);
        ArrayList<PolygonRoi> roiList = new ArrayList<PolygonRoi>();

        for (Roi roi : rois) {
            roiList.add((PolygonRoi) roi);
        }

        if (!roiList.isEmpty()) {
            imageJVRL.setAutoGenerateRoiList(roiList);
        }

        return imageJVRL;
    }
}
