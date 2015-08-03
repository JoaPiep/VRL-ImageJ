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
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.MessageDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JFileChooser;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
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
    protected ImageWindow iw;
    protected ImagePlus imagePlus = new ImagePlus();
    protected FloatPolygon floatPolygon;
    protected PolygonRoi polygonRoi;
    protected RoiManager roiManager = new RoiManager(true);
    protected ArrayList<PolygonRoi> tempList = new ArrayList<PolygonRoi>();
    protected ImageProcessor imageProcessor;
    protected ImageJVRL imageJVRLvalue;
    private boolean saveRoiInVRL = true;

    public ImageJPolygonRoiType() {

        plotPane = new PlotPane(this);

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        setValueName("");
        nameLabel.setAlignmentX(0.0f);

        setUpdateLayoutOnValueChange(false);

        setMinimumPlotPaneSize(new Dimension(70, 70));

        plotPane.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getButton() == MouseEvent.BUTTON1
                        && e.getClickCount() == 2) {

                            if (plotPane.getImage() != null || isInput()) {

                                roiManager.setVisible(true);
                                //roiManager.runCommand(moreButtonLabel);
                                //roiManager.setSize(new Dimension(200, 400));

                                iw = new ImageWindow(imagePlus);

                                //roiManager.runCommand("Show All with labels");
                                MenuBar menuBar = new MenuBar();

                                Menu editMenu = new Menu("Edit");
                                Menu helpMenu = new Menu("Help");

                                MenuItem removeAllRoisItem = new MenuItem("Remove all ROIs", new MenuShortcut(KeyEvent.VK_R));
                                MenuItem editItem = new MenuItem("Edit ROI", new MenuShortcut(KeyEvent.VK_E));
                                MenuItem removeItem = new MenuItem("Remove ROI", new MenuShortcut(KeyEvent.VK_Z));
                                MenuItem changeItem = new MenuItem("Change active ROI", new MenuShortcut(KeyEvent.VK_N));
                                MenuItem saveRoisItem = new MenuItem("Save ROIs in File", new MenuShortcut(KeyEvent.VK_S));
                                MenuItem loadRoisItem = new MenuItem("Load ROIs from File", new MenuShortcut(KeyEvent.VK_L));
                                MenuItem editModusItem = new MenuItem("Open Roi Manager", new MenuShortcut(KeyEvent.VK_M));

                                editMenu.add(changeItem);
                                editMenu.add(editItem);
                                editMenu.add(removeItem);
                                editMenu.add(removeAllRoisItem);
                                editMenu.add(saveRoisItem);
                                editMenu.add(loadRoisItem);
                                editMenu.add(editModusItem);

                                menuBar.add(editMenu);
                                menuBar.setHelpMenu(helpMenu);
                                iw.setMenuBar(menuBar);

                                saveRoisItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        if (roiManager.getCount() != 0) {
                                            File file = null;
                                            try {
                                                JFileChooser fileChooser = new JFileChooser();
                                                fileChooser.showSaveDialog(iw);
                                                file = fileChooser.getSelectedFile();
                                                if (file != null) {
                                                    imageJVRLvalue.saveROIsInFileRM(file, roiManager);
                                                }
                                            } catch (IOException ex) {
                                                Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } else {
                                            MessageDialog md = new MessageDialog(iw, "Message:", "No ROIs to save!");
                                        }
                                    }
                                });

                                loadRoisItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        File file = null;
                                        try {
                                            JFileChooser fileChooser = new JFileChooser();
                                            fileChooser.showOpenDialog(iw);
                                            file = fileChooser.getSelectedFile();
                                            if (file != null) {
                                                roiManager = imageJVRLvalue.getROIsfromFileRM(file);

                                                if (roiManager.getCount() != 0) {
                                                    polygonRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);
                                                }
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                imageJVRLvalue.setRoiManager(roiManager);
                                                imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                                iw.updateImage(imagePlus);
                                                IJ.run("Colors...", "foreground=blue selection=yellow");
                                                roiManager.runCommand("Draw");
                                                printPolygonRoi(polygonRoi, imagePlus);
                                            }
                                        } catch (IOException ex) {
                                            Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
                                            ex.printStackTrace(System.err);
                                        } catch (ClassNotFoundException ex) {
                                            Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });

                                removeAllRoisItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                        iw.updateImage(imagePlus);
                                        floatPolygon = new FloatPolygon();
                                        polygonRoi = new PolygonRoi(floatPolygon,
                                                Roi.POLYGON);
                                        roiManager.reset();
                                        imageJVRLvalue.setRoiManager(roiManager);
                                        imageJVRLvalue.setRoi(polygonRoi);
                                    }
                                }
                                );

                                editItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                        if (roiManager.getCount() != 0) {
                                            PolygonRoi tempRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);
                                            roiManager.select(roiManager.getCount() - 1);
                                            roiManager.runCommand("Delete");
                                            if (tempRoi != null) {
                                                floatPolygon = tempRoi.getFloatPolygon();
                                            }
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            IJ.run("Colors...", "foreground=blue selection=yellow");
                                            roiManager.runCommand("Draw");
                                            imagePlus.setRoi(polygonRoi);
                                        } else {
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                        }
                                        imageJVRLvalue.setRoi(polygonRoi);
                                        imageJVRLvalue.setRoiManager(roiManager);

                                    }
                                }
                                );

                                removeItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                        if (roiManager.getCount() > 1) {
                                            roiManager.select(roiManager.getCount() - 1);
                                            roiManager.runCommand("Delete");
                                            polygonRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);
                                            IJ.run("Colors...", "foreground=blue selection=yellow");
                                            roiManager.runCommand("Draw");
                                            printPolygonRoi(polygonRoi, imagePlus);

                                        } else if (roiManager.getCount() == 1) {
                                            floatPolygon = new FloatPolygon();
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);
                                            roiManager.reset();
                                        }
                                        imageJVRLvalue.setRoiManager(roiManager);
                                        imageJVRLvalue.setRoi(polygonRoi);
                                    }
                                }
                                );

                                changeItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        if (roiManager.getCount() != 0) {
                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            polygonRoi = (PolygonRoi) roiManager.getRoi(0);
                                            roiManager.select(0);
                                            roiManager.runCommand("Delete");
                                            roiManager.addRoi(polygonRoi);
                                            imageJVRLvalue.setRoi(polygonRoi);
                                            imageJVRLvalue.setRoiManager(roiManager);

                                            IJ.run("Colors...", "foreground=blue selection=yellow");
                                            roiManager.runCommand("Draw");
                                            printPolygonRoi(polygonRoi, imagePlus);
                                        }
                                    }
                                }
                                );

                                editModusItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        roiManager.setVisible(true);
                                    }
                                }
                                );

                                imageCanvas = iw.getCanvas();

                                imageCanvas.addMouseMotionListener(new MouseMotionAdapter() {

                                    @Override
                                    public void mouseDragged(MouseEvent e) {

                                        if (polygonRoi.isHandle(imageCanvas.offScreenX(e.getX()), imageCanvas.offScreenY(e.getY())) >= 0) {
                                            floatPolygon.xpoints[polygonRoi.isHandle(imageCanvas.offScreenX(e.getX()), imageCanvas.offScreenY(e.getY()))] = imageCanvas.offScreenX(e.getX());
                                            floatPolygon.ypoints[polygonRoi.isHandle(imageCanvas.offScreenX(e.getX()), imageCanvas.offScreenY(e.getY()))] = imageCanvas.offScreenY(e.getY());
                                        }
                                        e.consume();
                                    }
                                });

                                iw.addMouseWheelListener(new MouseWheelListener() {

                                    @Override
                                    public void mouseWheelMoved(MouseWheelEvent e) {
                                        if (e.getWheelRotation() < 0) {
                                            imageCanvas.zoomIn(imageCanvas.screenX(e.getX()),
                                                    imageCanvas.screenY(e.getY()));
                                        } else {
                                            imageCanvas.zoomOut(imageCanvas.screenX(e.getX()),
                                                    imageCanvas.screenY(e.getY()));
                                        }
                                    }
                                });

                                floatPolygon = new FloatPolygon();

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
                                            imagePlus.setRoi(polygonRoi);
                                        }

                                        if (e.getButton() == MouseEvent.BUTTON3
                                        && e.getClickCount() == 1) {
                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            if (polygonRoi.getNCoordinates() > 2) {
                                                printPolygonRoi(polygonRoi, imagePlus);
                                                imageJVRLvalue.setRoi(polygonRoi);

                                                ArrayList<Roi> list = new ArrayList<Roi>(Arrays.asList(roiManager.getRoisAsArray()));
                                                if (list.contains(polygonRoi) == false) {
                                                    roiManager.addRoi(polygonRoi);
                                                    System.out.println("ROI MANAGER SIZE " + roiManager.getCount());

                                                } else { // polygonRoi at the first position in Roi Manager
                                                    roiManager.select(roiManager.getCount() - 1);
                                                    roiManager.runCommand("Delete");
                                                    roiManager.addRoi(polygonRoi);
                                                }

                                                printRoisRManager(roiManager, polygonRoi, imagePlus);
                                                
                                            } else {
                                                if (roiManager.getCount() != 0) {
                                                    polygonRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);
                                                } else {
                                                    floatPolygon = new FloatPolygon();
                                                    polygonRoi = new PolygonRoi(floatPolygon,
                                                            Roi.POLYGON);
                                                }
                                                imageJVRLvalue.setRoi(polygonRoi);
                                            }
                                            imageJVRLvalue.setRoiManager(roiManager);
                                            floatPolygon = new FloatPolygon();
                                        }
                                    }
                                });

                                iw.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent ew) {

                                        plotPane.setImage(imagePlus.getImage());

                                        if (polygonRoi != null) {
                                            if (polygonRoi.getNCoordinates() > 2) {
                                                printPolygonRoi(polygonRoi, imagePlus);
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                
                                                ArrayList<Roi> list = new ArrayList<Roi>(Arrays.asList(roiManager.getRoisAsArray()));
                                                if (list.contains(polygonRoi) == false) {
                                                    roiManager.addRoi(polygonRoi);
                                                    imageJVRLvalue.setRoiManager(roiManager);
                                                    System.out.println("ROI MANAGER SIZE " + roiManager.getCount());
                                                }
                                            } else {
                                                if (roiManager.getCount() != 0) {
                                                    polygonRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);
                                                    printPolygonRoi(polygonRoi, imagePlus);
                                                } else {
                                                    polygonRoi = new PolygonRoi(new FloatPolygon(),
                                                            Roi.POLYGON);
                                                }
                                                imageJVRLvalue.setRoi(polygonRoi);
                                            }
                                        }

                                        if (saveRoiInVRL) {
                                            if (polygonRoi != null) {
                                                if (polygonRoi.getNCoordinates() != 0) {
                                                    imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                                                } else {
                                                    imageJVRLvalue.setRoiData(null);
                                                }
                                            }

                                            if (roiManager.getCount() != 0) {
                                                imageJVRLvalue.encodeROIListRM(roiManager);
                                            } else {
                                                imageJVRLvalue.setRoiDataListRM(new ArrayList<String>());
                                            }
                                        }

                                        setDataOutdated();
                                        roiManager.close();
                                    }
                                });
                            }
                        }
                    }
                });

        this.add(Box.createHorizontalStrut(3));

        container = Box.createVerticalBox();

        container.add(nameLabel);

        container.add(plotPane);

        this.add(container);

        this.setInputComponent(container);

    }

    @Override
    public void emptyView() {
        if (imagePlus.getImage() != null) {
            plotPane.setImage(imagePlus.getImage());
        } else {
            plotPane.setImage(null);
        }

    }

    @Override
    public void setViewValue(Object o) {

        setDataOutdated();
        ImageJVRL imageJVRL = null;
        try {
            imageJVRL = (ImageJVRL) o;
        } catch (Exception e) {
        }

        if (imageJVRL.getImage() != null) {
            imagePlus.setImage(imageJVRL.getImage());
            imagePlus.setTitle("Image");
        }

        if (imageJVRLvalue == null || isOutput()) {

            imageJVRLvalue = new ImageJVRL(imageJVRL.getImage());

            if (imageJVRL.getRoiData() != null && polygonRoi == null) {
                polygonRoi = imageJVRL.decodeROI();
                imageJVRLvalue.setRoi(polygonRoi);
                imageJVRL.setRoi(polygonRoi);
            }

            if (!imageJVRL.getRoiDataListRM().isEmpty() && roiManager.getCount() == 0) {
                
                roiManager = imageJVRL.decodeROIListRM(imagePlus);
                imageJVRLvalue.setRoiManager(roiManager);
                imageJVRL.setRoiManager(roiManager);

            }

            if (saveRoiInVRL) {
                if (polygonRoi != null) {
                    if (polygonRoi.getNCoordinates() != 0) {
                        imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                    } else {
                        imageJVRLvalue.setRoiData(null);
                    }
                }

                if (roiManager.getCount() > 0) {
                    imageJVRLvalue.encodeROIListRM(roiManager);
                } else {
                    imageJVRLvalue.setRoiDataListRM(new ArrayList<String>());
                }
            }

        } else {

            if (isInput()) {
                imageJVRLvalue.setImage(imageJVRL.getImage());

                if (!imageJVRL.getAutoGenerateRoiList().isEmpty()) {
                    ArrayList<PolygonRoi> list = imageJVRL.getAutoGenerateRoiList();

                    if ((!list.equals(tempList) && !tempList.isEmpty()) || imageJVRL.isGenerateRois()) {

                        ArrayList<PolygonRoi> roiTempList = new ArrayList<PolygonRoi>();
                        Roi[] rois = roiManager.getRoisAsArray();

                        roiManager.reset();

                        for (Roi roi : rois) {
                            roiTempList.add((PolygonRoi) roi);
                        }

                        for (PolygonRoi roi : tempList) {
                            if (roiTempList.contains(roi)) {
                                roiTempList.remove(roi);
                            }
                        }

                        for (PolygonRoi roi : list) {
                            roiTempList.add(roi);
                        }

                        for (PolygonRoi roi : roiTempList) {
                            roiManager.addRoi(roi);
                        }

                        polygonRoi = (PolygonRoi) roiManager.getRoi(roiManager.getCount() - 1);

                        if (saveRoiInVRL) {
                            if (polygonRoi != null) {
                                if (polygonRoi.getNCoordinates() != 0) {
                                    imageJVRLvalue.encodeROI(polygonRoi); // set the string roiData
                                } else {
                                    imageJVRLvalue.setRoiData(null);
                                }
                            }

                            if (roiManager.getCount() > 0) {
                                imageJVRLvalue.encodeROIListRM(roiManager);
                            } else {
                                imageJVRLvalue.setRoiDataListRM(new ArrayList<String>());
                            }
                        }
                    }
                    tempList = imageJVRL.getAutoGenerateRoiList();
                }

                imageJVRL.setRoi(polygonRoi);
                imageJVRL.setRoiManager(roiManager);

                printRoisRManager(roiManager, polygonRoi, imagePlus);

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

        if (!polygonRoiList.isEmpty() && polyRoi != null) {
            if (polygonRoiList.size() > 1) {
                for (int i = 0; i < polygonRoiList.size(); i++) {
                    if (!polygonRoiList.get(i).equals(polyRoi)) {
                        imProcessor.setColor(Color.blue);
                        polygonRoiList.get(i).setStrokeWidth(2);
                        polygonRoiList.get(i).drawPixels(imProcessor);
                    } else {
                        imProcessor.setColor(Color.red);
                        polygonRoiList.get(i).setStrokeWidth(2);
                        polygonRoiList.get(i).drawPixels(imProcessor);
                    }
                }
            } else if (polygonRoiList.size() == 1) {
                imProcessor.setColor(Color.red);
                polygonRoiList.get(0).setStrokeWidth(2);
                polygonRoiList.get(0).drawPixels(imProcessor);
            }
        } else {
            plotPane.setImage(imagePlus.getImage());
            System.out.println("Print no rois!");
        }
    }

    /**
     *
     * @param manager RoiManager with roisRM
     * @param polyRoi current Roi
     * @param ip current ImagePlus
     */
    private void printRoisRManager(RoiManager manager, PolygonRoi polyRoi, ImagePlus ip) {
        ImageProcessor imProcessor = ip.getProcessor();
        Roi[] rois = manager.getRoisAsArray();
        if (manager.getCount() > 0 && polyRoi != null) {
            for (Roi roi : rois) {
                if (!roi.equals(polyRoi)) {
                    imProcessor.setColor(Color.blue);
                    roi.setStrokeWidth(1);
                    roi.drawPixels(imProcessor);
                } else {
                    imProcessor.setColor(Color.red);
                    roi.setStrokeWidth(1);
                    roi.drawPixels(imProcessor);
                }
            }
        }

    }

    /**
     *
     * @param roi current ROI to print editMenuable
     * @param ip current ImagePlus
     */
    private void printRoi(PolygonRoi roi, ImagePlus ip) {
        if (roi != null) {
            roi.setStrokeWidth(1.5);
            ip.setRoi(roi);
        }
    }

    /**
     *
     * @param roi current Roi to print (color - red)
     * @param ip current ImagePlus
     */
    private void printPolygonRoi(PolygonRoi roi, ImagePlus ip) {
        ImageProcessor imProcessor = ip.getProcessor();
        if (roi != null) {
            imProcessor.setColor(Color.red);
            roi.setStrokeWidth(1);
            roi.drawPixels(imProcessor);
        }
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
