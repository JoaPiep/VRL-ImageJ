/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
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
 * @author Joanna Pieper <joanna.pieper1@gmail.com>
 */
@ComponentInfo(name = "Load and store",
        category = "ImageJ-VRL",
        description = "My Component")
public class LoadStoreImageJVRL implements Serializable {

    /**
     * empty constructor
     */
    public LoadStoreImageJVRL() {
    }

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

}
