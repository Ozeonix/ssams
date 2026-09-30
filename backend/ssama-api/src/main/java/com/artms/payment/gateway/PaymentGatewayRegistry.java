package com.artms.payment.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry of all available payment gateways.
 * New gateways auto-register via Spring DI – no code change required in the fee service.
 */
@Component
public class PaymentGatewayRegistry {

    private final Map<String, PaymentGateway> gatewayMap;

    public PaymentGatewayRegistry(List<PaymentGateway> gateways) {
        this.gatewayMap = gateways.stream()
            .collect(Collectors.toMap(PaymentGateway::getCode, Function.identity()));
    }

    public PaymentGateway getGateway(String code) {
        return Optional.ofNullable(gatewayMap.get(code.toUpperCase()))
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown payment gateway: " + code));
    }

    public boolean supports(String code) {
        return gatewayMap.containsKey(code.toUpperCase());
    }
}
