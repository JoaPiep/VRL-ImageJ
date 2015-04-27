/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package image.filters;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;

/**
 *
 * @author Joanna Pieper
 */
public class ImageFiltersPluginConfigurator extends VPluginConfigurator {
    
     public ImageFiltersPluginConfigurator() {
        //specify the plugin name and version
       setIdentifier(new PluginIdentifier("Image-Filters", "0.1"));

       // describe the plugin
       setDescription("Plugin Description");

       // copyright info
       setCopyrightInfo("Sample-Plugin",
               "(c) Joanna Pieper",
               "www.you.com", "License Name", "License Text...");
       
       exportPackage("image.filters");

       // specify dependencies
       // addDependency(new PluginDependency("VRL", "0.4.0", "0.4.0"));
    }
    
    @Override
    public void register(PluginAPI api) {

       // register plugin with canvas
       if (api instanceof VPluginAPI) {
           VPluginAPI vapi = (VPluginAPI) api;

           // Register visual components:
           //
           // Here you can add additional components,
           // type representations, styles etc.
           //
           // ** NOTE **
           //
           // To ensure compatibility with future versions of VRL,
           // you should only use the vapi or api object for registration.
           // If you directly use the canvas or its properties, please make
           // sure that you specify the VRL versions you are compatible with
           // in the constructor of this plugin configurator because the
           // internal api is likely to change.
           //
           // examples:
           //
           // vapi.addComponent(MyComponent.class);
           // vapi.addTypeRepresentation(MyType.class);
           
           vapi.addComponent(ImageFilters.class);
           vapi.addComponent(LoadStoreImageJVRL.class);
           vapi.addComponent(ConvertAndAnalyse.class);
           vapi.addTypeRepresentation(ImageJVRLType.class);
           vapi.addTypeRepresentation(ImageJPolygonRoiType.class);
       }
   }

    @Override
   public void unregister(PluginAPI api) {
       // nothing to unregister
   }

    @Override
    public void init(InitPluginAPI iApi) {
       // nothing to init
   }
    
}
