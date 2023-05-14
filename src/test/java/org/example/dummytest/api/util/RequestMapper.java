package org.example.dummytest.api.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.internal.mapping.Jackson2Mapper;

public class RequestMapper {
    private static Jackson2Mapper mapper;

    public static Jackson2Mapper getMapper() {
        if (mapper == null ) {
            mapper = new Jackson2Mapper(
                    (type, s) -> {
                        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
                        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        return om;
                    });
        }

        return mapper;
    }
}
