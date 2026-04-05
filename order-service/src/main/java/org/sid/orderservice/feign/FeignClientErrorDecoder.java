package org.sid.orderservice.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.sid.orderservice.exception.BadRequestException;
import org.sid.orderservice.exception.ResourceNotFoundException;
import org.sid.orderservice.exception.ServiceUnavailableException;

/**
 * Maps Feign HTTP errors to domain exceptions handled by {@link org.sid.orderservice.exception.GlobalExceptionHandler}.
 */
public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String reason = response.reason() != null ? response.reason() : "";

        return switch (status) {
            case 404 -> new ResourceNotFoundException("Downstream resource not found: " + reason);
            case 400 -> new BadRequestException("Downstream bad request: " + reason);
            default -> {
                if (status >= 400 && status < 500) {
                    yield new BadRequestException("Downstream client error (" + status + "): " + reason);
                }
                yield new ServiceUnavailableException("Downstream service error (" + status + "): " + reason);
            }
        };
    }
}
