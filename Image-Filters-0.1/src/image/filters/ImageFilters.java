/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
     * @param sigma sigma-parameter
     * @return filtered image (with gaussian blur)
     */
    public ImageJVRL gaussianBlur(@ParamInfo(name = "ImageJVRL") ImageJVRL image,
            @ParamInfo(name = "Sigma (Double)") double sigma) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        imageProcessor.blurGaussian(sigma);
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
        imageProcessor.snapshot();
        imageProcessor.setRoi((Roi) image.getRoi());
        imageProcessor.dilate();
        imageProcessor.reset(imageProcessor.getMask());
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
        imageProcessor.snapshot();
        imageProcessor.setRoi((Roi) image.getRoi());
        imageProcessor.medianFilter();
        imageProcessor.reset(imageProcessor.getMask());
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
        imageProcessor.snapshot();
        imageProcessor.setRoi((Roi) image.getRoi());
        imageProcessor.invert();
        imageProcessor.reset(imageProcessor.getMask());
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

}
