<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="murach.model.Cart" %>
<%@ page import="murach.model.LineItem" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<%
    Cart cart = (Cart) session.getAttribute("cart");
        NumberFormat vndFormat = NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN"));

    if (cart == null || cart.isEmpty()) {

        response.sendRedirect(
            request.getContextPath() + "/index.jsp"
        );

        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Checkout</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/main.css">

</head>

<body>

<header class="site-header">

    <div class="container">

        <h1 class="logo">
            🎵 CD Store
        </h1>

        <a class="cart-link"
           href="${pageContext.request.contextPath}/cart">
            ← Back to Cart
        </a>

    </div>

</header>


<main>

    <div class="container">

        <h2 class="page-title">
            Checkout
        </h2>

        <p class="page-description">
            Please enter your information to complete your order.
        </p>


        <div class="checkout-grid">


            <!-- Customer information -->

            <div class="card">

                <h3 class="section-title">
                    Customer Information
                </h3>


                <form
                    action="${pageContext.request.contextPath}/checkout"
                    method="post">


                    <div class="form-group">

                        <label for="name">
                            Full Name
                        </label>

                        <input
                            type="text"
                            id="name"
                            name="name"
                            required>

                    </div>


                    <div class="form-group">

                        <label for="email">
                            Email
                        </label>

                        <input
                            type="email"
                            id="email"
                            name="email"
                            required>

                    </div>


                    <div class="form-group">

                        <label for="phone">
                            Phone Number
                        </label>

                        <input
                            type="tel"
                            id="phone"
                            name="phone"
                            required>

                    </div>


                    <div class="form-group">

                        <label for="address">
                            Shipping Address
                        </label>

                        <textarea
                            id="address"
                            name="address"
                            rows="4"
                            required></textarea>

                    </div>


                    <div class="form-group">

                        <label for="city">
                            City
                        </label>

                        <input
                            type="text"
                            id="city"
                            name="city"
                            required>

                    </div>


                    <!-- Payment method -->

                    <div class="form-group">

                        <label>
                            Payment Method
                        </label>


                        <div class="payment-options">


                            <!-- COD -->

                            <label class="radio-option">

                                <input
                                    type="radio"
                                    name="payment"
                                    value="COD"
                                    checked>

                                Cash on Delivery (COD)

                            </label>


                            <!-- VNPay -->

                            <label class="radio-option">

                                <input
                                    type="radio"
                                    name="payment"
                                    value="VNPAY">

                                VNPay Sandbox

                            </label>


                        </div>

                    </div>


                    <div class="checkout-buttons">


                        <a
                            href="${pageContext.request.contextPath}/cart"
                            class="btn-secondary link-button">

                            ← Back to Cart

                        </a>


                        <button
                            type="submit"
                            class="btn-success">

                            Place Order ✓

                        </button>


                    </div>


                </form>

            </div>


            <!-- Order summary -->

            <div class="card order-summary">

                <h3 class="section-title">
                    Order Summary
                </h3>


                <%
                    for (LineItem item : cart.getItems()) {
                %>


                <div class="summary-item">

                    <div>

                        <div class="summary-description">

                            <%= item.getProduct().getDescription() %>

                        </div>


                        <div class="summary-quantity">

                            Quantity:

                            <%= item.getQuantity() %>

                        </div>

                    </div>


                    <div class="summary-price">

                        <%= vndFormat.format(item.getTotal()) %> ₫

                    </div>

                </div>


                <%
                    }
                %>


                <div class="summary-total">

                    <span>
                        Total
                    </span>

                    <strong>

                        <%= vndFormat.format(cart.getTotal()) %> ₫

                    </strong>

                </div>


            </div>


        </div>

    </div>

</main>


<footer class="site-footer">

    Murach's Java Servlets/JSP - Chapter 07

</footer>


</body>

</html>

