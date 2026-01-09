package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonUtil commonUtil;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {

		if (p != null) {
			String email = p.getName();

			UserDtls userDtls = userService.getUserByEmail(email);

			m.addAttribute("user", userDtls);

			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("category", allActiveCategory);

	}

	@GetMapping("/")
	public String index() {

		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {

		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);

		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name  = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name  = "pageSize", defaultValue = "5") Integer pageSize) {

		// Fresh object EVERY TIME
		m.addAttribute("categoryForm", new Category());

//		m.addAttribute("categories", categoryService.getAllCategory());
		
		Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
		 List<Category> categories = page.getContent();
			m.addAttribute("categories", categories);
//			m.addAttribute("categorySize", categories.size());
			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute("categoryForm") Category category,
			@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

		// Defensive logging
		System.out.println("ID: " + category.getId());
		System.out.println("Name: " + category.getName());

		// Check duplicate ONLY for NEW category
		if (category.getId() == null && categoryService.existCategory(category.getName())) {
			session.setAttribute("errorMsg", "Category already exists!");
			return "redirect:/admin/category";
		}

		// Handle image
		String imageName = "default.jpg";
		if (file != null && !file.isEmpty()) {
			imageName = file.getOriginalFilename();
		}
		category.setImageName(imageName);

		Category saved = categoryService.saveCategory(category);

		if (saved == null) {
			session.setAttribute("errorMsg", "Not Saved! Server Error");
			return "redirect:/admin/category";
		}

		// Save image file
		if (file != null && !file.isEmpty()) {
			File saveDir = new ClassPathResource("static/img/category_img").getFile();
			Path path = Paths.get(saveDir.getAbsolutePath(), imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}

		session.setAttribute("succMsg", "Category Saved Successfully!");
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "category deleted successfully !");
		} else {
			session.setAttribute("errorMsg", "something wrong on server !");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {

		m.addAttribute("category", categoryService.getCategoryById(id));

		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@RequestParam int id, @RequestParam String name, @RequestParam boolean isActive,
			@RequestParam(required = false) MultipartFile file, HttpSession session) throws IOException {

		Category category = categoryService.getCategoryById(id);

		if (category == null) {
			session.setAttribute("errorMsg", "Category not found!");
			return "redirect:/admin/category";
		}

		// duplicate check (ignore same record)
		Boolean exists = categoryService.existCategory(name);
		if (exists && !category.getName().equalsIgnoreCase(name)) {
			session.setAttribute("errorMsg", "Category already exists!");
			return "redirect:/admin/loadEditCategory/" + id;
		}

		// image logic
		String imageName = category.getImageName();
		if (file != null && !file.isEmpty()) {
			imageName = file.getOriginalFilename();

			File dir = new ClassPathResource("static/img/category_img").getFile();
			Path path = Paths.get(dir.getAbsolutePath(), imageName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}

		category.setName(name);
		category.setIsActive(isActive);
		category.setImageName(imageName);

		categoryService.saveCategory(category);

		session.setAttribute("succMsg", "Category updated successfully!");
		return "redirect:/admin/loadEditCategory/" + id;
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		System.out.println(imageName);

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());

		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ image.getOriginalFilename());

			// System.out.println(path);

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			session.setAttribute("succMsg", "Product Saved Successfully !");
		} else {

			session.setAttribute("errorMsg", "something wrong on server !");
		}

		return "redirect:/admin/loadAddProduct";

	}

	@GetMapping("/products")
	public String loadViewProduct(Model model,@RequestParam(defaultValue = "") String ch,@RequestParam(name  = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name  = "pageSize", defaultValue = "5") Integer pageSize) {

//		List<Product> products= null;
//		if(ch!=null && ch.length()>0) {
//			products =  productService.searchProduct(ch);
//		}else {
//			products =  productService.getAllProducts();
//		}
//		
//		model.addAttribute("products", products);
		
		Page<Product> page = null;
		if(ch!=null && ch.length()>0) {
			page =  productService.searchProductPagination(ch, pageNo, pageSize);
		}else {
			page =  productService.getAllProductsPagination(pageNo, pageSize);
		}
		
		model.addAttribute("products", page.getContent());
		
			model.addAttribute("pageNo", page.getNumber());
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("totalElements", page.getTotalElements());
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("isFirst", page.isFirst());
			model.addAttribute("isLast", page.isLast());
		

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {

		Boolean deleteProduct = productService.deleteProduct(id);

		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully !");
		} else {
			session.setAttribute("errorMsg", "something wrong on server !");
		}

		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {

		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());

		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {

			session.setAttribute("errorMsg", "Invalid Discount !");

		} else {

			Product updateProduct = productService.updateProduct(product, image);

			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product updated successfully !");
			} else {
				session.setAttribute("errorMsg", "something wrong on server !");
			}

		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

//	@GetMapping("/users")
//	public String users(Model m,@RequestParam Integer type) {
//
////		List<UserDtls> users = userService.getAllUsers("ROLE_USER");
//		List<UserDtls> users = null;
//		if(type==1) {
//			users = userService.getAllUsers("ROLE_USER");
//		}else {
//			users = userService.getAllUsers("ROLE_ADMIN");
//		}
//		
//		m.addAttribute("userType", type);
//		m.addAttribute("users", users);
//
//		return "/admin/users";
//	}
	
	@GetMapping("/users")
	public String users(
	        Model model,
	        @RequestParam Integer type,
	        @RequestParam(defaultValue = "") String ch,
	        @RequestParam(defaultValue = "0") Integer pageNo,
	        @RequestParam(defaultValue = "5") Integer pageSize) {

	    String role = (type == 1) ? "ROLE_USER" : "ROLE_ADMIN";

	    Page<UserDtls> page = userService.getUsersWithSearch(role, ch, pageNo, pageSize);

	    model.addAttribute("users", page.getContent());
	    model.addAttribute("userType", type);
	    model.addAttribute("ch", ch);

	    model.addAttribute("pageNo", page.getNumber());
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalElements", page.getTotalElements());
	    model.addAttribute("totalPages", page.getTotalPages());
	    model.addAttribute("isFirst", page.isFirst());
	    model.addAttribute("isLast", page.isLast());

	    return "admin/users";
	}



	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Integer id, @RequestParam Boolean status,@RequestParam Integer type, HttpSession session) {

		Boolean updateStatus = userService.updateAccountStatus(id, status);

		if (updateStatus) {
			session.setAttribute("succMsg", "Account status Updated !");
		} else {

			session.setAttribute("errorMsg", "something wrong on server !");
		}

		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model model,@RequestParam(defaultValue = "") String ch,@RequestParam(name  = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name  = "pageSize", defaultValue = "5") Integer pageSize) {

//		List<ProductOrder> allOrders = orderService.getAllOrders();
//
//		model.addAttribute("orders", allOrders);
//
//		model.addAttribute("srch", false);
		
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);

		model.addAttribute("orders", page.getContent());

		model.addAttribute("srch", false);
		
		model.addAttribute("pageNo", page.getNumber());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalElements", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isFirst", page.isFirst());
		model.addAttribute("isLast", page.isLast());
		
		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrderStatus = orderService.updateOrderStatus(id, status);
		 try {
				commonUtil.sendMailForProductOrder(updateOrderStatus, status);
			} catch (Throwable e) {
				e.printStackTrace();
			}

		if (!ObjectUtils.isEmpty(updateOrderStatus)) {
			session.setAttribute("succMsg", "Status updated !");
		} else {
			session.setAttribute("errorMsg", "Status Not updated !");
		}

		return "redirect:/admin/orders";
	}
	
	
	@GetMapping("/search-order")
	public String searchOrder(@RequestParam String orderId,Model model,@RequestParam(defaultValue = "") String ch,@RequestParam(name  = "pageNo", defaultValue = "0") Integer pageNo, @RequestParam(name  = "pageSize", defaultValue = "5") Integer pageSize,HttpSession session) {
		
		if(orderId!=null && orderId.length()>0) {
		
		ProductOrder ordersByOrderId = orderService.getOrdersByOrderId(orderId.trim());
		
		if(ObjectUtils.isEmpty(ordersByOrderId)) {
			session.setAttribute("errorMsg", "Incorrect orderId !");
			model.addAttribute("orderDtls", null);
		}else {
			model.addAttribute("orderDtls", ordersByOrderId);
		}
		
		model.addAttribute("srch", true);
		
		}else {
//			Page<ProductOrder> allOrders = orderService.getAllOrdersPagination(pageNo, pageSize);
//
//			model.addAttribute("orders", allOrders);
//
//			model.addAttribute("srch", false);
			
			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);

			model.addAttribute("orders", page.getContent());

			model.addAttribute("srch", false);
			
			model.addAttribute("pageNo", page.getNumber());
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("totalElements", page.getTotalElements());
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("isFirst", page.isFirst());
			model.addAttribute("isLast", page.isLast());
		}
		return "/admin/orders";
	}
	
	@GetMapping("/add-admin")
	public String loadAddAdmin() {
		
		return "/admin/add_admin";
	}
	
	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

		user.setProfileImage(imageName);

		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

				System.out.println(path);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Register Successfully !");

			}

		} else {
			session.setAttribute("errorMsg", "something wrong on server !");
		}

		return "redirect:/admin/add-admin";
	}
	
	
	
	
	@GetMapping("/profile")
	public String profile() {
		
		
		return "/admin/profile";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user,@RequestParam MultipartFile img,HttpSession session) {
		
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		
		if(ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile Not updated !");
		}else {
			session.setAttribute("succMsg", "Profile updated successfully !");
		}
		
		return "redirect:/admin/profile";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session) {
		
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);
		
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
		
		if(matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if(ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password Not updated! Error in server!");
			}else {
				session.setAttribute("succMsg", "Password updated successfully!");
			}
		}else {
			session.setAttribute("errorMsg", "Current password incorrect!");
		}
		
		return "redirect:/admin/profile";
	}

	
}