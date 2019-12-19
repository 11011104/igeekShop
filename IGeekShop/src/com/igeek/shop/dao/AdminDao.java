package com.igeek.shop.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.igeek.common.utils.DataSourceUtils;
import com.igeek.shop.entity.Category;
import com.igeek.shop.entity.Order;
import com.igeek.shop.entity.Product;

public interface AdminDao {
	/**
	 * 
	* @Title: findAllCategory  
	* @Description: �����������
	* @return
	 * @throws SQLException 
	 */
	public List<Category> findAllCategory() throws SQLException;
	/**
	 * 
	* @Title: addProduct  
	* @Description: TODO(������һ�仰�����������������)  
	* @param product
	* @return
	 * @throws SQLException 
	 */
	public int addProduct(Product product) throws SQLException;
	public List<Order> findAllOrders() throws SQLException ;
	public List<Map<String, Object>> findOrderInfoByOid(String oid) throws SQLException;

}
