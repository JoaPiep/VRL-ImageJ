/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
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

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param imgFile the file where the image is saved
     * @return the loaded image
     */
    public ImageJVRL loadImage(@ParamInfo(name = "", style = "load-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile) {

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
     * @param image image to filter
     * @param sigma parameter from the gaussian blur function
     * @return filtered image
     */
    public Image gaussianBlur(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image,
                              @ParamInfo(name = "Sigma (Double)") double sigma) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        //imageProcessor.setColor(Color.red);
        imageProcessor.snapshot();
        imageProcessor.setRoi(image.getRoi());
        imageProcessor.blurGaussian(sigma);
        imageProcessor.reset(imageProcessor.getMask());
           
        /*if(image.getRoi()!=null){
            image.getRoi().drawPixels(imageProcessor);
        }*/
        
        Image im = imageProcessor.createImage();

        return im;

    }

    /**
     *
     * @param image image to filter
     * @return with 3x3 minimum filter filtered image
     */
    public Image min3x3Filter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        imageProcessor.snapshot();
        imageProcessor.setRoi(image.getRoi());
        imageProcessor.dilate();
        imageProcessor.reset(imageProcessor.getMask());
      
        Image im = imageProcessor.createImage();

        return im;

    }

    /**
     *
     * @param image image to filter
     * @return with median filter filtered image
     */
    public Image medianFilter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image) {

        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        imageProcessor.snapshot();
        imageProcessor.setRoi(image.getRoi());
        imageProcessor.medianFilter();
        imageProcessor.reset(imageProcessor.getMask());
       
        Image im = imageProcessor.createImage();

        return im;
    }

    /**
     *
     * @param imgFile file where the image will be saved
     * @param image image to save
     */
    public void saveImageAsJpg(@ParamInfo(name = "", style = "save-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile, Image image) {

        BufferedImage bImage = (BufferedImage) image;
        try {
            ImageIO.write(bImage, "jpg", imgFile);
        } catch (IOException e) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     * @param name your filename
     * @param image image to save as png
     */
    public void saveImageAsPng(@ParamInfo(name = "", style = "save-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") String name, Image image) {

        ImagePlus imagePlus = new ImagePlus(name, image);
        FileSaver fileSaver = new FileSaver(imagePlus);
        fileSaver.saveAsPng();

    }

    public ImagePlus loadImagePlus(@ParamInfo(name = "", style = "load-dialog", options = "endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile) {
        Opener opener = new Opener();
        ImagePlus imagePlus = opener.openImage(imgFile.getPath());

        return imagePlus;
    }


    /**
     *
     * @param image image to filter - invert
     * @return filtered image
     */
    public Image invertFilter(@ParamInfo(name = "ImageJVRL", style = "ImageJPRoiType") ImageJVRL image) {
        
        ImageProcessor imageProcessor = new ColorProcessor(image.getImage());
        imageProcessor.snapshot();
        imageProcessor.setRoi(image.getRoi());
        imageProcessor.invert();
        imageProcessor.reset(imageProcessor.getMask());
        Image im = imageProcessor.createImage();

        return im;

    }



    /* public void testRoi(Image image) {

     ImagePlus imagePlus = new ImagePlus("image", image);

     FloatPolygon floatPolygon = new FloatPolygon();
     floatPolygon.addPoint(1500, 1400);
     floatPolygon.addPoint(1560, 2400);
     floatPolygon.addPoint(2500, 1200);
     floatPolygon.addPoint(2300, 450);
     floatPolygon.addPoint(1900, 780);

     PolygonRoi polygonRoi = new PolygonRoi(floatPolygon,
     Roi.POLYGON);
       
     ImageProcessor imageProcessor = imagePlus.getProcessor();
     imageProcessor.snapshot();
     imageProcessor.setRoi(polygonRoi);
 
     imageProcessor.dilate();
        
     imageProcessor.reset(imageProcessor.getMask());
     imagePlus.show();
        
       
     }*/
}
