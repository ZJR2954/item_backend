package com.item_backend.exam;

import com.sun.istack.internal.NotNull;

import java.util.Date;

public class Questions {

    @NotNull
    private String q_type;

    @NotNull
    private String knowledge;

    //题目所属专业
    @NotNull
    private String q_subject;

    public String getQ_subject() {
        return q_subject;
    }

    public void setQ_subject(String q_subject) {
        this.q_subject = q_subject;
    }

    private String answer;

    private Date upload_time;

    //题目内容
    private String q_content;


    //出题人id
    private Long u_id;

    @NotNull
    private Double difficulty;



    public String getQ_type() {
        return q_type;
    }

    public void setQ_type(String q_type) {
        this.q_type = q_type;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
    }

    public Long getU_id() {
        return u_id;
    }

    public void setU_id(Long u_id) {
        this.u_id = u_id;
    }

    public Double getDiffculty() {
        return difficulty;
    }

    public void setDiffculty(Double diffculty) {
        this.difficulty = diffculty;
    }

    public String getQ_content() {
        return q_content;
    }

    public void setQ_content(String q_content) {
        this.q_content = q_content;
    }

    @Override
    public String toString() {
        return "Questions{" +
                "q_type='" + q_type + '\'' +
                ", knowledge='" + knowledge + '\'' +
                ", q_subject='" + q_subject + '\'' +
                ", answer='" + answer + '\'' +
                ", upload_time=" + upload_time +
                ", q_content='" + q_content + '\'' +
                ", u_id=" + u_id +
                ", diffculty=" + difficulty +
                '}';
    }
}
