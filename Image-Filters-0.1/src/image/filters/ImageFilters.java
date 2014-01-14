/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image.filters;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import ij.ImagePlus;
import ij.io.FileSaver;
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

@ComponentInfo(name="VRL - ImageJ", 
               category="Filters", 
               description="My Component")

public class ImageFilters implements Serializable {

   private static final long serialVersionUID = 1L;
   
    /**
     * 
     * @param imgFile the file where the image is saved 
     * @return the loaded image
     */
   public Image loadImage(@ParamInfo(name="", style="load-dialog", options="endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile){
       
        Image  image;
        image = null;
        try {
            image = ImageIO.read(imgFile);
        } catch (IOException ex) {
            Logger.getLogger(ImageFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
   }
    
    /**
     * 
     * @param image image to filter
     * @param sigma  parameter from the gaussian blur function
     * @return filtered image
     */
   public Image gaussianBlur(Image image, @ParamInfo(name="Sigma (Double)") double sigma){
       
        ImageProcessor ip = new ColorProcessor(image);
        ip.blurGaussian(sigma);
        Image ig = ip.createImage();
       
        return ig;
        
   }
        /**
         * 
         * @param image image to filter
         * @return with 3x3 minimum filter filtered image
         */
   public Image min3x3Filter(Image image){
            
       
        ImageProcessor ip = new ColorProcessor(image);
        ip.dilate();
        Image ig = ip.createImage();
        
        return ig;
        
   }
    /**
     * 
     * @param image image to filter
     * @return with median filter filtered image
     */
   public Image medianFilter(Image image){
            
        ImageProcessor ip = new ColorProcessor(image);
        ip.medianFilter();
        Image ig = ip.createImage();
        
        return ig;
        
   }
    /**
     * 
     * @param imgFile file where the image will be saved
     * @param image image to save
     */
   public void saveImageAsJpg(@ParamInfo(name="", style="save-dialog", options="endings=[\"png\",\"jpg\"]; description=\"Image files\"") File imgFile, Image image){
       
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
   public void saveImageAsPng(@ParamInfo(name="", style="save-dialog", options="endings=[\"png\",\"jpg\"]; description=\"Image files\"") String name, Image image){
       
        ImagePlus imagePlus = new ImagePlus(name, image);
        FileSaver fileSaver = new FileSaver(imagePlus);
        fileSaver.saveAsPng();
       
   }
}
