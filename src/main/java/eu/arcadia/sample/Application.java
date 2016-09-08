package eu.arcadia.sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.arcadia.ArcadiaConstants;
import eu.arcadia.agentglue.GroundedComponentInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;

/**
 * Created by nikos on 5/5/2016.
 */
@Configuration
@ComponentScan(basePackages="eu.arcadia.sample")
@EnableAutoConfiguration
@RestController
public class Application extends SpringBootServletInitializer {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    public static final Integer defaultPort = 8080;
    public static final String getterURI = "/getInfo";


    @RequestMapping(value = getterURI, method = RequestMethod.GET)
    public String getInfo() {
        String serverEndpoint = System.getProperty("Component.connectedEndpoint");
        System.setProperty("Component.receivedRequests",String.valueOf(Integer.parseInt(System.getProperty("Component.receivedRequests"))+1));
        System.setProperty("Component.sentRequests",String.valueOf(Integer.parseInt(System.getProperty("Component.sentRequests"))+1));

        String ret = "[--- TPM Info ---]\n";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            GroundedComponentInfo groundedComponentInfo = gson.fromJson(new FileReader(ArcadiaConstants.basePathAgent + File.separator + ArcadiaConstants.groundedInfoJsonFn), GroundedComponentInfo.class);
            ret += gson.toJson(groundedComponentInfo);
        } catch (FileNotFoundException e) {
            ret += "Error getting information about TPM !";
            e.printStackTrace();
        }
        try {
            HttpResponse<String> response = Unirest.get(serverEndpoint).asString();
            ret += response.getBody();
        } catch (UnirestException e) {
            ret += "Error getting information about TPU !";
            e.printStackTrace();
        }
        return ret;

    }

    //Just needed to compile the jar. Never gets called.
    public static void main(String[] args) {
    }
}
