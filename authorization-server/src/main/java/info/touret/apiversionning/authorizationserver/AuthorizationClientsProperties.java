package info.touret.apiversionning.authorizationserver;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="authorization")
public class AuthorizationClientsProperties {

    private Map<String,ClientsConfigurationProperties> clients;

    @ConfigurationProperties
    public Map<String,ClientsConfigurationProperties> getClients(){
        return clients;
    }
    public void setClients(Map<String,ClientsConfigurationProperties> clients){
        this.clients = clients;
    }

}