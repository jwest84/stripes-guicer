package com.googlecode.stripesguicer;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

import com.google.inject.Injector;

public final class GuiceInjectorFactory {
    private static Injector injector;
    
    public static void setInjector(Injector injector){
        GuiceInjectorFactory.injector = injector;
    }
    
    public static Injector getInjector(){
        return injector;
    }
    
    public static ActionBean makeGuicedActionBean(Class<? extends ActionBean> type,
            ActionBeanContext context){
        GuiceInjectorFactory.getInjector().injectMembers(context);
        return GuiceInjectorFactory.getInjector().getInstance(type);
    }
}
