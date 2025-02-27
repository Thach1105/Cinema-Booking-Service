package vn.thachnn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.thachnn.dto.request.ZalopayCallbackRequest;
import vn.thachnn.model.RedisTicket;
import vn.thachnn.model.User;
import vn.thachnn.service.Impl.BookingService;
import vn.thachnn.service.Impl.RedisTicketService;
import vn.thachnn.util.zalopay.HMACUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ZALOPAY-SERVICE")
public class ZalopayService {

    private final RedisTicketService redisTicketService;
    private final BookingService bookingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${zalopay.app-id}")
    private String APP_ID;

    @Value("${zalopay.key1}")
    private String KEY1;

    @Value("${zalopay.key2}")
    private String KEY2;

    @Value("${zalopay.endpoint}")
    private String ENDPOINT;

    @Value("${zalopay.redirectURL}")
    private String REDIRECT_URL;

    @Value("${zalopay.callbackURL}")
    private String CALLBACK_URL;


    public JsonNode createPaymentLink(String transactionCode, User user) throws IOException {
        RedisTicket redisTicket = redisTicketService.get(transactionCode);

        //check user before create payment
        bookingService.checkBeforeCreatePayment(redisTicket, user);

        final Long appUser = user.getId();
        final Integer amount = redisTicket.getTotalAmount();
        final long expire_duration_seconds = 300;
        final String description = "Thanh toan dat ve";

        final Map<String, Object> embed_data = new HashMap<>() {{
            put("preferred_payment_method", new ArrayList<>().add("vietqr") );
            put("redirecturl", REDIRECT_URL);
        }};

        final JSONArray item = new JSONArray();
        for (String data : redisTicket.getSeatDetail()){
            System.out.println(data);
            /*Map map = new ObjectMapper().convertValue(i, Map.class);
            JSONObject itemObject = new JSONObject(map);*/

            JSONObject itemObject = new JSONObject(data);

            item.put(itemObject);
        }

        final Map<String, Object> orderParams = new HashMap<>();

        orderParams.put("app_id", APP_ID);
        orderParams.put("app_trans_id", transactionCode);
        orderParams.put("app_time", System.currentTimeMillis());
        orderParams.put("app_user", appUser);
        orderParams.put("amount", amount);
        orderParams.put("description", description);
        orderParams.put("bank_code", "");
        orderParams.put("item", item.toString());
        orderParams.put("embed_data", new JSONObject(embed_data).toString());
        orderParams.put("callback_url", CALLBACK_URL);
        orderParams.put("expire_duration_seconds", expire_duration_seconds);

        // app_id +”|”+ app_trans_id +”|”+ appuser +”|”+ amount +"|" + app_time +”|”+ embed_data +"|" +item
        String data = orderParams.get("app_id") + "|"
                + orderParams.get("app_trans_id") + "|"
                + orderParams.get("app_user") + "|"
                + orderParams.get("amount") + "|"
                + orderParams.get("app_time") + "|"
                + orderParams.get("embed_data") + "|"
                + orderParams.get("item");

        orderParams.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ENDPOINT + "/create");

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : orderParams.entrySet()){
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        // Content-Type: application/x-www-form-urlencoded
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultJson = objectMapper.readTree(resultJsonStr.toString());
        int returnCode = resultJson.get("return_code").asInt();
        if(returnCode == 1){
            redisTicket.setHasGenerated(true);
            redisTicketService.refreshTimeHeldSeat(redisTicket);
            redisTicketService.update(redisTicket);
        }
        return resultJson;
    }

    public JSONObject handleCallback(ZalopayCallbackRequest request){
        JSONObject result = new JSONObject();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ZalopayCallbackRequest.CallbackData data =
                    objectMapper.readValue(request.getData(), ZalopayCallbackRequest.CallbackData.class);

            String reqmac = request.getMac();
            String dataStr = request.getData();
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(KEY2.getBytes(), "HmacSHA256"));
            byte[] hashBytes = hmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
            String transactionId = data.getAppTransId();

            if (!reqmac.equals(mac)) {
                //callback không hợp lệ
                result.put("return_code", -1);
                result.put("return_message", "invalid callback");
            } else {
                // thanh toán thành công
                // merchant cập nhật trạng thái cho đơn hàng
                log.info("Callback success, create new ticket in database");
                RedisTicket ticket = redisTicketService.get(transactionId);
                ticket.setPaid(true);

                kafkaTemplate.send("ticket-payment-success", objectMapper.writeValueAsString(ticket));

                //callback hợp lệ
                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("return_message", ex.getMessage());
        }

        return result;
    }

    public boolean checksum(Map<String, String> data) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        hmacSHA256.init(new SecretKeySpec(KEY2.getBytes(), "HmacSHA256"));


        String checksumData = data.get("appid") + "|" +
                data.get("apptransid") + "|" +
                data.get("pmcid") + "|" +
                data.get("bankcode") + "|" +
                data.get("amount") + "|" +
                data.get("discountamount") + "|" +
                data.get("status");
        byte[] checksumBytes = hmacSHA256.doFinal(checksumData.getBytes());
        String checksum = DatatypeConverter.printHexBinary(checksumBytes).toLowerCase();

        if (!checksum.equals(data.get("checksum"))) {
            return false;
        } else {
            // kiểm tra xem đã nhận được callback hay chưa ?
            // nếu chưa thì tiến hành gọi API truy vấn trạng thái thanh toán của đơn hàng để lấy kết quả cuối cùng
            String transactionCode = data.get("apptransid");
            RedisTicket ticket = redisTicketService.get(transactionCode);

            if (!ticket.isPaid()){
                try {
                    log.info("Truy vấn trạng thái thanh toán");

                    JsonNode resultQuery = checkOrderStatus(transactionCode);
                    System.out.println(resultQuery.toString());
                    int returnCode = resultQuery.get("return_code").asInt();

                    if(returnCode == 1){
                        ticket.setPaid(true);

                        ObjectMapper objectMapper = new ObjectMapper();
                        String message = objectMapper.writeValueAsString(ticket);

                        kafkaTemplate.send("ticket-payment-success", message);

                    } else if (returnCode == 2){
                        //xóa session giao dịch
                        redisTicketService.releaseHeldSeat(ticket);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Không thể truy vấn trạng thái thanh toán");
                }
            }

            return true;
        }
    }

    public JsonNode checkOrderStatus(String app_trans_id) throws URISyntaxException, IOException {

        // app_id|app_trans_id|key1
        String data = APP_ID+ "|" + app_trans_id + "|" + KEY1;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, KEY1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", APP_ID));
        params.add(new BasicNameValuePair("app_trans_id", app_trans_id));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ENDPOINT + "/query");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        return new ObjectMapper().readTree(resultJsonStr.toString());
    }
}
