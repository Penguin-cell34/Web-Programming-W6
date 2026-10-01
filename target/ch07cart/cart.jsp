<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="murach.model.Cart" %>
<%@ page import="murach.model.LineItem" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<%
    Cart cart = (Cart) session.getAttribute("cart");
    NumberFormat vndFormat = NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN"));
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Your Cart</title>

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

        <h2 class="page-title">
            Your Shopping Cart
        </h2>

        <p class="page-description">
            Review your items before checkout.
        </p>


        <div class="card">

            <%
                if (cart != null && !cart.isEmpty()) {
            %>


            <!-- =========================
                 CART HAS ITEMS
                 ========================= -->

            <table>

                <thead>

                    <tr>

                        <th>Quantity</th>

                        <th>Description</th>

                        <th>Price</th>

                        <th>Amount</th>

                        <th>Action</th>

                    </tr>

                </thead>


                <tbody>

                <%
                    for (LineItem item : cart.getItems()) {
                %>

                <tr>

                    <!-- Quantity -->
                    <td>

                        <form
                            class="quantity-form"
                            action="${pageContext.request.contextPath}/cart"
                            method="get">

                            <input
                                type="hidden"
                                name="action"
                                value="update">

                            <input
                                type="hidden"
                                name="productCode"
                                value="<%= item.getProduct().getCode() %>">

                            <input
                                class="quantity-input"
                                type="number"
                                name="quantity"
                                min="1"
                                value="<%= item.getQuantity() %>">

                            <input
                                type="submit"
                                class="btn-secondary"
                                value="Update">

                        </form>

                    </td>


                    <!-- Description -->
                    <td>

                        <div class="product-description">

                            <%= item.getProduct().getDescription() %>

                        </div>

                        <span class="product-code">

                            Product #

                            <%= item.getProduct().getCode() %>

                        </span>

                    </td>


                    <!-- Price -->
                    <td class="price">

                        <%= vndFormat.format(
                            item.getProduct().getPrice()
                        ) %> ₫

                    </td>


                    <!-- Amount -->
                    <td class="amount">

                        <%= vndFormat.format(
                            item.getTotal()
                        ) %> ₫

                    </td>


                    <!-- Remove -->
                    <td>

                        <form
                            action="${pageContext.request.contextPath}/cart"
                            method="get">

                            <input
                                type="hidden"
                                name="action"
                                value="remove">

                            <input
                                type="hidden"
                                name="productCode"
                                value="<%= item.getProduct().getCode() %>">

                            <input
                                type="submit"
                                class="btn-danger"
                                value="Remove">

                        </form>

                    </td>

                </tr>


                <%
                    }
                %>

                </tbody>

            </table>


            <!-- Cart total -->
            <div class="cart-total">

                <span class="cart-total-label">
                    Total:
                </span>

                <span class="cart-total-price">

                    <%= vndFormat.format(
                        cart.getTotal()
                    ) %> ₫

                </span>

            </div>


            <!-- Information -->
            <div class="cart-info">

                <strong>
                    💡 Tip:
                </strong>

                Enter a new quantity and click

                <strong>Update</strong>

                to change the quantity of an item.

            </div>


            <!-- Actions -->
            <div class="actions">

                <!-- Continue Shopping -->
                <form
                    action="${pageContext.request.contextPath}/index.jsp"
                    method="get">

                    <input
                        type="submit"
                        class="btn-secondary"
                        value="← Continue Shopping">

                </form>


                <!-- Proceed to Checkout -->
                <form
                    action="${pageContext.request.contextPath}/checkout"
                    method="get">

                    <input
                        type="submit"
                        class="btn-success"
                        value="Proceed to Checkout →">

                </form>

            </div>


            <%
                } else {
            %>


            <!-- =========================
                 EMPTY CART
                 ========================= -->

            <div class="empty-cart">

                <div class="empty-cart-icon">
                    🛒
                </div>

                <h2>
                    Your cart is empty
                </h2>

                <p>
                    You haven't added any CDs to your cart yet.
                </p>


                <!-- Continue Shopping -->
                <form
                    action="${pageContext.request.contextPath}/index.jsp"
                    method="get">

                    <input
                        type="submit"
                        class="btn-secondary"
                        value="← Continue Shopping">

                </form>

            </div>


            <%
                }
            %>

        </div>

    </div>

</main>


<footer class="site-footer">

    Murach's Java Servlets/JSP - Chapter 07

</footer>


</body>

</html>