package com.tc.reptile.util;

import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.jsoup.helper.StringUtil;

import java.util.*;

/**
 * @Author: Chensr
 * @Description:
 * @Date: Create in 21:48 2019/4/22
 */
public class KeyWordUtil {
    private static final Map<String, Double> POS_SCORE = new HashMap<String, Double>();

    static {

        POS_SCORE.put("null", 0.0);

        POS_SCORE.put("w", 0.0);

        POS_SCORE.put("en", 0.0);

        POS_SCORE.put("m", 0.0);

        POS_SCORE.put("num", 0.0);

        POS_SCORE.put("nr", 3.0);

        POS_SCORE.put("nrf", 3.0);

        POS_SCORE.put("nw", 3.0);

        POS_SCORE.put("nt", 3.0);

        POS_SCORE.put("l", 0.2);

        POS_SCORE.put("a", 0.2);

        POS_SCORE.put("nz", 3.0);

        POS_SCORE.put("v", 0.2);

        POS_SCORE.put("kw", 6.0);//关键词词性

    }

    //    预设关键词个数为5。
    private int nKeyword = 5;

    //    判断title与content是否为空值，如果不是空值，title后面加制表符加content，组成新的字符串，传回新的参数(title +"\t" +content, title.length());
    public List<Keyword> computeArticleTfidf(String title, String content) {

        if (StringUtil.isBlank(title)) {

            title = "";

        }

        if (StringUtil.isBlank(content)) {

            content = "";

        }

        return computeArticleTfidf(title + "\t" + content, title.length());

    }

    //    Weight的计算：
//    调用computeArticleTfidf方法，传入“title+content”组成的content与titlelength。利用NlpAnalysis类中方法进行分词。遍历分词结果中的每一个词，调用getWeight方法计算weight，移除字符串两侧的空白字符或其他预定义字符后，判断词的长度，如果小于2，返回值为0，(trim()函数移除字符串两侧的空白字符或其他预定义字符)。定义posScore变量。判断分词的词性是否为预设POS_SCORE的的词性，是的话取预设的POS_SCOREe值，如果不是，取posScore=1，预设POS_SCORE为0的返回值为0。判断分出来的词是否在title位置，如果是，的返回值为5*posScore，如果不是score的返回值为(length -term.getOffe()) *posScore / (double)length。
    private List<Keyword> computeArticleTfidf(String content, int titleLength) {

        Map<String, Keyword> tm = new HashMap<String, Keyword>();
        List<Term> parse = NlpAnalysis.parse(content).getTerms();

        //FIXME:这个依赖于用户自定义词典的词性,所以得需要另一个方法..
//        parse = FilterModifWord.updateNature(parse);

        for (Term term : parse) {

            double weight = getWeight(term, content.length(), titleLength);

            if (weight == 0)

                continue;

            Keyword keyword = tm.get(term.getName());

            if (keyword == null) {

                keyword = new Keyword(term.getName(), term.natrue().allFrequency, weight);

                tm.put(term.getName(), keyword);

            } else {

                keyword.updateWeight(1);

            }

        }

        TreeSet<Keyword> treeSet = new TreeSet<Keyword>(tm.values());
        ArrayList<Keyword> arrayList = new ArrayList<Keyword>(treeSet);
        if (treeSet.size() <= nKeyword) {
            return arrayList;
        } else {
            return arrayList.subList(0, nKeyword);

        }


    }

    private double getWeight(Term term, int length, int titleLength) {
        if (term.getName().trim().length() < 2) {
            return 0;
        }
        String pos = term.natrue().natureStr;
        Double posScore = POS_SCORE.get(pos);

        if (posScore == null) {
            posScore = 1.0;
        } else if (posScore == 0) {
            return 0;
        }

        if (titleLength > term.getOffe()) {
            return 5 * posScore;
        }
        return (length - term.getOffe()) * posScore / (double) length;
    }

    public static void main(String[] args) {
        String title = "【红白机N合1】小时候打不通《外星战将》？你可能是盗版软件的受害者";
        String content = "<div> \n" +
                " <p> <span>大家周末好，一个多月不见，社长又回来和大家聊红白机了。其实“N合1”系列做到现在，有意思的红白机“小游戏”已经被讲得差不多了，我们之后可能会多讲些国内玩家比较熟悉的、容量更大的“高K”游戏。</span> </p> \n" +
                " <p> 比如今天的《外星战将》。 </p> \n" +
                " <p> 很多FC游戏都给国内玩家的童年留下了美好的回忆——但这个用主角是一只绿兔子的横版射击游戏肯定不在其中。如果要老玩家们列一个“最难FC游戏榜”，榜单上估计不会少了《外星战将》。 </p> \n" +
                " <p> 作为科乐美在红白机年代末期的作品，《外星战将》画面优秀、BGM动听。这只绿兔子不仅做的精细，来头也相当大，要追溯到漫画巨头DC的一次“失误”，我们在视频中再仔细讲。 </p> \n" +
                " <p> 虽然《外星战将》画面素质上不愧于大厂出产，但玩起来的就会发现，难度完全不像画面那么“轻松休闲”。&nbsp; </p> \n" +
                " <p> 玩家不仅要面对满屏幕的子弹和敌人，主角的血条还跟假的一样——看上去是长长一条，实际跟魂斗罗一样脆，只要挨上一下就要从头再来。 </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/da1bbf85d556f8a6a0d6afff9c0cd3bf.png_mw680water\" width=\"462\" height=\"418\"> </p> \n" +
                " <p> 所以在当年，能打通《外星战将》的玩家，属于高手中的高高手，会受到小朋友的瞩目和膜拜。到现在，《外星战将》还一直是国外游戏速通的热门对象。 </p> \n" +
                " <p> 但社长重温这个游戏的时候，却发现有些地方不太对劲——网上这些速通记录都要强调一点：挑战者都是在“HARD”模式下通关的。可我们当年也没见到难度选项啊？ </p> \n" +
                " <p> 上网一查才发现，原来要在标题画面输入指令后，游戏才会能进入“HARD模式”。在这个模式里，玩家只有一滴血，被碰一下就会……等等，这不就是国内玩家玩到的最普通的《外星战将》吗？ </p> \n" +
                " <p> <strong>没错，很多人的童年，都玩了一个假游戏。</strong> </p> \n" +
                " <p> 《外星战将》的原版并不是什么特别难的游戏，之所以会发生这一切，都是因为玩家被科乐美摆了一道。 </p> \n" +
                " <p> 在红白机生命末期，游戏厂商就和卡带盗版商已经展开了激烈的斗智斗勇，常见的反盗版措施无非是这样的： </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/2db1431699b247bfcf8bf1596554ca40.png_mw680water\" width=\"915\" height=\"453\"> </p> \n" +
                " <p> 但有些“心理阴暗”的厂商，会给卡带做更多的手脚：一旦程序在游戏启动时检测出卡带是盗版，就会偷偷修改游戏内容。 </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/425f57c0e3e45f4e985318956b0e8c62.png_mw680water\" width=\"889\" height=\"452\"> </p> \n" +
                " <p> 除了“一碰就死”的《外星战将》，科乐美旗下的什么《忍者神龟3》、《兔宝宝》……都有类似的反盗版措施，“折磨”玩家的方式五花八门——我们就在视频里仔细讲讲，当年做一个盗版软件的受害者，究竟会受多少苦。 </p> \n" +
                " <p style=\"text-align:center;\"> <strong><span class=\"r-triangle\">视频预览</span></strong> </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/500984c51a1ef7392be8c3d0b7ad817c.png_mw680water\" width=\"962\" height=\"525\"> </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/d6fb923db863a88c152922cc30c73dc2.png_mw680water\" width=\"958\" height=\"514\"> </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/4996841501e067c652ddc05c346f2831.png_mw680water\" width=\"964\" height=\"542\"> </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/0e90a0a632331fbfdf2067ea1f411b33.png_mw680water\" width=\"963\" height=\"537\"> </p> \n" +
                " <p class=\"picbox\"> <img data-original=\"http://alioss.yystv.cn/doc/4921/1d5319904bcf55afb4c11002e99627b7.png_mw680water\" width=\"962\" height=\"542\"> </p> \n" +
                " <br> \n" +
                "</div> \n" +
                "<div class=\"show-more-block\"> \n" +
                " <div class=\"doc-show-more rel\">\n" +
                "  展开全文\n" +
                " </div> \n" +
                "</div>";

        KeyWordUtil util = new KeyWordUtil();
        List<Keyword> keywords = util.computeArticleTfidf(title, content);
        keywords.forEach(keyword -> System.out.println(keyword));
    }
/*
    Score的计算：

    如果是第一次出现的词语，score = idf * weight。

    如果是第二次以上出现的词语，score +=weight *idf。*/
}
