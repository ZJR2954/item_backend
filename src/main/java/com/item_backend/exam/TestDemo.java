package com.item_backend.exam;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.item_backend.exam.Constants.EXPAND_ADATPER;

public class TestDemo {

    //例子就是这么个例子，再test 里面可以跑的，这个里面不能跑
    @Autowired
    GaMapper gaMapper;
    @Autowired
    GeneratePaperService generatePaperService;


    public static void main(String[] args) {
        TempExam expExam=new TempExam();
        List<Map<String,Object>> list=new ArrayList<>();
        Map <String,Object> map=new HashMap<>();
        map.put("qt","选择题");
        map.put("qtNum",20);
        map.put("qtScore",5);
        list.add(map);

        expExam.setQtList(list);

        Set Kpset=new HashSet();
        Kpset.add("链表");
        Kpset.add("图");
        Kpset.add("树");
        Kpset.add("队列");
        Kpset.add("栈");

        expExam.setKnowPoints(Kpset);
        expExam.setTotalScore(100);
        expExam.setDifficultyLevel(0.75);

        Map map1 =new HashMap();
        map1.put("qt","选择题");
        map1.put("q_subject","数据结构");
        map1.put("kpSet",Kpset);


        System.out.println(generatePaperService.getResult(20, expExam, "数据结构", EXPAND_ADATPER));
    }
}
