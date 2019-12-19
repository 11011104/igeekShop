package com.igeek.shop.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.igeek.common.utils.BeanFactory;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Order;
import com.igeek.shop.service.AdminService;

/**
 * Servlet implementation class AdminServlet
 */
public class AdminServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	* @Title: findOrderInfoByOid  
	* @Description: ���ݶ�����Ų��Ҷ������� 
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException
	 */
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//����
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��ȡ�������
		String oid=request.getParameter("oid");
		
		//����ӿڱ��
		//AdminService service = new AdminServiceImpl();
		//bean---����---��Ӧһ������  --д�������ļ���
		AdminService service = (AdminService)BeanFactory.getBean("adminService");
		
		List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
		
		//ajax����ͨ��responseд��ȥ
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		//[
		//{"shop_price":2299.0,"count":2,"pname":"�곞��acer��ATC705-N50 ̨ʽ����","pimage":"products/1/c_0031.jpg","subtotal":4598.0},
		//{"shop_price":1299.0,"count":4,"pname":"С�� 4c ��׼��","pimage":"products/1/c_0001.jpg","subtotal":5196.0},
		//{"shop_price":2298.0,"count":1,"pname":"vivo X5Pro","pimage":"products/1/c_0014.jpg","subtotal":2298.0}
		//]

		//System.out.println(json);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(json);
		
	}
   /**
    *     
   * @Title: findAllOrders  
   * @Description: �������ж���������
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
    */
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminService service = (AdminService)BeanFactory.getBean("adminService");
		List<Order> orderList = service.findAllOrders();
		
		//����request
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
		
		
	}
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��ȡ�������
		AdminService service = (AdminService)BeanFactory.getBean("adminService");
		List<Category> categoryList = service.findAllCategory();
		
		//�첽����
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
		//�����ļ���ʽ
		response.setContentType("text/html;charset=utf-8");
		
		PrintWriter out = response.getWriter();
		out.write(json);
		out.flush();
		out.close();
	}

}
