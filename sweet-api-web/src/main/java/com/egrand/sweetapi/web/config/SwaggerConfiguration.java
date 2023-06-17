package com.egrand.sweetapi.web.config;

import cn.hutool.core.util.StrUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * swagger 配置
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

	/**
	 * Reservation api docket.
	 *
	 * @return the docket
	 */
	@Bean
	public Docket createRestApi() {
		String basePackage = "com.egrand";
		String ignoredParameterTypes = "";
		List<Class> ignoreList = new ArrayList<>();
		if(StrUtil.isNotEmpty(ignoredParameterTypes)){
			StrUtil.split(ignoredParameterTypes, ',').forEach((ignore) -> {
				try {
					ignoreList.add(Class.forName(ignore));
				} catch (ClassNotFoundException e) {}
			});
		}
		List<Parameter> params=new ArrayList<>();
		Parameter egdTenant = new ParameterBuilder().name("x-tenant-header")
				.description("租户，用于指定租户环境，数据库连接池会根据该字段，自动切换数据库实例")
				.modelRef(new ModelRef("string"))
				.defaultValue("")
				.parameterType("header")
				.required(false).build();
		params.add(egdTenant);
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.ignoredParameterTypes(ignoreList.toArray(new Class[ignoreList.size()]))
				.select()
				.apis(RequestHandlerSelectors.basePackage(basePackage))
				.paths(PathSelectors.any())
				.build()
				//配置鉴权信息
				.securitySchemes(securitySchemes())
				.securityContexts(securityContexts())
				.globalOperationParameters(params)
				.enable(true);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Sweet-API")
				.description("快速开发平台")
				.version("1.2.1-SNAPSHOT")
				.license("Apache License 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
				.contact(new Contact("Wiki", "https://gitee.com/s-sweet/sweet-api", ""))
				.build();
	}

	private List<ApiKey> securitySchemes() {
		return new ArrayList(Collections.singleton(new ApiKey("Authorization", "Authorization", "header")));
	}

	private List<SecurityContext> securityContexts() {
		return new ArrayList(
				Collections.singleton(SecurityContext.builder()
						.securityReferences(defaultAuth())
						.forPaths(PathSelectors.regex("^(?!auth).*$"))
						.build())
		);
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return new ArrayList(Collections.singleton(new SecurityReference("Authorization", authorizationScopes)));
	}

}
