package vn.thachnn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.common.PaymentMethod;
import vn.thachnn.dto.request.ZalopayCallbackRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.model.User;
import vn.thachnn.service.ZalopayService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "Payment Controller")
@Slf4j(topic = "PAYMENT-CONTROLLER")
public class PaymentController {

    private final ZalopayService zalopayService;

    @PostMapping("/{transactionCode}")
    public ResponseEntity<?> createPaymentLink(
            @PathVariable String transactionCode,
            @RequestParam PaymentMethod paymentType,
            @AuthenticationPrincipal User user
            ) throws IOException {
        log.info("Create payment link");
        JsonNode result = paymentType.equals(PaymentMethod.ZALOPAY)
                ? zalopayService.createPaymentLink(transactionCode, user)
                : null;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("payment link")
                .data(result)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/zalopay-call-back")
    public ResponseEntity<?> zalopayCallback(
            @RequestBody ZalopayCallbackRequest request
    ) throws JsonProcessingException {

        log.info("Zalopay Server callback");
        JSONObject responseObj = zalopayService.handleCallback(request);
        ObjectMapper objectMapper = new ObjectMapper();

        return ResponseEntity.ok(objectMapper.readTree(responseObj.toString()));
    }

    @GetMapping("/redirect-from-zalopay")
    public ResponseEntity<?> checksumZalopay(
        @RequestParam String amount,
        @RequestParam String appid,
        @RequestParam String apptransid,
        @RequestParam(required = false) String bankcode,
        @RequestParam String checksum,
        @RequestParam String discountamount,
        @RequestParam String pmcid,
        @RequestParam String status

    ) throws NoSuchAlgorithmException, InvalidKeyException {

        Map<String, String> data = new HashMap<>();
        data.put("amount", amount);
        data.put("appid", appid);
        data.put("apptransid", apptransid);
        data.put("bankcode", bankcode);
        data.put("checksum", checksum);
        data.put("discountamount", discountamount);
        data.put("pmcid", pmcid);
        data.put("status", status);

        return ResponseEntity.ok(zalopayService.checksum(data));
    }
}
