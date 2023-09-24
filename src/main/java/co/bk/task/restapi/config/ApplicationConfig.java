package co.bk.task.restapi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zalando.problem.jackson.ProblemModule;

import java.text.SimpleDateFormat;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class ApplicationConfig {

    @Primary
    @Bean
    public ObjectMapper defaultObjectMapper() {

        /*
         * Custom object mapper:
         *  - prevents stacktrace leaking out in REST responses. See https://github.com/zalando/problem#stack-traces-and-causal-chains
         *  - configures date format to be UTC ISO8601 compliant e.g  2023-09-15T21:44:59.228Z
         */
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().json()
                .modulesToInstall(
                        new ProblemModule().withStackTraces(false))
                .featuresToDisable(
                        WRITE_DATES_AS_TIMESTAMPS,
                        FAIL_ON_EMPTY_BEANS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        ACCEPT_FLOAT_AS_INT)
                .featuresToEnable(
                        DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                        DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,
                        JsonParser.Feature.IGNORE_UNDEFINED)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                .serializationInclusion(JsonInclude.Include.ALWAYS) // NON_NULL would stop properties with null values being returned in REST payload
                .build();

        return objectMapper;
    }

}
