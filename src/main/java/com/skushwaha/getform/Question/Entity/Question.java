package com.skushwaha.getform.Question.Entity;


import com.skushwaha.getform.Forms.Entity.Form;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "idx_questions_form_id", columnList = "form_id"),
                @Index(name = "idx_questions_form_order", columnList = "form_id, question_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(nullable = false, length = 500)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private QuestionType type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean required = false;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> configuration;
}