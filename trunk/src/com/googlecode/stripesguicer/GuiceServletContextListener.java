package com.googlecode.stripesguicer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;

public final class GuiceServletContextListener implements ServletContextListener {
    private final String GUICE_MODULE_CLASSES_PARAM_NAME = "Guice.ModuleClasses";
    
    public void contextDestroyed(ServletContextEvent arg0) {

    }

    public void contextInitialized(ServletContextEvent ctx) {
        try{
            
            GuiceInjectorFactory.setInjector( 
                    Guice.createInjector(
                            getModuleList(ctx) ) );
        }
        catch(Throwable t){
            t.printStackTrace(System.err);
        }
    }

    
    private Module[] getModuleList(ServletContextEvent ctx) {
        List<Module> modules = new ArrayList<Module>();
        
        modules.add(new ServletModule());
        
        
        for(String moduleClassName : getModuleNames(ctx)){
            String errorText = "[stripes-guice] Error loading guice module "+moduleClassName+": ";
            
            try{
                Class<?> moduleClass = Class.forName(moduleClassName);
                Module m = (Module) moduleClass.newInstance();
                modules.add(m);
                
            }
            catch(ClassNotFoundException e){
                System.err.println(errorText + "module was not found");
            }
            catch(InstantiationException e){
                System.err.println(errorText + "exception instantiating module: ");
                e.getCause().printStackTrace(System.err);
            }
            catch(IllegalAccessException e){
                System.err.println(errorText + "unable to access default module constructor");
            }
            catch(ClassCastException e ){
                System.err.println(errorText + "the specified class is not a Guice module");
            }
        
        }
        return modules.toArray(new Module[0]);
    }
    
    private List<String> getModuleNames(ServletContextEvent ctx){
        String rawModuleParam = ctx.getServletContext().getInitParameter(GUICE_MODULE_CLASSES_PARAM_NAME);
        List<String> moduleNames = new ArrayList<String>();
        if(rawModuleParam != null && !rawModuleParam.trim().equals("")){
            for(String moduleClassName : rawModuleParam.split(",")){
                if(moduleClassName != null && !moduleClassName.trim().equals("")){
                    moduleNames.add(moduleClassName.trim());
                }
            }
        }
        return moduleNames;
                
    }

}
