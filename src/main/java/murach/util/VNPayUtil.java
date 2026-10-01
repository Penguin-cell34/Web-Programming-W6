package murach.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayUtil {

    // =========================================================
    // VNPay Sandbox
    // =========================================================

    public static final String VNPAY_PAYMENT_URL =
            "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static final String VNPAY_VERSION =
            "2.1.0";

    public static final String VNPAY_COMMAND =
            "pay";

    public static final String VNPAY_CURRENCY =
            "VND";


    // =========================================================
    // Lấy TMN Code từ Environment Variable
    // =========================================================

    public static String getTmnCode() {

        String tmnCode =
                System.getenv("VNPAY_TMN_CODE");

        if (tmnCode == null
                || tmnCode.isBlank()) {

            throw new IllegalStateException(
                    "VNPAY_TMN_CODE chưa được cấu hình."
            );
        }

        return tmnCode;
    }


    // =========================================================
    // Lấy Hash Secret từ Environment Variable
    // =========================================================

    public static String getHashSecret() {

        String hashSecret =
                System.getenv("VNPAY_HASH_SECRET");

        if (hashSecret == null
                || hashSecret.isBlank()) {

            throw new IllegalStateException(
                    "VNPAY_HASH_SECRET chưa được cấu hình."
            );
        }

        return hashSecret;
    }


    // =========================================================
    // Create Date
    // =========================================================

    public static String getCreateDate() {

        ZonedDateTime now =
                ZonedDateTime.now(
                        ZoneId.of("Asia/Ho_Chi_Minh")
                );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyyMMddHHmmss"
                );

        return now.format(formatter);
    }


    // =========================================================
    // Expire Date
    // =========================================================

    public static String getExpireDate() {

        ZonedDateTime expire =
                ZonedDateTime.now(
                        ZoneId.of("Asia/Ho_Chi_Minh")
                ).plusMinutes(15);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyyMMddHHmmss"
                );

        return expire.format(formatter);
    }


    // =========================================================
    // HMAC SHA-512
    // =========================================================

    public static String hmacSHA512(
            String key,
            String data) {

        try {

            Mac hmac =
                    Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            key.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            "HmacSHA512"
                    );

            hmac.init(secretKey);

            byte[] hash =
                    hmac.doFinal(
                            data.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không thể tạo HMAC SHA-512.",
                    e
            );
        }
    }


    // =========================================================
    // URL Encode
    // =========================================================

    public static String encode(
            String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }


    // =========================================================
    // Build Hash Data
    // =========================================================

    public static String buildHashData(
            Map<String, String> params) {

        TreeMap<String, String> sortedParams =
                new TreeMap<>(params);

        StringBuilder hashData =
                new StringBuilder();

        boolean first = true;

        for (Map.Entry<String, String> entry
                : sortedParams.entrySet()) {

            if (!first) {

                hashData.append("&");
            }

            hashData.append(
                    encode(entry.getKey())
            );

            hashData.append("=");

            hashData.append(
                    encode(entry.getValue())
            );

            first = false;
        }

        return hashData.toString();
    }


    // =========================================================
    // Build Query String
    // =========================================================

    public static String buildQueryString(
            Map<String, String> params) {

        TreeMap<String, String> sortedParams =
                new TreeMap<>(params);

        StringBuilder queryString =
                new StringBuilder();

        boolean first = true;

        for (Map.Entry<String, String> entry
                : sortedParams.entrySet()) {

            if (!first) {

                queryString.append("&");
            }

            queryString.append(
                    encode(entry.getKey())
            );

            queryString.append("=");

            queryString.append(
                    encode(entry.getValue())
            );

            first = false;
        }

        return queryString.toString();
    }


    // =========================================================
    // Create VNPay Payment URL
    // =========================================================

    public static String createPaymentUrl(
            Map<String, String> params) {

        String hashData =
                buildHashData(params);

        String secureHash =
                hmacSHA512(
                        getHashSecret(),
                        hashData
                );

        String queryString =
                buildQueryString(params);

        return VNPAY_PAYMENT_URL
                + "?"
                + queryString
                + "&vnp_SecureHash="
                + secureHash;
    }


    // =========================================================
    // Verify VNPay Signature
    // =========================================================

    public static boolean verifySignature(
            Map<String, String> params) {

        String receivedHash =
                params.get("vnp_SecureHash");

        if (receivedHash == null
                || receivedHash.isEmpty()) {

            return false;
        }


        Map<String, String> data =
                new TreeMap<>(params);

        data.remove("vnp_SecureHash");

        data.remove("vnp_SecureHashType");


        String hashData =
                buildHashData(data);


        String calculatedHash =
                hmacSHA512(
                        getHashSecret(),
                        hashData
                );


        return MessageDigest.isEqual(
                calculatedHash.getBytes(
                        StandardCharsets.UTF_8
                ),
                receivedHash.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }
}