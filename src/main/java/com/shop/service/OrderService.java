package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;

    public Long order (OrderDto orderDto, String email) {
        // item 결과 받기 (select)
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        // member 결과 받기 (select)
        Member member = memberRepository.findByEmail(email);

        // List<OrderItem> 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);       // orderItemList에 orderItem을 추가

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);    // 주문서 테이블에 저장

        return order.getId();       // 주문서에 있는 아이디 리턴
    }

    // Entity Order, OrderItem -> OrderHistDto, OrderItemDto로 만드는 과정
    // resutl = pageImpl로 만들어서 나중에 뽑아씀
    @Transactional
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        // 전체 주문 리스트
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 전체 주문 갯수
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto
        for (Order order : orders) {        // null 될때까지 orders에서 하나씩 빼서 order에 삽입
            OrderHistDto orderHistDto = new OrderHistDto(order);    // Order -> OrderHistDto
            List<OrderItem> orderItems = order.getOrderItems();     // Order -> List<OrderItem> 추출
            for (OrderItem orderItem : orderItems) {        // 아마 장바구니 이용해서 여러개 살때 대비해서 리스트로 가져오는듯
                // OrderItem.id 이용해서 이미지 객체 추출(repImgYn == "Y")
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn
                        (orderItem.getItem().getId(), "Y");
                // OrderItemDto 객체를 생성할때 orderItem 객체와 imgUrl을 매개변수로 받음
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                // OrderHistDto <- List<OrderItemDto> <- OrderItemDto 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            // OrderHistDtos (List<OrderHistDto>)에 OrderHistDto 추가
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        // 현재 로그인된 멤버
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // id에 맞는 주문서에 저장되어있는 멤버
        Member savedMember = order.getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        return true;
    }
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }
    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}








































