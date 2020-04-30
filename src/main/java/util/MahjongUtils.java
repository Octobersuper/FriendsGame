package util;


import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static util.Mahjong_Util.mahjong_Util;

/**
 * 检测胡牌牌型
 */
public class MahjongUtils {

    /**
     * 构造器
     */
    public MahjongUtils() {
    }

    /**
     * 返回胡牌应加颗数
     *
     * @return
     */
    public void getBrandKe(RoomBean roomBean, UserBean userBean, Integer brand, List<Integer> tingList, int huType) {
        if (huType == 0) {
            userBean.getBrands().add(brand);
        }
        if (lqd(userBean, brand)) {
            userBean.setPower(32);
            userBean.setHu_type("豪华七小对");
            System.err.println("豪华七小对");
        } else if (aqd(userBean, brand)) {// 暗七对
            userBean.setPower(16);
            System.err.println("七小对");
            userBean.setHu_type("七小对");
        }else  if (pph(userBean,huType)) {
            userBean.setPower(8);
            System.err.println("飘胡");
            userBean.setHu_type("飘胡");
        }else if (kz(userBean, brand, tingList) || bz(userBean, brand, tingList) || dd(userBean,brand,tingList)) {
            userBean.setPower(4);
            System.err.println("夹胡");
            userBean.setHu_type("夹胡");
        }else {
            userBean.setPower(2);
            System.err.println("屁胡");
            userBean.setHu_type("屁胡");
        }
    }

    private boolean sb(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        int hcount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 32) {
                hcount++;
            }
        }
        if (hcount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 碰碰胡  点炮5分、自摸10分；（胡牌时的牌型是AAA+AAA+AAA+AAA+AA的形式）
     * @param userBean
     * @param type
     * @return
     */
    private boolean pph(UserBean userBean, int type) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);

        if(userBean.getHide_brands().size()==0){
            List<Integer> th = new ArrayList<>();
            List<Integer> two = new ArrayList<>();
            for (Integer card:userBrands) {
                int t3 = 0;
                for (Integer c:userBrands) {
                    if(card.equals(c)){
                        t3++;
                    }
                }
                if (t3==3){
                    th.add(card);
                }else if(t3==2){
                    two.add(card);
                }else{
                    return false;
                }
            }

            //去重
            List<Integer> newList = new ArrayList<>();
            for (Integer ban : th) {
                if (!newList.contains(ban)) {
                    newList.add(ban);
                }
            }
            //去重
            List<Integer> two2 = new ArrayList<>();
            for (Integer ban : two) {
                if (!two2.contains(ban)) {
                    two2.add(ban);
                }
            }
            if(newList.size()==4&&two2.size()==1){
                return true;
            }
        }
        return false;
    }

    private boolean sf(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        int hcount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 32) {
                hcount++;
            }
        }
        if (hcount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 手牌红中
     *
     * @param userBean
     * @return
     */
    private boolean sh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        int hcount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                hcount++;
            }
        }
        if (hcount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 金钩钓 对对碰那种牌型，但是得碰完或者杠完手上只剩一张牌单钓这种就是金钩钓
     *
     * @param roomBean
     * @param userBean
     * @param brand
     * @return
     */
    private boolean jgd(RoomBean roomBean, UserBean userBean, Integer brand) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        int brand_value = getBrand_Value(brand);
        int size = userBrands.size();
        if (size == 2 && brand_value == userBrands.get(0)) {
            return true;
        }
        return false;
    }

    /**
     * 4发+4红=火箭（7颗）
     *
     * @param userBean
     * @return
     */
    private boolean fourfh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int hongcount = 0;
        int facount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                hongcount++;
            }
            if (userBrand == 32) {
                facount++;
            }
        }
        if (hongcount == 4 && facount == 4) {
            return true;
        }
        return false;
    }

    /**
     * 4白+4发=火箭（7颗）
     *
     * @param userBean
     * @return
     */
    private boolean fourbf(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int baicount = 0;
        int facount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 33) {
                baicount++;
            }
            if (userBrand == 32) {
                facount++;
            }
        }
        if (baicount == 4 && facount == 4) {
            return true;
        }
        return false;
    }

    /**
     * 4红+4白=火箭（7颗）
     *
     * @param userBean
     * @return
     */
    private void fourhb(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int hongcount = 0;
        int baicount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                hongcount++;
            }
            if (userBrand == 33) {
                baicount++;
            }
        }
        int angang = 0;
        int minggang = 0;
        if (userBean.getHide_brands().contains(31)) {
            angang++;
        }
        if (userBean.getShow_brands().contains(33)) {
            minggang++;
        }
        if (hongcount == 4 && baicount == 4) {
            userBean.setPower(7 - angang - minggang);
            userBean.getRecordMsgList().add("火箭+" + String.valueOf(7 - angang - minggang));
        }
    }

    /**
     * 3发+3红=1
     *
     * @param userBean
     * @return
     */
    private boolean threefh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int hongcount = 0;
        int facount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                hongcount++;
            }
            if (userBrand == 32) {
                facount++;
            }
        }
        if (hongcount == 3 && facount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 3白+3发=1
     *
     * @param userBean
     * @return
     */
    private boolean threebf(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int baicount = 0;
        int facount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 33) {
                baicount++;
            }
            if (userBrand == 32) {
                facount++;
            }
        }
        if (baicount == 3 && facount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 3红+3白=1
     *
     * @param userBean
     * @return
     */
    private boolean threehb(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int hongcount = 0;
        int baicount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                hongcount++;
            }
            if (userBrand == 33) {
                baicount++;
            }
        }
        if (hongcount == 3 && baicount == 3) {
            return true;
        }
        return false;
    }

    /**
     * 四归一  当前手中有三张一样的牌，胡牌时还是胡这张牌，胡牌时算是明杠，要加一颗。
     *
     * @param userBean
     * @return
     */
    private void sgy(UserBean userBean, int brand, int huType) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int count = 0;
        //存放四张牌集合
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < userBrands.size(); i++) {
            int conut = 0;
            for (int j = 0; j < userBrands.size(); j++) {
                //System.out.println(userBrands.get(i)+"----"+userBrands.get(j));
                if (userBrands.get(i) == userBrands.get(j)) {
                    conut++;
                }
                if (conut >= 4) {
                    list.add(userBrands.get(i));
                    break;
                }
            }
        }


        //用来存明杠暗杠的牌
        List<Integer> gang = new ArrayList<>();
        List<Integer> a = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : a) {
            gang.add(integer);
        }
        List<Integer> b = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : b) {
            gang.add(integer);
        }


        List list1 = removeDuplicate(list);
        if (gang.size() == 0 && list1.size() != 0) {
            userBean.setPower(list1.size());
            System.err.println("四归一+" + list1.size());
            userBean.getRecordMsgList().add("四归一+" + list1.size());
            return;
        }
        int num = 0;
        for (int i = 0; i < list1.size(); i++) {
            if (!gang.contains(list1.get(i))) {
                num++;
            }
        }
        if (num != 0) {
            userBean.setPower(num);
            System.err.println("四归一+" + num);
            userBean.getRecordMsgList().add("四归一+" + num);
        }
    }


    /**
     * 十八罗汉  指胡牌时手中的四副搭子全部都是杠牌（明杠、暗杠均可），因胡牌时手中共有18张牌，达到和牌最大张数，故名十八罗汉。
     *
     * @param userBean
     * @return
     */
    private boolean sblh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        if (userBrands.size() == 18) {
            return true;
        }
        return false;
    }

    /**
     * 小三元汇  胡牌后中发白都有，但是其中一个字只有2张，其他都是3张或3张以上，记作小三元
     *
     * @param userBean
     * @return
     */
    private boolean xsyh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int zhongcount = 0;
        int facount = 0;
        int baicount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                zhongcount++;
            }
            if (userBrand == 32) {
                facount++;
            }
            if (userBrand == 33) {
                baicount++;
            }
        }
        if (zhongcount == 2 && facount >= 2 && baicount >= 2) {
            return true;
        } else if (facount == 2 && zhongcount >= 2 && baicount >= 2) {
            return true;
        } else if (baicount == 2 && zhongcount >= 2 && facount >= 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 大三元汇  当胡牌后有中发白至少各三张或更多（比如杠），则算作三元汇。
     *
     * @param userBean
     * @return
     */
    private boolean dsyh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        Collections.sort(userBrands);
        int zhongcount = 0;
        int facount = 0;
        int baicount = 0;
        for (Integer userBrand : userBrands) {
            if (userBrand == 31) {
                zhongcount++;
            }
            if (userBrand == 32) {
                facount++;
            }
            if (userBrand == 33) {
                baicount++;
            }
        }
        if (zhongcount >= 3 && facount >= 3 && baicount >= 3) {
            return true;
        }
        return false;
    }

    /**
     * 超大火箭  是指胡牌时搭子中有四副四张相同序数跨度1的四连杠。6666777788889999
     *
     * @param userBean
     * @return
     */
    private void cdhj(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 4) {
                list.add(temp);
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),3);//判断牌值是否为9万桶条
            if(is){
                // 特大火箭
                tdhj(userBean);
                return;
            }
        }*/
        int count = 0;
        int angang = 0;
        int minggang = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 3);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            list1.add(temp1 + 3);
            if (list.containsAll(list1)) {
                count++;
            }
            if (userBean.getHide_brands().contains(list.get(i))) {
                angang++;
            }
            if (userBean.getShow_brands().contains(list.get(i))) {
                minggang++;
            }
        }
        if (count >= 1) {
            userBean.setPower(21 - angang - minggang);
            userBean.getRecordMsgList().add("特大火箭+" + String.valueOf(21 - angang - minggang));
        } else {
            // 特大火箭
            tdhj(userBean);
        }
    }

    /**
     * 特大火箭 是指胡牌时搭子中有三副四张相同序数跨度1的三连杠。666677778888
     *
     * @param userBean
     * @return
     */
    private void tdhj(UserBean userBean) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 4) {
                list.add(temp);
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),2);//判断牌值是否为9万桶条
            if(is){
                // 火箭
                hj(userBean);
                return;
            }
        }*/
        int count = 0;
        int angang = 0;
        int minggang = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 2);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            if (list.containsAll(list1)) {
                count++;

                if (userBean.getHide_brands().contains(temp1)) {
                    angang++;
                }
                if (userBean.getHide_brands().contains(temp1 + 1)) {
                    angang++;
                }
                if (userBean.getHide_brands().contains(temp1 + 2)) {
                    angang++;
                }
                if (userBean.getShow_brands().contains(temp1)) {
                    minggang++;
                }
                if (userBean.getShow_brands().contains(temp1 + 1)) {
                    minggang++;
                }
                if (userBean.getShow_brands().contains(temp1 + 2)) {
                    minggang++;
                }
            }
        }
        if (count >= 1) {
            userBean.setPower(14 - angang - minggang);
            userBean.getRecordMsgList().add("特大火箭+" + String.valueOf(14 - angang - minggang));
        } else {
            // 火箭
            hj(userBean);
        }
    }

    /**
     * 火箭 是指胡牌时搭子中有两副四张相同序数跨度1的两连杠 如66667777
     *
     * @param userBean
     * @return
     */
    private void hj(UserBean userBean) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 4) {
                list.add(temp);
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),1);//判断牌值是否为9万桶条
            if(is){
                return;
            }
        }*/
        int count = 0;
        int angang = 0;
        int minggang = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 1);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            Collections.sort(list1);
            if (list.containsAll(list1)) {
                count++;
                if (userBean.getHide_brands().contains(temp1)) {
                    angang++;
                }
                if (userBean.getHide_brands().contains(temp1 + 1)) {
                    angang++;
                }
                if (userBean.getShow_brands().contains(temp1)) {
                    minggang++;
                }
                if (userBean.getShow_brands().contains(temp1 + 1)) {
                    minggang++;
                }
            }
        }
        if (count >= 1) {
            List<String> recordMsgList = userBean.getRecordMsgList();
            userBean.setPower((7 * count) - angang - minggang);
            recordMsgList.add("火箭+" + String.valueOf((7 * count) - angang - minggang));
            if (userBean.getRecordMsgList().contains("小飞机+1")) {
                removeList(userBean, "小飞机+1", -1);
            } else if (userBean.getRecordMsgList().contains("小飞机+2")) {
                removeList(userBean, "小飞机+2", -2);
                if (count == 1) {
                    recordMsgList.add("小飞机+1");
                    userBean.setPower(1);
                }
            }
        }
    }

    /**
     * 特大飞机 是指胡牌时有两个大飞机牌型，如333444555666，两个大飞机的7颗相加。
     *
     * @param userBean
     * @return
     */
    private void tdfj(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        // userBrand.add(getBrand_Value(brand));
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        int sizhang = 0;
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount >= 3) {
                list.add(temp);
            }
            if (tempcount == 4) {
                sizhang++;
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),3);//判断牌值是否为9万桶条
            if(is){
                dfj(userBean,brand);
                return;
            }
        }*/
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 3);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            list1.add(temp1 + 3);
            if (list.containsAll(list1)) {
                count++;
            }
        }
        if (count >= 1) {
            userBean.setPower(14);
            System.err.println("特大飞机+" + 14);
            userBean.getRecordMsgList().add("特大飞机+" + 14);
            removeList(userBean, "小板子+1", -1);
           /* if(sizhang!=0){
                userBean.setPower(sizhang);
                userBean.getRecordMsgList().add("特大飞机(杠)+"+sizhang);
                System.err.println("特大飞机(杠)+"+sizhang);
            }*/
        } else {
            // 大飞机
            dfj(userBean, brand);
        }
    }

    /**
     * 大飞机 是指胡牌时搭子中有三副三张相同序数跨度1的搭子，如222333444，
     *
     * @param userBean
     * @return
     */
    private void dfj(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        int sizhang = 0;
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount >= 3) {
                list.add(temp);
            }
            if (tempcount == 4) {
                sizhang++;
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),2);//判断牌值是否为9万桶条
            if(is){
                // 小飞机
                xfj(userBean,brand);       return;
            }
        }*/
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 2);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            if (list.containsAll(list1)) {
                count++;
            }
        }
        if (count >= 1) {
            userBean.setPower(7);
            System.err.println("大飞机+7");
            userBean.getRecordMsgList().add("大飞机+7");
            removeList(userBean, "小板子+1", -1);
           /* if(sizhang!=0){
                userBean.setPower(sizhang);
                userBean.getRecordMsgList().add("大飞机(杠)+"+sizhang);
                System.err.println("大飞机(杠)+"+sizhang);
            }*/
        } else {
            // 小飞机
            xfj(userBean, brand);
        }
    }

    /**
     * 小飞机 是指胡牌时搭子中有两副三张相同序数跨度1的搭子，如111222或555666。
     *
     * @param userBean
     * @return
     */
    private void xfj(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        List<Integer> rem = new ArrayList<>();
        int sizhang = 0;
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 3) {
                list.add(temp);
                if (!rem.contains(temp)) {
                    rem.add(temp);
                    rem.add(temp);
                    rem.add(temp);
                }
            } else if (tempcount == 4) {
                if (!rem.contains(temp)) {
                    rem.add(temp);
                    rem.add(temp);
                    rem.add(temp);
                }
                sizhang++;
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),1);//判断牌值是否为9万桶条
            if(is){
                return;
            }
        }else if(list.size()<2){
            return;
        }*/
        List<Integer> allc = new ArrayList<>();
        int count = 0;
        List<Integer> list2 = removeDuplicate(list);
        for (int i = 0; i < list2.size(); i++) {
            temp1 = list2.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 1);//判断牌值是否为9万桶条
            if (is) {
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            Collections.sort(list1);
            if (list2.containsAll(list1)) {
                allc.addAll(list1);
                count++;
            }
        }
        if (rem.size() != 0 && allc.size() != 0) {
            for (int j = rem.size() - 1; j >= 0; j--) {
                if (!allc.contains(rem.get(j))) {
                    rem.remove(j);
                }
            }
        }
        if (count >= 1) {
            userBean.setPower(1 * count);
            userBean.getRecordMsgList().add("小飞机+" + 1 * count);
            removeList(userBean, "小板子+1", -1);
            //List<Integer> integers = GetIntersection2(rem, userBrand);
            //int state = Mahjong_Util.mahjong_Util.ssssssssss(integers);
            //if(state==0) {
            ///*if(sizhang!=0){
            //    userBean.setPower(sizhang);
            //    userBean.getRecordMsgList().add("小飞机(杠)+"+sizhang);
            //    System.err.println("小飞机(杠)+"+sizhang);
            //}*/
            //}
        }
    }

    private boolean panduanValue(int temp1, int i) {
        if (temp1 <= 8) {
            if (temp1 + i > 8) {
                return true;
            }
        } else if (temp1 <= 17) {
            if (temp1 + i > 17) {
                return true;
            }
        } else if (temp1 <= 26) {
            if (temp1 + i > 26) {
                return true;
            }
        }
        return false;
    }

    /**
     * 大板子  aabbccdd
     *
     * @param userBean
     * @return
     */
    private void dbz(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        List<Integer> rem = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 2) {
                list.add(temp);
                rem.add(temp);
                rem.add(temp);
            }
        }
        Collections.sort(list);
        /*if(list.size()!=0){
            boolean is = panduanValue(list.get(0),3);//判断牌值是否为9万桶条
            if(is){
                // 小板子
                xbz(userBean,brand);
                return;
            }
        }*/
        List<Integer> allc = new ArrayList<>();//存储最终组成板子的牌
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            List<Integer> list1 = new ArrayList<>();
            boolean is = panduanValue(temp1, 3);//判断牌值是否为9万桶条
            if (is) {
               /* for (int j = 0; j < rem.size(); j++) {
                    if(temp1==rem.get(j)){
                        rem.remove(j);
                    }
                }*/
                continue;
            }
            list1.add(temp1);
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            list1.add(temp1 + 3);
            if (list.containsAll(list1)) {
                allc.addAll(list1);
                count++;
            }
        }
        if (rem.size() != 0 && allc.size() != 0) {
            for (int j = rem.size() - 1; j >= 0; j--) {
                if (!allc.contains(rem.get(j))) {
                    rem.remove(j);
                }
            }
        }
        if (count >= 1) {
            List<Integer> integers = GetIntersection2(rem, userBrand);
            int state = Mahjong_Util.mahjong_Util.ssssssssss(integers);
            if (state == 0) {
                userBean.setPower(7 * count);
                List<String> brandList = userBean.getRecordMsgList();
                brandList.add("大板子+" + 7 * count);
                System.out.println("大板子+" + 7 * count);
            }

            List<Integer> list1 = new ArrayList<>();
            for (int i = 0; i < allc.size(); i++) {
                for (int j = 0; j < 2; j++) {
                    list1.add(allc.get(i));
                }
            }
            userBrand.removeAll(list1);
            xbz(userBrand, userBean);
            /*userBean.setPower(7 * count);
            List<String> brandList = userBean.getRecordMsgList();
            brandList.add("大板子+" + 7*count);
            System.out.println("大板子+" +7*count);*/
        } else {
            // 小板子
            xbz(userBean, brand);
        }
    }

    public List<Integer> GetIntersection2(List<Integer> list, List<Integer> list2) {

        List<Integer> list1 = new ArrayList<>();
        list1.addAll(list);
        //2:111223334455
        //1:334455
        //得到:111223

        //3:11122-1-13-1-1-1-1
        List<Integer> list3 = new ArrayList<>();

        list3.addAll(list2);
        for (int i = 0; i < list2.size(); i++) {
            for (int j = list1.size() - 1; j >= 0; j--) {
                if (list2.get(i) == list1.get(j)) {
                    list3.set(i, -1);
                    list1.remove(j);
                    break;
                }
            }
        }

        for (int i = list3.size() - 1; i >= 0; i--) {
            if (list3.get(i) == -1) {
                list3.remove(i);
            }
        }
        return list3;
    }

    //小板子
    public boolean miniban(List<Integer> list, int num) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                if ((list.get(i) + 1 + num) == list.get(j) && !panduanValue(j, 0))//并且i不能等于9万通条
                {
                    if (num < 2) {
                        num++;
                    }
                    if (num == 2) {
                        return true;
                    }
                } else {
                    num = 0;
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 小板子  大小板子同时存在的情况
     *
     * @param userBean
     * @return
     */
    private void xbz(List<Integer> userBrand, UserBean userBean) {
        //userBrand.add(getBrand_Value(brand));
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        List<Integer> rem = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 2) {
                list.add(temp);
                rem.add(temp);
                rem.add(temp);
            }
        }
        Collections.sort(list);

        List<Integer> allc = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            if (temp1 == 31 || temp1 == 32 || temp1 == 33) {
                continue;
            }
            List<Integer> list1 = new ArrayList<>();
            list1.add(temp1);
            if (list.size() != 0) {
                boolean is = panduanValue(temp1, 2);//判断牌值是否为9万桶条
                if (is) {
                    /*for (int j = 0; j < rem.size(); j++) {
                        if(temp1==rem.get(j)){
                            rem.remove(j);
                        }
                    }*/
                    continue;
                }
            }
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            Collections.sort(list1);
            if (list.containsAll(list1)) {
                allc.addAll(list1);
                count++;
            }
        }/*
        if(allc.contains(31)||allc.contains(32)||allc.contains(33)){
            return;
        }*/
        if (rem.size() != 0 && allc.size() != 0) {
            for (int j = rem.size() - 1; j >= 0; j--) {
                if (!allc.contains(rem.get(j))) {
                    rem.remove(j);
                }
            }
        }
        if (count >= 1) {
            List<Integer> integers = GetIntersection2(rem, userBrand);
            int state = Mahjong_Util.mahjong_Util.ssssssssss(integers);
            if (state == 0) {
                if (!userBean.getRecordMsgList().contains("大飞机+7") && !userBean.getRecordMsgList().contains("大飞机+14") && !userBean.getRecordMsgList().contains("大飞机+21")) {
                    userBean.setPower(count);
                    System.err.println("小板子+" + count);
                    userBean.getRecordMsgList().add("小板子+" + count);
                }
            }
        }

    }


    /**
     * 小板子  aabbcc
     *
     * @param userBean
     * @return
     */
    private void xbz(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        Collections.sort(userBrand);
        int temp;
        int temp1;
        //存放牌数为两张的集合
        List<Integer> list = new ArrayList<>();
        List<Integer> rem = new ArrayList<>();
        for (int i = 0; i < userBrand.size(); i++) {
            temp = userBrand.get(i);
            int tempcount = 0;
            for (int j = i; j < userBrand.size(); j++) {
                if (userBrand.get(j) == temp) {
                    tempcount++;
                }
            }
            //加入集合
            if (tempcount == 2) {
                list.add(temp);
                rem.add(temp);
                rem.add(temp);
            }
        }
        Collections.sort(list);

        List<Integer> allc = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            temp1 = list.get(i);
            if (temp1 == 31 || temp1 == 32 || temp1 == 33) {
                continue;
            }
            List<Integer> list1 = new ArrayList<>();
            list1.add(temp1);
            if (list.size() != 0) {
                boolean is = panduanValue(temp1, 2);//判断牌值是否为9万桶条
                if (is) {
                   /* for (int j = 0; j < rem.size(); j++) {
                        if(temp1==rem.get(j)){
                            rem.remove(j);
                        }
                    }*/
                    continue;
                }
            }
            list1.add(temp1 + 1);
            list1.add(temp1 + 2);
            Collections.sort(list1);
            if (list.containsAll(list1)) {
                allc.addAll(list1);
                count++;
            }
        }/*
        if(allc.contains(31)||allc.contains(32)||allc.contains(33)){
            return;
        }*/
        if (rem.size() != 0 && allc.size() != 0) {
            for (int j = rem.size() - 1; j >= 0; j--) {
                if (!allc.contains(rem.get(j))) {
                    rem.remove(j);
                }
            }
        }
        if (count >= 1) {
            List<Integer> integers = GetIntersection2(rem, userBrand);
            int state = Mahjong_Util.mahjong_Util.ssssssssss(integers);
            if (state == 0) {
                if (!userBean.getRecordMsgList().contains("大飞机+7") && !userBean.getRecordMsgList().contains("大飞机+14") && !userBean.getRecordMsgList().contains("大飞机+21")) {
                    userBean.setPower(count);
                    System.err.println("小板子+" + count);
                    userBean.getRecordMsgList().add("小板子+" + count);
                }
            }
        }

    }

    /**
     * 龙七对  是指胡暗七对时胡的那张牌有四张一样的且胡牌时必须是胡这四张牌中的一张。
     * aaaabbccddeeff
     *
     * @param userBean
     * @return
     */
    private boolean lqd(UserBean userBean, int brand) {
        List<Integer> cards = User_Brand_Value(userBean.getBrands());
        //cards.add(getBrand_Value(brand));
        Collections.sort(cards);
        List<Integer> pan = new ArrayList<>();
        int count = 0;
        boolean islong = false;
        int sizhang = 0;
        for (int i = 0; i < cards.size(); i++) {
            int num = 0;
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(j) == cards.get(i)) {
                    if (!pan.contains(cards.get(i))) {
                        count++;
                    }
                    pan.add(cards.get(i));
                    num++;
                }
            }
            if (num == 3) {
                sizhang++;
                islong = true;
            }
        }
        if (count == 5 && sizhang == 2 && islong == true) {
            return true;
        }
        if (count == 6 && islong == true) {
            return true;
        }
        return false;
    }


    /**
     * 暗七对  是指胡牌时是七对任意牌
     *
     * @param userBean
     * @return
     */
    private boolean aqd(UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        //userBrand.add(getBrand_Value(brand));
        if (userBrand.size() == 14) {
            Collections.sort(userBrand);
            boolean num = userBrand.get(0) == userBrand.get(1);
            boolean num1 = userBrand.get(2) == userBrand.get(3);
            boolean num2 = userBrand.get(4) == userBrand.get(5);
            boolean num3 = userBrand.get(6) == userBrand.get(7);
            boolean num4 = userBrand.get(8) == userBrand.get(9);
            boolean num5 = userBrand.get(10) == userBrand.get(11);
            boolean num6 = userBrand.get(12) == userBrand.get(13);
            if (num && num1 && num2 && num3 && num4 && num4 && num5 && num6) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清一色（纯清） 是指胡牌时只有一种花色的牌。
     *
     * @param userBean
     * @return
     */
    private boolean qysc(UserBean userBean) {
        //手牌整合
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        for (Integer integer : User_Brand_Value(userBean.getShow_brands())) {
            userBrand.add(integer);
        }
        for (Integer integer : User_Brand_Value(userBean.getBump_brands())) {
            userBrand.add(integer);
        }
        for (Integer integer : User_Brand_Value(userBean.getHide_brands())) {
            userBrand.add(integer);
        }
        //去重
        List<Integer> newList = new ArrayList<>();
        for (Integer ban : userBrand) {
            if (!newList.contains(ban)) {
                newList.add(ban);
            }
        }
        //排序
        Collections.sort(userBrand);
        //创建万牌集合
        List<Integer> wanList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            wanList.add(i);
        }
        //判断
        if (wanList.containsAll(newList)) {
            return true;
        }
        //创建筒牌集合
        List<Integer> tongList = new ArrayList<>();
        for (int i = 9; i < 18; i++) {
            tongList.add(i);
        }
        //判断
        if (tongList.containsAll(newList)) {
            return true;
        }
        //创建条牌集合
        List<Integer> tiaoList = new ArrayList<>();
        for (int i = 18; i < 27; i++) {
            tiaoList.add(i);
        }
        //判断
        if (tiaoList.containsAll(newList)) {
            return true;
        }
        return false;
    }

    /**
     * 清一色（混清） 是指胡牌时只有一种花色牌和中发白字牌。
     *
     * @param userBean
     * @return
     */
    private boolean qysh(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrands.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrands.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrands.add(integer);
        }
        //去重
        List<Integer> newList = new ArrayList<>();
        for (Integer ban : userBrands) {
            if (!newList.contains(ban)) {
                newList.add(ban);
            }
        }
        //排序
        Collections.sort(userBrands);
        //创建万牌集合
        List<Integer> wanList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            wanList.add(i);
        }
        wanList.add(31);
        wanList.add(32);
        wanList.add(33);
        //判断
        if (wanList.containsAll(newList)) {
            if (newList.contains(31) || newList.contains(32) || newList.contains(33)) {
                return true;
            }
        }
        //创建筒牌集合
        List<Integer> tongList = new ArrayList<>();
        for (int i = 9; i < 18; i++) {
            tongList.add(i);
        }
        tongList.add(31);
        tongList.add(32);
        tongList.add(33);
        //判断
        if (tongList.containsAll(newList)) {
            if (newList.contains(31) || newList.contains(32) || newList.contains(33)) {
                return true;
            }
        }
        //创建条牌集合
        List<Integer> tiaoList = new ArrayList<>();
        for (int i = 18; i < 27; i++) {
            tiaoList.add(i);
        }
        tiaoList.add(31);
        tiaoList.add(32);
        tiaoList.add(33);
        //判断
        if (tiaoList.containsAll(newList)) {
            if (newList.contains(31) || newList.contains(32) || newList.contains(33)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 一条龙 5颗。123456789
     *
     * @param userBean
     * @return
     */
    private boolean ytl(UserBean userBean) {
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        //排序
        Collections.sort(userBrands);
        //去重
        List<Integer> newList = new ArrayList<>();
        for (Integer ban : userBrands) {
            if (!newList.contains(ban)) {
                newList.add(ban);
            }
        }
        String brandstr = "";
        for (Integer i : newList) {
            brandstr += i;
        }
        boolean ytl = getYTL(userBean);
        if (ytl) {
            return true;
        }
        return false;
    }

    private boolean getYTL(UserBean userBean) {
        String temp = "012345678";
        List<Integer> list1 = new ArrayList<>();
        for (int i = 0; i < temp.length(); i++) {
            list1.add(Integer.valueOf(temp.charAt(i)));
        }

        String temp1 = "9-10-11-12-13-14-15-16-17";
        List<Integer> list2 = new ArrayList<>();
        String[] arr = temp1.split("-");
        for (int i = 0; i < arr.length; i++) {
            list2.add(Integer.valueOf(arr[i]));
        }

        String temp2 = "18-19-20-21-22-23-24-25-26";
        List<Integer> list3 = new ArrayList<>();
        String[] arr2 = temp2.split("-");
        for (int i = 0; i < arr2.length; i++) {
            list3.add(Integer.valueOf(arr2[i]));
        }

        List<Integer> rem = new ArrayList<>();
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        if (userBrand.containsAll(list1)) {
            rem.addAll(list1);
        } else if (userBrand.containsAll(list2)) {
            rem.addAll(list2);
        } else if (userBrand.containsAll(list3)) {
            rem.addAll(list3);
        } else {
            return false;
        }
        List<Integer> integers = GetIntersection2(rem, userBrand);
        int state = Mahjong_Util.mahjong_Util.ssssssssss(integers);
        if (state == 0) {
            return true;
        }
        return false;
    }


    private boolean getYTLl(String brandstr) {
        String temp = "012345678";
        String temp1 = "91011121314151617";
        String temp2 = "181920212223242526";
        if (brandstr.contains(temp) || brandstr.contains(temp1) || brandstr.contains(temp2)) {
            return true;
        }
        return false;
    }

    /**
     * 是否大对子  是胡牌时指四副搭子都是三张（或四张）相同的任意牌，并且只有两门花色。
     *
     * @param userBean （AAA+AAA+AAA+AAA+AA）
     * @return
     */
    private boolean ddz(RoomBean roomBean, UserBean userBean, int brand) {
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        List<Integer> integers1 = User_Brand_Value(userBean.getShow_brands());
        for (Integer integer : integers1) {
            userBrand.add(integer);
        }
        List<Integer> integers2 = User_Brand_Value(userBean.getBump_brands());
        for (Integer integer : integers2) {
            userBrand.add(integer);
        }
        List<Integer> integers3 = User_Brand_Value(userBean.getHide_brands());
        for (Integer integer : integers3) {
            userBrand.add(integer);
        }
        //userBrand.add(getBrand_Value(brand));
        Collections.sort(userBrand);
        int[] brands = getBrands(userBrand);
        //检测朴
        int j = ISPave_Count_s(brands, 0);
        //检测将
        int i = ISDouble_Count(brands);
        if (i == 1 && j == 4) {
            return true;
        }
        return false;
    }


    /**
     * 大牌门清 +5 只要牌型中出现大对子 一条龙 七对 清一色任意一种是门清  这就加个大牌门清
     *
     * @param userBean
     * @return
     */
    private boolean dpmq(RoomBean roomBean, UserBean userBean) {
        List<String> recordList = userBean.getRecordMsgList();
        for (String s : recordList) {
            if (s.contains("大对子") || s.contains("一条龙") || s.contains("龙七对") || s.contains("暗七对") || s.contains(
                    "清一色（纯）") || s.contains("清一色（混）")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 卡星五  胡牌时胡的是456中的5，并且只能胡这一张牌。在卡张的基础上再加1颗。
     *
     * @param userBean
     * @return
     */
    private boolean kwx(UserBean userBean, int brand, List<Integer> tingList) {
        List<Integer> brandsList = userBean.getBrands();
        //手里的牌转换
        List<Integer> userBrands = User_Brand_Value(brandsList);
        //单张牌值转换
        int brand_value = getBrand_Value(brand);
        //userBrands.add(brand_value);
        if (brandsList.size() == 2 && (brand_value == 4 || brand_value == 13 || brand_value == 22)) {
            return true;
        }
        if (brand_value == 4) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(3);
            list.add(4);
            list.add(5);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
                /*int count4 = 0;
                int count5 = 0;
                int count6 = 0;
                for (int i = 0; i < userBrands.size(); i++) {
                    if (userBrands.get(i) == list.get(0)) {
                        count4++;
                    } else if (userBrands.get(i) == list.get(1)) {
                        count5++;
                    } else if (userBrands.get(i) == list.get(2)) {
                        count6++;
                    }
                }
                if (count4 == 1 && count5 == 1 && count6 == 1) {

                }*/
            }
        }
        if (brand_value == 13) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(12);
            list.add(13);
            list.add(14);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
                /*int count4 = 0;
                int count5 = 0;
                int count6 = 0;
                for (int i = 0; i < userBrands.size(); i++) {
                    if (userBrands.get(i) == list.get(0)) {
                        count4++;
                    } else if (userBrands.get(i) == list.get(1)) {
                        count5++;
                    } else if (userBrands.get(i) == list.get(2)) {
                        count6++;
                    }
                }
                if (count4 == 1 && count5 == 1 && count6 == 1) {
                    for (Integer i : tingList) {
                        if (i == brand_value && tingList.size() == 1) {
                            return true;
                        }
                    }
                }*/
            }
        }
        if (brand_value == 22) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(21);
            list.add(22);
            list.add(23);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
                /*int count4 = 0;
                int count5 = 0;
                int count6 = 0;
                for (int i = 0; i < userBrands.size(); i++) {
                    if (userBrands.get(i) == list.get(0)) {
                        count4++;
                    } else if (userBrands.get(i) == list.get(1)) {
                        count5++;
                    } else if (userBrands.get(i) == list.get(2)) {
                        count6++;
                    }
                }
                if (count4 == 1 && count5 == 1 && count6 == 1) {
                    for (Integer i : tingList) {
                        if (i == brand_value && tingList.size() == 1) {
                            return true;
                        }
                    }
                }*/
            }
        }
        return false;
    }

    /**
     * 是否平胡 牌型中没出现大对子 一条龙 七对 清一色
     *
     * @param roomBean
     * @return
     */
    private boolean ph(RoomBean roomBean, UserBean userBean) {
        List<String> recordList = userBean.getRecordMsgList();
        for (String s : recordList) {
            if (s.contains("大对子") || s.contains("一条龙") || s.contains("龙七对") || s.contains("暗七对") || s.contains(
                    "清一色（纯）") || s.contains("清一色（混）") || s.contains("大三元") || s.contains("大飞机") || s.contains("小飞机")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否缺一门
     *
     * @param userBean
     * @return
     */
    private boolean qym(RoomBean roomBean, UserBean userBean) {
        return false;
    }

    /**
     * 是否单吊 +1颗  胡牌时胡的是将牌，并且只能胡这一张牌。
     *
     * @param userBean
     * @return
     */
    private boolean dd(UserBean userBean, int brand, List<Integer> tingList) {
        //手里的牌转换
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        //单张牌值转换
        int brand_value = getBrand_Value(brand);
        //userBrands.add(brand_value);
        Collections.sort(userBrands);
        int count = 0;
        for (Integer userBrand : userBrands) {
            if (brand_value == userBrand) {
                count++;
            }
        }
        if (count == 2) {
            for (Integer i : tingList) {
                if (i == brand_value && tingList.size() == 1) {
                    return true;

                }
            }
        }
        return false;
    }


    /**
     * 是否边张 +1颗 胡牌时胡的789中的7或者123中的3，并且只能胡这一张牌。
     *
     * @param userBean
     * @return
     */
    private boolean bz(UserBean userBean, int brand, List<Integer> tingList) {
        List<Integer> brandsList = userBean.getBrands();
        //手里的牌转换
        List<Integer> userBrands = User_Brand_Value(brandsList);
        //单张牌值转换
        int brand_value = getBrand_Value(brand);
        if (brand_value == 6) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(7);
            list.add(8);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        if (brand_value == 15) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(16);
            list.add(17);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        if (brand_value == 24) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(25);
            list.add(26);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        if (brand_value == 2) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(0);
            list.add(1);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        if (brand_value == 11) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(9);
            list.add(10);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        if (brand_value == 20) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(18);
            list.add(19);
            if (userBrands.containsAll(list)) {
                for (Integer i : tingList) {
                    if (i == brand_value && tingList.size() == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否卡张 +1颗  胡牌时胡的是手牌中三张序数牌的中间一张，不算碰杠,并且只能胡这一张牌。例如234中的3。
     *
     * @param userBean
     * @return
     */
    private boolean kz(UserBean userBean, int brand, List<Integer> tingList) {
        //整幅牌转换
        List<Integer> userBrands = User_Brand_Value(userBean.getBrands());
        //单张牌值转换
        int brand_value = getBrand_Value(brand);
        //userBrands.add(brand_value);
        List<Integer> list = new ArrayList<>();
        list.add(brand_value);
        list.add(brand_value + 1);
        list.add(brand_value - 1);
        if (userBrands.containsAll(list)) {
            for (Integer i : tingList) {
                if (i == brand_value && tingList.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为门清  +1颗
     *
     * @param userBean
     * @return
     */
    private boolean mq(RoomBean roomBean, UserBean userBean) {
        if (userBean.getBump_brands().size() == 0 && userBean.getShow_brands().size() == 0) {
            return true;
        }
        return false;
    }
/*******************************工具类*******************************/
    /**
     * 排序（升序）[万筒条东南西北中发白]
     *
     * @param list
     */
    public void Order_Brands(List<Integer> list) {
        //升序
        Collections.sort(list);
    }

    /**
     * 获取单幅牌
     *
     * @param index
     * @return
     */
    public int getBrand_Value(int index) {
        return index - 34 * (index / 34);
    }

    /**
     * 获取单张牌值和花色
     *
     * @param index
     * @return
     */
    public int[] ISUser_Mahjong(int index) {
        int[] indexs = new int[2];
        int color = -1;
        //获取单幅
        int value = getBrand_Value(index);
        //小于27的是万筒条
        if (value < 27) {
            //获取牌值ֵ
            index = value - 9 * (value / 9);
            //获取花色
            color = (value / 9);
        } else {
            color = value;
        }
        indexs[0] = index;
        indexs[1] = color;
        return indexs;
    }

    /**
     * 转换牌值
     *
     * @return
     */
    public List<Integer> User_Brand_Value(List<Integer> user_Brand) {
        List<Integer> list = new ArrayList<Integer>();
        for (int brand : user_Brand) {
            list.add(mahjong_Util.getBrand_Value(brand));
        }
        return list;
    }

    /**
     * 检测不完整的朴
     *
     * @param brands
     * @return
     */
    private boolean IS_DrawBrands(int[] brands) {
        int[] brand = new int[2];
        int count = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == -1) continue;
            if (count == 2) return false;
            brand[count] = brands[i];
            count++;
        }
        int[] brand_value = ISUser_Mahjong(brand[0]);
        int[] brand_value2 = ISUser_Mahjong(brand[1]);
        //AB
        if ((brand_value[0] + 1) == brand_value2[0] && brand_value[1] == brand_value2[1]) {
            return true;
        }
        //AC
        if ((brand_value[0] + 2) == brand_value2[0] && brand_value[1] == brand_value2[1]) {
            return true;
        }
        //AA
        if (brand_value[0] == brand_value2[0] && brand_value[1] == brand_value2[1]) {
            return true;
        }
        return false;
    }


    /**
     * 检测将
     *
     * @return
     */
    public int ISDouble_Count(int[] brands) {
        int count = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == -1) continue;
            if ((i + 1) < brands.length && brands[i] == brands[i + 1]) {
                brands[i] = -1;
                brands[i + 1] = -1;
                count++;
                if (count == 1) {
                    break;
                }
            }
        }
        return count;
    }

    /**
     * 检测朴（123）
     *
     * @return
     */
    public int ISPave_Count(int[] brands, int type) {
        int pave_count = 0;
        for (int i = 0; i < brands.length; i++) {
            //不检测癞子
            if (brands[i] == -1) continue;
            if (IS_Brands_A(brands, brands[i])) {
                brands[i] = -1;
                pave_count++;
                if (type == 1) return pave_count;
            }
        }
        return pave_count;
    }

    /**
     * 检测朴（321）
     *
     * @return
     */
    public int ISPave_Count_A(int[] brands, int type) {
        int pave_count = 0;
        for (int i = brands.length - 1; i > 0; i--) {
            //不检测癞子
            if (brands[i] == -1) continue;
            if (IS_Brands_CBA(brands, brands[i])) {
                brands[i] = -1;
                pave_count++;
                if (type == 1) return pave_count;
            }
        }
        return pave_count;
    }

    /**
     * 检测朴(321)
     *
     * @param brands
     * @param index
     * @return
     */
    private boolean IS_Brands_CBA(int[] brands, int index) {
        int brand_index = -1;
        int[] brand_t = ISUser_Mahjong(index);
        for (int i = brands.length - 1; i > 0; i--) {
            if (brands[i] == -1) continue;
            int[] brand = ISUser_Mahjong(brands[i]);
            //牌值大1且花色相同的牌
            if ((brand_t[0] - 1) == brand[0] && brand_t[1] == brand[1]) {
                brand_index = i;
            }
            //牌值大2且花色相同的牌
            if ((brand_t[0] - 2) == brand[0] && brand_t[1] == brand[1]) {
                if (brand_index != -1) {
                    brands[brand_index] = -1;
                    brands[i] = -1;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测朴(123)
     *
     * @param brands
     * @param index
     * @return
     */
    private boolean IS_Brands_A(int[] brands, int index) {
        int brand_index = -1;
        int[] brand_t = ISUser_Mahjong(index);
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == -1) continue;
            int[] brand = ISUser_Mahjong(brands[i]);
            //牌值大1且花色相同的牌
            if ((brand_t[0] + 1) == brand[0] && brand_t[1] == brand[1]) {
                brand_index = i;
            }
            //牌值大2且花色相同的牌
            if ((brand_t[0] + 2) == brand[0] && brand_t[1] == brand[1]) {
                if (brand_index != -1) {
                    brands[brand_index] = -1;
                    brands[i] = -1;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检测朴（111）
     *
     * @param brands
     * @return
     */
    public int ISPave_Count_s(int[] brands, int type) {
        int pave_count = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == -1) continue;
            if (IS_Brands_B(brands, brands[i])) {
                brands[i] = -1;
                pave_count++;
                if (type == 1) return pave_count;
            }
        }
        return pave_count;
    }

    /**
     * 检测朴（AAAA）
     *
     * @param brands
     * @param index
     * @return
     */
    private boolean IS_Brands_B(int[] brands, int index) {
        int brand_index = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == index) {
                brand_index++;
            }
        }
        if (brand_index > 2) {
            Remove_index(brands, index);
            return true;
        }
        return false;
    }

    /**
     * 删除指定牌值
     *
     * @param brands
     * @param index
     */
    private void Remove_index(int[] brands, int index) {
        int count = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == index) {
                brands[i] = -1;
                count++;
                if (count == 3) {
                    break;
                }
            }
        }
    }

    /**
     * 集合转数组
     *
     * @param list
     * @return
     */
    public int[] getBrands(List<Integer> list) {
        int[] brands = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            brands[i] = getBrand_Value(list.get(i));
        }
        return brands;
    }

    /**
     * 七对检测
     *
     * @param list
     * @param index
     */
    public void IS_Seven(List<Integer> list, int index) {
        //将来牌放入用户手中
        list.add(index);
        //重新进行排序
        Order_Brands(list);
        int count = 0;
        int draw_count = 0;
        for (int i = 0; i < list.size(); i++) {
            if ((i + 1) < list.size()) {
                if (list.get(i) == list.get(i + 1)) {
                    count++;
                    i++;
                }
            }
        }
        if (count == 7) System.out.println("七对胡");
        if (draw_count == 1 && count == 6) System.out.println("七对胡[1混]");
        if (draw_count == 2 && count == 5) System.out.println("七对胡[2混]");
        if (draw_count == 3 && count == 4) System.out.println("七对胡[3混]");
    }

    /**
     * 返回中文牌面
     *
     * @param i
     * @return
     */
    public String getBrand_str(int i) {
        String color = "";
        int[] index = ISUser_Mahjong(i);
        if (index[1] == 0) {
            color = "万";
        } else if (index[1] == 1) {
            color = "筒";
        } else if (index[1] == 2) {
            color = "条";
        } else if (index[1] == 27) {
            color = "东";
        } else if (index[1] == 28) {
            color = "南";
        } else if (index[1] == 29) {
            color = "西";
        } else if (index[1] == 30) {
            color = "北";
        } else if (index[1] == 31) {
            color = "中";
        } else if (index[1] == 32) {
            color = "发";
        } else if (index[1] == 33) {
            color = "白";
        }
        String name = index[0] + 1 + "";
        if (index[0] > 26) name = "";
        return name + color;
    }

    private void removeList(UserBean userBean, String string, int fan) {
        List<String> recordMsgList = new ArrayList<>();
        recordMsgList.addAll(userBean.getRecordMsgList());
        for (String str : recordMsgList) {
            if (str.equals(string)) {
                userBean.getRecordMsgList().remove(string);
                userBean.setPower(fan);
            }
        }
    }

    //去重
    public static List removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        return list;
    }
}