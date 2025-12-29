package com.ecom.service;

import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

public interface OrderService {

	public void saveOrder(Integer userId,OrderRequest orderRequest);
	
	public List<ProductOrder> getOrdersByUserId(Integer userId);
	
	public Boolean updateOrderStatus(Integer id,String status);
	
}
