package com.macbeth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//@ComponentScan(basePackages = {"com.macbeth"})
//@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket customDocket(){
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
        String url = this.getClass().getClassLoader().getResource("swagger-url.properties").getPath().substring(1);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.macbeth"))
                .paths(PathSelectors.any())
                .build();
//                .host(url);
    }

    private ApiInfo apiInfo(){
        Contact contact = new Contact("test","","");
        return new ApiInfoBuilder().licenseUrl("macbeth.com.cn").contact(contact).build();
//        return new ApiInfo("","","","",contact,"","");
    }

    public static void main(String[] args) {
        String url = SwaggerConfig.class.getClassLoader().getResource("swagger-url.properties").getPath();
        System.out.println(url);

    }
}




























