package com.googlecode.stripesguicer;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.StripesFilter;

/**
 * <p>Intercepts all Lifecycle methods and surrounds them with the 
 * {@link StripesFakeFilterChain} which will perform injection and
 * continue the execution chain.</p>
 * 
 * @author sberan@gmail.com (Sam Beran)
 *
 */
@Intercepts({LifecycleStage.ActionBeanResolution,
    LifecycleStage.HandlerResolution,
    LifecycleStage.BindingAndValidation,
    LifecycleStage.CustomValidation,
    LifecycleStage.EventHandling,
    LifecycleStage.ResolutionExecution,
    LifecycleStage.RequestInit,
    LifecycleStage.RequestComplete})
public class GuiceInterceptor implements Interceptor {
    private final String GUICE_MODULE_CLASSES_PARAM_NAME = "Guice.ModuleClasses";
    
    private GuiceFilter guiceFilter = null;
    
    private void init() throws ServletException{
    
        guiceFilter = new GuiceFilter();
        guiceFilter.init(new FilterConfig(){
    
            public String getFilterName() {return null;}
    
            public String getInitParameter(String arg0) {return null;}
            @SuppressWarnings("unchecked")
            public Enumeration getInitParameterNames() {return null;}
    
            public ServletContext getServletContext() {return null;}
            
        });  
        GuiceInjectorFactory.setInjector( 
                Guice.createInjector( getModuleList() ) );
    }
    
    public Resolution intercept(ExecutionContext context) throws Exception{
            if(guiceFilter == null){
                init();
            }
            
            
            StripesFakeFilterChain chain = new StripesFakeFilterChain(context);
            guiceFilter.doFilter(context.getActionBeanContext().getRequest(), 
                                 context.getActionBeanContext().getResponse(), 
                                 chain );
            
            if( chain.getException() != null)
                throw chain.getException();
            
            return chain.getResolution();
        
    }
    
    
    private Module[] getModuleList() {
        List<Module> modules = new ArrayList<Module>();
        
        modules.add(new ServletModule());
        
        
        for(String moduleClassName : getModuleNames()){
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
    
    private List<String> getModuleNames(){
        String rawModuleParam = StripesFilter.getConfiguration()
                         .getBootstrapPropertyResolver()
                         .getFilterConfig()
                         .getInitParameter(GUICE_MODULE_CLASSES_PARAM_NAME);
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
