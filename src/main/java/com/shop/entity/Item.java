package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.ItemType;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                        // 상품 코드

    @Column(nullable = false, length = 200)
    private String itemNm;                  // 상품멍

    @Column(nullable = false, name = "price")
    private int price;                      // 가격

    @Column(nullable = false)
    private int stockNumber;                // 재고 수량

    @Lob
    @Column(columnDefinition = "varchar(1000)", nullable = false)
    private String itemDetail;              // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private Integer discount;

    private String dateTime;                // 날짜 포멧용

/*    private LocalDateTime regTime;          // 등록 시간
    private LocalDateTime updateTime;       // 수정 시간*/

/*    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_item",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> members;*/

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemType = itemFormDto.getItemType();
        this.discount = itemFormDto.getDiscount();
    }

    public void removeStock (int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 : " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
        if (this.stockNumber == 0) {
            itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
        // 변경감지로 변경된 내용 자동 반영
    }

    public void addStock (int stockNumber) {
        this.stockNumber += stockNumber;
        itemSellStatus = ItemSellStatus.SELL;
    }
}





















