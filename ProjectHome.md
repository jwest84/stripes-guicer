# Guice Integration for Stripes #

_Stripes-guicer is early alpha software. Download and use at your own risk, and please leave bug reports!_

## What Do I Get with stripes-guicer? ##

### Constructor, Field, and Method Injection in Stripes ActionBean, ActionBeanContext, and Interceptors ###
(_Constructor injection is only available in ActionBeans._)

> For example, if MyActionBean relies on MyService:

```
public class MyActionBean implements ActionBean {

    private MyService myService;
    private MyService otherService;

    @Inject
    public MyActionBean( MyService myService ) {
        this.myService = myService;
    }

    @Inject public void setService(@MyAnnotation MyService service){
        this.otherService = service;
    }
}
```


> ActionBeanContext injection:

```
public MyActionBeanContext  extends ActionBeanContext {
    ...
    @Inject public void setService(MyService service) { ... }
}
```

> Interceptor injection. This example will close MyService after each request:

```
@Intercepts(LifecycleStage.RequestComplete)
public class MyInterceptor implements Interceptor {
    @Inject private MyService service;
    public Resolution intercept(ExecutionContext executionContext) {
       service.close();
    }
}

```

### Request scope bindings ###

> This Guice module will give one MyService per request, no matter where it is injected:

```
public class MyModule extends AbstractModule {
    protected void configure() {
        bind(MyService.class)
            .to(MyServiceImpl.class)
            .in(ServletScopes.REQUEST);
    }
}
```


# How Do I Get Guicing? #
Prerequisites:
  * Currently, stripes-guicer is only tested on Stripes 1.5rc1. It may work on older versions, but  I can make no guarantees at this point.
  * This guide assumes that Stripes is already properly set up.
  * guice-1.0.jar, and guice-servlet-1.0.jar, available [here](http://code.google.com/p/google-guice) in guice-1.0.zip.
  * stripes-guicer-0.2.jar, from the Downloads tab of this site
  * The following init-parameters must be added to StripesFilter configuration in web.xml:

Under Stripes' 

&lt;filter&gt;

 tag:
```

        <!-- For Guice -->
            <init-param>
                <param-name>Interceptor.Classes</param-name>
                <param-value>com.googlecode.stripesguicer.GuiceInterceptor</param-value> 
            </init-param>
	    <init-param>
                <param-name>ActionResolver.Class</param-name>
                <param-value>com.googlecode.stripesguicer.GuiceActionResolver</param-value> 
            </init-param>
            <init-param>
	           <param-name>Guice.ModuleClasses</param-name>
	           <param-value>
	               <!-- put your guice modules here, comma separated -->
	               my.example.InjectorModule
	           </param-value>
            </init-param>
        <!-- end -->
```
**Notes**:
  * Make sure to list all custom Guice modules under Guice.ModuleClasses
  * **If you already have Interceptor.Classes defined, add com.googlecode.stripesguicer.GuiceInterceptor to the _beginning_ of the list**
  * If you need to use a custom ActionResolver, or one other than the NameBasedActionResolver (the default) you will have to override makeNewActionBean() like so:

```
protected ActionBean makeNewActionBean(Class<? extends ActionBean> type,
            ActionBeanContext context) {
        return GuiceInjectorFactory.makeGuicedActionBean(type, context);
    }
```