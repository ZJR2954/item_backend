package com.item_backend.exam;


import java.util.List;


public interface GeneratePaperService {
    //初始种群
     List<TempExam> cszq(int count, TempExam expectedExam, String q_subject);
    //选择算子
     List<TempExam> select(List<TempExam> unitList, int count);
    //交叉算子
     List<TempExam> cross(List<TempExam> unitList, int count, TempExam expectedExam);
    //变异算子
     List<TempExam> change(List<TempExam> unitList, TempExam expectedExam);
    //判断结束
     boolean isEnd(List<TempExam> unitList, double expandAdapterDegree);


     TempExam getResult(int count, TempExam expectedExam, String q_subject, double targetAdaptDegree);

}
