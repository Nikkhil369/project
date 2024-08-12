package com.jsp.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.jsp.ecommerce.dao.CustomerDao;
import com.jsp.ecommerce.dao.ProductDao;
import com.jsp.ecommerce.dto.Customer;
import com.jsp.ecommerce.dto.Item;
/*import com.jsp.ecommerce.dto.PaymentDetails;*/
import com.jsp.ecommerce.dto.Product;
import com.jsp.ecommerce.dto.ShoppingCart;
import com.jsp.ecommerce.dto.ShoppingOrder;

/*import com.jsp.ecommerce.repository.PaymentDetailsRepository;*/
import com.jsp.ecommerce.repository.ShoppingOrderRepository;



import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	ShoppingOrderRepository orderRepository;

	/*@Autowired
	PaymentDetailsRepository detailsRepository;*/


	/**
	 * Creates a new customer account and saves it to the database.
	 * @param customer The customer object containing registration details.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String signup(Customer customer, ModelMap map) {
		// to check Email and Mobile is Unique
		List<Customer> exCustomers = customerDao.findByEmailOrMobile(customer.getEmail(), customer.getMobile());
		if (!exCustomers.isEmpty()) {
			map.put("fail", "Account Already Exists");
			return "Signup";
		} else {
			// Generating otp

			// Encrypting password
			customer.setPassword(customer.getPassword());
			customerDao.save(customer);

			// Carrying id
			map.put("success","Account created");
			return "Login.html";
		}
	}


	/**
	 * Logs in a customer or admin and redirects to the appropriate home page.
	 * @param usernameOrEmail Username or email entered by the user.
	 * @param password Password entered by the user.
	 * @param map The model map used to pass data to the view.
	 * @param session The HTTP session used to store user information.
	 * @return The name of the view to redirect to.
	 */

	public String login(String emph, String password, ModelMap map, HttpSession session) {
		if (emph.equals("admin") && password.equals("admin")) {
			session.setAttribute("admin", "admin");
			map.put("pass", "Admin Login Success");
			return "AdminHome";
		} else {
			long mobile = 0;
			String email = null;
			try {
				mobile = Long.parseLong(emph);
			} catch (NumberFormatException e) {
				email = emph;
			}

			List<Customer> customers = customerDao.findByEmailOrMobile(email, mobile);
			if (customers.isEmpty()) {
				map.put("fail", "Invalid Email or Mobile");
				return "Login.html";
			} else {
				Customer customer = customers.get(0);
				if ((customer.getPassword()).equals(password)) {
						session.setAttribute("customer", customer);
						map.put("pass", "Login Success");
						return "CustomerHome";
				} else {
					map.put("fail", "Invalid Password");
					return "Login.html";
				}
			}
		}

	}


	/**
	 * Fetches all products and displays them on the customer home page.
	 * Also checks and displays cart items if any.
	 * @param map The model map used to pass data to the view.
	 * @param customer The logged-in customer object (for checking cart).
	 * @return The name of the view to redirect to.
	 */
	public String fetchProducts(ModelMap map, Customer customer) {
		List<Product> products = productDao.fetchAll();
		if (products.isEmpty()) {
			map.put("fail", "No Products Present");
			return "CustomerHome";
		} else {

			if (customer.getCart() == null)
				map.put("items", null);
			else {
				map.put("items", customer.getCart().getItems());
			}

			map.put("products", products);
			return "CustomerViewProduct";
		}
	}



	/**
	 * Adds a product to the customer's shopping cart.
	 * @param customer The logged-in customer object.
	 * @param id The ID of the product to add.
	 * @param map The model map used to pass data to the view.
	 * @param session The HTTP session used to store customer information.
	 * @return The name of the view to redirect to.
	 */
	public String addToCart(Customer customer, int id, ModelMap map, HttpSession session) {
		// Find the product by its id
		Product product = productDao.findById(id);

		// Check if customer has a shopping cart, create a new one if not
		ShoppingCart cart = customer.getCart();
		if (cart == null)
			cart = new ShoppingCart();
		// Check if adding product would exceed transaction limit
		if ((cart.getTotalAmount() + product.getPrice()) < 100000) {
			List<Item> items = cart.getItems();
			// Get the cart items list
			if (items == null)
				items = new ArrayList<Item>();
			// Check product stock availability
			if (product.getStock() > 0) {
				boolean flag = true;
				// if item Already Exists in cart
				for (Item item : items) {
					if (item.getName().equals(product.getName())) {
						flag = false;
						item.setQuantity(item.getQuantity() + 1);
						item.setPrice(item.getPrice() + product.getPrice());
						break;
					}
				}
				if (flag) {
					// If item is New in cart
					Item item = new Item();
					item.setCategory(product.getCategory());
					item.setName(product.getName());
					item.setPicture(product.getPicture());
					item.setPrice(product.getPrice());
					item.setQuantity(1);
					items.add(item);
				}
				// Update cart items and total amount

				cart.setItems(items);
				cart.setTotalAmount(cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum());
				// Update customer with modified cart and save to database
				customer.setCart(cart);
				customerDao.save(customer);
				// updating stock
				product.setStock(product.getStock() - 1);
				productDao.save(product);
				session.setAttribute("customer", customer);
				map.put("pass", "Product Added to Cart");
				return fetchProducts(map, customer);
			} else {
				map.put("fail", "Out of stock");
				return fetchProducts(map, customer);
			}
		} else {
			map.put("fail", "Out of Transaction Limit");
			return fetchProducts(map, customer);
		}
	}


	/**
	 * Displays the customer's shopping cart contents.
	 *
	 * @param customer The logged-in customer object.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String viewCart(Customer customer, ModelMap map) {
		ShoppingCart cart = customer.getCart();
		if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
			map.put("fail", "No Items in Cart");
			return "CustomerHome";
		} else {
			map.put("cart", cart);
			return "ViewCart";
		}
	}

	/**
	 * Removes a product from the customer's shopping cart.
	 * @param customer The logged-in customer object.
	 * @param id The ID of the product to remove.
	 * @param map The model map used to pass data to the view.
	 * @param session The HTTP session used to store customer information.
	 * @return The name of the view to redirect to.
	 */
	public String removeFromCart(Customer customer, int id, ModelMap map, HttpSession session) {
		Product product = productDao.findById(id);

		ShoppingCart cart = customer.getCart();
		if (cart == null) {
			map.put("fail", "Item not in Cart");
			return fetchProducts(map, customer);
		} else {
			List<Item> items = cart.getItems();
			if (items == null || items.isEmpty()) {
				map.put("fail", "Item not in Cart");
				return fetchProducts(map, customer);
			} else {
				Item item = null;
				for (Item item2 : items) {
					if (item2.getName().equals(product.getName())) {
						item = item2;
						break;
					}
				}
				if (item == null) {
					map.put("fail", "Item not in Cart");
					return fetchProducts(map, customer);
				} else {
					if (item.getQuantity() > 1) {
						item.setQuantity(item.getQuantity() - 1);
						item.setPrice(item.getPrice() - product.getPrice());
					} else {
						items.remove(item);
					}
				}
				cart.setItems(items);
				cart.setTotalAmount(cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum());
				customer.setCart(cart);
				customerDao.save(customer);

				// updating stock
				product.setStock(product.getStock() + 1);
				productDao.save(product);

				if (item != null && item.getQuantity() == 1)
					productDao.deleteItem(item);

				session.setAttribute("customer", customer);
				map.put("pass", "Product Removed from Cart");
				return fetchProducts(map, customer);
			}
		}
	}

	/**
	 * Creates a new shopping order for the customer's cart items.
	 *
	 * @param customer The logged-in customer object.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String createOrder(Customer customer, ModelMap map) {

		/*PaymentDetails details = new PaymentDetails();*/
		/*details.setStatus("successful"); // Set a simulated successful status
		details.setAmount(customer.getCart().getTotalAmount()); */// Set the order amount

		ShoppingOrder order = new ShoppingOrder();
		order.setAmount(customer.getCart().getTotalAmount());
		order.setCustomer(customer);
		order.setDateTime(LocalDateTime.now());
		order.setOrder_id(String.valueOf(order.hashCode())); // Generate unique order ID
		order.setItems(customer.getCart().getItems());

		// Save PaymentDetails (optional, not for real payment processing)
		/*detailsRepository.save(details);*/

		orderRepository.save(order);

		customer.getCart().setItems(null);
		customerDao.save(customer);

		map.put("pass", "Order placed successfully!");
		return "CustomerHome"; // Redirect to customer home page
	}


	/*public String completeOrder(int id,Customer customer, ModelMap map) {
		*//*PaymentDetails details = detailsRepository.findById(id).orElse(null);
		details.setStatus("success");
		detailsRepository.save(details);*//*

		ShoppingOrder order = new ShoppingOrder();
		order.setAmount(details.getAmount());
		order.setCustomer(customer);
		order.setDateTime(LocalDateTime.now());
		order.setOrder_id(details.getOrder_id());
		order.setItems(customer.getCart().getItems());

		orderRepository.save(order);

		customer.getCart().setItems(null);
		customerDao.save(customer);

		map.put("pass", "Payment Complete");
		return "CustomerHome";
	}*/


	/**
	 * Fetches all past orders placed by the customer.
	 *
	 * @param customer The logged-in customer object.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String fetchAllorder(Customer customer, ModelMap map) {
		List<ShoppingOrder> orders = orderRepository.findByCustomer(customer);
		if (orders.isEmpty()) {
			map.put("fail", "No Orders Placed Yet");
			return "CustomerHome";
		} else {
			map.put("orders", orders);
			return "ViewOrders";
		}
	}


	/**
	 * Fetches the details of a specific order placed by the customer.
	 * @param id The ID of the order to fetch.
	 * @param customer The logged-in customer object (for verification).
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String fetchAllorderItems(int id, Customer customer, ModelMap map) {
		ShoppingOrder order = orderRepository.findById(id).orElse(null);
		map.put("order", order);
		return "ViewOrderItems";
	}

}
