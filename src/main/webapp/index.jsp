<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>CD Store</title>

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
            🛒 View Cart
        </a>

    </div>

</header>


<main>

    <div class="container">

        <h2 class="page-title">
            CD Collection
        </h2>

        <p class="page-description">
            Choose your favorite albums and add them to your shopping cart.
        </p>


        <div class="card">

            <table>

                <thead>

                    <tr>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Action</th>
                    </tr>

                </thead>

                <tbody>


                    <!-- CD 1 -->

                    <tr>

                        <td>

                            <div class="product-description">
                                86 (the band) - True Life Songs and Pictures
                            </div>

                            <span class="product-code">
                                Product #8601
                            </span>

                        </td>

                        <td class="price">
                            373.750 ₫
                        </td>

                        <td>

                            <form
                                action="${pageContext.request.contextPath}/cart"
                                method="get">

                                <input type="hidden"
                                       name="action"
                                       value="add">

                                <input type="hidden"
                                       name="productCode"
                                       value="8601">

                                <input type="submit"
                                       class="btn-primary"
                                       value="Add To Cart">

                            </form>

                        </td>

                    </tr>


                    <!-- CD 2 -->

                    <tr>

                        <td>

                            <div class="product-description">
                                Paddlefoot - The first CD
                            </div>

                            <span class="product-code">
                                Product #pf01
                            </span>

                        </td>

                        <td class="price">
                            323.750 ₫
                        </td>

                        <td>

                            <form
                                action="${pageContext.request.contextPath}/cart"
                                method="get">

                                <input type="hidden"
                                       name="action"
                                       value="add">

                                <input type="hidden"
                                       name="productCode"
                                       value="pf01">

                                <input type="submit"
                                       class="btn-primary"
                                       value="Add To Cart">

                            </form>

                        </td>

                    </tr>


                    <!-- CD 3 -->

                    <tr>

                        <td>

                            <div class="product-description">
                                Paddlefoot - The second CD
                            </div>

                            <span class="product-code">
                                Product #pf02
                            </span>

                        </td>

                        <td class="price">
                            373.750 ₫
                        </td>

                        <td>

                            <form
                                action="${pageContext.request.contextPath}/cart"
                                method="get">

                                <input type="hidden"
                                       name="action"
                                       value="add">

                                <input type="hidden"
                                       name="productCode"
                                       value="pf02">

                                <input type="submit"
                                       class="btn-primary"
                                       value="Add To Cart">

                            </form>

                        </td>

                    </tr>


                    <!-- CD 4 -->

                    <tr>

                        <td>

                            <div class="product-description">
                                Joe Rut - Genuine Wood Grained Finish
                            </div>

                            <span class="product-code">
                                Product #jr01
                            </span>

                        </td>

                        <td class="price">
                            373.750 ₫
                        </td>

                        <td>

                            <form
                                action="${pageContext.request.contextPath}/cart"
                                method="get">

                                <input type="hidden"
                                       name="action"
                                       value="add">

                                <input type="hidden"
                                       name="productCode"
                                       value="jr01">

                                <input type="submit"
                                       class="btn-primary"
                                       value="Add To Cart">

                            </form>

                        </td>

                    </tr>

                </tbody>

            </table>

        </div>

    </div>

</main>


<footer class="site-footer">

    Murach's Java Servlets/JSP - Chapter 07

</footer>

</body>

</html>