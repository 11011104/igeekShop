package com.igeek.shop.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.igeek.shop.dao.AdminDao;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Order;
import com.igeek.shop.entity.Product;

public interface AdminService {

	/**
	 * 
	* @Title: findAllCategory  
	* @Description: �����������
	* @return
	 */
	public List<Category> findAllCategory() ;
	/**
	 * 
	* @Title: addProduct  
	* @Description: �����Ʒ
	* @param product
	* @return
	 */
	public boolean addProduct(Product product);
	
	public List<Order> findAllOrders() ;
	public List<Map<String, Object>> findOrderInfoByOid(String oid);
}
