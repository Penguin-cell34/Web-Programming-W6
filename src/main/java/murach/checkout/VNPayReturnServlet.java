package murach.checkout;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;
import murach.util.VNPayUtil;

@WebServlet("/vnpay-return")
public class VNPayReturnServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // =====================================================
        // 1. Lấy toàn bộ parameters VNPay trả về
        // =====================================================

        Map<String, String> params = new TreeMap<>();

        var parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String field = parameterNames.nextElement();

            String value = request.getParameter(field);

            if (value != null && !value.isEmpty()) {

                params.put(field, value);
            }
        }

        // =====================================================
        // 2. Kiểm tra chữ ký VNPay
        // =====================================================

        boolean validSignature =
                VNPayUtil.verifySignature(params);

        if (!validSignature) {

            request.setAttribute(
                    "paymentStatus",
                    "INVALID_SIGNATURE"
            );

            request.setAttribute(
                    "paymentMessage",
                    "Chữ ký thanh toán không hợp lệ."
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 3. Lấy thông tin giao dịch
        // =====================================================

        String responseCode =
                params.get("vnp_ResponseCode");

        String transactionStatus =
                params.get("vnp_TransactionStatus");

        String txnRef =
                params.get("vnp_TxnRef");

        String amount =
                params.get("vnp_Amount");

        String transactionNo =
                params.get("vnp_TransactionNo");

        // =====================================================
        // 4. Kiểm tra các thông tin bắt buộc
        // =====================================================

        if (responseCode == null
                || transactionStatus == null
                || txnRef == null
                || amount == null) {

            request.setAttribute(
                    "paymentStatus",
                    "INVALID_RESPONSE"
            );

            request.setAttribute(
                    "paymentMessage",
                    "Dữ liệu trả về từ VNPay không đầy đủ."
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 5. Kiểm tra transaction ID
        // =====================================================

        String sessionTxnRef =
                (String) session.getAttribute(
                        "vnpayTransactionId"
                );

        if (sessionTxnRef == null
                || !sessionTxnRef.equals(txnRef)) {

            request.setAttribute(
                    "paymentStatus",
                    "INVALID_TRANSACTION"
            );

            request.setAttribute(
                    "paymentMessage",
                    "Mã giao dịch không hợp lệ hoặc "
                            + "giao dịch không thuộc phiên hiện tại."
            );

            request.setAttribute(
                    "txnRef",
                    txnRef
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 6. Kiểm tra số tiền
        // =====================================================

        Long sessionAmount =
                getSessionAmount(
                        session.getAttribute("vnpayAmount")
                );

        Long vnpayAmount =
                parseAmount(amount);

        if (sessionAmount == null
                || vnpayAmount == null
                || !sessionAmount.equals(vnpayAmount)) {

            request.setAttribute(
                    "paymentStatus",
                    "INVALID_AMOUNT"
            );

            request.setAttribute(
                    "paymentMessage",
                    "Số tiền thanh toán không hợp lệ."
            );

            request.setAttribute(
                    "txnRef",
                    txnRef
            );

            request.setAttribute(
                    "amount",
                    amount
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 7. Kiểm tra kết quả thanh toán
        // =====================================================

        boolean success =
                "00".equals(responseCode)
                        && "00".equals(transactionStatus);

        // =====================================================
        // 8. Thanh toán thất bại
        // =====================================================

        if (!success) {

            request.setAttribute(
                    "paymentStatus",
                    "FAILED"
            );

            request.setAttribute(
                    "paymentMessage",
                    getPaymentMessage(responseCode)
            );

            request.setAttribute(
                    "txnRef",
                    txnRef
            );

            request.setAttribute(
                    "amount",
                    amount
            );

            request.setAttribute(
                    "transactionNo",
                    transactionNo
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 9. Thanh toán thành công
        // =====================================================

        Cart pendingCart =
                (Cart) session.getAttribute(
                        "pendingCart"
                );

        if (pendingCart == null
                || pendingCart.isEmpty()) {

            request.setAttribute(
                    "paymentStatus",
                    "INVALID_CART"
            );

            request.setAttribute(
                    "paymentMessage",
                    "Không tìm thấy giỏ hàng đang thanh toán."
            );

            forwardToResultPage(request, response);

            return;
        }

        // =====================================================
        // 10. Lưu Cart đã thanh toán
        // =====================================================

        session.setAttribute(
                "lastCart",
                pendingCart
        );

        // =====================================================
        // 11. Lưu thông tin VNPay
        // =====================================================

        session.setAttribute(
                "vnpayTransactionNo",
                transactionNo
        );

        session.setAttribute(
                "vnpayResponseCode",
                responseCode
        );

        session.setAttribute(
                "vnpayTransactionStatus",
                transactionStatus
        );

        // =====================================================
        // 12. Xóa Cart đang chờ thanh toán
        // =====================================================

        session.removeAttribute("cart");

        session.removeAttribute("pendingCart");

        session.removeAttribute("vnpayTransactionId");

        session.removeAttribute("vnpayAmount");

        // =====================================================
        // 13. Chuyển sang trang confirmation
        // =====================================================

        response.sendRedirect(
                request.getContextPath()
                        + "/confirmation.jsp"
        );
    }

    // =========================================================
    // Chuyển đến payment-result.jsp
    // =========================================================

    private void forwardToResultPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/payment-result.jsp"
        ).forward(
                request,
                response
        );
    }

    // =========================================================
    // Chuyển vnpayAmount từ session thành Long
    // =========================================================

    private Long getSessionAmount(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number) {

            return ((Number) value).longValue();
        }

        try {

            return Long.parseLong(
                    value.toString()
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }

    // =========================================================
    // Chuyển amount VNPay từ String thành Long
    // =========================================================

    private Long parseAmount(String amount) {

        if (amount == null || amount.isEmpty()) {
            return null;
        }

        try {

            return Long.parseLong(amount);

        } catch (NumberFormatException e) {

            return null;
        }
    }

    // =========================================================
    // VNPay response message
    // =========================================================

    private String getPaymentMessage(
            String responseCode) {

        if (responseCode == null) {

            return "Không nhận được kết quả thanh toán.";
        }

        switch (responseCode) {

            case "00":
                return "Thanh toán thành công.";

            case "07":
                return "Giao dịch bị nghi ngờ gian lận.";

            case "09":
                return "Thẻ/Tài khoản chưa đăng ký Internet Banking.";

            case "10":
                return "Khách hàng xác thực thông tin thanh toán quá thời gian.";

            case "11":
                return "Giao dịch đã hết hạn thanh toán.";

            case "12":
                return "Thẻ/Tài khoản bị khóa.";

            case "13":
                return "Khách hàng nhập sai mật khẩu xác thực giao dịch.";

            case "24":
                return "Khách hàng hủy giao dịch.";

            case "51":
                return "Tài khoản không đủ số dư.";

            case "65":
                return "Tài khoản đã vượt quá hạn mức giao dịch.";

            case "75":
                return "Ngân hàng đang bảo trì.";

            case "79":
                return "Khách hàng nhập sai mật khẩu quá số lần quy định.";

            default:
                return "Thanh toán không thành công. Mã lỗi: "
                        + responseCode;
        }
    }
}