package com.googlecode.stripesguicer;


import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.NameBasedActionResolver;
/**
 * <p>Responsible for the creation of Guiced up ActionBean instances.</p>
 * 
 * <p> Rather than using Class.newInstance(), an ActionBean instances is created
 * using GuiceInjectorFactory.
 * StripesFilter must be configured to use this ActionResolver, or one which
 * overrides ActionResolver.makeNewActionBean() in the following way:</p>
 * <p>
 * <pre>
 *   @Override
 *   protected ActionBean makeNewActionBean(Class<? extends ActionBean> type,
 *           ActionBeanContext context) {
 *       return GuiceInjectorFactory.makeGuicedActionBean(type, context);
 *   }
 * </pre>
 * </p>
 * <p> This will allow guice to inject constructors as well as members in the ActionBean
 * like so: </p>
 * 
 * <p><pre>
 *   public class MyActionBean implements ActionBean {
 *      
 *      &#064;Inject
 *      public MyActionBean(MyService service){
 *        ...
 *      }
 *      
 *      
 *   
 *   }
 * </pre></p>
 * 
 * @see GuiceInjectorFactory
 * @author Sam Beran
 */
public final class GuiceActionResolver extends NameBasedActionResolver {

    protected ActionBean makeNewActionBean(Class<? extends ActionBean> type,
            ActionBeanContext context) {
        return GuiceInjectorFactory.makeGuicedActionBean(type, context);
    }
}
