package com.igeek.shop.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BaseServlet
 */
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		try {
			// ��ȡ�û�����ķ�����
			String method = request.getParameter("method");
			// this---����������� product/user
			Class clazz = this.getClass();
			// ʹ�÷�����ƻ�ȡ��������
			Method methodObj = clazz.getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
			// ʹ�÷���C�Ƶ��÷���
			methodObj.invoke(this, request,response);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		// ���ø÷���

	}

}
