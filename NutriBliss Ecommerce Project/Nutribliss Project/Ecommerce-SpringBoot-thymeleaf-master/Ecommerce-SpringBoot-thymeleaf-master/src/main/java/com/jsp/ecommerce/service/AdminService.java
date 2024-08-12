package com.jsp.ecommerce.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.jsp.ecommerce.dto.*;
import com.jsp.ecommerce.repository.BlogRepository;
import com.jsp.ecommerce.repository.ShoppingOrderRepository;
/*import jakarta.servlet.http.HttpSession;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.ecommerce.dao.ProductDao;

@Service
public class AdminService {

	@Autowired
	ProductDao productDao;
	@Autowired
	ShoppingOrderRepository orderRepository;

	@Autowired
	private BlogRepository blogRepository;

	/**
	 * Saves a new product to the database.
	 *
	 * @param product The product to save.
	 * @param pic The multipart file containing the product image.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 * @throws IOException If there is an error reading the image data.
	 */
	public String addProduct(Product product, MultipartFile pic, ModelMap map) throws IOException {
		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);
		product.setPicture(picture);
		productDao.save(product);

		map.put("pass", "Product Added Success");
		return "AdminHome";
	}



	/**
	 * Fetches all products from the database and adds them to the model map.
	 *
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String fetchProducts(ModelMap map) {
		List<Product> products = productDao.fetchAll();
		if (products.isEmpty()) {
			map.put("fail", "No Products Found");
			return "AdminHome";
		} else {
			map.put("products", products);
			return "AdminViewProduct";
		}
	}

	/**
	 * Updates the display status of a product.
	 *
	 * @param id The ID of the product to update.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String changeStatus(int id, ModelMap map) {
		Product product=productDao.findById(id);
		if(product.isDisplay())
			product.setDisplay(false);
		else
			product.setDisplay(true);
		
		productDao.save(product);
		
		map.put("pass", "Status Update Success");
		return fetchProducts(map);
	}

	/**
	 * Deletes a product from the database.
	 *
	 * @param id The ID of the product to delete.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String deleteProduct(int id, ModelMap map) {
		Product product=productDao.findById(id);
		productDao.delete(product);
		
		map.put("pass", "Product Deleted Success");
		return fetchProducts(map);
	}

	/**
	 * Fetches a product by its ID and adds it to the model map.
	 *
	 * @param id The ID of the product to fetch.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 */
	public String editProduct(int id, ModelMap map) {
		Product product=productDao.findById(id);
		map.put("product", product);
		return "EditProduct.html";
	}

/**
 * Updates a product in the database.
 *
 * @param product The product to update.
 * @param pic The multipart file containing the product image (optional).
 * @param map The model map used to pass data to the view.
 * @return The name of the view to redirect to.
 * * @throws IOException If there is an error reading the image data.
 *      */
	public String updateProduct(Product product, MultipartFile pic, ModelMap map) throws IOException {
		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);
		
		if(picture.length==0)
			product.setPicture(productDao.findById(product.getId()).getPicture());
		else
		product.setPicture(picture);
		
		productDao.save(product);

		map.put("pass", "Product Updated Success");
		return fetchProducts(map);
	}


	/**
	 * Saves a new blog post to the database.
	 *
	 * @param blog The blog post to save.
	 * @param pic The multipart file containing the blog post image.
	 * @param map The model map used to pass data to the view.
	 * @return The name of the view to redirect to.
	 * @throws IOException If there is an error reading the image data.
	 */
	public String addBlog(Blog blog, MultipartFile pic, ModelMap map) throws IOException {
		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);
		blog.setPicture(picture);
		blog.setUploadDateTime(LocalDateTime.now());
		blogRepository.save(blog);

		map.put("pass", "Blog Added Success");
		return "AdminHome";
	}

	/**
	 * Fetches all blog posts from the database.
	 *
	 * @return A list of all blog posts.
	 */
	public List<Blog> fetchAllBlogs() {
		return blogRepository.findAll();
	}




}
