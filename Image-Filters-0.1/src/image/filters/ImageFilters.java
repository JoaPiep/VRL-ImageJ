/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
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
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
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
            imageProcessor.snapshot();
            imageProcessor.setRoi((Roi) image.getRoi());
            blur.blurGaussian(imageProcessor, sigmaX, sigmaY, accuracy);
            imageProcessor.reset(imageProcessor.getMask());
        } else {
            for (int i = 0; i < image.getRoiList().size(); i++) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) image.getRoiList().get(i));
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
            imageProcessor.snapshot();
            imageProcessor.setRoi((Roi) image.getRoi());
            imageProcessor.dilate();
            imageProcessor.reset(imageProcessor.getMask());
        } else {
            for (int i = 0; i < image.getRoiList().size(); i++) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) image.getRoiList().get(i));
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
            imageProcessor.snapshot();
            imageProcessor.setRoi((Roi) image.getRoi());
            imageProcessor.medianFilter();
            imageProcessor.reset(imageProcessor.getMask());
        } else {
            for (int i = 0; i < image.getRoiList().size(); i++) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) image.getRoiList().get(i));
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

            imageProcessor.snapshot();
            imageProcessor.setRoi((Roi) image.getRoi());
            imageProcessor.invert();
            imageProcessor.reset(imageProcessor.getMask());
        } else {
            for (int i = 0; i < image.getRoiList().size(); i++) {

                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) image.getRoiList().get(i));
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
            imageProcessor.snapshot();
            imageProcessor.setRoi((Roi) image.getRoi());
            imageProcessor.findEdges();
            imageProcessor.reset(imageProcessor.getMask());
        } else {
            for (int i = 0; i < image.getRoiList().size(); i++) {
                imageProcessor.snapshot();
                imageProcessor.setRoi((Roi) image.getRoiList().get(i));
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
     * @param image image to analyse
     * @return image
     */
    public ImageJVRL analyseTool(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType", options = "saveRoi=false") ImageJVRL image) {

        ImagePlus imagePlus = new ImagePlus("", image.getImage());
        ImagePlus outputImage = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imagePlus);
        ic.convertToGray32();

        ByteProcessor byteProcessor = new ByteProcessor(imagePlus.getImage());
        BinaryProcessor binaryProcessor = new BinaryProcessor(byteProcessor);
        binaryProcessor.autoThreshold();
        ImagePlus img = new ImagePlus("", binaryProcessor.createImage());

        ParticleAnalyzer pa = new ParticleAnalyzer();
        pa.showDialog();
        Boolean analyse = pa.analyze(img);

        if (!analyse) {
            System.out.println("ANALYSE ERROR!");
        } else {
            if (pa.getOutputImage() == null) {
                System.out.println("Output image do not exist !");
            } else {
                outputImage = pa.getOutputImage();
                outputImage.show();
            }
        }

        Image im = outputImage.getImage();

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
     * @param minSize minimal size of the area (Pixel)
     * @param maxSize maximal size of the area (Pixel)
     * @param minCirc minimal size of the circle
     * @param maxCirc maximal size of the circle
     * @return image with ROIs
     */
    public ImageJVRL generateRoisAuto(@ParamInfo(name = "ImageJVRL", options = "saveRoi=true") ImageJVRL image,
            @ParamInfo(name = "min size   (double)") double minSize,
            @ParamInfo(name = "max size   (double)") double maxSize,
            @ParamInfo(name = "min circle (double)") double minCirc,
            @ParamInfo(name = "max circle (double)") double maxCirc) {

        if (maxSize == 0.0) {
            maxSize = Double.MAX_VALUE;
        }

        ImagePlus imp = new ImagePlus("", image.getImage());

        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray32();

        IJ.setAutoThreshold(imp, "Default");
        RoiManager manager = new RoiManager(true);
        ParticleAnalyzer.setRoiManager(manager);

        //options
        int includeHoles = ParticleAnalyzer.INCLUDE_HOLES;
        int showOverlayMasks = ParticleAnalyzer.SHOW_OVERLAY_MASKS;
        int excludeEdgeParticles = ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;
        int showSummary = ParticleAnalyzer.SHOW_SUMMARY;
        int showResluts = ParticleAnalyzer.SHOW_RESULTS;
        int showMasks = ParticleAnalyzer.SHOW_MASKS;
        int showOutlines = ParticleAnalyzer.SHOW_OUTLINES;
        int fourConnected = ParticleAnalyzer.FOUR_CONNECTED;
        int showOverlayOutlines = ParticleAnalyzer.SHOW_OVERLAY_OUTLINES;
        int recordStarts = ParticleAnalyzer.RECORD_STARTS;
        int addToManager = ParticleAnalyzer.ADD_TO_MANAGER;
        int showRoiMasks = ParticleAnalyzer.SHOW_ROI_MASKS;

        int option = includeHoles + showOverlayOutlines + excludeEdgeParticles;

        ParticleAnalyzer pa = new ParticleAnalyzer(option, 0, new ResultsTable(), minSize, maxSize, minCirc, maxCirc);
        pa.analyze(imp);

        if (pa.getOutputImage() != null) {
            pa.getOutputImage().show();
        } else {
            imp.show();
        }

        Roi[] rois = manager.getRoisAsArray();
        ArrayList<PolygonRoi> roiList = new ArrayList<PolygonRoi>();
        for (Roi roi : rois) {
            imp.setRoi(roi);
            roiList.add((PolygonRoi) roi);
        }
        image.setRoiList(roiList);

        return image;

    }
}
