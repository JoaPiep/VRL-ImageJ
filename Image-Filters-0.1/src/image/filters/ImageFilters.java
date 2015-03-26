/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import groovy.xml.Entity;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Joanna Pieper
 */
@ComponentInfo(name = "VRL - ImageJ",
        category = "Filters",
        description = "My Component")
public class ImageFilters implements Serializable {

    public ImageFilters() {
    }

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param imgFile image filepath
     * @return loaded image as ImageJVRL
     */
    public ImageJVRL loadImageJVRL(@ParamInfo(name = "", style = "load-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile) {

        Image image;
        image = null;
        try {
            image = ImageIO.read(imgFile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ImageJVRL(image);
    }

    /**
     *
     * @param imgFile image filepath
     * @return ImagePlus image
     */
    public ImagePlus loadImagePlus(@ParamInfo(name = "", style = "load-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile) {

        return new Opener().openImage(imgFile.getPath());
    }

    /**
     *
     * @param imgFile fileposition from the image
     * @param image image to save
     */
    public void saveImageJVRLAsJpg(@ParamInfo(name = "", style = "save-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"File\"") File imgFile,
            @ParamInfo(name = "ImageJVRL") ImageJVRL image) {

        BufferedImage bImage = (BufferedImage) image.getImage();
        try {
            ImageIO.write(bImage, "jpg", imgFile);
        } catch (IOException e) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     * @param name filename
     * @param image image to save (as .png)
     */
    public void saveImageJVRLAsPng(@ParamInfo(name = "", style = "save-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") String name,
            @ParamInfo(name = "ImageJVRL") ImageJVRL image) {

        ImagePlus imagePlus = new ImagePlus(name, image.getImage());
        FileSaver fileSaver = new FileSaver(imagePlus);
        fileSaver.saveAsPng();

    }

    /**
     *
     * @param image image to be filter
     * @param sigmaX sigma X-direction
     * @param sigmaY sigma Y-direction
     * @param accuracy filter accuracy
     * @return filtered image (with gaussian blur)
     */
    public ImageJVRL gaussianBlur(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=true") ImageJVRL image,
            @ParamInfo(name = "sigmaX   (double)") double sigmaX,
            @ParamInfo(name = "sigmaY   (double)") double sigmaY,
            @ParamInfo(name = "accuracy (double)") double accuracy) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        GaussianBlur blur = new GaussianBlur();

        if (image.getRoiList().isEmpty()) {
            blur.blurGaussian(imageProcessor, sigmaX, sigmaY, accuracy);
        } else {
            for (PolygonRoi roi : image.getRoiList()) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) roi);
                blur.blurGaussian(imageProcessor, sigmaX, sigmaY, accuracy);
                imageProcessor.reset(imageProcessor.getMask());
            }
        }
        Image im = imageProcessor.createImage();

        return new ImageJVRL(im);

    }

    /**
     *
     * @param image image to be filter
     * @return filtered image (with 3x3 minimum filter)
     */
    public ImageJVRL min3x3Filter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());

        if (image.getRoiList().isEmpty()) {
            imageProcessor.dilate();
        } else {
            for (PolygonRoi roi : image.getRoiList()) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) roi);
                imageProcessor.dilate();
                imageProcessor.reset(imageProcessor.getMask());
            }
        }

        Image im = imageProcessor.createImage();

        return new ImageJVRL(im);

    }

    /**
     *
     * @param image image to be filter
     * @return filtered image (with median filter)
     */
    public ImageJVRL medianFilter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());

        if (image.getRoiList().isEmpty()) {
            imageProcessor.medianFilter();
        } else {
           for (PolygonRoi roi : image.getRoiList()) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) roi);
                imageProcessor.medianFilter();
                imageProcessor.reset(imageProcessor.getMask());
            }
        }

        Image im = imageProcessor.createImage();

        return new ImageJVRL(im);
    }

    /**
     *
     * @param image image to be filter
     * @return filtered image (with invert filter)
     */
    public ImageJVRL invertFilter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=true") ImageJVRL image) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());

        if (image.getRoiList().isEmpty()) {
            imageProcessor.invert();
        } else {

            for (PolygonRoi roi : image.getRoiList()) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) roi);
                imageProcessor.invert();
                imageProcessor.reset(imageProcessor.getMask());

            }
        }
        Image im = imageProcessor.createImage();

        return new ImageJVRL(im);

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
     * @param image imageJVRL
     * @return imageJVRL with detected edges
     */
    public ImageJVRL detectEdgesSobel(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=true") ImageJVRL image) {

        ImageProcessor ip = new ColorProcessor(image.getImage());

        ImageProcessor imageProcessor = ip.convertToByte(true);

        if (image.getRoiList().isEmpty()) {
            imageProcessor.findEdges();
        } else {
            for (PolygonRoi roi : image.getRoiList()) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) roi);
                imageProcessor.findEdges();
                imageProcessor.reset(imageProcessor.getMask());
            }
        }

        Image im = imageProcessor.createImage();

        return new ImageJVRL(im);

    }

    /**
     *
     * @param image imageJVRL
     * @param lowThreshold a low hysteresis threshol
     * @param highThreshold a high hysteresis threshold
     * @param gaussianKernelRadius a Gaussian kernel radius in pixels, must
     * exceed 0.1f
     * @param gaussianKernelWidth a radius for the convolution operation in
     * pixels, at least 2
     * @return imageJVRL with detected edges
     */
    public ImageJVRL detectEdgesCanny(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=false") ImageJVRL image,
            @ParamInfo(name = "low threshold  (float)") float lowThreshold,
            @ParamInfo(name = "high threshold (float)") float highThreshold,
            @ParamInfo(name = "Gaussian kernel radius (float)") float gaussianKernelRadius,
            @ParamInfo(name = "Gaussian kernel width    (int)") int gaussianKernelWidth) {

        Canny_Edge_Detector cd = new Canny_Edge_Detector();

        cd.setLowThreshold(lowThreshold);
        cd.setHighThreshold(highThreshold);
        cd.setGaussianKernelRadius(gaussianKernelRadius);

        if (gaussianKernelWidth < 2) {
            cd.setGaussianKernelWidth(2);
        } else {
            cd.setGaussianKernelWidth(gaussianKernelWidth);
        }

        ImagePlus img = cd.process(new ImagePlus("", image.getImage()));
        Image im = img.getImage();

        return new ImageJVRL(im);

    }

    /**
     *
     * @param image imageJVRL
     * @param lowThreshold a low hysteresis threshol
     * @param highThreshold a high hysteresis threshold
     * @param gaussianKernelRadius a Gaussian kernel radius in pixels, must
     * exceed 0.1f
     * @param gaussianKernelWidth a radius for the convolution operation in
     * pixels, at least 2
     * @return imageJVRL with detected edges
     */
    public ImageJVRL detectEdgesCannyInvert(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=false") ImageJVRL image,
            @ParamInfo(name = "low threshold  (float)") float lowThreshold,
            @ParamInfo(name = "high threshold (float)") float highThreshold,
            @ParamInfo(name = "Gaussian kernel radius (float)") float gaussianKernelRadius,
            @ParamInfo(name = "Gaussian kernel width    (int)") int gaussianKernelWidth) {

        Canny_Edge_Detector cd = new Canny_Edge_Detector();

        cd.setLowThreshold(lowThreshold);
        cd.setHighThreshold(highThreshold);
        cd.setGaussianKernelRadius(gaussianKernelRadius);

        if (gaussianKernelWidth < 2) {
            cd.setGaussianKernelWidth(2);
        } else {
            cd.setGaussianKernelWidth(gaussianKernelWidth);
        }

        ImagePlus img = cd.process(new ImagePlus("", image.getImage()));
        ImageProcessor ip = new ColorProcessor(img.getImage());
        ip.invert();
        Image im = ip.createImage();

        return new ImageJVRL(im);
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
            @ParamInfo(name = "Min size   (double)") double minSize,
            @ParamInfo(name = "Max size   (double)") double maxSize,
            @ParamInfo(name = "Min circle (double)") double minCirc,
            @ParamInfo(name = "Max circle (double)") double maxCirc,
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

        ImagePlus imp = new ImagePlus("", image.getImage());

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
        pa.analyze(imp);

        if (pa.getOutputImage() != null) {
            return new ImageJVRL(pa.getOutputImage().getImage());
        } else {
            return image;
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
    public ImageJVRL autoGenerateROIs(@ParamInfo(name = "ImageJVRL") ImageJVRL image,
            @ParamInfo(name = "Min size   (double)") double minSize,
            @ParamInfo(name = "Max size   (double)") double maxSize,
            @ParamInfo(name = "Min circle (double)") double minCirc,
            @ParamInfo(name = "Max circle (double)") double maxCirc,
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
        pa.setHideOutputImage(false);

        Roi[] rois = manager.getRoisAsArray();
        ArrayList<PolygonRoi> roiList = new ArrayList<PolygonRoi>();
        for (Roi roi : rois) {
            roiList.add((PolygonRoi) roi);
        }
        if (!roiList.isEmpty()) {
            image.setRoiList(roiList);
            image.setRoi(roiList.get(roiList.size() - 1));
        }
        return image;
    }
}
