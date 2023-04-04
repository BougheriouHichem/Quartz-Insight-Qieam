package com.apigames.quartzinsight.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {

        ApiResponse badRequestAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value("{\"code\": 400, \"status\": \"Bad request!\", \"Message\": \"Bad request!\"}")))
        ).description("Bad request!");

        ApiResponse internalServerErrorAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value("{\"code\": 500, \"status\": \"Internal server Error!\", \"Message\": \"Internal server Error!\"}")))
        ).description("Internal server Error!");

        final String securitySchemeName = "Authorization";
        return new OpenAPI()
                .components(new Components()
                        .addResponses("badRequestAPI", badRequestAPI)
                        .addResponses("internalServerErrorAPI", internalServerErrorAPI)
                )
                .info(new Info().title("Api").version("0.0.1").description("api"));
    }

    @Bean
    public GroupedOpenApi customApi() {
        return GroupedOpenApi.builder().group("api").pathsToMatch("/api/**").build();
    }
}
