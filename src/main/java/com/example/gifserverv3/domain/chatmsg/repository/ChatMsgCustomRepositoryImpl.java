package com.example.gifserverv3.domain.chatmsg.repository;

import com.example.gifserverv3.domain.chatmsg.entity.ChatMsgEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMsgCustomRepositoryImpl implements ChatMsgCustomRepository{

    private final EntityManager entityManager;

    @Override
    public List<ChatMsgEntity> findChatRoomIdByChatMsg(Long chatMsg, Long lastId) {
        String first = "select c from ChatMsgEntity c where c.chatRoom.id =: chatMsg order by c.id asc";
        String paging = "select c from ChatMsgEntity c where c.chatRoom.id =: chatMsg and c.id > :lastId order by c.id asc";

        TypedQuery<ChatMsgEntity> query = null;

        if (lastId == null) {
            query = entityManager
                    .createQuery(first, ChatMsgEntity.class)
                    .setParameter("chatMsg", chatMsg);
        } else {
            query = entityManager
                    .createQuery(paging, ChatMsgEntity.class)
                    .setParameter("chatMsg", chatMsg)
                    .setParameter("lastId", lastId);
        }
        return query
                .setMaxResults(10)
                .getResultList();
    }
}
