package server.helpers;

import models.Customer;
import models.Product;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains helper methods for the handlers.
 * The methods are static, so they can be called without instantiating the class.
 * The methods are public, so they can be called from other classes.
 * The methods are: getBytesFromInputStream, generateProductListHTML, generateCustomerListHTML, generateProductCategoryCheckboxes
 */
public class HtmlHelper {
    /**
     * This method reads an input stream and returns a byte array.
     * @param is InputStream object
     * @return byte[] containing the bytes read from the input stream
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        byte[] bytes = null;
        try {
            bytes = new byte[is.available()];
            var res = is.read(bytes);
        } catch (IOException ex) {
            System.out.println("Error reading file");
            return bytes;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                System.out.println("Error closing file");
            }
        }
        return bytes;
    }

    /**
     * This method generates HTML for a list of products.
     * @param itemList List of Product objects
     * @param isAdmin boolean true if user is admin, false if not
     * @return String containing the HTML for the list of products
     */
    public static String generateProductListHTML(List<Product> itemList, boolean isAdmin) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<thead\n>");
        htmlBuilder.append("<tr>\n");
        htmlBuilder.append("<th scope=\"col\">ID</th>\n");
        htmlBuilder.append("<th scope=\"col\">SKU</th>\n");
        htmlBuilder.append("<th scope=\"col\">Description</th>\n");
        htmlBuilder.append("<th scope=\"col\">Category</th>\n");
        htmlBuilder.append("<th scope=\"col\">Price</th>\n");
        if (isAdmin) {
            htmlBuilder.append("<th scope=\"col\"></th>\n");
            htmlBuilder.append("<th scope=\"col\"></th>\n");
        }
        else {
            htmlBuilder.append("<th scope=\"col\"></th>\n");
        }
        htmlBuilder.append("</tr>\n");
        htmlBuilder.append("</thead\n>");
        htmlBuilder.append("<tbody\n>");
        for (Product item : itemList) {
            htmlBuilder.append("<tr>\n");
            htmlBuilder.append("<th scope=\"row\">").append(item.getId()).append("</th>\n");
            htmlBuilder.append("<td>").append(item.getSKU()).append("</td>\n");
            htmlBuilder.append("<td>").append(item.getDescription()).append("</td>\n");
            htmlBuilder.append("<td>").append(item.getCategory()).append("</td>\n");
            htmlBuilder.append("<td>").append(item.getPrice()).append("</td>\n");

            // only show delete button if user is admin
            if (isAdmin) {
                htmlBuilder.append("<td><a class=\"btn btn-primary\" href='products/edit/")
                        .append(item.getId()).append("'>Edit</a></td>\n");
                htmlBuilder.append("<td><a class=\"btn btn-danger\" href='products/del/")
                        .append(item.getId()).append("'>Delete</a></td>\n");
            }
            else {
                htmlBuilder.append("<td><a class=\"btn btn-outline-primary\" href='cart/add/")
                        .append(item.getId()).append("'>Add</a></td>\n");
            }
            htmlBuilder.append("</tr>\n");
        }
        htmlBuilder.append("</tbody\n>");
        return htmlBuilder.toString();
    }

    /**
     * This method generates HTML for a list of customers.
     * @param itemList List of Customer objects
     * @param isAdmin boolean true if user is admin, false if not
     * @return String containing the HTML for the list of customers
     */
    public static String generateCustomerListHTML(List<Customer> itemList, boolean isAdmin) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<thead\n>");
        htmlBuilder.append("<tr>\n");
        htmlBuilder.append("<th scope=\"col\">ID</th>\n");
        htmlBuilder.append("<th scope=\"col\">Business Name</th>\n");
        htmlBuilder.append("<th scope=\"col\">Address</th>\n");
        htmlBuilder.append("<th scope=\"col\">Telephone</th>\n");
        htmlBuilder.append("<th scope=\"col\"></th>\n");
        htmlBuilder.append("<th scope=\"col\"></th>\n");
        htmlBuilder.append("</tr>\n");
        htmlBuilder.append("</thead\n>");
        htmlBuilder.append("<tbody\n>");
        for (Customer item : itemList) {
            htmlBuilder.append("<tr>\n");
            htmlBuilder.append("<th scope=\"row\">").append(item.getId()).append("</th>\n");
            htmlBuilder.append("<td>").append(item.getBusinessName()).append("</td>\n");
            htmlBuilder.append("<td>").append(item.getAddress()).append("</td>\n");
            htmlBuilder.append("<td>").append(item.getTelephone()).append("</td>\n");
            // only show delete button if user is admin
            if (isAdmin) {
                htmlBuilder.append("<td><a class=\"btn btn-primary\" href='customers/edit/")
                        .append(item.getId()).append("'>Edit</a></td>\n");
                htmlBuilder.append("<td><a class=\"btn btn-danger\" href='customers/del/")
                        .append(item.getId()).append("'>Delete</a></td>\n");
            }
            htmlBuilder.append("</tr>\n");
        }
        htmlBuilder.append("</tbody\n>");
        return htmlBuilder.toString();
    }

    /**
     * This method generates HTML for a list of checkboxes for product categories.
     * @param products List of Product objects
     * @return String containing the HTML for the list of checkboxes
     */
    public static String generateProductCategoryCheckboxes(List<Product> products) {
        StringBuilder htmlBuilder = new StringBuilder();
        var categories = products.stream().map(Product::getCategory).distinct().toList();
        for (var category : categories) {
            String listItem = String.format("""
                    <li>
                        <label class="form-check-label" for="%s">
                            <input class="form-check-input" type="checkbox" id="%s" name=%s value="%s">
                        </label>
                        <span>%s</span>
                    </li>""",
                    "filter_"+categories.indexOf(category),
                    "filter_"+categories.indexOf(category),
                    "filter_"+categories.indexOf(category), category, category);
            htmlBuilder.append(listItem);
        }
        return htmlBuilder.toString();
    }

    public static String generateCartItemsListHtml(HashMap<Product, Integer> cartItems) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<thead\n>");
        htmlBuilder.append("<tr>\n");
        htmlBuilder.append("<th scope=\"col\">ID</th>\n");
        htmlBuilder.append("<th scope=\"col\">SKU</th>\n");
        htmlBuilder.append("<th scope=\"col\">Description</th>\n");
        htmlBuilder.append("<th scope=\"col\">Category</th>\n");
        htmlBuilder.append("<th scope=\"col\">Price</th>\n");
        htmlBuilder.append("<th scope=\"col\">Count</th>\n");
        htmlBuilder.append("<th scope=\"col\"></th>\n");
        htmlBuilder.append("<th scope=\"col\"></th>\n");
        htmlBuilder.append("</tr>\n");
        htmlBuilder.append("</thead\n>");
        htmlBuilder.append("<tbody\n>");
        if(!cartItems.isEmpty()) {
            int total = 0;
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                Product item = entry.getKey();
                int count = entry.getValue();
                htmlBuilder.append("<tr>\n");
                htmlBuilder.append("<th scope=\"row\">").append(item.getId()).append("</th>\n");
                htmlBuilder.append("<td>").append(item.getSKU()).append("</td>\n");
                htmlBuilder.append("<td>").append(item.getDescription()).append("</td>\n");
                htmlBuilder.append("<td>").append(item.getCategory()).append("</td>\n");
                htmlBuilder.append("<td>").append(item.getPrice()).append("</td>\n");
                htmlBuilder.append("<td>").append(count).append("</td>\n");
                // add minus button
                htmlBuilder.append("<td><a class=\"btn btn-outline-secondary\" href='cart/del/")
                        .append(item.getId()).append("'>-</a></td>\n");
                // add plus button
                htmlBuilder.append("<td><a class=\"btn btn-outline-success\" href='cart/add/")
                        .append(item.getId()).append("'>+</a></td>\n");
                htmlBuilder.append("</tr>\n");
                total += item.getPrice() * count;
            }
            // add total price
            htmlBuilder.append("<tr>\n");
            htmlBuilder.append("<th scope=\"row\"></th>\n");
            htmlBuilder.append("<td>Total Price: </td>\n");
            htmlBuilder.append("<td></td>\n");
            htmlBuilder.append("<td></td>\n");
            htmlBuilder.append("<td>").append(total).append("</td>\n");
            htmlBuilder.append("<td></td>\n");
        }
        htmlBuilder.append("</tbody\n>");
        return htmlBuilder.toString();
    }
}
