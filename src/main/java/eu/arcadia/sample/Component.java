package eu.arcadia.sample;

import eu.arcadia.agentglue.ChainingInfo;
import eu.arcadia.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.logging.Logger;


/**
 * Created by nikos on 26/5/2016.
 */
@ArcadiaComponent(componentname = "tpm001",componentversion = "0.0.1",componentdescription = "A transcoding management app",tags={"transcoder"})
@DependencyExport(CEPCID = "transcodingprocessor",allowsMultipleTenants = true)
public class Component {

    private static final Logger logger = Logger.getLogger(Component.class.getName());

    public static ConfigurableApplicationContext appContext;

    /*
    ArcadiaConfigurationParameter setter/getter
    */

    /*
    ArcadiaMetrics (getters only)
    */

    /*
    Component Lifecycle Management
    */
    @LifecycleInitialize
    public static void init(){
        System.setProperty("Component.sentRequests", String.valueOf(0));
        System.setProperty("Component.receivedRequests", String.valueOf(0));
        System.setProperty("app.port",Application.defaultPort.toString());
        logger.info("----INIT END---");
    }

    @LifecycleStart
    public static String start(){
        if(appContext == null) {
            appContext = SpringApplication.run(new Class[]{Application.class, CustomizationBean.class}, new String[]{});
        }else logger.severe("AppContext is not null ! Application is already started !");
        logger.info("----START END---");
        return String.valueOf(appContext.isActive());
    }

    @LifecycleStop
    public static String stop(){
        if(appContext != null) {
            SpringApplication.exit(appContext);
            appContext.close();
            appContext = null;
        }else logger.severe("AppContext is null ! Application has not been started !");
        return String.valueOf((appContext == null));

    }


    /*
    DependencyExport-related methods
    */
    public static String getUri(){
        return Application.getterURI;
    }

    public static String getPort(){
        return  System.getProperty("app.port");
    }


    
    //leaf
    @DependencyBindingHandler(CEPCID = "transcodingprocessor")
    public static void  bindDependency(ChainingInfo chainingInfo){
        logger.info("BINDED COMPONENT:"+chainingInfo.toString());
        String connectedEndpoint = "http://"+chainingInfo.getPrivateIP()+":"+chainingInfo.getParameterValues().get("port")+"/"+chainingInfo.getParameterValues().get("uri");
        logger.info("Connected Server URI:"+connectedEndpoint);
        System.setProperty("Component.connectedEndpoint",connectedEndpoint);

    }
}