package com.salesmanager.context;

import com.salesmanager.core.business.configuration.DroolsConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author <a href="mailto:luca@camphuisen.com">Luca Camphuisen</a>
 * @since 6/13/20
 */
@Configuration
@EnableAutoConfiguration
@ActiveProfiles()
@ImportResource("classpath:/spring/test-shopizer-context.xml")
@ComponentScan({"com.salesmanager.core.business", "com.salesmanager.shop"})
@Import(DroolsConfiguration.class)
public class TestingContextConfiguration {
}
