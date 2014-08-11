/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.types.PlotPane;
import eu.mihosoft.vrl.visual.FullScreenComponent;
import eu.mihosoft.vrl.visual.VBoxLayout;
import groovy.lang.Script;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;

/**
 *
 * @author Joanna Pieper
 */
@TypeInfo(type = ImageJVRL.class, input = true, output = false, style = "ImageJPRoiType")
public class ImageJPolygonRoiType extends TypeRepresentationBase
        implements TypeRepresentation, FullScreenComponent {

    private static final long serialVersionUID = 1988974656391008424L;
    protected PlotPane plotPane;
    protected Box container;
    protected Dimension plotPaneSize;
    protected Dimension previousPlotPaneSize;
    protected Dimension minimumPlotPaneSize;

    protected ImageWindow iw;
    protected ImageCanvas imageCanvas;
    protected ImagePlus imagePlus = new ImagePlus();
    protected FloatPolygon floatPolygon;
    protected PolygonRoi polygonRoi;
    protected ImageProcessor imageProcessor;
    protected boolean roiSelected;
    private boolean editDone;
    private boolean saveImageInVRL = false;

    private ImageJVRL imageJVRLvalue;

    public ImageJPolygonRoiType() {

        plotPane = new PlotPane(this);

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        setValueName("");
        nameLabel.setAlignmentY(0.5f);

        setUpdateLayoutOnValueChange(false);

        setMinimumPlotPaneSize(new Dimension(70, 70));

        plotPane.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1
                        && e.getClickCount() == 2) {
                            if (plotPane.getImage() != null || isInput()) {

                                editDone = false;
                                roiSelected = false;

                                imagePlus.setImage((Image) getViewValue());
                                iw = new ImageWindow(imagePlus);
                                imageCanvas = iw.getCanvas();
                                floatPolygon = new FloatPolygon();

                                imageCanvas.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {

                                        if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 1 && !editDone) {

                                            floatPolygon.addPoint(
                                                    imageCanvas.offScreenX(e.getX()),
                                                    imageCanvas.offScreenY(e.getY()));
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoi.setStrokeColor(Color.red);
                                            polygonRoi.setStrokeWidth(3);
                                            imagePlus.setRoi(polygonRoi);
                                        }
                                        if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 2 && !editDone) {

                                            editDone = true;
                                            roiSelected = true;
                                            imageProcessor = imagePlus.getProcessor();
                                            imageProcessor.setColor(Color.red);
                                            //imageProcessor.snapshot();
                                            //imageProcessor.setRoi(polygonRoi);
                                            //imageProcessor.invert();
                                            //imageProcessor.reset(imageProcessor.getMask());
                                            polygonRoi.drawPixels(imageProcessor);

                                        }

                                    }
                                });

                                iw.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent ew) {
                                        plotPane.setImage(imagePlus.getImage());

                                        if (imageJVRLvalue != null && editDone == true) {
                                            imageJVRLvalue.setRoi(polygonRoi); //set max one roi
                                        }
                                        setDataOutdated();
                                    }
                                });

                                imageCanvas.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                        if (e.getKeyChar() == 'r' && !editDone) { //reset ROI
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            imagePlus.setRoi(polygonRoi);
                                            floatPolygon = new FloatPolygon();

                                        } else {
                                            System.out.println("Press 'r' to reset the ROI");
                                        }

                                    }
                                });

                            }
                        }
                    }
                });

        this.add(Box.createHorizontalStrut(3));

        container = Box.createHorizontalBox();

        container.add(nameLabel);

        container.add(plotPane);

        this.add(container);

        this.setInputComponent(container);

    }

    @Override
    public void emptyView() {
        plotPane.setImage(null);
    }

    @Override
    public void setViewValue(Object o) {

        setDataOutdated();
        ImageJVRL image = null;
        try {
            image = (ImageJVRL) o;
        } catch (Exception e) {
        }

        if (roiSelected == false) {
            imagePlus.setImage(image.getImage());
            imagePlus.setTitle("Image");
        }

        plotPane.setImage(imagePlus.getImage());

        if (imageJVRLvalue == null || isOutput()) {
            imageJVRLvalue = image;

        } else if (imageJVRLvalue.equals(image) == false) {
            imageJVRLvalue = image;
        } else {
            if (isInput()) {
                imageJVRLvalue.setImage(image.getImage());
            }
        }
        roiSelected = false;

        if (polygonRoi != null && saveImageInVRL) {
            try {
                image.encodeROI();

            } catch (IOException ex) {
                Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                polygonRoi = (PolygonRoi) image.decodeROI();
                imageJVRLvalue.setRoi(polygonRoi);
                System.out.println("Decode " + image.decodeROI().toString());
            } catch (IOException ex) {
                Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public Object getViewValue() {

        return plotPane.getImage();

    }

    public Dimension getPlotPaneSize() {
        return plotPaneSize;
    }

    public boolean isKeepAspectRatio() {
        return plotPane.isFixedAspectRatio();
    }

    protected void setMinimumPlotPaneSize(Dimension plotPaneSize) {
        this.plotPaneSize = plotPaneSize;

        plotPane.setPreferredSize(plotPaneSize);
        plotPane.setMinimumSize(plotPaneSize);
        minimumPlotPaneSize = plotPaneSize;

        setValueOptions("width=" + plotPaneSize.width + ";"
                + "height=" + plotPaneSize.height);
    }

    private void setPlotPaneSizeFromValueOptions(Script script) {
        Object property = null;
        Integer w = null;
        Integer h = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("width")) {
                property = script.getProperty("width");
            }

//            System.out.println("Property:" + property.getClass());
            if (property != null) {
                w = (Integer) property;
            }

            property = null;

            if (getValueOptions().contains("height")) {
                property = script.getProperty("height");
            }

            if (property != null) {
                h = (Integer) property;
            }

            property = null;

            if (getValueOptions().contains("fixedAspectRatio")) {
                property = script.getProperty("fixedAspectRatio");
            }

            if (property != null) {
                plotPane.setFixedAspectRatio((Boolean) property);
            }
        }

        if (w != null && h != null) {
            // TODO find out why offset is 5
            plotPane.setPreferredSize(new Dimension(w - 5, h));
            plotPane.setSize(new Dimension(w - 5, h));
        }
    }

    @Override
    protected void evaluationRequest(Script script) {
        setPlotPaneSizeFromValueOptions(script);

        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("saveImage")) {
                property = script.getProperty("saveImage");
            }

            if (property != null) {
                saveImageInVRL = (Boolean) property;
            }

            property = null;
        }
    }

    @Override
    public void enterFullScreenMode(Dimension size) {
        super.enterFullScreenMode(size);
        previousPlotPaneSize = plotPane.getSize();
        container.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        container.setMinimumSize(null);
        container.setMaximumSize(null);

        plotPane.setPreferredSize(null);
        plotPane.setMinimumSize(null);
        plotPane.setMaximumSize(null);

        revalidate();
    }

    @Override
    public void leaveFullScreenMode() {
        super.leaveFullScreenMode();
        container.setPreferredSize(null);
        container.setMinimumSize(null);
        container.setMaximumSize(null);

        plotPane.setSize(previousPlotPaneSize);
        plotPane.setPreferredSize(previousPlotPaneSize);

        plotPane.setMinimumSize(minimumPlotPaneSize);

        revalidate();
    }
}
