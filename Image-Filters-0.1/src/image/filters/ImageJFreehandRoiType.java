/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import ij.gui.FreehandRoi;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_TAB;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JLabel;

/**
 *
 * @author Joanna Pieper
 */
@TypeInfo(type = ImageJVRL.class, input = true, output = true, style = "ImageJFRoiType")
public class ImageJFreehandRoiType extends TypeRepresentationBase
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
    private boolean saveRoiInVRL = true;
    private ImageJVRL imageJVRLvalue;

    FreehandRoi fRoi;

    public ImageJFreehandRoiType() {

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

                                iw = new ImageWindow(imagePlus);
                                JLabel textLabel = new JLabel("Press: 'e' to edit the ROI, 'r' to reset the ROI, 'z' to remove the current ROI and TAB to change the current ROI");
                                textLabel.setForeground(Color.red);
                                Panel panel = new Panel();
                                panel.add(textLabel);
                                iw.add(panel, 0);

                                imageCanvas = iw.getCanvas();
                                floatPolygon = new FloatPolygon();

                                printRois(polygonRoiList, polygonRoi, imagePlus);
                                imageCanvas.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {

                                        if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 1) {

                                            floatPolygon.addPoint(
                                                    imageCanvas.offScreenX(e.getX()),
                                                    imageCanvas.offScreenY(e.getY()));
                                            
                                          /*  fRoi = new FreehandRoi(imageCanvas.offScreenX(e.getX()),
                                                    imageCanvas.offScreenY(e.getY()), imagePlus);
                                            System.out.println("RFOIR" + fRoi.toString());
                                            fRoi.setStrokeColor(Color.green);
                                            fRoi.setStrokeWidth(3);
                                            imagePlus.setRoi(fRoi);*/
                                           polygonRoi = new PolygonRoi(floatPolygon,
                                                Roi.POLYGON);

                                           printRoi(polygonRoi, imagePlus);
                                        }
                                        if (e.getButton() == MouseEvent.BUTTON3
                                        && e.getClickCount() == 1) {

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            imageJVRLvalue.setRoi(polygonRoi);

                                            if (polygonRoiList.contains(polygonRoi) == false) {
                                                polygonRoiList.add(polygonRoi);
                                                imageJVRLvalue.setRoiList(polygonRoiList);
                                            }
                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                            floatPolygon = new FloatPolygon();
                                        }

                                    }

                                });

                                iw.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent ew) {

                                        plotPane.setImage(imagePlus.getImage());

                                        if (saveRoiInVRL) {
                                            if (polygonRoi != null && polygonRoi.getNCoordinates() != 0) {
                                                imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                                            } else {
                                                imageJVRLvalue.setRoiData(null);
                                            }
                                        }

                                        if (saveRoiInVRL && !polygonRoiList.isEmpty()) {
                                            imageJVRLvalue.encodeROIList(polygonRoiList);// roiDataList
                                        } else {
                                            imageJVRLvalue.setRoiDataList(null);
                                        }
                                        setDataOutdated();
                                    }
                                });

                                imageCanvas.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyTyped(KeyEvent e) {

                                        if (e.getKeyChar() == 'r') { //reset ROI and ROIList

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoiList = new ArrayList();
                                            imageJVRLvalue.setRoi(null);
                                            imageJVRLvalue.setRoiList(polygonRoiList); // empty List of ROIs

                                        } else if (e.getKeyChar() == 'e') {// edit the common ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            PolygonRoi tempRoi = null;
                                            if (!polygonRoiList.isEmpty()) {
                                                tempRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                polygonRoiList.remove(polygonRoiList.size() - 1);
                                            }
                                            if (tempRoi != null) {
                                                floatPolygon = tempRoi.getFloatPolygon();
                                            }
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                            printRoi(polygonRoi, imagePlus);

                                            if (polygonRoiList.isEmpty()) {
                                                floatPolygon = new FloatPolygon();
                                                polygonRoi = new PolygonRoi(floatPolygon,
                                                        Roi.POLYGON);
                                                polygonRoiList = new ArrayList();
                                                imageJVRLvalue.setRoi(null);
                                                imageJVRLvalue.setRoiList(polygonRoiList); //empty List of ROIs
                                            }

                                        } else if (e.getKeyChar() == 'z') { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            if (polygonRoiList.size() > 1) {
                                                polygonRoiList.remove(polygonRoiList.size() - 1);
                                                polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                printRois(polygonRoiList, polygonRoi, imagePlus);
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                imageJVRLvalue.setRoiList(polygonRoiList);
                                            } else if (polygonRoiList.size() == 1) {
                                                floatPolygon = new FloatPolygon();
                                                polygonRoi = new PolygonRoi(floatPolygon,
                                                        Roi.POLYGON);
                                                polygonRoiList = new ArrayList();
                                                imageJVRLvalue.setRoi(null);
                                                imageJVRLvalue.setRoiList(polygonRoiList); //empty List of ROIs
                                            }
                                        } else if (e.getKeyChar() == VK_TAB && polygonRoiList.size() > 1) { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            polygonRoi = polygonRoiList.get(0);
                                            polygonRoiList.remove(0);
                                            polygonRoiList.add(polygonRoi);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);

                                        } else {
                                            System.out.println("Press 'e' to edit  the ROI");
                                            System.out.println("Press 'r' to reset the ROI");
                                            System.out.println("Press 'z' to remove the current ROI");
                                            System.out.println("Press TAB to change the current ROI");
                                        }
                                    }
                                });

                                iw.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyTyped(KeyEvent e) {

                                        if (e.getKeyChar() == 'r') { //reset ROI and ROIList

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoiList = new ArrayList();
                                            imageJVRLvalue.setRoi(null);
                                            imageJVRLvalue.setRoiList(polygonRoiList); // empty List of ROIs

                                        } else if (e.getKeyChar() == 'e') {// edit the common ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            PolygonRoi tempRoi = null;
                                            if (!polygonRoiList.isEmpty()) {
                                                tempRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                polygonRoiList.remove(polygonRoiList.size() - 1);
                                            }
                                            if (tempRoi != null) {
                                                floatPolygon = tempRoi.getFloatPolygon();
                                            }
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                            printRoi(polygonRoi, imagePlus);

                                            if (polygonRoiList.isEmpty()) {
                                                floatPolygon = new FloatPolygon();
                                                polygonRoi = new PolygonRoi(floatPolygon,
                                                        Roi.POLYGON);
                                                polygonRoiList = new ArrayList();
                                                imageJVRLvalue.setRoi(null);
                                                imageJVRLvalue.setRoiList(polygonRoiList); //empty List of ROIs
                                            }

                                        } else if (e.getKeyChar() == 'z') { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            if (polygonRoiList.size() > 1) {
                                                polygonRoiList.remove(polygonRoiList.size() - 1);
                                                polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                printRois(polygonRoiList, polygonRoi, imagePlus);
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                imageJVRLvalue.setRoiList(polygonRoiList);
                                            } else if (polygonRoiList.size() == 1) {
                                                floatPolygon = new FloatPolygon();
                                                polygonRoi = new PolygonRoi(floatPolygon,
                                                        Roi.POLYGON);
                                                polygonRoiList = new ArrayList();
                                                imageJVRLvalue.setRoi(null);
                                                imageJVRLvalue.setRoiList(polygonRoiList); //empty List of ROIs
                                            }

                                        } else if (e.getKeyChar() == VK_TAB && polygonRoiList.size() > 1) { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            polygonRoi = polygonRoiList.get(0);
                                            polygonRoiList.remove(0);
                                            polygonRoiList.add(polygonRoi);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);

                                        } else {
                                            System.out.println("Press 'e' to edit  the ROI");
                                            System.out.println("Press 'r' to reset the ROIs");
                                            System.out.println("Press 'z' to remove the common ROI");
                                            System.out.println("Press TAB to change the common ROI");
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

        if (image.getRoiData() != null && polygonRoi == null) {
            polygonRoi = image.decodeROI();
            image.setRoi(polygonRoi);
        }

        if (image.getRoiDataList() != null && polygonRoiList == null) {
            polygonRoiList = image.decodeROIList();
            image.setRoiList(polygonRoiList);
        }

        if (imageJVRLvalue == null || isOutput()) {
            setImageJVRLvalue(image);
            if (polygonRoi != null) {
                imageJVRLvalue.setRoi(polygonRoi);
            }
            if (polygonRoiList != null) {
                imageJVRLvalue.setRoiList(polygonRoiList);
            }

        } else if (!imageJVRLvalue.equals(image)) {
            setImageJVRLvalue(image);
            if (polygonRoiList != null) {
                imageJVRLvalue.setRoiList(polygonRoiList);
                printRois(polygonRoiList, polygonRoi, imagePlus);
                if (saveRoiInVRL) {
                    imageJVRLvalue.encodeROIList(polygonRoiList); // set the string roiData
                }
            }
            if (polygonRoi != null) {
                imageJVRLvalue.setRoi(polygonRoi);
                if (saveRoiInVRL) {
                    imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                }
            }
        } else {
            if (isInput()) {
                if (polygonRoiList != null) {
                    imageJVRLvalue.setRoiList(polygonRoiList);
                    printRois(polygonRoiList, polygonRoi, imagePlus);
                }
                if (polygonRoi != null) {
                    imageJVRLvalue.setRoi(polygonRoi);
                }
            }
        }
        plotPane.setImage(imagePlus.getImage());
    }

    @Override
    public Object getViewValue() {
        return imageJVRLvalue;
    }

    /**
     *
     * @param polygonRoiList list of ROIs to print (blue)
     * @param polyRoi current ROI (red)
     * @param ip current ImagePlus
     */
    private void printRois(ArrayList<PolygonRoi> polygonRoiList, PolygonRoi polyRoi, ImagePlus ip) {
        ImageProcessor imProcessor = ip.getProcessor();
        if (polygonRoiList.size() > 1) {
            for (int i = 0; i < polygonRoiList.size(); i++) {
                if (!polygonRoiList.get(i).equals(polyRoi)) {
                    imProcessor.setColor(Color.blue);
                    polygonRoiList.get(i).setStrokeWidth(3);
                    polygonRoiList.get(i).drawPixels(imProcessor);
                } else {
                    imProcessor.setColor(Color.red);
                    polygonRoiList.get(i).setStrokeWidth(3);
                    polygonRoiList.get(i).drawPixels(imProcessor);
                }
            }
        } else if (polygonRoiList.size() == 1) {
            imProcessor.setColor(Color.red);
            polygonRoiList.get(0).setStrokeWidth(3);
            polygonRoiList.get(0).drawPixels(imProcessor);
        }
    }

    /**
     *
     * @param roi current ROI to print editable
     * @param ip current ImagePlus
     */
    private void printRoi(PolygonRoi roi, ImagePlus ip) {
        roi.setStrokeColor(Color.green);
        roi.setStrokeWidth(3);
        ip.setRoi(polygonRoi);
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
