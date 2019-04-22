package com.tc.reptile.util;

import java.util.*;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 21:48 2019/4/22
 */
public class KeyWordUtil {
    public static String TFIDF (String title,String content, int topK){

        FilterRecognition filterRecognition = new FilterRecognition();
        filterRecognition.insertStopWords(stopWords);
        filterRecognition.insertStopWord("事儿", "有没有", "前有", "后有", "更多");
        filterRecognition.insertStopNatures("d", "p", "m", "r", "w", "a", "j", "l","null","num");

        List<Term> terms = NlpAnalysis.parse(content).recognition(filterRecognition).getTerms();
        //词的总数
        int totalWords= terms.size();
        Map<String, Integer> wordsCount = new HashMap<String, Integer>();
        //根据词的长度加权
        int maxWordLen = 0;

        for(Term term:terms){
            Integer count = wordsCount.get(term.getName());
            count = count == null ? 0 : count;
            wordsCount.put(term.getName(), count+1);
            if(maxWordLen<term.getName().length()){
                maxWordLen = term.getName().length();
            }
        }

        //计算tf
        Map<String, Double> tf = new HashMap<String, Double>();
        for(String word:wordsCount.keySet()){
            tf.put(word, (double)wordsCount.get(word)/(totalWords+1));
        }

        //保留词的长度
        Set<Integer> perWordLen = new HashSet<Integer>();
        //计算每个词的词长权重
        Map<String, Double> lenWeight = new HashMap<String, Double>();
        for( String key:tf.keySet()){
            lenWeight.put(key, (double)key.length()/maxWordLen);
            perWordLen.add(key.length());
        }

        //标题中出现的关键词
        List<Term> titleTerms = NlpAnalysis.parse(title).recognition(filterRecognition).getTerms();
        Map<String, String> titleWords = new HashMap<String, String>();
        for(Term term:titleTerms){
            titleWords.put(term.getName(), term.getNatureStr());
        }
        //计算idf
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int len:perWordLen){
            int sum = 0;
            for(String w:wordsCount.keySet()){
                if(w.length()==len){
                    sum += wordsCount.get(w);
                }
            }
            map.put(len, sum);
        }
        Map<String, Double> idf = new HashMap<String, Double>();
        for(String w:wordsCount.keySet()){
            Integer integer = wordsCount.get(w);
            int len = w.length();
            Integer totalSim = map.get(len);
            idf.put(w, Math.log(((double)totalSim/integer)+1));
        }
        //计算每个词的在文章中的权重

        Map<String, Double> wordWeight = new HashMap<String, Double>();

        for(Term term:terms){
            String word = term.getName();
            String nature = term.getNatureStr();
            if(word.length()<2){
                continue;
            }
            if(wordWeight.get(word)!=null){
                continue;
            }
            Double aDouble = tf.get(word);
            Double aDouble1 = idf.get(word);
            double weight = 1.0;
            if(titleWords.keySet().contains(word)){
                weight += 3.0;
            }
            weight += (double)word.length()/maxWordLen;
            switch (nature){
                case "en":
                    weight += 3.0;
                case "nr":
                    weight += 6.0;
                case "nrf":
                    weight += 6.0;
                case "nw" :
                    weight += 3.0;
                case "nt":
                    weight += 6.0;
                case "nz":
                    weight += 3.0;
                case "kw":
                    weight += 3.0;
                case "ns":
                    weight += 3.0;
                default:
                    weight += 1.0;
            }

            wordWeight.put(word,aDouble*aDouble1*weight);
        }

        Map<String, Double> stringDoubleMap = MapUtil.sortByValue(wordWeight);

        List<String> topKSet = new ArrayList<String>();

        int i = 0;
        for(String word:stringDoubleMap.keySet()){
            if(i >= topK){
                break;
            }
            topKSet.add(word+" ``
                    +stringDoubleMap.get(word));
            i++;
        }
        return StringUtils.join(topKSet, "\t");
    }
}
