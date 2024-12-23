package com.example.gifserverv3.domain.chatroom.controller.request;

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
public class ChatRoomUpdateRequest {
    @NotEmpty
    @Length(min = 2, max = 20)
    private String title;
}
