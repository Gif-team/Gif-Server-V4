package com.example.gifserverv3.domain.request.entity;

import com.example.gifserverv3.domain.auth.entity.UserEntity;
import com.example.gifserverv3.domain.chat.entity.RoomEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "request")
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "request_only")
    private Boolean requestOnly;

    @JoinColumn(name = "author_id")
    @ManyToOne
    @JsonIgnoreProperties({"password", "email"})
    private UserEntity author;

    @ElementCollection
    @Column(name = "recipients_id")
    private List<Long> recipientsId;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEntity> roomEntityList;

    public void setRecipientsId(List<Long> recipientsId) {
        this.recipientsId = recipientsId;
    }

    public void modifyRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setRequestOnly(boolean requestOnly) {
        this.requestOnly = requestOnly;
    }
}
