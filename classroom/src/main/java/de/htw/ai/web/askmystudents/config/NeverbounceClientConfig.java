package de.htw.ai.web.askmystudents.config;

import com.neverbounce.api.client.NeverbounceClient;
import com.neverbounce.api.client.NeverbounceClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NeverbounceClientConfig {

    @Bean
    public NeverbounceClient neverbounceClient() {
        return NeverbounceClientFactory.create("private_673db2b1e66f08110447aa551d058eb7");
    }

}