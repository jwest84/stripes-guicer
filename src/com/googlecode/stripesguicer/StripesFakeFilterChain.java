package com.googlecode.stripesguicer;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Injector;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.StripesFilter;
/**
 * 
 * <p>A FilterChain called by Guice's GuiceFilter. Since we don't want to 
 * add yet another filter to the web.xml, we can give GuiceFilter this guy,
 * and GuiceFilter will happily run whatever's inside. In our case, we'll
 * inject the members of our Stripes beans, injectors, and whatever else we
 * want, and run the Lifecycle method.</p>
 * 
 * @author sberan@gmail.com (Sam Beran)
 */
public class StripesFakeFilterChain implements FilterChain {

    private ExecutionContext context;
    private Resolution resolution;
    private Exception exception = null;
    public StripesFakeFilterChain(ExecutionContext context){
        this.context = context;
    }
    
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse){
        try{
            Configuration conf = StripesFilter.getConfiguration();
            Injector injector = GuiceInjectorFactory.getInjector();
            
            injector.injectMembers(context.getActionBean());
            injector.injectMembers(context.getActionBeanContext());
            for(Interceptor interceptor : 
                    conf.getInterceptors(context.getLifecycleStage())) {
                injector.injectMembers(interceptor);
            }
            
            resolution = context.proceed();
        }
        catch(Exception e){
            exception = e;
        }
    }
    
    public Resolution getResolution(){
        return resolution;
    }
    
    public Exception getException(){
        return exception;
    }


}
