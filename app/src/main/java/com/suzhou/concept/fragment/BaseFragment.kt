package com.suzhou.concept.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.suzhou.concept.R
import com.suzhou.concept.utils.BaseBinding
import com.suzhou.concept.utils.OnLoadDialogListener
import com.suzhou.concept.viewmodel.AdvertiseViewModel
import com.suzhou.concept.viewmodel.ConceptViewModel
import com.suzhou.concept.viewmodel.EvaluationViewModel
import com.suzhou.concept.viewmodel.ExerciseViewModel
import com.suzhou.concept.viewmodel.UserActionViewModel
import com.suzhou.concept.viewmodel.WordViewModel
import com.suzhou.concept.viewmodel.YoungViewModel
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
苏州爱语吧科技有限公司
 */

abstract class BaseFragment<VB:ViewDataBinding> :Fragment() , BaseBinding<VB> {
    protected lateinit var bind:VB
    protected val conceptViewModel by lazy { ViewModelProvider(requireActivity())[ConceptViewModel::class.java] }
    protected val userAction by lazy { ViewModelProvider(requireActivity())[UserActionViewModel::class.java] }
    protected val wordViewModel by lazy { ViewModelProvider(requireActivity())[WordViewModel::class.java] }
    protected val evaluation by lazy { ViewModelProvider(requireActivity())[EvaluationViewModel::class.java] }
    protected val young by lazy { ViewModelProvider(requireActivity())[YoungViewModel::class.java] }
    protected val adModel by lazy { ViewModelProvider(requireActivity())[AdvertiseViewModel::class.java] }
    protected val exercise by lazy { ViewModelProvider(requireActivity())[ExerciseViewModel::class.java] }
    private lateinit var loadDialog: AlertDialog
    private var eventBusEnabled=false
    private lateinit var standardTitle:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind=getViewBinding(inflater, container)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (initEventBus()){
            EventBus.getDefault().register(this)
        }
        initView()
        bind.initBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bind.isInitialized){
            bind.unbind()
        }
        if (initEventBus()){
            EventBus.getDefault().unregister(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VB: ViewBinding> Any.getViewBinding(inflater: LayoutInflater, container: ViewGroup?):VB{
        val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return inflate.invoke(null, inflater, container, false) as VB
    }

    /*@Suppress("UNCHECKED_CAST")
    private fun <VB: ViewBinding> Any.getViewBinding(inflater: LayoutInflater):VB{
        val clz: Class<*> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VB>
        val method = clz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as VB
    }*/

    open fun initView(){}

    fun setTitleText(title:String, rightFlag:Boolean=false,rightMethod:()->Unit={}){
        standardTitle=bind.root.findViewById<TextView>(R.id.standard_title)

        //去掉新概念和新概念英语几个字
        var showTitle = title
        if (showTitle.startsWith("新概念英语")){
            showTitle = showTitle.replace("新概念英语","");
        }

        if (showTitle.startsWith("新概念")){
            showTitle = showTitle.replace("新概念","");
        }

        standardTitle.text = showTitle

        //设置右侧样式
        if (rightFlag){
            bind.root.findViewById<ImageView>(R.id.standard_right).apply {
//                if (this@BaseFragment is BreakThroughFragment){
//                    setBackgroundResource(R.drawable.another_menu)
//                }else{
//                    setBackgroundResource(R.drawable.menu)
//                }
                setBackgroundResource(R.drawable.menu_new)
                setOnClickListener { rightMethod() }
            }
        }
    }

    fun changeTitle(desc:String){
        if (::standardTitle.isInitialized){
            standardTitle.text=desc
        }
    }


    /**
     * 利用kotlin泛型实例化来调用当前Fragment依附的Activity的loadingDialog
     * */
    inline fun  <reified T : OnLoadDialogListener> showActivityLoad(){
        if (requireActivity() is T){
            (requireActivity() as T).showLoad()
        }
    }

    inline fun  <reified T : OnLoadDialogListener> dismissActivityLoad(){
        if (requireActivity() is T){
            (requireActivity() as T).dismissLoad()
        }
    }

    /**
     * 只有出现@Subscribe注解的类里才注册EventBus
     */
    open fun initEventBus():Boolean=false

}