package com.suzhou.concept.lil.service.temp;

import com.suzhou.concept.bean.ConceptItem;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 原文界面的临时会话
 * @date: 2023/10/18 13:30
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenPlaySession {

    private static ListenPlaySession instance;

    public static ListenPlaySession getInstance(){
        if (instance==null){
            synchronized (ListenPlaySession.class){
                if (instance==null){
                    instance = new ListenPlaySession();
                }
            }
        }
        return instance;
    }

    //当前文章的列表数据
    private List<ListenPlayTempBean> tempList = new ArrayList<>();

    public void setTempList(List<ListenPlayTempBean> list){
        tempList.clear();
        tempList.addAll(list);
    }

    public List<ListenPlayTempBean> getTempList(){
        return tempList;
    }

    //当前选中文章的数据
    private ListenPlayTempBean tempBean;

    public void setTempBean(ListenPlayTempBean tempBean) {
        this.tempBean = tempBean;
    }

    public ListenPlayTempBean getTempBean() {
        return tempBean;
    }


    /*******************************其他功能*****************************/
    //将首页的数据进行转换并且保存
    public void transDataToTemp(List<ConceptItem> list){
        if (list!=null&&list.size()>0){
            List<ListenPlayTempBean> tempBeanList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                ConceptItem item = list.get(i);

                tempBeanList.add(new ListenPlayTempBean(
                        item.getVoa_id(),
                        String.valueOf(item.getBookId()),
                        GlobalMemory.INSTANCE.getVideoUrl(Integer.parseInt(item.getVoa_id())),
                        item.getTitle(),
                        item.getTitle_cn(),
                        i
                ));

            }

            setTempList(tempBeanList);
        }
    }

    //设置选中的数据
    public void setSelectData(int position){
        if (tempList!=null&&tempList.size()>position){
            setTempBean(tempList.get(position));
        }
    }
}
