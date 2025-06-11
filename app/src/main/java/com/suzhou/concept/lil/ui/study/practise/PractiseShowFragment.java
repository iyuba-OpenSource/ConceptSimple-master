package com.suzhou.concept.lil.ui.study.practise;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.jn.yyz.practise.PractiseInit;
import com.jn.yyz.practise.fragment.PractiseFragment;
import com.suzhou.concept.R;
import com.suzhou.concept.activity.dollar.MemberCentreActivity;
import com.suzhou.concept.activity.user.LoginActivity;
import com.suzhou.concept.databinding.FragmentExerciseNewXdBinding;
import com.suzhou.concept.lil.data.library.StrLibrary;
import com.suzhou.concept.lil.mvp.ui.BaseViewBindingFragment;
import com.suzhou.concept.utils.GlobalMemory;

/**
 * 新版练习题界面
 */
public class PractiseShowFragment extends BaseViewBindingFragment<FragmentExerciseNewXdBinding> {

    public static PractiseShowFragment getInstance(String type, int voaId, int position){
        PractiseShowFragment fragment = new PractiseShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,type);
        bundle.putInt(StrLibrary.voaId,voaId);
        bundle.putInt(StrLibrary.position,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClick();
        initFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initClick(){
        binding.startExercise.setOnClickListener(v->{
            if (!GlobalMemory.INSTANCE.isLogin()){
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                return;
            }

            //当前位置(第一个免费，后续付费)
            int position = getArguments().getInt(StrLibrary.position,0);

            //判断vip用户
            if (position>0 && !GlobalMemory.INSTANCE.getUserInfo().isVip()){
                new AlertDialog.Builder(requireActivity())
                        .setTitle("会员购买")
                        .setMessage("非会员仅能练习第一课的内容，会员无限制。是否开通会员使用?")
                        .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(requireActivity(), MemberCentreActivity.class));
                            }
                        }).setNegativeButton("暂不使用",null)
                        .setCancelable(false)
                        .create().show();
                return;
            }

            PractiseInit.setUid(GlobalMemory.INSTANCE.getUserInfo().getUid());
            binding.startLayout.setVisibility(View.GONE);
        });
    }

    private void initFragment(){
        String type = getArguments().getString(StrLibrary.type);
        int voaId = getArguments().getInt(StrLibrary.voaId);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PractiseFragment practiseFragment = PractiseFragment.newInstance(false,false,"练习题",type,0,String.valueOf(voaId),PractiseFragment.page_exerciseOther);
        transaction.add(R.id.container,practiseFragment).show(practiseFragment).commitNowAllowingStateLoss();
    }
}
