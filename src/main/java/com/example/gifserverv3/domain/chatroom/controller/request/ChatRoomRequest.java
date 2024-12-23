package com.example.gifserverv3.domain.chatroom.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequest {

    @NotEmpty
    @Length(min = 2, max = 20)
    private String title;

    @NotNull
    @Max(8)
    private Integer userCountMax;
    @Length(min = 4, max = 20)
    private String password;
}
