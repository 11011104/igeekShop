package com.igeek.shop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.igeek.common.utils.DataSourceUtils;
import com.igeek.shop.dao.ProductDao;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Order;
import com.igeek.shop.entity.OrderItem;
import com.igeek.shop.entity.PageBean;
import com.igeek.shop.entity.Product;

public class ProductService {
	private ProductDao dao = new ProductDao();
	/**
	 * 
	* @Title: findHotProductList  
	* @Description: ����������Ʒ  
	* @return
	 */
	public List<Product> findHotProductList() {
		List<Product> hotProducts = null;
		try {
			hotProducts = dao.findHotProducts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hotProducts;
	}
	/**
	 * 
	* @Title: findNewProductList  
	* @Description:��ѯ������Ʒ
	* @return
	 */
	public List<Product> findNewProductList() {
		List<Product> newProducts = null;
		try {
			newProducts = dao.findNewProducts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newProducts;
	}
	/**
	 * 
	* @Title: findCategoryList  
	* @Description: ����������Ʒ���  
	* @return
	 */
	public List<Category> findCategoryList() {
		 List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategorys();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categoryList;
	}
	/**
	 * 
	* @Title: findProductListByCid  
	* @Description:���������ҷ�ҳPageBean����  
	* @param cid
	* @return
	 */
	public PageBean<Product> findProductListByCid(String cid,int currentPage,int currentCount) {
		PageBean<Product> pageBean = new PageBean<>();
		
		//��ǰҳ��
		
		pageBean.setCurrentPage(currentPage);
		//��ǰ��ʾ����������
		
		pageBean.setCurrentCount(currentCount);
		
		//�ܹ�������,��ѯ����
		int totalCount = 0;
		try {
			totalCount = dao.getCount(cid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pageBean.setTotalCount(totalCount);
		
		//��ҳ��  
		int totalPage = (int)Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		
		//��ҳ��ʾ������,Ĭ�ϲ��ҵ�һҳ��start   currentCount
		int start = (currentPage-1)*currentCount;
		List<Product> list = null;
		try {
			list = dao.findProductByCid(cid,start,currentCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���ü���
		pageBean.setList(list);
		
		return pageBean;
	}
	public Product findProductById(String pid) {
		
		Product product = null;
		try {
			product = dao.findProductById(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return product;
		
	}
	/**
	 * 
	* @Title: submitOrders  
	* @Description: �ύ������ҵ��
	* @param order
	 */
	public void submitOrders(Order order) {
		// TODO Auto-generated method stub
		
		try {
			//1.��������
			DataSourceUtils.startTransaction();

			//2.����dao��Ĳ���order�ķ���
			dao.addOrders(order);
			
			//3.����dao��Ĳ���orderitem�ķ���
			dao.addOrderItems(order);
			
		} catch (SQLException e) {
			//����ع�
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				//�ύ�ͷ���Դ
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 
	* @Title: updateOrderInfo  
	* @Description:���¶������ջ�����Ϣ  
	* @param order
	 */
	public void updateOrderInfo(Order order) {
		
		try {
			//����
			dao.updateOrderInfo(order);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void updateOrderState(String r6_Order) {
		// TODO Auto-generated method stub
		try {
			dao.updateOrderState(r6_Order);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	* @Title: findAllOrders  
	* @Description: ��ѯָ���û������ж����������ѯ��  
	* @param uid
	* @return
	 */
	public List<Order> findAllOrders(String uid) {
		List<Order> orderList = null;
		try {
			orderList = dao.findAllOrders(uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderList;
	}
	/**
	 * 
	* @Title: findAllOrderItems  
	* @Description: ���ݶ�����Ų�ѯ������ļ���
	* @param oid
	* @return
	 */
	public List<Map<String, Object>> findAllOrderItems(String oid) {
		List<Map<String, Object>> itemList = null;
		try {
			itemList = dao.findAllOrderItems(oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemList;
	}

}
