package com.jsp.ecommerce.controller;

import java.io.IOException;
import java.util.List;

import com.jsp.ecommerce.dto.Blog;
import com.jsp.ecommerce.dto.Customer;
import com.jsp.ecommerce.dto.ShoppingOrder;
import com.jsp.ecommerce.repository.ShoppingOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.ecommerce.dto.Product;
import com.jsp.ecommerce.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminService adminService;
	@Autowired
	ShoppingOrderRepository orderRepository;


	@GetMapping("/add-product")
	public String loadAddProduct(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null)
			return "AddProduct";
		else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/home")
	public String loadHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null)
			return "AdminHome";
		else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@PostMapping("/add-product")
	public String addProduct(Product product, @RequestParam MultipartFile pic, HttpSession session, ModelMap map)
			throws IOException {
		if (session.getAttribute("admin") != null) {
			return adminService.addProduct(product, pic, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/fetch-products")
	public String fetchProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			return adminService.fetchProducts(map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/change/{id}")
	public String changeStatus(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			return adminService.changeStatus(id, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			return adminService.deleteProduct(id, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable int id, HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			return adminService.editProduct(id, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@PostMapping("/update-product")
	public String updateProduct(Product product, @RequestParam MultipartFile pic, HttpSession session, ModelMap map)
			throws IOException {
		if (session.getAttribute("admin") != null) {
			return adminService.updateProduct(product, pic, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/view-orders")
	public String viewOrders(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			List<ShoppingOrder> orders = orderRepository.findAll();
			if (orders.isEmpty()) {
				map.put("fail", "No Orders Placed Yet");
				return "AdminHome";
			} else {
				map.put("orders", orders);
				return "ViewOrderDetails"; // Replace with your desired view name
			}
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}


	@GetMapping("/add-blog")
	public String loadAddBlog(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null)
			return "AddBlog"; // Replace with your template name for adding blogs
		else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}
	@PostMapping("/add-blog")
	public String addBlog(Blog blog, @RequestParam MultipartFile pic, HttpSession session, ModelMap map) throws IOException {
		if (session.getAttribute("admin") != null) {
			return adminService.addBlog(blog, pic, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}





}
