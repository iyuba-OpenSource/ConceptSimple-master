package com.suzhou.concept.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import coil.load
import com.suzhou.concept.R
import com.suzhou.concept.utils.BaseBinding
import com.suzhou.concept.utils.OnLoadDialogListener
import com.suzhou.concept.viewmodel.*
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
苏州爱语吧科技有限公司
 */
abstract class BaseActivity<VB:ViewDataBinding> :AppCompatActivity() , BaseBinding<VB>, OnLoadDialogListener {
    protected val conceptViewModel by lazy { ViewModelProvider(this)[ConceptViewModel::class.java] }
    protected val userAction by lazy { ViewModelProvider(this)[UserActionViewModel::class.java] }
    protected val wordViewModel by lazy { ViewModelProvider(this)[WordViewModel::class.java] }
    protected val evaluation by lazy { ViewModelProvider(this)[EvaluationViewModel::class.java] }
    protected val young by lazy { ViewModelProvider(this)[YoungViewModel::class.java] }
    protected val adModel by lazy { ViewModelProvider(this)[AdvertiseViewModel::class.java] }
    protected val exercise by lazy { ViewModelProvider(this)[ExerciseViewModel::class.java] }
    private lateinit var standardTitle:TextView
    private lateinit var standardRight:ImageView
    lateinit var standardRightText:TextView
    private lateinit var dialog: AlertDialog
    // internal 被定义为 “只有这个模块可以调用”
    internal val binding:VB by lazy(mode = LazyThreadSafetyMode.NONE) { getViewBinding(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (initEventBus()){
            EventBus.getDefault().register(this)
        }

        initLoading()
        binding.initBinding()
        initView()
    }
    @Suppress("UNCHECKED_CAST")
    private fun <VB: ViewBinding> Any.getViewBinding(inflater: LayoutInflater):VB{
        val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return  inflate.invoke(null, inflater) as VB
    }

    /**
     * 只有出现@Subscribe注解的类里才注册EventBus
     * */
    open fun initEventBus()=false

    open fun initView(){}

    private fun initLoading(){
        val inflaterView =View.inflate(this,R.layout.loading_dialog,null)
        var textView = inflaterView.findViewById<TextView>(R.id.showText)
        textView.text = "正在加载所需的内容～"
        dialog= with(AlertDialog.Builder(this)){
            setTitle("提示")
            setView(inflaterView)
            setCancelable(false)
            create()
        }
    }

    override fun showLoad() {
        dialog.apply {
            if (!isShowing) show()
        }
    }

    override fun dismissLoad() {
        dialog.apply {
            if (isShowing) dismiss()
        }
    }

    fun setTitleText(title:String,leftFlag:Boolean=true){
        standardTitle=findViewById(R.id.standard_title)
        standardTitle.text=title
        if (leftFlag){
            val standardLeft=findViewById<ImageView>(R.id.standard_left)
            standardLeft.setBackgroundResource(R.drawable.left)
            standardLeft.setOnClickListener { finish() }
        }
    }

    //设置编辑按钮
    fun initRightText(){
        standardRight = findViewById(R.id.standard_right)
        standardRightText = findViewById(R.id.standard_right_text)
    }

    fun installStandRight(
        @DrawableRes
        draw:Int=R.drawable.menu,
        rightMethod:(v:View)->Unit){
      val standardRight=findViewById<ImageView>(R.id.standard_right)
      standardRight.apply {
          load(draw)
          setOnClickListener { rightMethod(it) }
      }
    }

    fun installStandRight(
        @DrawableRes
        draw:Int=R.drawable.menu,
        listener:Consumer<Unit>){
       installStandRight(draw){
           listener.accept(null)
       }
    }

    fun changeTitle(title:String){
        standardTitle.text=title
    }

    override fun onDestroy() {
        super.onDestroy()
        if (initEventBus()){
            EventBus.getDefault().unregister(this)
        }
    }
}