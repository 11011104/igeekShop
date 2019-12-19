package com.igeek.shop.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.igeek.common.utils.BeanFactory;
import com.igeek.common.utils.CommonUtils;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Product;
import com.igeek.shop.service.AdminService;

/**
 * 
 * @ClassName: AdminAddProduct
 * @Description: Ϊ���ܹ���ȡ��ͨ���ݺ��ļ��ϴ����Լ���װ��Servlet
 * @date 2017��12��25�� ����1:38:00 Company www.igeekhome.com
 *
 */
public class AdminAddProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @Title: doGet
	 * @Description: �����Ʒ
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			// ��ȡ����Ԫ��ֵ����װ��Productʵ�壬���뵽�ײ�����
			// 1.��������
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2.����������
			ServletFileUpload upload = new ServletFileUpload(factory);
			Product product = new Product();
			
			Map<String, Object> map = new HashMap<String,Object>();
			
			// 3.����request������
			List<FileItem> parseRequest = upload.parseRequest(request);		
			//��������
			for(FileItem item : parseRequest)
			{
				//�ж��ǲ�����ͨ��
				if(item.isFormField())
				{
					//����ͨ��,��ȡ����name����ֵ����ȡ����Ӧ������ֵ
					String fieldName = item.getFieldName();
					String fieldValue = item.getString("utf-8");
					
					//����map������
					map.put(fieldName, fieldValue);
					
				}else{
					//�ļ��ϴ�
					//��ȡ�ļ������ļ����ݣ�д�������
					String fileName = item.getName();
					
					//�ļ��ϴ���·��
					String path =this.getServletContext().getRealPath("upload");
					
					InputStream in = item.getInputStream();
					OutputStream out = new FileOutputStream(path+"/" + fileName);
					
					IOUtils.copy(in, out);
					in.close();
					out.close();
					
					//�ļ���·������map������
					map.put("pimage","upload"+File.separator + fileName);
					
				}
			}
			
			//��map�е�����ӳ�䵽productʵ����
			BeanUtils.populate(product, map);
			
			//	private String pid;
			product.setPid(CommonUtils.getUUID());
			//private String pimage;
			
			//private Date pdate;
			product.setPdate(new Date());
			//	private int pflag;//�ϼ�/�¼�
			product.setPflag(0);
			//private Category category;
			Category c = new Category();
			c.setCid(map.get("cid").toString());
			
			product.setCategory(c);
		
			//��ӵ��ײ����ݿ�
			AdminService service = (AdminService)BeanFactory.getBean("adminService");
			
			boolean isSuccess = service.addProduct(product);
			//
			
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		// �����ƷͼƬ�Ĵ��������
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
