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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JFileChooser;

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
    protected ImageWindow iw;
    protected ImagePlus imagePlus = new ImagePlus();
    protected FloatPolygon floatPolygon;
    protected PolygonRoi polygonRoi;
    protected ArrayList<PolygonRoi> polygonRoiList = new ArrayList<PolygonRoi>();
    protected ImageProcessor imageProcessor;
    private ImageJVRL imageJVRLvalue;
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

                                iw = new ImageWindow(imagePlus);

                                MenuBar menuBar = new MenuBar();

                                Menu editMenu = new Menu("Edit");
                                Menu helpMenu = new Menu("Help");

                                MenuItem removeAllRoisItem = new MenuItem("Remove all ROIs", new MenuShortcut(KeyEvent.VK_R));
                                MenuItem editItem = new MenuItem("Edit ROI", new MenuShortcut(KeyEvent.VK_E));
                                MenuItem removeItem = new MenuItem("Remove ROI", new MenuShortcut(KeyEvent.VK_Z));
                                MenuItem changeItem = new MenuItem("Change active ROI", new MenuShortcut(KeyEvent.VK_N));
                                MenuItem saveRoisItem = new MenuItem("Save ROIs in File", new MenuShortcut(KeyEvent.VK_S));
                                MenuItem loadRoisItem = new MenuItem("Load ROIs from File", new MenuShortcut(KeyEvent.VK_L));

                                editMenu.add(changeItem);
                                editMenu.add(editItem);
                                editMenu.add(removeItem);
                                editMenu.add(removeAllRoisItem);
                                editMenu.add(saveRoisItem);
                                editMenu.add(loadRoisItem);

                                menuBar.add(editMenu);
                                menuBar.setHelpMenu(helpMenu);
                                iw.setMenuBar(menuBar);

                                saveRoisItem.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        File file = null;
                                        try {
                                            JFileChooser fileChooser = new JFileChooser();
                                            fileChooser.showSaveDialog(iw);
                                            file = fileChooser.getSelectedFile();
                                            if (file != null) {
                                                imageJVRLvalue.saveROIsInFile(file);
                                            }
                                        } catch (IOException ex) {
                                            Logger.getLogger(ImageJPolygonRoiType.class.getName()).log(Level.SEVERE, null, ex);
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
                                                polygonRoiList = imageJVRLvalue.getROIsfromFile(file);
                                                polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                imageJVRLvalue.setRoiList(polygonRoiList);
                                                imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());
                                                iw.updateImage(imagePlus);
                                                printRois(polygonRoiList, polygonRoi, imagePlus);
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

                                        if (!polygonRoiList.isEmpty()) {
                                            PolygonRoi tempRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                            polygonRoiList.remove(polygonRoiList.size() - 1);
                                            if (tempRoi != null) {
                                                floatPolygon = tempRoi.getFloatPolygon();
                                            }
                                            polygonRoi = new PolygonRoi(floatPolygon,
                                                    Roi.POLYGON);

                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                            printRoi(polygonRoi, imagePlus);
                                        } else if (polygonRoiList.isEmpty()) {
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
                                imageCanvas = iw.getCanvas();

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
                                            printRoi(polygonRoi, imagePlus);

                                        } else if (e.getButton() == MouseEvent.BUTTON3
                                        && e.getClickCount() == 1) {

                                            imagePlus.setImage(((ImageJVRL) getViewValue()).getImage());

                                            if (polygonRoi.getNCoordinates() > 2) {
                                                imageJVRLvalue.setRoi(polygonRoi);
                                                if (polygonRoiList.contains(polygonRoi) == false) {
                                                    polygonRoiList.add(polygonRoi);
                                                    imageJVRLvalue.setRoiList(polygonRoiList);
                                                }
                                            } else {
                                                if (!polygonRoiList.isEmpty()) {
                                                    polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                    imageJVRLvalue.setRoi(polygonRoi);
                                                }
                                            }
                                            printRois(polygonRoiList, polygonRoi, imagePlus);
                                            floatPolygon = new FloatPolygon();
                                        }
                                    }
                                });

                                imageCanvas.addMouseMotionListener(new MouseMotionListener() {

                                    @Override
                                    public void mouseDragged(MouseEvent e) {
                                        Rectangle r = imageCanvas.getSrcRect();
                                        if (r.x > 0 || r.y > 0) {
                                            Point p = e.getPoint();
                                        }
                                    }

                                    @Override
                                    public void mouseMoved(MouseEvent e) {
                                    }
                                });

                                iw.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent ew) {
                                        plotPane.setImage(imagePlus.getImage());

                                        if (polygonRoi != null) {
                                            if (polygonRoi.getNCoordinates() > 2) {
                                                imageJVRLvalue.setRoi(polygonRoi);

                                                if (polygonRoiList.contains(polygonRoi) == false) {
                                                    polygonRoiList.add(polygonRoi);
                                                    imageJVRLvalue.setRoiList(polygonRoiList);
                                                }

                                            } else {
                                                if (!polygonRoiList.isEmpty()) {
                                                    polygonRoi = polygonRoiList.get(polygonRoiList.size() - 1);
                                                    imageJVRLvalue.setRoi(polygonRoi);
                                                }
                                            }

                                            if (saveRoiInVRL && imageJVRLvalue.getRoi().getNCoordinates() != 0) {
                                                imageJVRLvalue.encodeROI(); // set the string roiData
                                            } else {
                                                imageJVRLvalue.setRoiData(null);
                                            }

                                            if (saveRoiInVRL && !imageJVRLvalue.getRoiList().isEmpty()) {
                                                imageJVRLvalue.encodeROIList();// roiDataList
                                            } else {
                                                imageJVRLvalue.setRoiDataList(new ArrayList());
                                            }

                                            setDataOutdated();

                                            if (!polygonRoiList.isEmpty()) {
                                                printRois(polygonRoiList, polygonRoi, imagePlus);
                                            }
                                        }
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
        if (imagePlus != null) {
            plotPane.setImage(imagePlus.getImage());
        } else {
            plotPane.setImage(null);
        }
    }

    @Override
    public void setViewValue(Object o) {
        System.out.println(" ++++ Set View Value ++++ ");
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
            System.out.println(" ++ imageJVRLvalue == null ++ ");
            setImageJVRLvalue(imageJVRL);

            if (imageJVRL.getRoiData() != null && polygonRoi == null) {
                polygonRoi = imageJVRL.decodeROI();
                imageJVRL.setRoi(polygonRoi);
            }

            if (!imageJVRL.getRoiDataList().isEmpty() && polygonRoiList.isEmpty()) {
                polygonRoiList = imageJVRL.decodeROIList();
                imageJVRL.setRoiList(polygonRoiList);
            }

        } else {

            if (isInput()) {
                imageJVRLvalue.setImage(imageJVRL.getImage());

               if (imageJVRL.getRoiList() != null) { // generateRoisAuto()
                    if (!imageJVRL.getRoiList().isEmpty()) {
                        polygonRoiList = imageJVRL.getRoiList();
                        polygonRoi = imageJVRL.getRoiList().get(imageJVRL.getRoiList().size() - 1);
                    }
                }
                System.out.println(" ++ Is input ++ ");
                if (imageJVRLvalue.getRoi() != null) {
                    imageJVRL.setRoi(imageJVRLvalue.getRoi());
                    imageJVRL.setRoiList(imageJVRLvalue.getRoiList());
                }
                printRois(imageJVRLvalue.getRoiList(), imageJVRLvalue.getRoi(), imagePlus);
            }

        }
        plotPane.setImage(imagePlus.getImage());

    }

    @Override
    public Object getViewValue() {
        return this.imageJVRLvalue;
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
        }
    }

    /**
     *
     * @param roi current ROI to print editMenuable
     * @param ip current ImagePlus
     */
    private void printRoi(PolygonRoi roi, ImagePlus ip) {
        if (roi != null) {
            roi.setStrokeColor(Color.green);
            roi.setStrokeWidth(2);
            ip.setRoi(roi);
        }
    }

    /**
     *
     * @param imageJVRLvalue imageJVRLJVRLvalue to set
     */
    public void setImageJVRLvalue(ImageJVRL imageJVRLvalue) {
        this.imageJVRLvalue = imageJVRLvalue;
    }

    /**
     *
     * @return imageJVRLJVRLvalue
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
