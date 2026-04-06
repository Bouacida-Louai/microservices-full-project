package org.sid.orderservice.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 404:
                return new RuntimeException("Resource not found: " + methodKey);
            case 503:
                return new RuntimeException("Service unavailable: " + methodKey);
            default:
                return new Default().decode(methodKey, response);
        }
    }
}