package murach.checkout;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;
import murach.util.VNPayUtil;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Cart cart =
                (Cart) session.getAttribute("cart");

        // Không có sản phẩm thì quay về trang mua hàng
        if (cart == null || cart.isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/index.jsp"
            );

            return;
        }

        request.getRequestDispatcher(
                "/checkout.jsp"
        ).forward(
                request,
                response
        );
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession();

        Cart cart =
                (Cart) session.getAttribute("cart");


        // =====================================================
        // 1. Kiểm tra giỏ hàng
        // =====================================================

        if (cart == null || cart.isEmpty()) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/index.jsp"
            );

            return;
        }


        // =====================================================
        // 2. Lấy thông tin khách hàng
        // =====================================================

        String name =
                request.getParameter("name");

        String email =
                request.getParameter("email");

        String phone =
                request.getParameter("phone");

        String address =
                request.getParameter("address");

        String city =
                request.getParameter("city");

        String payment =
                request.getParameter("payment");


        // =====================================================
        // 3. Kiểm tra phương thức thanh toán
        // =====================================================

        if (payment == null || payment.isBlank()) {

            payment = "COD";
        }


        // =====================================================
        // 4. Lưu thông tin khách hàng vào Session
        // =====================================================

        session.setAttribute(
                "customerName",
                name
        );

        session.setAttribute(
                "customerEmail",
                email
        );

        session.setAttribute(
                "customerPhone",
                phone
        );

        session.setAttribute(
                "customerAddress",
                address
        );

        session.setAttribute(
                "customerCity",
                city
        );

        session.setAttribute(
                "paymentMethod",
                payment
        );


        // =====================================================
        // 5. Nếu chọn VNPay
        // =====================================================

        if ("VNPAY".equalsIgnoreCase(payment)) {

            /*
             * Chưa xóa cart ở đây.
             *
             * Người dùng vẫn chưa thanh toán thành công.
             *
             * Cart sẽ được lưu vào pendingCart.
             */

            session.setAttribute(
                    "pendingCart",
                    cart
            );


            // -------------------------------------------------
            // Tạo mã giao dịch
            // -------------------------------------------------

            String txnRef =
                    createTransactionReference();

            session.setAttribute(
                    "vnpayTransactionId",
                    txnRef
            );


            // -------------------------------------------------
            // Tính số tiền VND gửi VNPay
            // -------------------------------------------------

            long amount =
                    getVNPayAmount(cart);

            session.setAttribute(
                    "vnpayAmount",
                    amount
            );


            // -------------------------------------------------
            // Return URL
            // -------------------------------------------------

            String returnUrl =
                    getBaseUrl(request)
                            + request.getContextPath()
                            + "/vnpay-return";


            // -------------------------------------------------
            // Lấy IP người dùng
            // -------------------------------------------------

            String ipAddress =
                    getClientIpAddress(request);


            // -------------------------------------------------
            // Tạo parameters gửi VNPay
            // -------------------------------------------------

            Map<String, String> params =
                    new LinkedHashMap<>();

            params.put(
                    "vnp_Version",
                    VNPayUtil.VNPAY_VERSION
            );

            params.put(
                    "vnp_Command",
                    VNPayUtil.VNPAY_COMMAND
            );

            params.put(
                    "vnp_TmnCode",
                    VNPayUtil.getTmnCode()
            );

            params.put(
                    "vnp_Amount",
                    String.valueOf(amount)
            );

            params.put(
                    "vnp_CurrCode",
                    VNPayUtil.VNPAY_CURRENCY
            );

            params.put(
                    "vnp_TxnRef",
                    txnRef
            );

            params.put(
                    "vnp_OrderInfo",
                    "Thanh toan don hang " + txnRef
            );

            params.put(
                    "vnp_OrderType",
                    "other"
            );

            params.put(
                    "vnp_Locale",
                    "vn"
            );

            params.put(
                    "vnp_ReturnUrl",
                    returnUrl
            );

            params.put(
                    "vnp_IpAddr",
                    ipAddress
            );

            params.put(
                    "vnp_CreateDate",
                    VNPayUtil.getCreateDate()
            );

            params.put(
                    "vnp_ExpireDate",
                    VNPayUtil.getExpireDate()
            );


            // -------------------------------------------------
            // Tạo URL thanh toán VNPay
            // -------------------------------------------------

            String paymentUrl =
                    VNPayUtil.createPaymentUrl(
                            params
                    );


            // -------------------------------------------------
            // Chuyển người dùng sang VNPay
            // -------------------------------------------------

            response.sendRedirect(
                    paymentUrl
            );

            return;
        }


        // =====================================================
        // 6. Thanh toán COD
        // =====================================================

        /*
         * COD:
         *
         * Lưu cart cuối cùng.
         * Sau đó xóa cart hiện tại.
         */

        session.setAttribute(
                "lastCart",
                cart
        );

        session.removeAttribute(
                "cart"
        );


        // =====================================================
        // 7. Chuyển sang trang xác nhận
        // =====================================================

        response.sendRedirect(
                request.getContextPath()
                        + "/confirmation.jsp"
        );
    }


    // =========================================================
    // Tạo mã giao dịch VNPay
    // =========================================================

    private String createTransactionReference() {

        String uuid =
                UUID.randomUUID()
                        .toString()
                        .replace(
                                "-",
                                ""
                        );

        return System.currentTimeMillis()
                + uuid.substring(
                        0,
                        6
                );
    }


    // =========================================================
    // Tính amount gửi VNPay
    // =========================================================

    private long getVNPayAmount(Cart cart) {

        /*
         * Cart của project đang sử dụng VND.
         *
         * Ví dụ:
         *
         * Tổng đơn hàng:
         * 150,000 VND
         *
         * VNPay yêu cầu:
         * 150,000 × 100
         *
         * = 15,000,000
         */

        double total =
                cart.getTotal();

        return Math.round(
                total * 100
        );
    }


    // =========================================================
    // Tạo Base URL
    // =========================================================

    private String getBaseUrl(
            HttpServletRequest request) {

        /*
         * Khi chạy Render:
         *
         * Browser
         *    ↓
         * HTTPS
         *    ↓
         * Render
         *    ↓
         * Tomcat
         *
         * Có thể Tomcat nhìn thấy request là HTTP
         * phía sau reverse proxy.
         *
         * Vì vậy ưu tiên X-Forwarded-Proto.
         */

        String forwardedProto =
                request.getHeader(
                        "X-Forwarded-Proto"
                );

        String scheme;

        if (forwardedProto != null
                && !forwardedProto.isBlank()) {

            scheme =
                    forwardedProto
                            .split(",")[0]
                            .trim();

        } else {

            scheme =
                    request.getScheme();
        }


        String serverName =
                request.getServerName();

        int port =
                request.getServerPort();


        boolean defaultPort =
                ("http".equalsIgnoreCase(scheme)
                        && port == 80)
                        ||
                ("https".equalsIgnoreCase(scheme)
                        && port == 443);


        if (defaultPort) {

            return scheme
                    + "://"
                    + serverName;
        }


        return scheme
                + "://"
                + serverName
                + ":"
                + port;
    }


    // =========================================================
    // Lấy IP Client
    // =========================================================

    private String getClientIpAddress(
            HttpServletRequest request) {

        String ip =
                request.getHeader(
                        "X-Forwarded-For"
                );


        if (ip != null
                && !ip.isEmpty()
                && !"unknown".equalsIgnoreCase(ip)) {

            return ip
                    .split(",")[0]
                    .trim();
        }


        ip =
                request.getHeader(
                        "X-Real-IP"
                );


        if (ip != null
                && !ip.isEmpty()
                && !"unknown".equalsIgnoreCase(ip)) {

            return ip;
        }


        return request.getRemoteAddr();
    }
}