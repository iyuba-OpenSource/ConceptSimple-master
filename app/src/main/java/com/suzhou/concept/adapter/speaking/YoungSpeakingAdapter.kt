package com.suzhou.concept.adapter.speaking

import android.view.View
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.adapter.BaseAdapter
import com.suzhou.concept.bean.MineReleaseItem
import com.suzhou.concept.dao.AppDatabase
import com.suzhou.concept.databinding.YoungSpeakingItemBinding
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.KouyuItemClickListener
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
@Date:  2022/12/12
@Author:  han rong cheng
 */
class YoungSpeakingAdapter():BaseAdapter<MineReleaseItem,YoungSpeakingItemBinding>() {
    //点击回调
    private lateinit var itemListener:KouyuItemClickListener
    //选中数据
    private var saveMap:MutableMap<Int,MineReleaseItem> = mutableMapOf()
    //编辑状态
    private var isShowEdit:Boolean = false

    override fun YoungSpeakingItemBinding.onBindViewHolder(bean: MineReleaseItem, position: Int) {
        item=bean
        root.setOnClickListener {
            if (::itemListener.isInitialized){
                itemListener.onClick(bean)
            }
        }
//        root.setOnLongClickListener{
//            if (::itemListener.isInitialized){
//                itemListener.onLongClick(bean)
//            }
//            true
//        }

        //判断是否编辑
        if (isShowEdit){
            this.check.visibility = View.VISIBLE
        }else{
            this.check.visibility = View.GONE
        }

        //判断是否选中
        val selectItem:MineReleaseItem? = saveMap.get(bean.id)
        if (selectItem == null){
            this.check.setImageResource(R.drawable.ic_unseelct)
        }else{
            this.check.setImageResource(R.drawable.ic_selected)
        }

        //判断是否点赞
        if (hasAgreeData(bean.id)){
            this.agreeImage.setImageResource(R.drawable.agree_theme)
        }else{
            this.agreeImage.setImageResource(R.drawable.awesome)
        }

        //设置点击
        this.check.setOnClickListener {
            val tempItem:MineReleaseItem? = saveMap.get(bean.id)
            if (tempItem == null){
                saveMap.put(bean.id,bean)
            }else{
                saveMap.remove(bean.id)
            }
            notifyItemChanged(position)

            //这里判断是否存在数据，然后刷新显示
            if (saveMap.isEmpty()){
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.KOUYU_SELECT,""))
            }else{
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.KOUYU_SELECT,saveMap.keys.size.toString()))
            }
        }
    }

    fun registerItemListener(itemListener:KouyuItemClickListener){
        this.itemListener=itemListener
    }

    //设置编辑状态
    public fun setEditStatus(isEdit:Boolean){
        this.isShowEdit = isEdit
        notifyDataSetChanged()
    }


    //获取选中的数据
    public fun getSelectData():List<Int>{
        var list = mutableListOf<Int>()
        if (saveMap!=null&& saveMap.isNotEmpty()){
            for (key in saveMap.keys){
                saveMap.get(key)?.let { list.add(it.id) }
            }
        }
        return list
    }

    //清空选中的数据
    public fun clearSelectData(){
        saveMap.clear()
    }

    //获取当前的点赞数据
    fun hasAgreeData(otherId:Int):Boolean{
        val userId = GlobalMemory.userInfo.uid
        val agreeList = AppDatabase.getDatabase(AppClient.context).youngLikeDao().selectSimple(userId, otherId)
        return agreeList.isNotEmpty()
    }
}