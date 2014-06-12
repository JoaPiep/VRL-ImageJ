/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.TypeInfo;

import eu.mihosoft.vrl.types.BufferedImageType;
import ij.ImagePlus;


/**
 * @author Joanna Pieper
 */
@TypeInfo(type = ImageJVRL.class, input = true, output = true, style = "default")
public class ImageJVRLType extends BufferedImageType {

   

    public ImageJVRLType() {
    
    }

    @Override
    public void setViewValue(Object o) {
        setDataOutdated();
        ImageJVRL imageJVRL = (ImageJVRL) o;
        ImagePlus image = imageJVRL.getImage();
        //ImagePlus ip = new ImagePlus("",image1);
        //ip.setRoi(imageJVRL.getRoi());
        System.out.println("Set view value ImageJVRLType");
        super.setViewValue(image.getImage());

    }

    @Override
    public Object getViewValue() {
        return this.value;
    }
}
