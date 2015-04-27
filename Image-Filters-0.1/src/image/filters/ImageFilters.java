/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.GaussianBlur;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.io.Serializable;

/**
 *
 * @author Joanna Pieper <joanna.pieper1@gmail.com>
 */
@ComponentInfo(name = "Filters",
        category = "ImageJ-VRL",
        description = "My Component")
public class ImageFilters implements Serializable {

    //protected ImageJVRL imageJVRL = new ImageJVRL();
    
    public ImageFilters() {
    }

    private static final long serialVersionUID = 1L;

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
        ImageJVRL imageJ = new ImageJVRL(im);
        return imageJ;

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

}
