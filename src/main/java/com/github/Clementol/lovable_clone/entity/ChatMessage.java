package com.github.Clementol.lovable_clone.entity;

import com.github.Clementol.lovable_clone.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.simpleframework.xml.Order;

import java.time.Instant;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id", referencedColumnName = "project_id"),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    })
    ChatSession chatSession;

    @Column(columnDefinition = "text")
    String content; // NULL unless USER role

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> events; // Empty unless ASSISTANT role

    String toolCalls;

    Integer tokensUsed = 0;

    @CreationTimestamp
    Instant createdAt;
}
