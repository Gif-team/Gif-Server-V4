package com.example.gifserverv3.domain.chatmsg.entity.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRequest {
    @NotEmpty
    @Length(max = 300)
    private String message;

}
