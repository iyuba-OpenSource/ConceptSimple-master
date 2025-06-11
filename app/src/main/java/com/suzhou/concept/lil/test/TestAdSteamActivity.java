package com.suzhou.concept.lil.test;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.ad.adblocker.AdBlocker;
import com.suzhou.concept.R;
import com.suzhou.concept.databinding.ItemWordBinding;
import com.suzhou.concept.databinding.LayoutRefreshListBinding;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingActivity;
import com.suzhou.concept.lil.ui.ad.util.show.template.AdTemplateShowManager;
import com.suzhou.concept.lil.ui.ad.util.show.template.AdTemplateViewBean;
import com.suzhou.concept.lil.ui.ad.util.show.template.OnAdTemplateShowListener;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

public class TestAdSteamActivity extends BaseViewBindingActivity<LayoutRefreshListBinding> {

    private TestAdapter testAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showTest();
        refreshTemplateAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showTest(){
        List<String> showList = new ArrayList<>();
        for (int i = 0; i < 70; i++) {
            showList.add("这里显示文本--"+i);
        }

        testAdapter = new TestAdapter(this,showList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(testAdapter);
    }

    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

        private Context context;
        private List<String> list;

        public TestAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemWordBinding itemWordBinding = ItemWordBinding.inflate(LayoutInflater.from(context),parent,false);
            return new TestHolder(itemWordBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull TestHolder holder, int position) {
            if (holder==null){
                return;
            }

            String showText = list.get(position);
            holder.test1.setText(showText);
            holder.test2.setText("文本显示-"+position);
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }

        class TestHolder extends RecyclerView.ViewHolder{

            private TextView test1;
            private TextView test2;

            public TestHolder(ItemWordBinding binding) {
                super(binding.getRoot());

                test1 = binding.unitView;
                test2 = binding.progressView;
            }
        }
    }

    /*****************************设置新的信息流广告************************/
    //当前信息流广告的key
    private String adTemplateKey = TestAdSteamActivity.class.getName();
    //模版广告数据
    private AdTemplateViewBean templateViewBean = null;
    //显示广告
    private void showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = new AdTemplateViewBean(R.layout.item_ad_mix, R.id.template_container, R.id.ad_whole_body, R.id.native_main_image, R.id.native_title, binding.recyclerView, testAdapter, new OnAdTemplateShowListener() {
                @Override
                public void onLoadFinishAd() {

                }

                @Override
                public void onAdShow(String showAdMsg) {

                }

                @Override
                public void onAdClick() {

                }
            });
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean);
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey,this);
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private void refreshTemplateAd(){
        if (!AdBlocker.getInstance().shouldBlockAd() && !GlobalMemory.INSTANCE.getUserInfo().isVip()) {
            showTemplateAd();
        } else {
            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey);
        }
    }
}
