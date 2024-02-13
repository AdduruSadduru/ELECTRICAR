package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
                orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

/*    private LocalDateTime regTime;
    private LocalDateTime updateTime;*/

    // 주문서 주문 아이템 리스트에 주문 아이템 추가
    // 주문 아이템에 주문서 추가
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    // 주문서 생성
    // 현재 로그인된 멤버 주문서에 추가
    // 주문 아이템 리스트를 반복문을 통해서 주문서에 추가
    // 상태는 주문으로 세팅
    // 주문 시간은 현재 시간으로 세팅
    // 주문서 리턴
    public static Order createOrder (Member member, List<OrderItem> orderItemList) {
        Order order = new Order();          // 주문서 객체 생성
        order.setMember(member);            // order에 멤버 셋
        // Order에 있는 List<OrderItem> 매개변수로 넘어온 OrderItemList데이터 추가
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);        // Order 상태를 ORDER로 변경
        order.setOrderDate(LocalDateTime.now());        // Order에 시간을 현재시간으로 세팅
        return order;       // 주문서 리턴
    }
    // 주문서에 있는 주문 아이템 리스트를 반복
    // 주문 아이템마다 가지고 있는 총 가격을 totalPrice에 추가
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        // 주문서 상태 캔슬로 전환
        this.orderStatus = OrderStatus.CANCEL;
        // order에 있는 orderItemList 하나씩 뽑아서 cancel 실행
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}
