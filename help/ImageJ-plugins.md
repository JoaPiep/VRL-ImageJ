CSS:	resources/css/vrl-documentation.css

# Using ImageJ components in VRL-Studio #

## Requirements ##

- VRL-Studio >= 4.5.5
- VRL >= 0.4.2

## Depends on ##

[Defining a Workflow](defining-a-workflow.html)

[Creating your first project](creating-your-first-project.html)

[Create your first VRL - Plugin](first-plugin.html) (external link)


## Contents ##

In this tutorial you will learn how tog

- use Image-Filters-0.1 components
- use ImageJ library in VRL-Studio


## Introduction ##

ImageJ is an image processing and analysis program written in Java. It runs, either as an online applet or as a downloadable application, on any computer with a Java 1.4 or later virtual machine. It can display, edit, analyze, process, save and print images. To see the official ImageJ website click [here](#http://rsb.info.nih.gov/ij/) (external link).

## Difficulty Level ##

### Difficulty Level ###

Intermediate

## Creating a New Project ##

1. Start VRL-Studio
2. If a dialog shows up, that asks whether to create or load project, click `Cancel`.
3. Click on `File->New-Project`
4. A file dialog will appear. Choose the destination of your project.

## Using the Image-Filters-0.1 components ##

- Download *Image-Filters-0.1*
- Click on `Plugins->Install-Plugin`
 
![Install Plugin][]
[Install Plugin]: /install-plugin.png 
 width="600px"

- Search the file *Image-Filters-0.1* and open it
- Save your project
- Restart VRL-Studio and open your project
- Click on `File->Select-Plugins`

 ![Select Plugins][]
[Select Plugins]: /select-plugins.png 
 width="600px"

- Select *Image-Filters-0.1*
- To display the component open the component management window. To do so, press `CTRL+SPACE`.  A window (*Manage Components*) should appear
-  To add the component click on the custom category *Filters*

 ![Select Filters][]
[Select Filters]: /select-filters.png 
 width="600px"

-  Now drag the *Image-Filters* component to the *canvas*. At the destination of the drag gesture a window will appear after releasing the mouse button. The new window is the visual representation of component  *Image-Filters*.

 ![Select Function][]
[Select Function]: /select-function.png  
width="600px"

## Image-Filters-0.1 components ##

The plugin has five components. With Image-Filters-0.1-Plugin you can save, load and filter the chosen images.

### Load image ###

With *loadImage(File file)*-function you can load an image that you later use. At first you must choose the location of your image. When you click `invoke` the image will be loaded.

 ![Load the Image][]
[Load the Image]: /load-image.png 
width="600px"

### Save image ###

With *saveImage(File file, Image image)* you can save the given image in the selected location and the chosen name. The name of your image must include the type of your image: e.g. *.png* or *.jpg*. When you click `invoke` the image will be saved.

![Save the Image][]
[Save the Image]: /save-image.png 
 width="600px"

### 3x3 minimum filter ###

*min3x3Filter(Image image)* use the *dilate()*-function from the class *ColorProcessor* in *ImageJ*-library. *dilate()* dilates the image or ROI using a 3x3 minimum filter. Requires 8-bit or RGB image. 

![3x3 Filter in VRL-Studio][]
[3x3 Filter in VRL-Studio]: /filter-3x3.png
 width="600px"

![3x3 Filter Before][]
[3x3 Filter Before]: /image1.jpg "Before using 3x3 filter" width="600px"

![3x3 Filter After][]
[3x3 Filter After]: /image-filter3x3.png "After using 3x3 filter"  width="600px"

### Gaussian blur ###

*gaussianBlur(Image image, double sigma)* use the *blurGaussian(double sigma)*-function from the class *ColorProcessor* in *ImageJ*-library. *blurGaussian(double sigma)* blurs the image by convolving with a Gaussian function. 

![Gaussian Blur1][]
[Gaussian Blur1]: /gaussian-blur.png 
 width="600px"

![Gaussian Blur Before][]
[Gaussian Blur Before]: /image2.png 
 width="450px"

![Gaussian Blur After][]
[Gaussian Blur After]: /image-gblur.png 
 width="450px"

### Median filter ###

*medianFilter(Image image)* use the *medianFilter()*-function from the class *ColorProcessor* in *ImageJ*-library. *medianFilter()* is a 3x3 median filter. Requires 8-bit or RGB image. 

![Median Filter1][]
[Median Filter1]: /median-filter.png 
 width="600px"

![Median Filter Original][]
[Median Filter Original]: /image1.jpg 
 width="600px"

![Median Filter Before][]
[Median Filter Before]: /image1-defect.png 
 width="600px"

![Median Filter After][]
[Median Filter After]: /image-median-filter.png 
 width="600px"

##Source code example##

You can use in VRL-Studio the ImageJ-library. Example - gaussian blur:

 	  public Image gaussian(Image image, double sigma){

		  ImageProcessor ip = new ColorProcessor(image);
      	  ip.blurGaussian(sigma);
      	  Image ig = ip.createImage();
        
      	  return ig;
     }

![ImageJ in VRL-Studio][]
[ImageJ in VRL-Studio]: /Test-class.png 
 width="600px"

## Further Reading ##

- [Version Management](version-management.html)
