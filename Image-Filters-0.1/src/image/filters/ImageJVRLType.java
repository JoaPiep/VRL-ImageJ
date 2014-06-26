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
@TypeInfo(type = ImageJVRL.class, input = true, output = true, style = "default")
public class ImageJVRLType extends ImageJWindowType {// Buffered Image

   
    
    public ImageJVRLType() {
    System.out.println("ImageJVRLType");
    }

    @Override
    public void setViewValue(Object o) {
        setDataOutdated();
        ImageJVRL imageJVRL  = (ImageJVRL) o;
        
        Image image = imageJVRL.getImage();
        System.out.println("Set view value ImageJVRLType");
        super.setViewValue(image);
        //super.setViewValue((Image) getViewValue());
    }

    @Override
    public Object getViewValue() {
        return this.value;
    }
}
