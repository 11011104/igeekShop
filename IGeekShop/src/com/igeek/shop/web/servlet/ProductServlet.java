package com.igeek.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.igeek.common.utils.CommonUtils;
import com.igeek.common.utils.JedisPoolUtils;
import com.igeek.common.utils.PaymentUtil;
import com.igeek.shop.entity.Cart;
import com.igeek.shop.entity.CartItem;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Order;
import com.igeek.shop.entity.OrderItem;
import com.igeek.shop.entity.PageBean;
import com.igeek.shop.entity.Product;
import com.igeek.shop.entity.User;
import com.igeek.shop.service.ProductService;

import redis.clients.jedis.Jedis;

/**
 * 
 * @ClassName: ProductServlet
 * @Description: ʵ����Ʒģ�������web���ֵĹ���
 * @date 2017��12��18�� ����11:17:24 Company www.igeekhome.com
 *
 */
public class ProductServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	// product--->service--->�������Ƿ���дservice
	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // ̎���Ñ���Ո�� String method
	 * = request.getParameter("method"); if("categoryList".equals(method)) {
	 * categoryList(request,response); }else if("index".equals(method)) {
	 * index(request,response); }else if("productInfo".equals(method)) {
	 * productInfo(request,response); }else if("productList".equals(method)) {
	 * productList(request,response); }
	 * 
	 * 
	 * }
	 * 
	 * protected void doPost(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // TODO Auto-generated
	 * method stub doGet(request, response); }
	 */

	// ÿ��������ʵ����ԭ����һServlet�Ĺ���
	// ��Ʒ����
	public void categoryList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductService service = new ProductService();

		// �ȴӻ����ȡcategoryList�������ȡ������ȥ���ݿ��л�ȡ
		Jedis jedis = JedisPoolUtils.getJedis();
		// �ӻ����л�ȡ����
		String categoryListJson = jedis.get("categoryListJson");
		if (categoryListJson == null) {
			//
			System.out.println("������û�����ݣ������ݿ��л�ȡ");
			List<Category> categoryList = service.findCategoryList();
			// ��Ҫ��categoryListת��json
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);

			// ���뻺����
			jedis.set("categoryListJson", categoryListJson);
		}

		response.setContentType("text/html;charset=utf-8");
		response.getWriter().write(categoryListJson);
	}

	// ��ҳ
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();

		// ��ȡ������Ʒ---List<Product>
		List<Product> hotProductList = service.findHotProductList();
		// ��ȡ������Ʒ---List<Product>
		List<Product> newProductList = service.findNewProductList();

		/*
		 * List<Category> categoryList = service.findCategoryList();
		 * 
		 * 
		 * request.setAttribute("categoryList", categoryList);
		 */
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);

		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	// չ����Ʒ����
	public void productInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ��ȡ��ƷID
		String pid = request.getParameter("pid");
		ProductService service = new ProductService();
		Product product = service.findProductById(pid);

		// cid
		String cid = request.getParameter("cid");

		// currentPage
		String currentPage = request.getParameter("currentPage");
		request.setAttribute("product", product);
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);

		/**
		 * ������ʷ��¼
		 */
		// 1.�Ȼ�ȡCookie
		Cookie[] cookies = request.getCookies();

		String pidsValue = pid;

		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("pids".equals(c.getName())) {
					// ��ȡpidsֵ
					String pids = c.getValue();

					// 2. 48-31-2-1 ÿ�ν����·��ʵļ�¼������ǰ��
					// �·��� 5 ----->5-48-31-2-1
					// �·��� 31 ----->31-5-48-2-1
					// 1 ------>1-5-48-31-2 ������ظ��ģ���ԭ���н���ɾ�����ٽ��ظ���ID������ǰ��

					// ��String-->���� -->���ϣ�LinkedList��
					String[] arrays = pids.split("-");
					// ������ת��List
					List<String> list = Arrays.asList(arrays);
					//
					LinkedList<String> pidsList = new LinkedList<String>(list);

					if (pidsList.contains(pid)) {
						// ����ɾ��
						pidsList.remove(pid);
					}
					// ���������ǰ��
					pidsList.addFirst(pid);
					pidsValue = "";
					// ������ת���ַ���
					for (int i = 0; i < pidsList.size() && i < 7; i++) {
						pidsValue += pidsList.get(i) + "-";
					}
					// ��ȥ���һ��-
					pidsValue = pidsValue.substring(0, pidsValue.length() - 1);
				}
			}
		}

		// ����Cookie
		Cookie cookie = new Cookie("pids", pidsValue);

		// Я���ؿͻ���
		response.addCookie(cookie);

		// ҳ����ת
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}

	// ��Ʒ�б�
	public void productList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ������Ʒ�ķ�����Ҹ÷����µ�������Ʒ(Ĭ���ҵ�һҳ����Ϣ)
		String cid = request.getParameter("cid");

		// ��ȡҳ��
		String page = request.getParameter("currentPage");

		int currentPage = 1;
		// �ж�page�Ƿ���ֵ
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		int currentCount = 12;
		//
		ProductService service = new ProductService();
		PageBean<Product> pageBean = service.findProductListByCid(cid, currentPage, currentCount);
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);

		// ��ʷ�����¼����ȡpids��Cookie
		Cookie[] cookies = request.getCookies();
		List<Product> list = new ArrayList<Product>();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("pids".equals(c.getName())) {
					String pids = c.getValue();

					// ����
					String[] arr = pids.split("-");

					for (String pid : arr) {
						Product pro = service.findProductById(pid);
						list.add(pro);
					}

				}
			}
		}
		// �����ַ��������ݲ�����Ʒ

		// ����request��
		request.setAttribute("historyList", list);
		// ҳ�����ת
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}

	// ��չ��ﳵ����
	public void clearCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		// ���
		// session.invalidate();
		session.removeAttribute("cart");

		// ��ת
		// ҳ�����ת
		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// ɾ������������

	public void delFromCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		// ��ȡ��Ʒid
		String pid = request.getParameter("pid");

		// ��ȡ���ﳵ����
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart != null) {
			Map<String, CartItem> list = cart.getList();

			// ���¼����ܽ�� = ԭ�����ܽ��-�Ƴ������С��
			cart.setTotal(cart.getTotal() - list.get(pid).getSubTotal());

			// ��ɾ���Ĺ�����ӹ��ﳵ���Ƴ�
			list.remove(pid);
		}

		session.setAttribute("cart", cart);

		// ҳ�����ת
		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	public void addToCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		ProductService service = new ProductService();

		// ��ȡ��Ʒid
		String pid = request.getParameter("pid");

		// ����ID��ȡ��Ʒ����
		Product product = service.findProductById(pid);

		// ��ȡ��Ʒ����
		String num = request.getParameter("buyNum");// ��Ŵ���
		int buyNum = Integer.parseInt(num);

		// ����С��
		double subTotal = buyNum * product.getShop_price();

		// ����һ��CartItem����

		double newSubTotal = subTotal;// ����С��

		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			// ��һ�μӹ�
			cart = new Cart();
		}

		// ��ȡ���ﳵ�еĹ������
		Map<String, CartItem> list = cart.getList();

		// �ж�list���Ƿ��Ѿ����ڵ�ǰ��Ʒ��pid
		if (list.containsKey(pid)) {
			// �ظ���Ʒ����Ҫ�ϲ�
			int oldNum = list.get(pid).getBuyNum();
			// �ϲ�֮�������buyNum
			buyNum += oldNum;

			// �ϲ�֮���С��
			newSubTotal = buyNum * product.getShop_price();

		}

		CartItem item = new CartItem(product, buyNum, newSubTotal);

		// cart�϶�����
		// ��CartItem�浽cart���ﳵ��
		cart.getList().put(product.getPid(), item);

		// �����ܼƣ����õ�cart��
		double total = cart.getTotal() + subTotal;
		cart.setTotal(total);

		// �����ﳵ����session
		session.setAttribute("cart", cart);

		// ҳ����ת��cart.jsp
		// request.getRequestDispatcher("/cart.jsp").forward(request, response);
		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	/**
	 * 
	 * @Title: submitOrder
	 * @Description: �ύ����
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void submitOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		// ��֤�û��Ƿ��¼
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// δ��¼״̬
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		// ����Order����
		Order order = new Order();

		// ���Է�װ
		/**
		 * 
		 * 1.private String oid; 2.private Date ordertime;//�µ�ʱ�� 3.private
		 * double total;//�ܽ�� 4.private int state;//����״̬ ����״̬1--�Ѹ��� 0--δ����
		 * 5.private String address;//�ջ���ַ 6.private String name;//�ջ�������
		 * 7.private String telephone;//��ϵ��ʽ 8.private User user;//˭�µĵ�
		 * 
		 * 9.private List<OrderItem> orderItems = new ArrayList<OrderItem>();
		 */
		// �������
		String oid = CommonUtils.getUUID();
		order.setOid(oid);
		// �µ�ʱ��
		order.setOrdertime(new Date());
		// �ܽ��

		// ��ȡ���ﳵ
		Cart cart = (Cart) session.getAttribute("cart");
		// ������ϸ�߼���ʡ�ԣ�
		order.setTotal(cart.getTotal());

		// ���ù����û�
		order.setUser(user);

		// ��List<OrderItem> orderitems;
		// �ӹ��ﳵ�л�ȡCartItemת��OrderItem
		Map<String, CartItem> list = cart.getList();

		for (Entry<String, CartItem> entry : list.entrySet()) {
			// �õ�ÿһ��������
			CartItem cartItem = entry.getValue();

			// ׼��OrderItem����CartItemת������
			OrderItem orderitem = new OrderItem();
			// private String itemid;
			orderitem.setItemid(CommonUtils.getUUID());
			// private int count;//���������
			orderitem.setCount(cartItem.getBuyNum());
			// private double subtotal;//С��
			orderitem.setSubtotal(cartItem.getSubTotal());
			// private Product product;//�����й������Ʒ����
			orderitem.setProduct(cartItem.getProduct());
			// private Order order;
			orderitem.setOrder(order);

			// ����������뵽order�еĶ�����ļ�����
			order.getOrderItems().add(orderitem);

		}

		// ����service�� ����
		ProductService service = new ProductService();
		service.submitOrders(order);

		// order�浽session
		session.setAttribute("order", order);

		// ��ת
		response.sendRedirect(request.getContextPath() + "/order_info.jsp");

	}

	// ȷ�϶�������
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductService service = new ProductService();
		HttpSession session = request.getSession();
		// ����Ϣ
		Order order = (Order) session.getAttribute("order");
		try {
			BeanUtils.populate(order, request.getParameterMap());

			// 1.�����û����ջ���Ϣ
			service.updateOrderInfo(order);

			// 2.���֧������
			// ��ȡ�û�ѡ�������

			// ��� ֧�������������
			// �������
			String orderid = order.getOid();
			// ����
			// String money = order.getTotal()+"";
			String money = "0.01";
			// ����
			String pd_FrpId = request.getParameter("pd_FrpId");

			// ����֧����˾��Ҫ��Щ����
			String p0_Cmd = "Buy";
			String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
			String p2_Order = orderid;
			String p3_Amt = money;
			String p4_Cur = "CNY";
			String p5_Pid = "";
			String p6_Pcat = "";
			String p7_Pdesc = "";
			// ֧���ɹ��ص���ַ ---- ������֧����˾����ʡ��û�����
			// ������֧�����Է�����ַ
			String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
			String p9_SAF = "";
			String pa_MP = "";
			String pr_NeedResponse = "1";
			// ����hmac ��Ҫ��Կ
			String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
			String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
					p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);

			// ���ױ�֧���ĵ�ַ
			String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId + "&p0_Cmd=" + p0_Cmd
					+ "&p1_MerId=" + p1_MerId + "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt + "&p4_Cur=" + p4_Cur
					+ "&p5_Pid=" + p5_Pid + "&p6_Pcat=" + p6_Pcat + "&p7_Pdesc=" + p7_Pdesc + "&p8_Url=" + p8_Url
					+ "&p9_SAF=" + p9_SAF + "&pa_MP=" + pa_MP + "&pr_NeedResponse=" + pr_NeedResponse + "&hmac=" + hmac;

			// �ض��򵽵�����֧��ƽ̨
			response.sendRedirect(url);

			// 3.����order��״̬

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: myOrders
	 * @Description: �ҵĶ���
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void myOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		// ��֤�û��Ƿ��¼
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// δ��¼״̬
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		
		//�û��϶���¼��
		ProductService service = new ProductService();
		//���Ҹ��û��µ����ж�������
		List<Order> orderList = service.findAllOrders(user.getUid());
		if(orderList!=null)
		{
			//��������
			for(Order order : orderList)
			{
				//��ȡÿһ��Order����,����oid���Ҷ�Ӧ��List<OrderItem>������ļ���
				List<Map<String, Object>> listMap = service.findAllOrderItems(order.getOid());
				
				//��װ�����µ����ж���������
				for(Map<String, Object> map : listMap)
				{
					
					try {
						OrderItem orderItem = new OrderItem();
						//orderItem.setCount(Integer.parseInt(map.get("count").toString()));//�ֶ�����ת��
						//ӳ��orderitem����
						BeanUtils.populate(orderItem, map);
						
						Product product = new Product();
						///ӳ��product����
						BeanUtils.populate(product, map);//pimage,pname,shop_price
						
						//��������֮��Ĺ�ϵ
						orderItem.setProduct(product);
						
						//orderItem����order��List<OrderItem>������ļ���
						order.getOrderItems().add(orderItem);
						
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//count,subtotal
					
				}

			}
		}

		
		//������
		request.setAttribute("orderList", orderList);
		//��ת��ǰ̨ҳ��
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
		
		
	}
}
