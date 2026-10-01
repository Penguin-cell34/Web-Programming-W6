<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="vi">

<head>

    <meta charset="UTF-8">

    <title>Payment Result</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 50px;
        }

        .container {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        h1 {
            margin-bottom: 20px;
        }

        .message {
            margin: 20px 0;
            font-size: 18px;
        }

        .button {
            display: inline-block;
            padding: 12px 20px;
            background: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }

    </style>

</head>

<body>

<div class="container">

    <%
        String status =
                (String) request.getAttribute(
                        "paymentStatus"
                );

        String message =
                (String) request.getAttribute(
                        "paymentMessage"
                );
    %>


    <% if ("FAILED".equals(status)) { %>

        <h1>Thanh toán thất bại</h1>

        <div class="message">
            <%= message %>
        </div>

        <a
            class="button"
            href="<%= request.getContextPath() %>/checkout"
        >
            Quay lại thanh toán
        </a>


    <% } else { %>

        <h1>Thanh toán không hợp lệ</h1>

        <div class="message">
            <%= message %>
        </div>

        <a
            class="button"
            href="<%= request.getContextPath() %>/index.jsp"
        >
            Quay lại trang mua hàng
        </a>

    <% } %>

</div>

</body>

</html>