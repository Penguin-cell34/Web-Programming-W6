<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="murach.model.Cart" %>
<%@ page import="murach.model.LineItem" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<%
    Cart cart = (Cart) session.getAttribute("lastCart");

    String name =
            (String) session.getAttribute("customerName");

    String email =
            (String) session.getAttribute("customerEmail");

    String phone =
            (String) session.getAttribute("customerPhone");

    String address =
            (String) session.getAttribute("customerAddress");

    String city =
            (String) session.getAttribute("customerCity");

    String payment =
            (String) session.getAttribute("paymentMethod");
        NumberFormat vndFormat = NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN"));

    String paymentDisplay = switch (payment == null ? "" : payment) {
        case "COD" -> "Cash on Delivery (COD)";
        case "VNPAY" -> "VNPay";
        case "MOMO" -> "MoMo";
        case "PAYPAL" -> "PayPal";
        default -> "Not specified";
    };
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Order Confirmation</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/main.css">

</head>


<body>


<header class="site-header">

    <div class="container">

        <h1 class="logo">
            🎵 CD Store
        </h1>

    </div>

</header>


<main>

    <div class="container">

        <div class="confirmation-card">


            <div class="success-icon">
                ✓
            </div>


            <h2>
                Order Placed Successfully!
            </h2>


            <p class="confirmation-message">

                Thank you for your order,
                <strong><%= name %></strong>!

            </p>


            <div class="order-details">

                <h3>
                    Customer Information
                </h3>

                <p>
                    <strong>Name:</strong>
                    <%= name %>
                </p>

                <p>
                    <strong>Email:</strong>
                    <%= email %>
                </p>

                <p>
                    <strong>Phone:</strong>
                    <%= phone %>
                </p>

                <p>
                    <strong>Address:</strong>
                    <%= address %>, <%= city %>
                </p>

                <p>
                    <strong>Payment:</strong>
                    <%= paymentDisplay %>
                </p>

            </div>


            <div class="order-details">

                <h3>
                    Order Summary
                </h3>


                <%
                    if (cart != null) {

                        for (LineItem item : cart.getItems()) {
                %>


                <div class="confirmation-item">

                    <span>

                        <%= item.getProduct().getDescription() %>

                        × <%= item.getQuantity() %>

                    </span>

                    <strong>

                        <%= vndFormat.format(item.getTotal()) %> ₫

                    </strong>

                </div>


                <%
                        }
                    }
                %>


                <div class="confirmation-total">

                    <span>
                        Total
                    </span>

                    <strong>

                        <%= vndFormat.format(cart.getTotal()) %> ₫

                    </strong>

                </div>

            </div>


            <a href="${pageContext.request.contextPath}/index.jsp"
                class="continue-shopping-btn">
                ← Continue Shopping
            </a>


        </div>

    </div>

</main>


<footer class="site-footer">

    Murach's Java Servlets/JSP - Chapter 07

</footer>


</body>

</html>