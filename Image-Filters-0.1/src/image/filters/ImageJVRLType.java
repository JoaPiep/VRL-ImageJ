/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.BufferedImageType;
import java.awt.Image;

/**
 * @author Joanna Pieper
 */
@TypeInfo(type = ImageJVRL.class, input = false, output = true, style = "default")
public class ImageJVRLType extends BufferedImageType {

    
    public ImageJVRLType() {
    }

    @Override
    public void setViewValue(Object o) {

        ImageJVRL imageJVRL = (ImageJVRL) o;
        Image image = imageJVRL.getImage();
        
        super.setViewValue(image);

    }

   
}
