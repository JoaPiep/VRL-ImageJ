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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
    protected ArrayList<PolygonRoi> polygonRoiList;
    protected ImageProcessor imageProcessor;
    private boolean editDone;
    private boolean saveRoiInVRL = true;
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
                                imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                iw = new ImageWindow(imagePlus);
                                imageCanvas = iw.getCanvas();

                                if (polygonRoi != null) {

                                    floatPolygon = polygonRoi.getFloatPolygon();
                                    polygonRoi = new PolygonRoi(floatPolygon,
                                            Roi.POLYGON);
                                    imageProcessor = imagePlus.getProcessor();
                                    imageProcessor.setColor(Color.green);
                                    polygonRoi.setStrokeWidth(3);
                                    polygonRoi.drawPixels(imageProcessor);

                                }

                                floatPolygon = new FloatPolygon();

                                if (!polygonRoiList.isEmpty() && polygonRoiList.size() > 1) {
                                    printRois(polygonRoiList, imagePlus, polygonRoiList.size() - 1);
                                }

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

                                            polygonRoi.setStrokeColor(Color.green);
                                            polygonRoi.setStrokeWidth(3);
                                            imagePlus.setRoi(polygonRoi);

                                        }
                                        if (e.getButton() == MouseEvent.BUTTON3
                                        && e.getClickCount() == 1 && !editDone) {

                                            imageProcessor = imagePlus.getProcessor();
                                            imageProcessor.setColor(Color.green);
                                            polygonRoi.drawPixels(imageProcessor);
                                            imageJVRLvalue.setRoi(polygonRoi);

                                            
                                            
                                            if (polygonRoiList.isEmpty() || !polygonRoiList.get(polygonRoiList.size() - 1).equals(polygonRoi)) {
                                                polygonRoiList.add(polygonRoi);
                                                imageJVRLvalue.setRoiList(polygonRoiList);
                                            }
                                            
                                            if (!polygonRoiList.isEmpty() && polygonRoiList.size() > 1) {
                                                printRois(polygonRoiList, imagePlus, polygonRoiList.size()-1);
                                            }
                                            floatPolygon = new FloatPolygon();
                                        }

                                    }

                                });

                                iw.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent ew) {
                                        plotPane.setImage(imagePlus.getImage());
                                        if (imageJVRLvalue != null) {

                                            if (saveRoiInVRL && polygonRoi != null) {
                                                imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                                            }
                                            if (saveRoiInVRL && polygonRoiList != null) {
                                                imageJVRLvalue.encodeROIList(polygonRoiList);// roiDataList
                                            }
                                        }
                                        setDataOutdated();
                                    }
                                });

                                iw.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyTyped(KeyEvent e) {

                                        if (e.getKeyChar() == 'r') { //reset ROI and ROIList

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            floatPolygon = new FloatPolygon();

                                            // TODO : What happens when we do not choose any roi?
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoiList = new ArrayList();
                                            imageJVRLvalue.setRoi(polygonRoi); // empty ROI
                                            imageJVRLvalue.setRoiList(polygonRoiList); // empty ROIList

                                        } else if (e.getKeyChar() == 'e') {// edit the common ROI

                                            editDone = false;
                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            if (!polygonRoiList.isEmpty() && polygonRoiList.size() > 1) {
                                                printRois(polygonRoiList, imagePlus, polygonRoiList.size() - 1);
                                            }

                                            polygonRoiList.remove(polygonRoiList.size() - 1);
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoi.setStrokeColor(Color.green);
                                            polygonRoi.setStrokeWidth(3);
                                            imagePlus.setRoi(polygonRoi);

                                        } else if (e.getKeyChar() == 'z' && polygonRoiList.size() > 1
                                        && (!polygonRoiList.isEmpty() || polygonRoi != null)) { //edit ROI

                                            polygonRoiList.remove(polygonRoiList.size() - 1);
                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            printRois(polygonRoiList, imagePlus, polygonRoiList.size() - 1);
                                            polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);

                                            imageJVRLvalue.setRoi(polygonRoi);
                                            imageProcessor = imagePlus.getProcessor();
                                            imageProcessor.setColor(Color.green);
                                            polygonRoi.drawPixels(imageProcessor);
                                            imageJVRLvalue.setRoiList(polygonRoiList);
                                            System.out.println("Size e.getKeyChar() == 'z' : " + imageJVRLvalue.getRoiList().size());

                                            editDone = false;

                                        } else {
                                            System.out.println("Press 'e' to edit  the ROI");
                                            System.out.println("Press 'r' to reset the ROI");
                                            System.out.println("Press 'z' to remove the common ROI");
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

        if (image.getImage() != null) {
            imagePlus.setImage(image.getImage());
            imagePlus.setTitle("Image");
        }

        if (image.getRoiData() != null) {
            polygonRoi = image.decodeROI();
            image.setRoi(polygonRoi);
        }

        if (image.getRoiDataList() != null && polygonRoiList == null) {
            polygonRoiList = image.decodeROIList();
            System.out.println("image.getRoiDataList()  polygonRoiList size " + polygonRoiList.size());
        }

        if (imageJVRLvalue == null || isOutput()) {
            setImageJVRLvalue(image);
            if (polygonRoi != null) {
                imageJVRLvalue.setRoi(polygonRoi);
            }
            if (polygonRoiList != null) {
                imageJVRLvalue.setRoiList(polygonRoiList);
            }

        } else if (imageJVRLvalue.equals(image) == false) {
            setImageJVRLvalue(image);
            if (polygonRoi != null) {
                imageJVRLvalue.setRoi(polygonRoi);
                if (saveRoiInVRL) {
                    imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                }
            }
            if (polygonRoiList != null) {
                imageJVRLvalue.setRoiList(polygonRoiList);
                if (saveRoiInVRL) {
                    imageJVRLvalue.encodeROIList(polygonRoiList); // set the string roiData
                }
            }
        } else {
            if (isInput() && image.getImage() != null) {
                imageJVRLvalue.setImage(image.getImage());
                if (polygonRoi != null) {
                    imageJVRLvalue.setRoi(polygonRoi);
                    imageProcessor = imagePlus.getProcessor();
                    imageProcessor.setColor(Color.red);
                    polygonRoi.setStrokeWidth(3);
                    polygonRoi.drawPixels(imageProcessor);
                }
                if (polygonRoiList != null) {
                    imageJVRLvalue.setRoiList(polygonRoiList);
                    printRois(imageJVRLvalue.getRoiList(), imagePlus, imageJVRLvalue.getRoiList().size());

                }
            }
        }
        plotPane.setImage(imagePlus.getImage());
    }

    public void printRois(ArrayList<PolygonRoi> polygonRoiList, ImagePlus ip, int n) {
        ImageProcessor imProcessor = ip.getProcessor();
        imProcessor.setColor(Color.red);
        for (int i = 0; i < n; i++) {
            polygonRoiList.get(i).setStrokeWidth(3);
            polygonRoiList.get(i).drawPixels(imProcessor);
        }
    }

    /**
     *
     * @param imageJVRLvalue imageJVRLvalue to set
     */
    public void setImageJVRLvalue(ImageJVRL imageJVRLvalue) {
        this.imageJVRLvalue = imageJVRLvalue;
    }

    /**
     *
     * @return imageJVRLvalue
     */
    public ImageJVRL getImageJVRLvalue() {
        return imageJVRLvalue;
    }

    @Override
    public Object getViewValue() {
        return imageJVRLvalue;
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

            if (getValueOptions().contains("saveRoi")) {
                property = script.getProperty("saveRoi");
            }

            if (property != null) {
                saveRoiInVRL = (Boolean) property;
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
