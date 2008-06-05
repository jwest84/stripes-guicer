package com.googlecode.stripesguicer;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
/**
 * <p>A storage area for the Guice injector</p>
 * 
 * @author sberan@gmail.com (Sam Beran)
 *
 */
public final class GuiceInjectorFactory {
    private static Injector injector;
    private static GuiceFilter guiceFilter;
    
    public static void setInjector(Injector injector){
        GuiceInjectorFactory.injector = injector;
    }
    
    public static Injector getInjector(){
        return injector;
    }
    

    public static GuiceFilter getGuiceFilter(){
        return guiceFilter;
    }
    
    public static void setGuiceFilter(GuiceFilter guiceFilter){
        GuiceInjectorFactory.setGuiceFilter(guiceFilter);
    }
    
    /**
     * Uses Guice to create an ActionBean for the given type. This allows
     * for constructor injection in the ActionBean.
     * 
     * @param type
     * @param context
     * @return The newly created ActionBean
     * @throws Exception
     */
    public static ActionBean makeGuicedActionBean(Class<? extends ActionBean> type,
            ActionBeanContext context) throws Exception {
        
        try{
            return GuiceInjectorFactory.getInjector().getInstance(type);
        }
        catch(Exception e){
            e.getCause().printStackTrace();
            throw e;
        }
    }
    
}
