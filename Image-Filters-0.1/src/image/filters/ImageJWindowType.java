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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;

/**
 *
 * @author Joanna Pieper
 */
@TypeInfo(type = Image.class, input = true, output = true, style = "ImageJWindowType")
public class ImageJWindowType extends TypeRepresentationBase
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

    protected Boolean roiSelected = false;

    public ImageJWindowType() {

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
                                            roiSelected = true; // roi wurde ausgewählt
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            // polygonRoi.setImage(imagePlus);
                                            // polygonRoi.setColor(Color.red);
                                            imageProcessor = imagePlus.getProcessor();

                                            imageProcessor.setRoi(polygonRoi);
                                            imageProcessor.dilate();
                                            imageProcessor.setColor(Color.green);
                                            //imageProcessor.medianFilter();
                                            imageProcessor.fill(imageProcessor.getMask());
                                            // imageProcessor.fill(polygonRoi);

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
        Image image = null;
        try {
            image = (Image) o;
        } catch (Exception e) {
        }
        plotPane.setImage(image);

        if (roiSelected = true) {

            imageProcessor = imagePlus.getProcessor();
            imageProcessor.setRoi(polygonRoi);
            imageProcessor.dilate();
            imageProcessor.setColor(Color.red);
            //imageProcessor.medianFilter();
            imageProcessor.fill(imageProcessor.getMask());
            // imageProcessor.fill(polygonRoi);
            roiSelected = false;
        } else {
            imagePlus.setImage(image);
        }
        
        
        
        System.out.println("setViewValue ImageJType");
    }

    @Override
    public Object getViewValue() {

        Image o = null;
        try {
            o = plotPane.getImage();
        } catch (Exception e) {
        }

        return o;
        // return plotPane.getImage();
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
