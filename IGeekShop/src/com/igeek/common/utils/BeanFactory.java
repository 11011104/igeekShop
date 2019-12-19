package com.igeek.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class BeanFactory {
	/**
	 * 
	 * @Title: getBean
	 * @Description: ����ID��ȡbean����
	 * @param id
	 * @return
	 */
	public static Object getBean(String id) {
		try {
			// ����xml�ļ�������dom4jjar��
			//����һ��������
			SAXReader reader = new SAXReader();
			String url = BeanFactory.class.getClassLoader().getResource("beans.xml").getPath();
			//��ȡ�ĵ�����
			Document doc = reader.read(url);
			//��ȡһ���ڵ�
			Element element = (Element)doc.selectSingleNode("//bean[@id='"+id+"']");
			
			//��ȡ�����������
			String classValue = element.attributeValue("class");
			//ͨ�������ȡ�����
			Class clazz = Class.forName(classValue);
			
			Object instance = clazz.newInstance();
			
			return instance;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
