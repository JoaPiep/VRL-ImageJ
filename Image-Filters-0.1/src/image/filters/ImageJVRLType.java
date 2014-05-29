/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.types.BufferedImageType;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Joanna Pieper
 */
@TypeInfo(type = ImageJVRL.class, input = true, output = true, style = "default")
public class ImageJVRLType extends BufferedImageType {

    protected ImageWindow iw;
    protected ImageCanvas imageCanvas;
    protected ImagePlus imagePlus = new ImagePlus();
    protected FloatPolygon floatPolygon;
    protected PolygonRoi freehandRoi;
    protected ImageProcessor imageProcessor;

    public ImageJVRLType() {
        plotPane.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1
                        && e.getClickCount() == 2) {
                    if (plotPane.getImage() != null || isInput()) {
                        imagePlus.setImage((Image) getViewValue());

                        iw = new ImageWindow(imagePlus);
                        imageCanvas = iw.getCanvas();
                        floatPolygon = new FloatPolygon();

                        imageCanvas.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {

                                if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 1) {
                                    floatPolygon.addPoint(
                                            imageCanvas.offScreenX(e.getX()),
                                            imageCanvas.offScreenY(e.getY()));

                                } else if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 2) {

                                    freehandRoi = new PolygonRoi(floatPolygon,
                                            Roi.POLYGON);
                                    freehandRoi.setImage(imagePlus);
                                    // freehandRoi.setColor(Color.red);
                                    imageProcessor = imagePlus.getProcessor();
                                    imageProcessor.setRoi(freehandRoi);
                                    imageProcessor.dilate();
                                    imageProcessor.setColor(Color.yellow);
                                    //imageProcessor.medianFilter();
                                    imageProcessor.fill(imageProcessor.getMask());
                                    // imageProcessor.fill(freehandRoi);

                                    iw.addWindowListener(new WindowAdapter() {
                                        @Override
                                        public void windowClosed(WindowEvent e) {
                                            setViewValue(imagePlus.getImage());
                                            System.out.println("window closed");
                                            setDataOutdated();
                                        }
                                    });
                                    floatPolygon = new FloatPolygon();
                                } else {
                                    System.out.println("One click - add point"
                                            + "\n" + "Double click - print the polygon");
                                }

                            }
                        });


                    }
                }
            }
        });
    }

    @Override
    public void setViewValue(Object o) {
        setDataOutdated();
        ImageJVRL imageJVRL = null;
        Image image = null;
        try {
            imageJVRL = (ImageJVRL) o;
            image = imageJVRL.getImage();

        } catch (Exception e) {
        }

        System.out.println("Set view value ImageJVRLType");
        super.setViewValue(image);

    }

    @Override
    public Object getViewValue() {
        return this.value;
    }
}
