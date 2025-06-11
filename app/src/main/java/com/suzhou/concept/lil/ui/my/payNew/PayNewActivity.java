package com.suzhou.concept.lil.ui.my.payNew;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.imooclib.IMooc;
import com.suzhou.concept.BuildConfig;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.other.UseInstructionsActivity;
import com.suzhou.concept.databinding.ActivityPayNewBinding;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.User_info;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.ui.my.walletList.RewardMarkActivity;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.LibRxTimer;
import com.suzhou.concept.lil.util.ToastUtil;
import com.suzhou.concept.lil.view.NoScrollLinearLayoutManager;
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog;
import com.suzhou.concept.utils.ExpandKt;
import com.suzhou.concept.utils.ExtraKeysFactory;
import com.suzhou.concept.utils.GlobalMemory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 新的支付界面(正在使用)
 */
public class PayNewActivity extends BaseViewBindingActivity<ActivityPayNewBinding> implements PayNewView{
    //类型
    public static final int PayType_vip = 0;
    public static final int PayType_iyuIcon = 1;
    public static final int PayType_moc = 2;

    //参数
    private int payType;//购买类型
    private String amount;//数量/月份
    private String price;//价格(元)
    private String subject;//购买信息(简要)
    private String productId;//购买的id
    private String body;//购买信息(全)
    private long deduction;//抵扣金额(分)

    //支付类型
    private static final String pay_aliPay = "pay_aliPay";
    private static final String pay_wxPay = "pay_wxPay";

    //适配器
    private PayNewAdapter adapter;
    //数据
    private PayNewPresenter presenter;

    public static void start(Context context,int payType,String amount,String price,String subject,String productId){
        Intent intent = new Intent();
        intent.setClass(context,PayNewActivity.class);
        intent.putExtra(ExtraKeysFactory.payType,payType);//类型
        intent.putExtra(ExtraKeysFactory.amount,amount);//数量
        intent.putExtra(ExtraKeysFactory.payPrice,price);//价格
        intent.putExtra(ExtraKeysFactory.productId,productId);//会员、爱语币、课程id
        intent.putExtra(ExtraKeysFactory.subject,subject);//类型名称、课程名称
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        payType = getIntent().getIntExtra(ExtraKeysFactory.payType,PayType_vip);
        amount = getIntent().getStringExtra(ExtraKeysFactory.amount);
        price = getIntent().getStringExtra(ExtraKeysFactory.payPrice);
//        if (BuildConfig.DEBUG){
//            price = "0.02";
//        }
        subject = getIntent().getStringExtra(ExtraKeysFactory.subject);
        productId = getIntent().getStringExtra(ExtraKeysFactory.productId);

        presenter = new PayNewPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initData();
        initClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    /**********************************初始化*****************************/
    private void initToolbar(){
        binding.toolbar.standardLeft.setImageResource(R.drawable.left);
        binding.toolbar.standardLeft.setOnClickListener(v->{
            finish();
        });
        binding.toolbar.standardTitle.setText("支付");
    }

    private void initData(){
        binding.userName.setText(GlobalMemory.INSTANCE.getUserInfo().getUsername());
        //根据类型显示
        switch (payType){
            case PayType_vip:
                body = "花费"+price+"元购买"+subject+amount+"个月";
                break;
            case PayType_iyuIcon:
                body = "花费"+price+"元购买爱语币"+amount+"个";
                break;
            case PayType_moc:
                body = subject;
                break;
            default:
                body = "花费"+price+"元购买其他类型产品";
                break;
        }
        binding.orderDesc.setText(body);
        binding.orderPrice.setText(price+"元");

        //设置会员的服务协议
        binding.vipAgreement.setText(setVipAgreement());
        binding.vipAgreement.setMovementMethod(new LinkMovementMethod());

        //显示抵扣操作
        showDeduction();
    }

    private void initList(){
        adapter = new PayNewAdapter(this,new ArrayList<>());
        binding.recyclerView.setLayoutManager(new NoScrollLinearLayoutManager(this,false));
        binding.recyclerView.setAdapter(adapter);

        List<Pair<String, Pair<Integer, Pair<String, String>>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(pay_aliPay, new Pair<>(R.drawable.ic_pay_alipay, new Pair<>("支付宝支付", "推荐有支付宝账号的用户使用"))));
        adapter.refreshData(pairList);
    }

    private void initClick(){
        binding.verifyPay.setOnClickListener(v->{
            //获取类型显示
            String showPayType = adapter.getPayMethod();
            showPay(showPayType);
        });
    }

    /********************************方法******************************/
    //设置会员服务协议的样式
    private SpannableStringBuilder setVipAgreement() {
        String vipStr = "《会员服务协议》";
        String showMsg = "点击支付即代表您已充分阅读并同意" + vipStr;

        SpannableStringBuilder spanStr = new SpannableStringBuilder();
        spanStr.append(showMsg);
        //会员服务协议
        int termIndex = showMsg.indexOf(vipStr);
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(PayNewActivity.this, UseInstructionsActivity.class);
                String url = ExpandKt.getVipAgreement(PayNewActivity.this);
                intent.putExtra(ExtraKeysFactory.webUrlOut, url);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        }, termIndex, termIndex + vipStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanStr;
    }

    /*********************************回调*****************************/
    @Override
    public void showPayLinkStatus(boolean isError, String showMsg) {
        stopLoading();
        showPayLinkStatusDialog(showMsg);
    }

    @Override
    public void showPayFinishStatus(boolean isFinish, String payStatus) {
        stopLoading();
        showPayStatusDialog(isFinish,payStatus);
    }

    /***********************************其他操作******************************/
    //进行支付操作
    private void showPay(String payMethod) {
        switch (payMethod) {
            case pay_aliPay:
                //支付宝支付
                startLoading("正在进行支付～");
                presenter.getAliPayOrderLink(amount, productId, subject, body, price,deduction);
                break;
            /*case pay_wxPay:
                //微信支付
                startLoading("正在进行支付～");
                presenter.getWXPayOrderLink(amount, productId, subject, body, price);
                break;*/
            default:
                ToastUtil.showToast(this, "暂无当前支付方式");
                break;
        }
    }

    //加载弹窗
    private LoadingMsgDialog loadingDialog;

    private void startLoading(String showMsg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingMsgDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    //支付链接状态弹窗
    private void showPayLinkStatusDialog(String statusMsg) {
        new AlertDialog.Builder(this)
                .setMessage(statusMsg)
                .setPositiveButton("确定", null)
                .create().show();
    }

    //支付状态弹窗
    private void showPayStatusDialog(boolean isFinish, String showMsg) {
        if (!isFinish) {
            showPayLinkStatusDialog(showMsg);
            return;
        }

        if (!TextUtils.isEmpty(showMsg)){
            showPayFailDialog(showMsg);
            return;
        }

        showPayUnknownDialog();
    }

    //支付失败状态弹窗
    private void showPayFailDialog(String showMsg) {
        new AlertDialog.Builder(this)
                .setMessage(showMsg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false)
                .create().show();
    }

    //支付中间状态弹窗
    private void showPayUnknownDialog() {
        new AlertDialog.Builder(this)
                .setMessage("是否支付完成\n\n(如会员、课程未生效，请退出后重新登录)")
                .setPositiveButton("已完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //刷新微课信息
                        if (payType == PayType_moc){
                            IMooc.notifyCoursePurchased();
                        }
                        getUserInfo();
                    }
                }).setNegativeButton("未完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //刷新微课信息
                if (payType == PayType_moc){
                    IMooc.notifyCoursePurchased();
                }
                getUserInfo();
            }
        }).setCancelable(false)
                .create().show();
    }

    //获取用户信息
    private void getUserInfo(){
        startLoading("正在更新用户信息～");

        //这里延迟1s后刷新用户信息，便于服务端合并数据
        LibRxTimer.getInstance().timerInMain("delayTime", 1000L, new LibRxTimer.RxActionListener() {
            @Override
            public void onAction(long number) {
                LibRxTimer.getInstance().cancelTimer("delayTime");

                //加载用户信息
                RetrofitUtil.getInstance().getUserInfo()
                        .subscribe(new Observer<User_info>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(User_info userInfo) {
                                //刷新数据并推出
                                PayUserInfoHelpUtil.saveUserinfo(GlobalMemory.INSTANCE.getUserInfo().getUid(),userInfo);
                                stopLoading();
                                //回调信息
                                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.USER_VIP,null));

                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //结束并推出
                                stopLoading();
                                finish();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    /*****************************抵扣功能操作**************************/
    //显示抵扣内容
    private void showDeduction() {
        //如果当前为调试模式，则直接不使用抵扣操作
        double showPrice = Double.parseDouble(price);
        if (BuildConfig.DEBUG && showPrice < 2) {
            //因为价格低于2块钱的话，抵扣钱数为0，无法抵扣
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //根据要求，爱语币暂时不增加抵扣功能，微课直购也不支持，仅支持会员
        if (payType != PayType_vip) {
            binding.deductionLayout.setVisibility(View.GONE);
            return;
        }

        //最大不超过价格的一半，数据要为整数
        //这里的GlobalMemory.INSTANCE.getUserInfo().getMoney()已经是分的表现形式
        String hasMoney = GlobalMemory.INSTANCE.getUserInfo().getSelf().getMoney();
        long walletMoney = 0l;
        if (!TextUtils.isEmpty(hasMoney)){
            walletMoney = (long) (BigDecimalUtil.trans2Double(0, Double.parseDouble(hasMoney)));
        }
        //价格的一半(取整)
        long halfPrice = (long) (BigDecimalUtil.trans2Double(0, Double.parseDouble(price)) * 100L / 2);
        //判断这两个，然后选择其中一个抵扣
        long deductionPrice = 0;
        if (walletMoney > 0 && halfPrice > 0) {
            if (walletMoney > halfPrice) {
                deductionPrice = halfPrice;
            } else {
                deductionPrice = walletMoney;
            }
        }
        //显示可用钱包
        int showWalletMoney = (int) (deductionPrice / 100L);
        binding.userMoneyTv.setText("(可用金额:" + showWalletMoney + "元)");
        binding.userMoneyTv.setOnClickListener(v -> {
            RewardMarkActivity.start(this);
        });
        binding.deductionTv.setText("0元");
        binding.clickDeductionLayout.setOnClickListener(v -> {
            if (showWalletMoney <= 0) {
                ToastUtil.showToast(this, "您当前可用金额为：" + showWalletMoney + "元，不足以进行抵扣");
                return;
            }

            showDeductionDialog(showWalletMoney);
        });
    }

    private void showDeductionDialog(int deductionMoney) {
        //价格数据
        deductionMoney += 1;
        //设置显示数据
        String[] showMoneyArray = new String[deductionMoney];
        for (int i = 0; i < deductionMoney; i++) {
            showMoneyArray[i] = String.valueOf(i);
        }

        OptionPicker optionPicker = new OptionPicker(this, showMoneyArray);
        //设置标题
        optionPicker.setTitleText("请选择抵扣金额(元)");
        optionPicker.setTitleTextColor(getResources().getColor(R.color.black));
        optionPicker.setTitleTextSize(16);
        optionPicker.setTopLineColor(getResources().getColor(R.color.gray));
        //设置按钮
        optionPicker.setSubmitTextSize(16);
        optionPicker.setSubmitTextColor(getResources().getColor(R.color.colorPrimary));
        optionPicker.setCancelTextSize(16);
        optionPicker.setCancelTextColor(getResources().getColor(R.color.colorPrimary));
        //设置item
        optionPicker.setTextSize(20);
        optionPicker.setTextColor(getResources().getColor(R.color.black));
        optionPicker.setDividerColor(getResources().getColor(R.color.gray));
        //设置回调
        optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int which, String s) {
                long showDeduction = Long.parseLong(showMoneyArray[which]);
                binding.deductionTv.setText(showDeduction + "元");
                //设置抵扣数据
                deduction = showDeduction * 100L;
            }
        });
        optionPicker.show();
    }
}
