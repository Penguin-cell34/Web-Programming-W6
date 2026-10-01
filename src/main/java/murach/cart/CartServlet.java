package murach.cart;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import murach.model.Cart;
import murach.model.Product;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private Product getProduct(String productCode) {

        return switch (productCode) {

            case "8601" ->
                new Product(
                    "8601",
                    "86 (the band) - True Life Songs and Pictures",
                    373750
                );

            case "pf01" ->
                new Product(
                    "pf01",
                    "Paddlefoot - The first CD",
                    323750
                );

            case "pf02" ->
                new Product(
                    "pf02",
                    "Paddlefoot - The second CD",
                    373750
                );

            case "jr01" ->
                new Product(
                    "jr01",
                    "Joe Rut - Genuine Wood Grained Finish",
                    373750
                );

            default -> null;
        };
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "view";
        }

        HttpSession session = request.getSession();

        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        switch (action) {

            case "view" -> request.getRequestDispatcher(
                "/cart.jsp"
            ).forward(request, response);

            case "add" -> {

                String productCode =
                    request.getParameter("productCode");

                Product product = getProduct(productCode);

                if (product != null) {
                    cart.addItem(product);
                }

                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
            }

            case "update" -> {

                String productCode =
                    request.getParameter("productCode");

                String quantityString =
                    request.getParameter("quantity");

                try {

                    int quantity =
                        Integer.parseInt(quantityString);

                    cart.updateItem(
                        productCode,
                        quantity
                    );

                } catch (NumberFormatException e) {
                    // Ignore invalid quantity
                }

                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
            }

            case "remove" -> {

                String productCode =
                    request.getParameter("productCode");

                cart.removeItem(productCode);

                response.sendRedirect(
                    request.getContextPath() + "/cart"
                );
            }
        }
    }
}