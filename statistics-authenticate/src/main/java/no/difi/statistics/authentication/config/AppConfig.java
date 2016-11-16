package no.difi.statistics.authentication.config;

import no.difi.statistics.authentication.AuthenticationService;
import no.difi.statistics.authentication.ElasticsearchUserDetailsService;
import no.difi.statistics.authentication.api.AuthenticationRestController;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.String.format;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
// Exclude security auto configuration, as Spring Boot otherwise sets up a default authentication configuration when
// spring-security is on classpath
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableSwagger2
public class AppConfig {

    @Autowired
    private Environment environment;

    @Bean
    public AuthenticationRestController api() {
        return new AuthenticationRestController(authenticationService());
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(authenticationProvider(), elasticsearchClient());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new ElasticsearchUserDetailsService(elasticsearchClient());
    }

    @Bean(destroyMethod = "close")
    public Client elasticsearchClient() {
        String host = environment.getRequiredProperty("no.difi.statistics.elasticsearch.host");
        int port = environment.getRequiredProperty("no.difi.statistics.elasticsearch.port", Integer.class);
        try {
            return TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to create Elasticsearch client", e);
        }
    }

    @Bean
    public Docket apiDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("statistikk-autentisering")
                .select()
                .apis(basePackage(AuthenticationRestController.class.getPackage().getName()))
                .paths(any())
                .build()
                .apiInfo(new ApiInfoBuilder()
                        .title("Statistikk for offentlige tjenester")
                        .description(
                                format(
                                        "Beskrivelse av API for autentisering (versjon %s).",
                                        System.getProperty("difi.version", "N/A")
                                )
                        )
                        .build()
                );
    }

}
