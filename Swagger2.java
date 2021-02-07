package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2配置类 在与spring boot集成时，放在与Application.java同级的目录下。
 * 通过@Configuration注解，让Spring来加载该类配置。 再通过@EnableSwagger2注解来启用Swagger2。
 * 
 * @auther QIANG.CQ.ZHOU
 * @VERSING 2020年6月2日下午2:46:13
 */

@Configuration
@EnableSwagger2
public class Swagger2 {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.example.controller")).paths(PathSelectors.any()).build();
	}

	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Swagger2API文檔").description("Controller層Swagger2API文檔").termsOfServiceUrl("")
				.version("1.0").build();
	}

}
