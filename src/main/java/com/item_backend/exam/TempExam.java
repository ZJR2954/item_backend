package com.item_backend.exam;

import java.util.*;

public class TempExam {

    //生成临时时卷的id
    private Long eId;
    //试卷总分
    private int totalScore;

    private int qtNum;

    public int getQtNum() {
        return qtNum;
    }

    public void setQtNum(int qtNum) {
        this.qtNum = qtNum;
    }

    //试卷的难度系数
    private double difficultyLevel;

    //试卷中所有的知识点id----知识点用set存储--
    private Set<String> knowPoints;

    //各种题型的题目数量
    /*
    * 每个map 里面包含 qt ---汤姆题目类型
    *               qtNum ----该类型题目的个数
    *               qtScore  这类题目 每个多少分
    * */
    private List<Map<String, Object>> qtList;

    //题目
    private List<Questions> questionList;

    //适应度
    private double adapterDegree;

    //知识点覆盖率
    private double kpCoverage;

    public TempExam() {
        this.questionList=new ArrayList<Questions>();
    }




    public Long geteId() {
        return eId;
    }
    public void seteId(Long eId) {
        this.eId = eId;
    }
    public int getTotalScore() {
        return totalScore;
    }
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    //设置现有试卷总分
    public void calTotalScore() {
        String qt;
        int sum=0;
        for(Questions questions:questionList) {
            qt=questions.getQ_type();
            for(Map<String, Object> map:qtList) {
                if(qt.equals(map.get("qt").toString())) {
                    sum+=Integer.parseInt(map.get("qtScore").toString());
                }
            }
        }
        this.totalScore=sum;
    }

    public double getDifficultyLevel() {
        return difficultyLevel;
    }
    public void setDifficultyLevel(double difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    //设置  --试卷--  的难度系数(所有    题目分数*难度系数/总分)
    public void calDifficultyLevel() {
        double difficult=0.0;
        String q_type;
        for(Questions questions:questionList) {
            q_type =questions.getQ_type();
            //qtlist 里面有这个类型题目的具体信息 人位规定 某种类型的题目是多少分
            for(Map<String, Object> map : qtList) {
                if(q_type.equals(map.get("qt").toString())) {
                    difficult+=questions.getDiffculty()*(Double.parseDouble(map.get("qtScore").toString()));
                }
            }
        }
        this.difficultyLevel = difficult/totalScore;
    }

    public Set<String> getKnowPoints() {
        return knowPoints;
    }

    public void setKnowPoints(Set<String> knowPoints) {
        this.knowPoints = knowPoints;
    }

    //设置试卷的知识点
    public void calKnowPoints() {
        Set<String> knowPoints=new HashSet<>();
        for(Questions questions:questionList) {
            knowPoints.add(questions.getKnowledge());
        }
        this.knowPoints=knowPoints;
    }

    public List<Map<String, Object>> getQtList() {
        return qtList;
    }

    public void setQtList(List<Map<String, Object>> qtList) {
        this.qtList = qtList;
    }

    public List<Questions> getQuestionList() {
        return questionList;
    }

    public double getAdapterDegree() {
        return adapterDegree;
    }

    public double getKpCoverage() {
        return kpCoverage;
    }

    public void setQuestionList(List<Questions> questionList) {
        this.questionList = questionList;
    }

    public void setAdapterDegree(double adapterDegree) {
        this.adapterDegree = adapterDegree;
    }

    public void setKpCoverage(double kpCoverage) {
        this.kpCoverage = kpCoverage;
    }

    @Override
    public String toString() {
        return "TempExam{" +
                "eId=" + eId +
                ", totalScore=" + totalScore +
                ", qtNum=" + qtNum +
                ", difficultyLevel=" + difficultyLevel +
                ", knowPoints=" + knowPoints +
                ", qtList=" + qtList +
                ", questionList=" + questionList +
                ", adapterDegree=" + adapterDegree +
                ", kpCoverage=" + kpCoverage +
                '}';
    }
}
