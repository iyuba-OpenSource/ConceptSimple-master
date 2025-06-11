package com.jn.yyz.practise.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.activity.PractiseActivity;
import com.jn.yyz.practise.adapter.PhoneticAdapter;
import com.jn.yyz.practise.databinding.FragmentPhoneticBinding;
import com.jn.yyz.practise.event.PLoginEventbus;
import com.jn.yyz.practise.model.bean.PronBean;
import com.jn.yyz.practise.util.DpUtil;
import com.jn.yyz.practise.util.GridSpacingItemDecoration;
import com.jn.yyz.practise.vm.PhoneticViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 音标首页
 */
public class PhoneticFragment extends Fragment {

    private FragmentPhoneticBinding binding;

    private PhoneticViewModel phoneticViewModel;

    private PhoneticAdapter vowelPhoneticAdapter;

    private PhoneticAdapter consonantPhoneticAdapter;

    private boolean showBack = false;

    public PhoneticFragment() {
    }

    public static PhoneticFragment newInstance(boolean showBack) {

        PhoneticFragment fragment = new PhoneticFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("SHOW_BACK", showBack);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneticViewModel = new ViewModelProvider(this).get(PhoneticViewModel.class);
        Bundle bundle = getArguments();
        if (bundle != null) {

            showBack = bundle.getBoolean("SHOW_BACK");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPhoneticBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.toolbarTvTitle.setText("音标");
        if (showBack) {

            binding.toolbar.toolbarIvBack.setVisibility(View.VISIBLE);
        } else {

            binding.toolbar.toolbarIvBack.setVisibility(View.GONE);
        }
        binding.phoneticTvStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PractiseConstant.UID.equals("0")) {

                    EventBus.getDefault().post(new PLoginEventbus());
                    return;
                }

                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("PRON", Context.MODE_PRIVATE);
                int id = sharedPreferences.getInt("MAX_ID", 0);
                PractiseActivity.startActivity(requireActivity(), true, "pron", id, "0",PractiseFragment.page_exercisePron);
            }
        });


        GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(3, DpUtil.dpToPx(view.getContext(), 10), false);
        binding.phoneticRvVowel.addItemDecoration(gridSpacingItemDecoration);
        GridSpacingItemDecoration gridSpacingItemDecoration2 = new GridSpacingItemDecoration(3, DpUtil.dpToPx(view.getContext(), 10), false);
        binding.phoneticRvConsonant.addItemDecoration(gridSpacingItemDecoration2);

        phoneticViewModel.getPronBeanMLD()
                .observe(getViewLifecycleOwner(), new Observer<PronBean>() {
                    @Override
                    public void onChanged(PronBean pronBean) {

                        //元音
                        List<PronBean.VowelDTO> pronBeanList = pronBean.getVowel();
                        vowelPhoneticAdapter = new PhoneticAdapter(pronBeanList);
                        binding.phoneticRvVowel.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                        binding.phoneticRvVowel.setAdapter(vowelPhoneticAdapter);
                        //辅音
                        List<PronBean.VowelDTO> pronBeanList2 = pronBean.getConsonant();
                        consonantPhoneticAdapter = new PhoneticAdapter(pronBeanList2);
                        binding.phoneticRvConsonant.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                        binding.phoneticRvConsonant.setAdapter(consonantPhoneticAdapter);
                    }
                });

        phoneticViewModel.requestPronNew();
    }
}