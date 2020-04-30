package util;


import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 麻将算法类(仙桃晃晃)
 *
 * @author Administrator
 */
public class Mahjong_Util {
    public static Mahjong_Util mahjong_Util = new Mahjong_Util();

    /**
     * 构造器
     */
    private Mahjong_Util() {
    }

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
     * 检测是否可吃
     *
     * @param list  用户手里的牌
     * @param index 别人出的牌
     * @return
     */
    public int[] IS_Eat(List<Integer> list, int index) {
        //获取牌值和花色
        int[] index_brand = ISUser_Mahjong(index);
        //上牌和上上牌
        int index_s = index_brand[0] > 0 && index_brand[0] < 10 ? index - 1 : -1;
        int index_ss = index_brand[0] > 1 && index_brand[0] < 10 ? index - 2 : -1;
        //下牌
        int index_x = index_brand[0] < 8 ? index + 1 : -1;
        //下下牌
        int index_xx = index_brand[0] < 7 ? index + 2 : -1;
        //中发白另算
        if (index_brand[0] == 31) {
            index_x = 32;
            index_xx = 33;
        } else if (index_brand[0] == 32) {
            index_s = 31;
            index_x = 33;
        } else if (index_brand[0] == 33) {
            index_ss = 31;
            index_s = 32;
        }
        //上上牌-上牌-下牌-下下牌
        int[] eat = new int[]{-1, -1, -1, -1};
        //
        for (int i = 0; i < list.size(); i++) {
            //上牌
            if (list.get(i) == index_s) eat[1] = index_s;
            //上上牌
            if (list.get(i) == index_ss) eat[0] = index_ss;
            //下牌
            if (list.get(i) == index_x) eat[2] = index_x;
            //下下牌
            if (list.get(i) == index_xx) eat[3] = index_xx;
        }
        return eat;
    }

    /**
     * 检测是否可碰/杠
     *
     * @param list
     * @param index
     * @return
     */
    public int[] IS_Bump(List<Integer> list, int index) {
        int[] bump = new int[]{-1, -1, -1};
        for (int i = 0; i < list.size(); i++) {
            int thisbrand = mahjong_Util.getBrand_Value(list.get(i));
            if (thisbrand == mahjong_Util.getBrand_Value(index)) {
                for (int j = 0; j < bump.length; j++) {
                    if (bump[j] == -1) {
                        bump[j] = list.get(i);
                        break;
                    }
                }
            }
        }
        return IS_Bump_s(bump) ? bump : null;
    }

    private boolean IS_Bump_s(int[] bump) {
        int count = 0;
        for (int num : bump) {
            if (num != -1) {
                count++;
            }
        }
        return count >= 2 ? true : false;
    }

    /**
     * 检测胡牌(11-123-123-123-111)
     *
     * @param
     * @param index
     */
    public int IS_Victory(UserBean userBean, int index) {
        System.out.println("用户" + userBean.getUserid());
        List<Integer> list = new ArrayList<>();
        list.addAll(userBean.getBrands());
        list.add(index);
        int state = IS_Victory(list, userBean, index);
        /*if(state==0){
            userBean.Remove_Brands(index);
        }*/
        return state;
    }

    /**
     * 转换牌值
     *
     * @return
     */
    public List<Integer> User_Brand_Value(List<Integer> user_Brand) {
        List<Integer> list = new ArrayList<Integer>();
        for (int brand : user_Brand) {
            list.add(Mahjong_Util.mahjong_Util.getBrand_Value(brand));
        }
        return list;
    }

    /**
     * 胡牌
     *
     * @param list
     * @return
     */
    public int IS_Victory(List<Integer> list, UserBean userBean, int brand) {
        int state = 0;
        List<Integer> brandlist = User_Brand_Value(list);
        Order_Brands(brandlist);
        for (int i = 0; i < 12; i++) {
            int[] brands = getBrands(brandlist);
            state = IS_Hu_ABC(i, brands, userBean);
            if (state != 0) {
                break;
            }
            brands = getBrands(brandlist);
            state = IS_Hu_A(i, brands, userBean);
            if (state != 0) {
                break;
            }
        }
        if (state == 0) {
            state = IS_Seven(list, brand, userBean);
        }
        return state;
    }

    public int ssssssssss(List<Integer> asd) {
        for (int s = 0; s < 2; s++) {
            ArrayList<Integer> brandlist = new ArrayList<>();
            brandlist.addAll(asd);
            if (s == 0) {
                ArrayList<Integer> list = new ArrayList<>();
                list.addAll(brandlist);
                //去扑 abc
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < brandlist.size(); i++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        Integer t = brandlist.get(i);
                        list1.add(t);
                        list1.add(t + 1);
                        list1.add(t + 2);
                        if (list.containsAll(list1)) {
                            list.remove(t);
                            list.remove(Integer.valueOf(t + 1));
                            list.remove(Integer.valueOf(t + 2));
                            i = i + 2;
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                //去三对 aaa
                for (int i = 0; i < brandlist.size(); i++) {
                    int t = brandlist.get(i);
                    for (int j = i + 1; j < brandlist.size(); j++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        if (brandlist.get(j) == t) {
                            list1.add(brandlist.get(j));
                        }
                        if (list1.size() >= 1) {
                            list.removeAll(list1);
                        }
                    }
                }
                if (list.size() == 0) {
                    return list.size();
                }
            }
            if (s == 1) {
                ArrayList<Integer> list = new ArrayList<>();
                list.addAll(brandlist);

                //去三对 aaa
                for (int i = 0; i < brandlist.size(); i++) {
                    int t = brandlist.get(i);
                    for (int j = i + 1; j < brandlist.size(); j++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        if (brandlist.get(j) == t) {
                            list1.add(brandlist.get(j));
                        }
                        if (list1.size() >= 1) {
                            list.removeAll(list1);
                        }
                    }
                }
                //去扑 abc
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < brandlist.size(); i++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        Integer t = brandlist.get(i);
                        list1.add(t);
                        list1.add(t + 1);
                        list1.add(t + 2);
                        if (list.containsAll(list1)) {
                            list.remove(t);
                            list.remove(Integer.valueOf(t + 1));
                            list.remove(Integer.valueOf(t + 2));
                            i = i + 2;
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                if (list.size() == 0) {
                    return list.size();
                }
            }
        }
        return -1;
    }

    public int s3(List<Integer> cards, int index, UserBean user) {
        //将来牌放入用户手中
        //list.add(index);
        //重新进行排序
        Order_Brands(cards);
        int count = 0;
        for (int i = 0; i < cards.size(); i++) {
            if ((i + 1) < cards.size()) {
                if (cards.get(i) == cards.get(i + 1)) {
                    count++;
                    i++;
                }
            }
        }
        if (count == 7) {
            //user.setHu_type(777);
            return 777;
        }
        return count;
    }

    public int s1(int type, int[] brands, UserBean userBean) {
        int state = 0;
        //朴(123)
        int pave_count = 0;
        int pave_count_s = 0;
        int double_count = 0;
        String[] strs = Public_State.sorts[type].split("-");
        for (String index : strs) {
            if (Integer.parseInt(index) == 1) {
                //朴(123)
                pave_count += ISPave_Count(brands, 0);
            }
            if (Integer.parseInt(index) == 4) {
                //朴(321)
                pave_count += ISPave_Count_A(brands, 0);
            }
            if (Integer.parseInt(index) == 2) {
                //朴(111)
                pave_count_s += ISPave_Count_s(brands, 0);
            }
            if (Integer.parseInt(index) == 3) {
                //将
                double_count += ISDouble_Count(brands);
            }
        }
        //用户碰的牌
        pave_count_s += userBean.getBump_brands().size() / 3;
        //用户暗杠的牌
        pave_count_s += userBean.getHide_brands().size() / 4;
        //用户明杠的牌
        pave_count_s += userBean.getShow_brands().size() / 4;
        state = IS_State(double_count, pave_count_s, pave_count, brands);
		/*for(int index:brands){
			System.out.print(getBrand_str(getBrand_Value(index)));
		}
		System.out.println();*/
        //userBean.setHu_type(state);
        return state;
    }

    public int s2(int type, int[] brands, UserBean userBean) {
        int state = 0;
        //朴(123)
        int pave_count = 0;
        int pave_count_s = 0;
        int double_count = 0;
        for (int index : brands) {
            System.out.print(getBrand_str(getBrand_Value(index)));
        }
        System.out.println();
        //检测顺序abc-acb-bac-bca-cab-cba
        String[] strs = Public_State.sorts[type].split("-");
        for (int i = 0; i < 4; i++) {
            for (String index : strs) {
                if (Integer.parseInt(index) == 1) {
                    //朴(123)
                    pave_count += ISPave_Count(brands, 1);
                }
                if (Integer.parseInt(index) == 4) {
                    //朴(321)
                    pave_count += ISPave_Count_A(brands, 1);
                }
                if (Integer.parseInt(index) == 2) {
                    //朴(111)
                    pave_count_s += ISPave_Count_s(brands, 1);
                }
                if (Integer.parseInt(index) == 3) {
                    //将
                    double_count += ISDouble_Count(brands);
                }
            }
        }
        //用户碰的牌
        pave_count_s += userBean.getBump_brands().size() / 3;
        //用户暗杠的牌
        pave_count_s += userBean.getHide_brands().size() / 4;
        //用户明杠的牌
        pave_count_s += userBean.getShow_brands().size() / 4;
        //判断用户胡牌状态
        state = IS_State(double_count, pave_count_s, pave_count, brands);
        //userBean.setHu_type(state);
        return state;
    }


    public int IS_Hu_A(int type, int[] brands, UserBean userBean) {
        int state = 0;
        //朴(123)
        int pave_count = 0;
        int pave_count_s = 0;
        int double_count = 0;
        String[] strs = Public_State.sorts[type].split("-");
        for (String index : strs) {
            if (Integer.parseInt(index) == 1) {
                //朴(123)
                pave_count += ISPave_Count(brands, 0);
            }
            if (Integer.parseInt(index) == 4) {
                //朴(321)
                pave_count += ISPave_Count_A(brands, 0);
            }
            if (Integer.parseInt(index) == 2) {
                //朴(111)
                pave_count_s += ISPave_Count_s(brands, 0);
            }
            if (Integer.parseInt(index) == 3) {
                //将
                double_count += ISDouble_Count(brands);
            }
        }
        //用户碰的牌
        pave_count_s += userBean.getBump_brands().size() / 3;
        //用户暗杠的牌
        pave_count_s += userBean.getHide_brands().size() / 4;
        //用户明杠的牌
        pave_count_s += userBean.getShow_brands().size() / 4;
        state = IS_State(double_count, pave_count_s, pave_count, brands);
		/*for(int index:brands){
			System.out.print(getBrand_str(getBrand_Value(index)));
		}
		System.out.println();*/
        //userBean.setHu_type(state);
        return state;
    }

    public int IS_Hu_ABC(int type, int[] brands, UserBean userBean) {
        int state = 0;
        //朴(123)
        int pave_count = 0;
        int pave_count_s = 0;
        int double_count = 0;
		/*for(int index:brands){
			System.out.print(getBrand_str(getBrand_Value(index)));
		}*/
        System.out.println();
        //检测顺序abc-acb-bac-bca-cab-cba
        String[] strs = Public_State.sorts[type].split("-");
        for (int i = 0; i < 4; i++) {
            for (String index : strs) {
                if (Integer.parseInt(index) == 1) {
                    //朴(123)
                    pave_count += ISPave_Count(brands, 1);
                }
                if (Integer.parseInt(index) == 4) {
                    //朴(321)
                    pave_count += ISPave_Count_A(brands, 1);
                }
                if (Integer.parseInt(index) == 2) {
                    //朴(111)
                    pave_count_s += ISPave_Count_s(brands, 1);
                }
                if (Integer.parseInt(index) == 3) {
                    //将
                    double_count += ISDouble_Count(brands);
                }
            }
        }
        //用户碰的牌
        pave_count_s += userBean.getBump_brands().size() / 3;
        //用户暗杠的牌
        pave_count_s += userBean.getHide_brands().size() / 4;
        //用户明杠的牌
        pave_count_s += userBean.getShow_brands().size() / 4;
        //判断用户胡牌状态
        state = IS_State(double_count, pave_count_s, pave_count, brands);
        //userBean.setHu_type(state);
        return state;
    }

    private int IS_State(int double_count, int pave_count_s, int pave_count, int[] brands) {
        int state = 0;
        //4朴1将胡法
        if ((pave_count_s + pave_count) == 4 && double_count == 1) {
            System_Mess.system_Mess.ToMessagePrint("4朴1将");
            state = 900;
        }/*else
			//1癞子3朴1将
			if((pave_count_s+pave_count)==3&&double_count==1){
				state = IS_DrawBrands(brands)?890:0;
				System_Mess.system_Mess.ToMessagePrint("state"+state);
			}else
				//1癞子4朴缺将
				if((pave_count_s+pave_count)==4&&double_count==0){
					state = 880;
					System_Mess.system_Mess.ToMessagePrint("880");
				}*/
        return state;
    }

    /**
     * 检测摸牌时是否可以补杠
     *
     * @param userBean
     * @return
     */
    public int IS_Repair_Bar_Bump(UserBean userBean) {
        int count = 0;
        for (int bump_brand : userBean.getBump_brands()) {
            for (int brand : userBean.getBrands()) {
                int index = Mahjong_Util.mahjong_Util.getBrand_Value(brand);
                if (Mahjong_Util.mahjong_Util.getBrand_Value(brand) == index) {
                    count++;
                }
            }

        }
        if (count < 3) {

        }
        return count > 2 ? 1 : 0;
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
     * 检测朴（111）
     *
     * @param brands
     * @return
     */
    public int ISPave_Count_ss(int[] brands) {
        int pave_count = 0;
        for (int i = 0; i < brands.length; i++) {
            if (brands[i] == -1) continue;
            if (IS_Brands_Bb(brands, brands[i])) {
                brands[i] = -1;
                pave_count++;
            }
        }
        return pave_count;
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
     * 检测朴（AAAA）
     *
     * @param brands
     * @param index
     * @return
     */
    private boolean IS_Brands_B(int[] brands, int index) {
        int brand_index = 0;
        for (int i = 0; i < brands.length; i++) {
            //for(int j=i;j<brands.length;j++){
            if (brands[i] == index) {
                brand_index++;
            }
            //}
        }
        if (brand_index > 2) {
            Remove_index(brands, index);
            return true;
        }
        return false;
    }

    /**
     * 检测朴（AAAA）
     *
     * @param brands
     * @param index
     * @return
     */
    private boolean IS_Brands_Bb(int[] brands, int index) {
        int brand_index = 0;
        for (int i = 0; i < brands.length; i++) {
            for (int j = i; j < brands.length; j++) {
                if (brands[i] == index) {
                    brand_index++;
                }
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
            ;
        }
        return brands;
    }

    /**
     * 七对检测
     *
     * @param list
     * @param index
     */
    public int IS_Seven(List<Integer> list, int index, UserBean user) {
        //将来牌放入用户手中
        //list.add(index);
        List<Integer> cards = User_Brand_Value(list);
        //重新进行排序
        Order_Brands(cards);
        int count = 0;
        for (int i = 0; i < cards.size(); i++) {
            if ((i + 1) < cards.size()) {
                if (cards.get(i) == cards.get(i + 1)) {
                    count++;
                    i++;
                }
            }
        }
        if (count == 7) {
            //user.setHu_type(777);
            return 777;
        }
        return 0;
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

    public int checkHu(UserBean userBean,int outbrand) {
        //手牌整合
        List<Integer> userBrand = User_Brand_Value(userBean.getBrands());
        userBrand.add(getBrand_Value(outbrand));
        //排序
        Collections.sort(userBrand);

        for (int s = 0; s < 2; s++) {
            ArrayList<Integer> brandlist = new ArrayList<>();
            brandlist.addAll(userBrand);
            if(s==0){
                ArrayList<Integer> list = new ArrayList<>();
                list.addAll(brandlist);
                //去扑 abc
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < brandlist.size(); i++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        Integer t = brandlist.get(i);
                        list1.add(t);
                        list1.add(t+1);
                        list1.add(t+2);
                        if(list.containsAll(list1)){
                            list.remove(t);
                            list.remove(Integer.valueOf(t+1));
                            list.remove(Integer.valueOf(t+2));
                            i = i+2;
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                //去三对 aaa
                int lenth = brandlist.size();
                //去三对 aaa
                int duizi = 0;
                for (int i = 0; i < brandlist.size();i++) {
                    if(lenth!=brandlist.size()){
                        i = 0;
                        lenth = brandlist.size();
                    }
                    int t = brandlist.get(i);
                    ArrayList<Integer> list1 = new ArrayList<>();
                    list1.add(i);
                    for (int j = i+1; j < brandlist.size(); j++) {
                        if(brandlist.get(j)==t){
                            list1.add(j);
                        }
                    }
                    Collections.sort(list1);
                    if(list1.size()==2){
                        duizi++;
                    }
                    if(list1.size()>=2 && list1.size()<4){
                        for (int j = list1.size()-1; j >= 0; j--) {
                            list.remove(list1.get(j).intValue());
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                System.out.println(list.toString());
                System.out.println("对子个数："+duizi);
                if(list.size()==0 && duizi==1){
                    return 0;
                }
            }
            if(s==1){
                ArrayList<Integer> list = new ArrayList<>();
                list.addAll(brandlist);

                int duizi = 0;
                int lenth = brandlist.size();
                //去三对 aaa
                for (int i = 0; i < brandlist.size();i++) {
                    if(lenth!=brandlist.size()){
                        i = 0;
                        lenth = brandlist.size();
                    }
                    int t = brandlist.get(i);
                    ArrayList<Integer> list1 = new ArrayList<>();
                    list1.add(i);
                    for (int j = i+1; j < brandlist.size(); j++) {
                        if(brandlist.get(j)==t){
                            list1.add(j);
                        }
                    }
                    Collections.sort(list1);
                    if(list1.size()==2){
                        duizi++;
                    }
                    if(list1.size()>=2 && list1.size()<4){
                        for (int j = list1.size()-1; j >= 0; j--) {
                            list.remove(list1.get(j).intValue());
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                System.out.println(list.toString());
                //去扑 abc
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < brandlist.size(); i++) {
                        ArrayList<Integer> list1 = new ArrayList<>();
                        Integer t = brandlist.get(i);
                        list1.add(t);
                        list1.add(t+1);
                        list1.add(t+2);
                        if(list.containsAll(list1)){
                            list.remove(t);
                            list.remove(Integer.valueOf(t+1));
                            list.remove(Integer.valueOf(t+2));
                            i = i+2;
                        }
                    }
                    brandlist.clear();
                    brandlist.addAll(list);
                }
                System.out.println("对子个数："+duizi);
                if(list.size()==0 && duizi==1){
                    return list.size();
                }
            }
        }
        return -1;
    }

}
