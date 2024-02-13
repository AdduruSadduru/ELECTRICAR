package com.shop.dto;

import com.shop.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
/*
   직렬화 : 자바 시스템에서 사용 할 수 있도록 바이트 스트림 형태로 연속적인 데이터로
   포멧하는 변환기술
   (역직렬화도 있음)
   직렬화 : 자바 Object, Data -> Byte Stream으로 변환
   역직렬화 : 바이트 스트림을 -> 자바 Object, Data로 변환

   직렬화 하는놈 상속
   마킹만 해서 이름가지고 자바에서 확인
*/
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
