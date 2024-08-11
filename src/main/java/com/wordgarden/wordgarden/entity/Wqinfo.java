package com.wordgarden.wordgarden.entity;

import com.wordgarden.wordgarden.dto.WqResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "wqinfo_tb")
public class Wqinfo {
    @Id
    @Column(name = "wq_id") //단어 퀴즈 아이디
    private String wqId;

    @Column(name = "wq_question", columnDefinition = "LONGTEXT")    // 단어퀴즈 문제
    private String wqQuestion;

    @Column(name = "wq_answer", columnDefinition = "LONGTEXT")      // 단어퀴즈 정답
    private String wqAnswer;

    @Column(name = "wq_title", length = 255)    // 단어퀴즈 제목
    private String wqTitle;

    @ManyToOne
    @JoinColumn(name = "word_id")   // 문제 단어 아이디
    private Word word;

    @OneToMany(mappedBy = "wqInfo")
    private List<Wqresult> wqResults;

    @OneToMany(mappedBy = "wqInfo")
    private List<Wqwrong> wqWrongs;

    public WqResponseDto toDto() {
        WqResponseDto dto = new WqResponseDto();
        dto.setWqId(this.wqId);
        dto.setWqQuestion(this.wqQuestion);
        dto.setWqTitle(this.wqTitle);
        dto.setWordId(this.word.getWordId());
        dto.setWord(this.word.getWord());
        return dto;
    }

}
