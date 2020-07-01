package com.item_backend.exam;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.item_backend.exam.Constants.mutationRate;



/*
 * 目标试卷中，需要包含的数据是
 *  试卷总分  各类题型的名字 个数  单个类型题目的分数  知识点覆盖率
 * */


@Component
public class GeneratePaperServiceImpl implements GeneratePaperService {

    @Autowired
    GaMapper gaMapper;
    /**
     *  count:种群数量
     *  expectExam：期望的试卷
     *  questionsDB：题库
     *  kpCoverageRate:知识点所占权重
     *  difficultyRate：难度系数所占权重
     */
    public List<TempExam> cszq(int count, TempExam expectedExam, String q_subject) {
        // 种群
        List<TempExam> unitList = new ArrayList<>();
        // 各种题型信息
        List<Map<String, Object>> expectedExamQtList = expectedExam.getQtList();
        // 知识点
        Set<String> kpSet = expectedExam.getKnowPoints();
        TempExam unit;
        Random random = new Random();
        String qt;// 题目类型
        Map<String, Object> qtMap;// 期望题型信息
        int qtNum, index;


        //防止随机数相同，做数据交换使用
        Questions tempQues;


        for (int j = 0; j < count; j++) {
          //每次循环 产生一张试卷
            unit = new TempExam();// 种群个体
            unit.seteId(StringUtil.seqGenerate());
            unit.setAdapterDegree(0.0);
            List<Questions> questionsList = unit.getQuestionList();

            // 总分限制
            while (expectedExam.getTotalScore() != unit.getTotalScore()) {
                System.out.println("总分限制++++"+expectedExam.getTotalScore()+"+++++"+unit.getTotalScore());
                // 清空试题信息
                unit.setQuestionList(null);
                // 各种题型   map  就是题型

                //map里面有题目类型，该类型题目的数量
                //for循环完全走完，就是一张试卷了 ，每一次完成的是一个类型的题目，一张试卷有好多个类型的题目
                for (Map<String, Object> map : expectedExamQtList) {
                    //就是说，一个类型的题目可能要好几个，比如 选择题 要10个 qtid 就是 选择题，qtnum 就是10
                    qt = map.get("qt").toString();

                    // 题型信息
                   // qtMap = getQtById(expectedExamQtList, qtId);
                    qtNum = Integer.parseInt(map.get("qtNum").toString());
                    //-------------------------------------------------
                    // 题库中该题型题目,在题库中找到 qtid 这种类型的题目，并且这些题目中有部分知识点时符合要求的


                    Map map1=new HashMap();
                    map1.put("qt",qt);
                    map1.put("q_subject",q_subject);
                    map1.put("kpSet",new ArrayList<>(kpSet));


                    List<Questions> oneTypeQuestions = gaMapper.getOneTypeQuestions(map1);

                    //for 循环走完，就是一个类型的题目已经完成。
                    for (int i = 0; i < qtNum; i++) {
                        // 从[0,oneTypeQuestions.size()-1)中任意取一个数
                        //左含又不含
                        //如果right<=0抛出异常
                        index = random.nextInt(oneTypeQuestions.size());

                        questionsList.add(oneTypeQuestions.get(index));

                        // 将放入题库的题目排到末尾，避免选择重复题目
                        //操作体统由一个原则就是最近经常访问的数据，总会访问。下面的操作就是，
                        //即使你经常访问同一个index，访问到的数据也时不同的，因为每一次访问后就把数据换了
                        tempQues = oneTypeQuestions.get(qtNum - i - 1);
                        oneTypeQuestions.set(qtNum - i - 1, oneTypeQuestions.get(index));
                        oneTypeQuestions.set(index, tempQues);
                    }
                }
                //将题目放进unit中   试卷的所有题目
                unit.setQuestionList(questionsList);
                //各种题型信息    该套试卷相关题目类型的分数，难以程度等信息
                unit.setQtList(expectedExamQtList);
                // 设置种群个体的得分  通过上面设置的 所有题目，和与这些题型相关的信息，计算总分，和试卷的难以程度
                unit.calTotalScore();
                // 该套试卷的难以程度
                unit.calDifficultyLevel();
                // 设置种群个体知识点  每个题目中都自带了知识点，设置了questionsList 就可以统计知识点
                unit.calKnowPoints();
            }
            unitList.add(unit);
        }

        // 设置知识点覆盖率以及适应度 使用生成的试卷的知识点
        unitList = getKPCoverage(unitList, expectedExam);
        unitList = getAdaptionDegree(unitList, expectedExam, Constants.KP_COVERAGE_RATE, Constants.DIFFICULTY_RATE);
        return unitList;
    }

    /*
     * 选择算子（轮盘赌选择）
     *  适应度越强被选到的概率越大
     *  unitList:初代种群
     * count:选择的次数（下一代种群的大小）
     * 返回值：List<TempExam> 进入下一代的种群
     *
     */
    public List<TempExam> select(List<TempExam> unitList, int count) {
        System.out.println("选择");
        List<TempExam> selectedUnitList = new ArrayList<>();

        // 种群的适应度之和
        double allAdaptionDegree = 0;
        for (TempExam unit : unitList) {
            allAdaptionDegree += unit.getAdapterDegree();
        }

        Random random = new Random();
        int count1=0;
        // 次数限制
        while (selectedUnitList.size() != count) {
            //System.out.println("选择种群的数量："+selectedUnitList.size());
            count1+=1;
            //选择次数超过最大迭代次数，直接退出
            if(count1>Constants.iterations) {
                break;
            }

            double degree = 0.00;
            // 随机产生一个概率在0-allAdaptionDegree
            double randomDegree = random.nextInt(100) * 0.01 * allAdaptionDegree;

            for (int i = 0; i < unitList.size(); i++) {
                //赌轮盘算法，就是轮盘分成几个区间，区间是按照顺序 排列的，比如一个转盘分2部分，1，2。
                //第一部分 20% 第二部分80% ，开始是按照20 试探，就算是随机数也是落到80%的几率大
                //如果随机数 小于20% 就说名在第一个区间，因为每一次计算，都是在上一次的基础上迭代的。
                //所以，迭代到80% 的区间， 最终的结果是 100%，如果小于100% 说明落到了80%的区间。
                //也就是说，第一次20%，随机数不小于这个数，第二次随机数小于100%， 说明 选中的是那80% 的区间
                degree += unitList.get(i).getAdapterDegree();
                if (randomDegree <= degree) {

                    // 不重复选择
                    if (!selectedUnitList.contains(unitList.get(i))) {
                        selectedUnitList.add(unitList.get(i));
                    }
                    break;
                }
            }

        }

        return selectedUnitList;
    }

    /*
    单点交换：就是直接将数组切成两段，就换其中的一段
    多点交换： 切一刀成两段，切两刀，成三段，依此类推，交换的区间就是切点之间的那一部分

     * 交叉算子 一套试卷作为染色体，每一道题作为基因 按照题型分段，每种题型单点交叉，整套试卷就表现为分段多点交叉
     * 交叉点选择：在[0,N-2]（N为题目数量）产生随机数r，交换r位置的两个题目 交叉后很可能存在重复题目而冲突--重新选择题目（知识点相同，类型相同）
     *
     * unitList:初始种群
     * count:交叉后产生新的种群个体数量
     *  expectedExam：期望试卷
     *
     */
    public List<TempExam> cross(List<TempExam> unitList, int count, TempExam expectedExam) {
        System.out.println("交叉");
        List<TempExam> crossedUnitList = new ArrayList<>();
        Random random = new Random();
        List<Map<String, Object>> qtList = expectedExam.getQtList();// 不同题型的信息
        int count1=0;

        // 交叉后种群大小限制
        while (crossedUnitList.size() < count) {
            count1+=1;
            //交叉次数过多，无法产生足够数量的交叉种群个数
            if(count1>50000) {
                break;
            }


            // 随机选择两个染色体
            if (unitList.size()<=0){
                System.out.println("unitlist<=0  ------------>   "+unitList.size());
            }

            int indexOne = random.nextInt(unitList.size());

            int indexTwo = random.nextInt(unitList.size());
            TempExam unitOne, unitTwo;

            if (indexOne != indexTwo) {
                unitOne = unitList.get(indexOne);
                unitTwo = unitList.get(indexTwo);

                // 随机选择交叉位置---种群中题目数量都一样
                int crossPositon = random.nextInt(unitOne.getQuestionList().size() - 1);

                // 保证交叉的分数和相同
                double scoreOne = getScoreByqQtName(unitOne.getQuestionList().get(crossPositon).getQ_type(),qtList)
                        + getScoreByqQtName(unitOne.getQuestionList().get(crossPositon + 1).getQ_type(),qtList);
                double scoreTwo = getScoreByqQtName( unitTwo.getQuestionList().get(crossPositon).getQ_type(),qtList)
                        + getScoreByqQtName( unitTwo.getQuestionList().get(crossPositon + 1).getQ_type(),qtList);

                if (scoreOne == scoreTwo) {
                    // 产生两个新个体
                    TempExam unitNewOne=null;
                    List<Questions> tempList1;

                    // 交叉位置后的两道题目
                    for (int i = crossPositon; i < crossPositon + 2; i++) {
                        tempList1 = new ArrayList<>(unitOne.getQuestionList());
                        // 确保题目没有重复
                        if (!tempList1.contains(unitTwo.getQuestionList().get(i))) {
                            //实例化在此处
                            unitNewOne= new TempExam();
                            tempList1.set(i, unitTwo.getQuestionList().get(i));
                            unitNewOne.setQuestionList(tempList1);

                            // 设置新个体的其他信息
                            unitNewOne.seteId(StringUtil.seqGenerate());
                            unitNewOne.setQtList(qtList);
                            unitNewOne.calTotalScore();
                            unitNewOne.calDifficultyLevel();
                            unitNewOne.calKnowPoints();
                        }

                    }

                    // 将新个体添加到种群中
                    if (crossedUnitList.size() < count) {
                        if (unitNewOne != null) {
                            crossedUnitList.add(unitNewOne);
                        }
                    }
                }
            }
        }
        // 计算知识点覆盖率以及适应度
        crossedUnitList=getKPCoverage(crossedUnitList, expectedExam);
        crossedUnitList=getAdaptionDegree(crossedUnitList, expectedExam, Constants.KP_COVERAGE_RATE, Constants.DIFFICULTY_RATE);

        return crossedUnitList;
    }

    /*
     * 变异算子
     * unitList:初始种群
     * questionDB：题库
     * expectedExam:期望试卷
     *
     * 随机试卷中的一道题目进行变异
     *（要求：题目题型相同、题号不同、知识点最好为Ue-(Ue ∩ Un)
     * Ue:期望试卷  Un：现有试卷
     * */
    public List<TempExam> change(List<TempExam> unitList,  TempExam expectedExam) {
        System.out.println("变异");
        Random random=new Random();
        int index,index1;Questions tempQues;
        Set<String> expectedKPSet = expectedExam.getKnowPoints();
        Set<String> unitKPSet;//种群个体知识点
        Set<String> resultKPSet;
        List<Questions> expectedQuestionsList;
        List<Questions> temp;

        for(TempExam unit:unitList) {
            //突变率 15%
            if(Math.random() <= mutationRate){
                //随机变异的试题位置
                index=random.nextInt(unit.getQuestionList().size());
                //变异的试题
                tempQues=unit.getQuestionList().get(index);
                unitKPSet = unit.getKnowPoints();

                //期望  新试题  所在的知识点范围 -----> 找到期望试卷中没有被包含的部分
                resultKPSet=getResultKPList(expectedKPSet,unitKPSet);

                List resultList=new ArrayList(resultKPSet);

                //期望试题列表---如果resultKPSet为空，就不限制知识点

                Map map1=new HashMap();
                map1.put("qt",tempQues.getQ_type());
                map1.put("q_subject",tempQues.getQ_subject());
                map1.put("kpSet",resultList);



                expectedQuestionsList = gaMapper.getOneTypeQuestions(map1);

                //将新的试题替换掉原来的试题
                if(expectedQuestionsList.size()>0) {
                    index1=random.nextInt(expectedQuestionsList.size());
                    temp=unit.getQuestionList();

                    temp.set(index, expectedQuestionsList.get(index1));
                    unit.setQuestionList(temp);

                    //重新计算  试卷的几个信息
                    unit.calKnowPoints();
                    unit.calTotalScore();
                    unit.calDifficultyLevel();
                }else {
                    System.out.println(unit.geteId()+"种群个体没有可变异的待选试题");
                }
            }


        }

        // 计算知识点覆盖率以及适应度
        unitList = getKPCoverage(unitList, expectedExam);
        unitList = getAdaptionDegree(unitList, expectedExam, Constants.KP_COVERAGE_RATE, Constants.DIFFICULTY_RATE);
        return unitList;
    }

    //Ue-(Ue ∩ Un)
    // Ue:期望卷
    // Un：现有试卷     获取生成试卷的时候缺少的知识点部分
    public Set<String> getResultKPList(Set<String> expectedKPSet,Set<String> unitKPSet) {
        List<String> intersectionKPList=new ArrayList<>();//交集
        Set<String> tempSet=new HashSet(expectedKPSet);
        Set<String> resultKPSet=new HashSet<>();

        //Ue ∩ Un
        for(String up:unitKPSet) {
            if(tempSet.add(up)==false) {
                intersectionKPList.add(up);
            }
        }

        //Ue-(Ue ∩ Un)
        for(String ep:expectedKPSet) {
            if(!intersectionKPList.contains(ep)) {
                resultKPSet.add(ep);
            }
        }

        return resultKPSet;
    }


    // 根据qtId得到题型的分数
    public double getScoreByqQtName( String qt,List<Map<String, Object>> qtList) {
       //更具qid 查question 表，获取题目的分数

        for (Map<String, Object> map : qtList) {
            if (map.get("qt").equals(qt)){
                return Double.parseDouble(map.get("qtScore").toString());
            }
        }

        return 0;
    }

    // 计算知识点覆盖率
    //把生成的试卷的知识点 除以 期望生成的试卷所包含的知识点  ===覆盖率
    public List<TempExam> getKPCoverage(List<TempExam> unitList, TempExam expectedExam) {
        int kpNum, expectedKpNum;
        for (TempExam unit : unitList) {
            kpNum = unit.getKnowPoints().size();
            expectedKpNum = expectedExam.getKnowPoints().size();
            unit.setKpCoverage(kpNum * 1.00 / expectedKpNum);
        }
        return unitList;
    }

    /*
     * 计算适应度 公式：f=1-(1-M/N)*f1-|Ep-P|*f2
     * M/N为知识点覆盖率  越大越好
     * Ep：期望难度系数   |Ep-P|  越小越好
     * P:实际难度系数
     *
     * unitList:种群
     *  expectedExam：期望试卷
     * kpCoverage：知识点覆盖率所占权重  --->f1
     *  difficulty：难度系数所占权重   ------f2
     *
     * 适应度函数 人为的决定了选择的方向
     */
    public List<TempExam> getAdaptionDegree(List<TempExam> unitList, TempExam expectedExam, double kpCoverage,
                                            double difficulty) {
        double adapterDegree;
        for (TempExam unit : unitList) {
            adapterDegree = 1 - (1 - unit.getKpCoverage()) * kpCoverage
                    - Math.abs(expectedExam.getDifficultyLevel() - unit.getDifficultyLevel()) * difficulty;
            unit.setAdapterDegree(adapterDegree);
        }
        return unitList;
    }

    @Override
    public boolean isEnd(List<TempExam> unitList, double expandAdapterDegree) {
        for(TempExam unit:unitList) {
            if(unit.getAdapterDegree()>=expandAdapterDegree) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TempExam getResult(int count, TempExam expectedExam, String q_subject,double targetAdaptDegree) {

       List<TempExam> tempExamList= cszq(count,expectedExam,q_subject);
       int step=1000;

       if (isEnd(tempExamList,targetAdaptDegree)){
           for(TempExam unit:tempExamList) {
               if(unit.getAdapterDegree()>=targetAdaptDegree) {
                   return unit;
               }
           }
       }

       while (step> 0){
           step--;
          tempExamList= select(tempExamList,16);
           System.out.println("选择后的 size----->  "+tempExamList.size());

          tempExamList = cross(tempExamList,count,expectedExam);
           System.out.println("交叉后的 size----->  "+tempExamList.size());
            if (isEnd(tempExamList,targetAdaptDegree)){
                for(TempExam unit:tempExamList) {
                    if(unit.getAdapterDegree()>=targetAdaptDegree) {
                        return unit;
                    }
                }
            }


           tempExamList=change(tempExamList,expectedExam);
           System.out.println("变异后的 size----->  "+tempExamList.size());
          if (isEnd(tempExamList,targetAdaptDegree)){
              for(TempExam unit:tempExamList) {
                  if(unit.getAdapterDegree()>=targetAdaptDegree) {
                      return unit;
                  }
              }
          }
           System.out.println("--------------============-----------------");

       }

       //1000 次循环还没成功就直接放弃
       return null;
    }
}
