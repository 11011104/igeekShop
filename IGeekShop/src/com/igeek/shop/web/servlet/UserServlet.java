package com.igeek.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.igeek.common.utils.CommonUtils;
import com.igeek.common.utils.MailUtils;
import com.igeek.shop.entity.User;
import com.igeek.shop.service.UserService;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // ̎���Ñ���Ո�� String method
	 * = request.getParameter("method"); if ("active".equals(method)) {
	 * active(request, response); } else if ("checkUsername".equals(method)) {
	 * checkUsername(request, response); } else if ("regist".equals(method)) {
	 * regist(request, response); } }
	 * 
	 *//**
		 * @see HttpServlet#doPost(HttpServletRequest request,
		 *      HttpServletResponse response)
		 *//*
		 * protected void doPost(HttpServletRequest request, HttpServletResponse
		 * response) throws ServletException, IOException { // TODO
		 * Auto-generated method stub doGet(request, response); }
		 */
	public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1.��ȡ������
		String activeCode = request.getParameter("activeCode");
		// 2.�����û�
		UserService service = new UserService();
		boolean isSuccess = service.active(activeCode);
		if (isSuccess) {
			// ��ת����¼ҳ��
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		} else {
			// ����ʧ��
			response.sendRedirect(request.getContextPath() + "/error.jsp");
		}
	}

	public void checkUsername(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ��ȡ����
		String username = request.getParameter("username");

		// service��֤�Ƿ����
		UserService service = new UserService();
		boolean isExist = service.checkUsername(username);
		String json = "{\"isExist\":" + isExist + "}";
		response.getWriter().write(json);

	}

	public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 0.���������������
		request.setCharacterEncoding("utf-8");

		User user = new User();
		// 1.��ȡ���е�����
		try {

			// �ֶ���Stringת��Date����
			ConvertUtils.register(new Converter() {
				/**
				 * 
				 * @Title: convert
				 * @Description: ʵ������ת����
				 * @param clazz��Ŀ�����������
				 * @param value:Ҫת��������
				 * @return
				 * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class,
				 *      java.lang.Object)
				 */
				@Override
				public Object convert(Class clazz, Object value) {
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
					Date desc = null;
					try {
						desc = sf.parse((String) value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return desc;
				}
			}, Date.class);

			//// 2.��װ��Userʵ�����
			BeanUtils.populate(user, request.getParameterMap());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// uid,state(Ĭ�Ͼ���0),code�������룩
		// UUID���ɲ��ظ����ַ���
		user.setUid(CommonUtils.getUUID());
		String activeCode = CommonUtils.getUUID();
		user.setCode(activeCode);
		// 3.����service��ע��ķ�ʽ

		UserService service = new UserService();
		boolean isSuccess = service.regist(user);
		if (isSuccess) {
			String emailMsg = "��ϲ����ע��ɹ�����������ļ���������˻����<br>"
					+ "<a href='http://localhost:8080/IGeekShop/user?method=active&activeCode=" + activeCode
					+ "'>http://localhost:8080/IGeekShop/user?method=active&activeCode=" + activeCode + "</a>";
			// �����ʼ��������û�
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ��ת���ɹ�ҳ��
			response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
		} else {
			// ʧ��Ҷ��
			// ��ת���ɹ�ҳ��
			response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
		}

	}

	// �û���¼
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		// ���������û���������
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// ��������м���
		// password = MD5Utils.md5(password);

		// ���û��������봫�ݸ�service��
		UserService service = new UserService();
		User user = null;
		try {
			user = service.login(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// �ж��û��Ƿ��¼�ɹ� user�Ƿ���null
		if (user != null) {
			// ��¼�ɹ�
			// ***************�ж��û��Ƿ�ѡ���Զ���¼*****************
			String autoLogin = request.getParameter("autoLogin");
			if ("autoLogin".equals(autoLogin)) {
				// Ҫ�Զ���¼
				// �����洢�û�����cookie
				Cookie cookie_username = new Cookie("cookie_username", user.getUsername());
				cookie_username.setMaxAge(10 * 60);
				// �����洢�����cookie
				Cookie cookie_password = new Cookie("cookie_password", user.getPassword());
				cookie_password.setMaxAge(10 * 60);

				response.addCookie(cookie_username);
				response.addCookie(cookie_password);

			}

			// ***************************************************
			// ��user����浽session��
			session.setAttribute("user", user);

			// �ض�����ҳ
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		} else {
			request.setAttribute("loginError", "�û������������");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}
	/**
	 * 
	* @Title: logout  
	* @Description: �û��˳�
	* @param request
	* @param response
	* @throws ServletException
	* @throws IOException
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		//��session���Ƴ��û�
		session.removeAttribute("user");
		
		//��cookie��ҳɾ���ɾ�
		// Ҫ�Զ���¼
		// �����洢�û�����cookie
		Cookie cookie_username = new Cookie("cookie_username", "");
		cookie_username.setMaxAge(0);
		// �����洢�����cookie
		Cookie cookie_password = new Cookie("cookie_password", "");
		cookie_password.setMaxAge(0);

		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		
		
		response.sendRedirect(request.getContextPath()+"/login.jsp");
		
	}

}
