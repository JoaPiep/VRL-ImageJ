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
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    protected ImageCanvas imageCanvas;
    protected ImagePlus imagePlus = new ImagePlus();
    protected FloatPolygon floatPolygon;
    protected PolygonRoi polygonRoi;
    protected ArrayList<PolygonRoi> polygonRoiList;
    protected ImageProcessor imageProcessor;
    private ImageJVRL imageJVRLvalue;
    private boolean saveRoiInVRL = true;

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

                                ImageWindow iw = new ImageWindow(imagePlus);
                                imageCanvas = iw.getCanvas();
                                floatPolygon = new FloatPolygon();

                                printRois(polygonRoiList, polygonRoi, imagePlus);

                                MenuBar menuBar = new MenuBar();

                                Menu editMenu = new Menu("Edit");
                                Menu helpMenu = new Menu("Help");

                                MenuItem removeAllRoisItem = new MenuItem("Remove all ROIs", new MenuShortcut(KeyEvent.VK_R));
                                MenuItem editItem = new MenuItem("Edit ROI", new MenuShortcut(KeyEvent.VK_E));
                                MenuItem removeItem = new MenuItem("Remove ROI", new MenuShortcut(KeyEvent.VK_Z));
                                MenuItem changeItem = new MenuItem("Change ROI", new MenuShortcut(KeyEvent.VK_TAB));
                                MenuItem showItem = new MenuItem("Show ROI List", new MenuShortcut(KeyEvent.VK_L));

                                editMenu.add(changeItem);
                                editMenu.add(editItem);
                                editMenu.add(removeItem);
                                editMenu.add(removeAllRoisItem);
                                editMenu.add(showItem);

                                menuBar.add(editMenu);
                                menuBar.setHelpMenu(helpMenu);
                                iw.setMenuBar(menuBar);

                                removeAllRoisItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                        floatPolygon = new FloatPolygon();
                                        polygonRoi = new PolygonRoi(floatPolygon,
                                                Roi.POLYGON);
                                        polygonRoiList = new ArrayList();
                                        imageJVRLvalue.setRoi(null);
                                        imageJVRLvalue.setRoiList(polygonRoiList); // empty List of ROIs
                                    }

                                }
                                );

                                editItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

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
                                    }

                                }
                                );

                                removeItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

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
                                    }
                                }
                                );

                                changeItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        if (polygonRoiList.size() > 1) {
                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            polygonRoi = polygonRoiList.get(0);
                                            polygonRoiList.remove(0);
                                            polygonRoiList.add(polygonRoi);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);

                                        }
                                    }
                                }
                                );

                                imageCanvas.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {

                                        if (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 1) {

                                            floatPolygon.addPoint(
                                                    imageCanvas.offScreenX(e.getX()),
                                                    imageCanvas.offScreenY(e.getY()));
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

                                        imageJVRLvalue.setRoi(polygonRoi);

                                        if (polygonRoiList.contains(polygonRoi) == false && polygonRoi.getNCoordinates() != 0) {
                                            polygonRoiList.add(polygonRoi);
                                            imageJVRLvalue.setRoiList(polygonRoiList);
                                        }

                                        if (saveRoiInVRL) {
                                            if (polygonRoi != null && polygonRoi.getNCoordinates() != 0) {
                                                imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                                            } else {
                                                imageJVRLvalue.setRoiData(null);
                                            }
                                        }

                                        if (saveRoiInVRL && !polygonRoiList.isEmpty()) {
                                            imageJVRLvalue.encodeROIList(polygonRoiList);// roiDataList
                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                        } else {
                                            imageJVRLvalue.setRoiDataList(null);
                                        }
                                        setDataOutdated();

                                    }
                                });

                                imageCanvas.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyReleased(KeyEvent e) {

                                        if (e.getKeyCode() == KeyEvent.VK_R) { //reset ROI and ROIList

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoiList = new ArrayList();
                                            imageJVRLvalue.setRoi(null);
                                            imageJVRLvalue.setRoiList(polygonRoiList); // empty List of ROIs

                                        }

                                        if (e.getKeyCode() == KeyEvent.VK_E) {// editMenu the common ROI

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

                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_Z) { //edit ROI

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
                                        }

                                        if (e.getKeyCode() == KeyEvent.VK_TAB && polygonRoiList.size() > 1) { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            polygonRoi = polygonRoiList.get(0);
                                            polygonRoiList.remove(0);
                                            polygonRoiList.add(polygonRoi);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);

                                        }
                                    }
                                });

                                iw.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyReleased(KeyEvent e) {

                                        if (e.getKeyCode() == KeyEvent.VK_R) { //reset ROI and ROIList

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            polygonRoiList = new ArrayList();
                                            imageJVRLvalue.setRoi(null);
                                            imageJVRLvalue.setRoiList(polygonRoiList); // empty List of ROIs

                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_E) {// editMenu the common ROI

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

                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_Z) { //edit ROI

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
                                        }

                                        if (e.getKeyCode() == KeyEvent.VK_TAB && polygonRoiList.size() > 1) { //edit ROI

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                            polygonRoi = polygonRoiList.get(0);
                                            polygonRoiList.remove(0);
                                            polygonRoiList.add(polygonRoi);
                                            printRois(polygonRoiList, polygonRoi, imagePlus);

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
     * @param roi current ROI to print editMenuable
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
