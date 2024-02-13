package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {     // 주문 상품 정보
    private Long itemId;
    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;
    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemId = orderItem.getItem().getId();
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
}
