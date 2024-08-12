package com.jsp.ecommerce.controller;

import com.jsp.ecommerce.dto.Blog;
import com.jsp.ecommerce.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.ecommerce.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class CommonController {
	@Autowired
	AdminService adminService;
	@Autowired
	CustomerService customerService;

	@GetMapping("/")
	public String loadHome() {
		return "Home";
	}

	@GetMapping("/about-us")
	public String loadAboutUs() {
		return "AboutUs";
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "Login.html";
	}

	@PostMapping("/login")
	public String login(@RequestParam String emph, @RequestParam String password, ModelMap map, HttpSession session) {
		return customerService.login(emph, password, map, session);
	}
	@GetMapping("/blog")
	public String loadBlog(ModelMap map) {
		List<Blog> blogs = adminService.fetchAllBlogs(); // Assuming this fetches all blogs
		map.addAttribute("blogs", blogs);
		return "Blog"; // Assuming your blog template name is "Blog"
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, ModelMap map) {
		session.invalidate();
		map.put("pass", "Logout Success");
		return "Home";
	}
}
