package com.igeek.shop.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 
* @ClassName: Cart  
* @Description: ���ﳵ
* @date 2017��12��18�� ����2:51:31    
* Company www.igeekhome.com
*
 */
public class Cart {
	//���ﳵ�еĹ����Ϊ�˷����װmap����
	private Map<String,CartItem> list = new HashMap<String,CartItem>();
	//�ܼ�
	private double total;
	/**
	 * @return the list
	 */
	public Map<String, CartItem> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(Map<String, CartItem> list) {
		this.list = list;
	}
	/**
	 * @return the total
	 */
	public double getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(double total) {
		this.total = total;
	}
	
	
}
