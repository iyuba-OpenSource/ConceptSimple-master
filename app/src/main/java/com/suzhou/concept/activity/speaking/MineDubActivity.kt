package com.suzhou.concept.activity.speaking

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.PagerAdapter
import com.suzhou.concept.databinding.ActivityMineDubBinding
import com.suzhou.concept.fragment.mydub.CollectOrDownloadFragment
import com.suzhou.concept.fragment.mydub.MineReleaseFragment
import com.suzhou.concept.lil.data.remote.RetrofitUtil
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.ui.my.kouyu.KouyuDeleteBean
import com.suzhou.concept.lil.util.LibRxUtil
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory

class MineDubActivity : BaseActivity<ActivityMineDubBinding>() {
    private lateinit var mediator: TabLayoutMediator

    //正式发布界面
    private var releaseFragment:MineReleaseFragment = MineReleaseFragment()
    //是否存在配音的发布数据
    private var hasPublishKou:Boolean = true

    override fun ActivityMineDubBinding.initBinding() {
        //设置标题
        setTitleText("我的配音")
        //设置编辑选项
        setEditClick()

        dubPager.apply {
            val array = mutableListOf(
                releaseFragment,
                CollectOrDownloadFragment(),
                CollectOrDownloadFragment(false)
            )
            adapter=PagerAdapter(this@MineDubActivity,array)
        }
        val titleArray= arrayOf("已发布","已收藏","已下载")
        mediator= with(TabLayoutMediator(dubTab,dubPager,false,true) { tab, position ->
            tab.text = titleArray[position]
        }){
            attach()
            this
        }

        dubPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position==0){
                    if (hasPublishKou){
                        standardRightText.visibility = View.VISIBLE
                    }else{
                        standardRightText.visibility = View.INVISIBLE
                    }
                }else{
                    standardRightText.visibility = View.INVISIBLE
                }
            }
        })
    }

    //设置编辑按钮
    private fun setEditClick(){
        initRightText()
        standardRightText.visibility = View.VISIBLE
        standardRightText.text = "编辑"
        standardRightText.setOnClickListener {
            val showText:String = standardRightText.text.toString()
            if (showText == "编辑"){
                releaseFragment.showEditStatus(true)
                standardRightText.text = "取消"
            }else if (showText == "删除"){
                val list:List<Int> = releaseFragment.getSelectData()
                if (list.isNotEmpty()){
                    deleteKouyu(list)
                }else{
                    ToastFactory.showShort(this,"未查找到选中的数据")
                }
            }else if (showText == "取消"){
                releaseFragment.showEditStatus(false)
                standardRightText.text = "编辑"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator.detach()
    }

    //这里弄一个数据回调，选中时设置编辑按钮
    //第二个回调，数据不存在的时候隐藏按钮
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event:RefreshEvent){
        if (event.type.equals(RefreshEvent.KOUYU_SELECT)){
            if (event.msg.isNotEmpty()){
                standardRightText.text = "删除"
            }else{
                standardRightText.text = "取消"
            }
        }else if (event.type.equals(RefreshEvent.KOUYU_NODATA)){
            hasPublishKou = false
            standardRightText.visibility = View.INVISIBLE
        }
    }

    override fun initEventBus(): Boolean {
        return true
    }

    //加载弹窗
    private lateinit var loadingDialog:LoadingMsgDialog

    private fun startLoading(){
        if (!::loadingDialog.isInitialized){
            loadingDialog = LoadingMsgDialog(this)
            loadingDialog.create()
        }
        loadingDialog.setMessage("正在操作中～")
        loadingDialog.show()
    }

    private fun stopLoading(){
        if (::loadingDialog.isInitialized){
            loadingDialog.dismiss()
        }
    }

    //删除选中的配音
    private lateinit var deleteKouyuDis:Disposable
    //第几个数据
    private var deleteIndex:Int = 0
    private fun deleteKouyu(list:List<Int>){
        startLoading()

        //根据当前的顺序判断是否完完全删除
        if (deleteIndex>=list.size){
            stopLoading()
            ToastFactory.showShort(this,"删除配音完成")
            releaseFragment.refreshData()
            LibRxUtil.unDisposable(deleteKouyuDis)

            //这里同步检查下数据，同时设置下操作
            standardRightText.text = "取消"
            releaseFragment.clearSelectData()
            return
        }

        val deleteId:Int = list.get(deleteIndex)
        RetrofitUtil.getInstance().deleteKouyuData(deleteId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<KouyuDeleteBean>{
                override fun onSubscribe(d: Disposable) {
                    deleteKouyuDis = d
                }

                override fun onError(e: Throwable) {
                    stopLoading()
                    LibRxUtil.unDisposable(deleteKouyuDis)
                    ToastFactory.showShort(this@MineDubActivity,"删除配音异常，请重试～")
                }

                override fun onComplete() {

                }

                override fun onNext(bean: KouyuDeleteBean) {
                    if (bean.message.lowercase() == "ok"){
                        //下一个
                        deleteIndex++
                        deleteKouyu(list)
                    }else{
                        stopLoading()
                        LibRxUtil.unDisposable(deleteKouyuDis)
                        ToastFactory.showShort(this@MineDubActivity,"删除配音失败，请重试～")
                    }
                }

            })
    }
}