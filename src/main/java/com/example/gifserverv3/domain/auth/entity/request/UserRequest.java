package com.example.gifserverv3.domain.auth.entity.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @Email // email 형식
    @NotEmpty // valid 유효성(String 은 Empty, 다른 타입은 NotNull)
    @Length(min = 4, max = 20) // 길이 제한 (입력값이 포함하지 않는 경우 오류)
    private String email;

    @NotEmpty
    @Length(min = 4, max = 20)
    private String password;
}
