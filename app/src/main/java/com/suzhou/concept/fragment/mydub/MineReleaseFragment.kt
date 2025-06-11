package com.suzhou.concept.fragment.mydub

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.iyuba.module.toolbox.GsonUtils
import com.suzhou.concept.activity.speaking.OtherOneVideoActivity
import com.suzhou.concept.adapter.speaking.YoungSpeakingAdapter
import com.suzhou.concept.bean.MineReleaseItem
import com.suzhou.concept.databinding.MineReleaseFragmentBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.lil.data.remote.RetrofitUtil
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.ui.my.kouyu.KouyuDeleteBean
import com.suzhou.concept.lil.view.dialog.LoadingDialog
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.KouyuItemClickListener
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory

/**
苏州爱语吧科技有限公司
@Date:  2022/12/12
@Author:  han rong cheng

 我的配音-已发布
 */
class MineReleaseFragment:BaseFragment<MineReleaseFragmentBinding>(){

    //适配器
    private var speakAdapter:YoungSpeakingAdapter = YoungSpeakingAdapter()

    override fun MineReleaseFragmentBinding.initBinding() {
        young.requestMineReleased()
        lifecycleScope.launch {
            young.mineReleasedResult.collect{result->
                result.onError {
                    it.judgeType().showToast()
                    stopLoading()
                }.onSuccess {
                    dubReleaseList.adapter= with(speakAdapter){
                        changeData(it)
                        registerItemListener(itemListener)

                        //没有数据则隐藏编辑按钮
                        if (it.isEmpty()){
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.KOUYU_NODATA,""))
                        }
                        this
                    }
                    stopLoading()
                }.onLoading {
                    startLoading()
                }
            }
        }
    }

    private val itemListener = object:KouyuItemClickListener{
        override fun onLongClick(item: MineReleaseItem) {
            //暂时关闭此操作，使用其他方式
            AlertDialog.Builder(requireActivity())
                .setTitle("删除配音")
                .setMessage("是否删除此配音数据？")
                .setCancelable(false)
                .setPositiveButton("确定",object:DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()

                        deleteKouyu(item.id)
                    }
                }).setNegativeButton("取消",null)
                .show()
        }

        override fun onClick(item: MineReleaseItem) {
            //设置数据
            GlobalMemory.speakingItem.name=item.Title_cn
            GlobalMemory.speakingItem.voa_id = item.TopicId.toString()

            val rankData = GsonUtils.toJson(item.toYoungRankItem())
            requireActivity().startActivity<OtherOneVideoActivity> {
                putExtra(ExtraKeysFactory.youngRankItem,rankData)
            }
        }
    }

    //删除选中的配音
    private lateinit var deleteKouyuDis:Disposable
    private fun deleteKouyu(id:Int){
        startLoading()

        RetrofitUtil.getInstance().deleteKouyuData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<KouyuDeleteBean>{
                override fun onSubscribe(d: Disposable) {
                    deleteKouyuDis = d
                }

                override fun onError(e: Throwable) {
                    ToastFactory.showShort(requireActivity(),"删除配音异常，请重试")
                }

                override fun onComplete() {
                    stopLoading()
                }

                override fun onNext(bean: KouyuDeleteBean) {
                    if (bean.message == "OK"){
                        young.requestMineReleased()
                    }else{
                        ToastFactory.showShort(requireActivity(),"删除配音失败，请重试")
                    }
                }
            })
    }

    //加载弹窗
    private lateinit var loadingDialog:LoadingDialog

    private fun startLoading() {
        if (!::loadingDialog.isInitialized){
            loadingDialog = LoadingDialog(requireActivity())
            loadingDialog.create()
        }
        loadingDialog.show()
    }

    private fun stopLoading(){
        if (::loadingDialog.isInitialized){
            loadingDialog.dismiss()
        }
    }

    //开启或者关闭编辑操作
    public fun showEditStatus(isEdit:Boolean){
        speakAdapter.setEditStatus(isEdit)
    }

    //获取选中的数据
    public fun getSelectData():List<Int>{
        return speakAdapter.getSelectData()
    }

    //刷新数据
    public fun refreshData(){
        young.requestMineReleased()
    }

    //清除选中的数据
    public fun clearSelectData() {
        speakAdapter.clearSelectData()
    }

    override fun initEventBus(): Boolean {
        return true
    }

    //点赞回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event: RefreshEvent){
        if (event.type.equals(RefreshEvent.KOUYU_AGREE)){
            //点赞回调
            young.requestMineReleased()
        }
    }
}