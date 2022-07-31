package com.ggjcbgg.guguji.dto;

import com.ggjcbgg.guguji.entity.OrderDetail;
import com.ggjcbgg.guguji.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private Integer sumNum;//菜品总数

    private List<OrderDetail> orderDetails;
	
}
