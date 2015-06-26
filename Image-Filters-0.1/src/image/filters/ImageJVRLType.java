/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.BufferedImageType;

/**
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
@TypeInfo(type = ImageJVRL.class, input = false, output = true, style = "default")
public class ImageJVRLType extends BufferedImageType {

    public ImageJVRLType() {
        setValueName(" ");
    }

    @Override
    public void setViewValue(Object o) {

        ImageJVRL imageJVRL = (ImageJVRL) o;
        super.setViewValue(imageJVRL.getImage());
    }

    @Override
    public Object getViewValue() {
        return this.viewValue;
    }

  
}
