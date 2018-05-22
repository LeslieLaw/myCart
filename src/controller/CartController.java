package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pojo.User;
import pojo.Category;
import pojo.OrderItem;
import pojo.Product;
import service.CategoryService;
import service.OrderItemService;
import service.OrderService;
import service.ProductService;
import service.UserService;

@Controller
@RequestMapping("")
public class CartController{
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderItemService orderItemService;
	
	@Autowired
	UserService userService;
		
	@RequestMapping("listProduct")
	public ModelAndView listProduct() {
		ModelAndView mav = new ModelAndView();
		List<Category> cs = categoryService.list();
		List<Product> ps = productService.list();
		
		//����ת������
		mav.addObject("cs",cs);
		mav.addObject("ps",ps);
		//����jsp·��
		mav.setViewName("listProduct");
		return mav;
	}
	
	@RequestMapping("addOrderItem")
	//Ϊadd��������OrderItem����������ע��
	public ModelAndView add(OrderItem oi,HttpSession session) {
		
		List<Product> ps = productService.list();
		
		ModelAndView mav = new ModelAndView("listOrderItem");
	
		/*
		 * ��session.ois�м����½���OrderItem����
		 */
		
		List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
		
		/*����ǵ�һ�α���OrderItem��������session���½�ois*/
		if(null == ois) {
			ois = new ArrayList<OrderItem>();
			session.setAttribute("ois", ois);
		}
		/*���product.id��һ����,�͵�������Ŀ*/
		boolean found = false;
		for(OrderItem orderItem : ois) {
			if(orderItem.getPid() == oi.getPid()) {
				orderItem.setNum(orderItem.getNum() + oi.getNum());
				found = true;
				break;
			}
		}
		if(!found)
			ois.add(oi);
		
		mav.addObject("ps",ps);
		mav.addObject("ois",ois);
		
		return mav;
	} 
	
	@RequestMapping("createOrder")
	public ModelAndView createOrder(HttpSession session,HttpServletResponse response) throws IOException, InterruptedException {
		
		/*�ж��û��Ƿ��½��δ��½����ת��login.jsp*/
		User user = (User)session.getAttribute("user");
		if(null == user) {
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		orderService.add(user.getId());
		
		/*��ȡsession�е�ois,����order���Ա��������ݿ�,������session*/
		List<OrderItem> ois = (List<OrderItem>)session.getAttribute("ois");
		for(OrderItem oi : ois) {
			oi.setOid(orderService.getId(user.getId()));
			orderItemService.add(oi);
		}
		
		ois.clear();
		
		ModelAndView mav = new ModelAndView("jump");
		return mav;
	}
	
	@RequestMapping("login")
	public ModelAndView login() {
		
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}
	
	@RequestMapping("userLogin")
	public ModelAndView login(User user,HttpSession session) {
		
		List<User> us = userService.list();
		
		for(User u : us) {
			if(u.getName().equals(user.getName()) && u.getPassword().equals(user.getPassword())) {
				session.setAttribute("user",user);
				ModelAndView mav = new ModelAndView("redirect:/listProduct");
				return mav;
			}
		}
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}
}
