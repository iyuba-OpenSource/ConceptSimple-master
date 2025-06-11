package com.jn.yyz.practise.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.activity.PractiseActivity;
import com.jn.yyz.practise.adapter.HomeAdapter;
import com.jn.yyz.practise.databinding.FragmentHomeBinding;
import com.jn.yyz.practise.event.BookChooseEventsbus;
import com.jn.yyz.practise.event.TestFinishEventbus;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.vm.HomeViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 练习首页-进度
 *
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private PopupWindow popupWindow;
    private HomeAdapter homeAdapter;

    private String type;
    private int bookId;

    private int[] powerBookId = new int[]{500, 501, 502, 503, 504, 505, 506};
    private int powerPos = 0;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String type, int bookId) {

        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        bundle.putInt("BOOK_ID", bookId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("TYPE");
            bookId = bundle.getInt("BOOK_ID");
        }
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeRv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.homeRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {

                    int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
                    if (firstItem<=0){
                        return;
                    }

                    List<HomeTestTitleBean.Unity> unityList = homeAdapter.getUnityList();
                    HomeTestTitleBean.Unity unity = unityList.get(firstItem);

                    binding.homeTvTitle.setText(String.format("第%s阶段，第%s单元", powerPos + 1, unity.getUnit()));
                    if (unity.getDesc() != null && !unity.getDesc().isEmpty()) {

                        binding.homeTvTitle2.setText(unity.getDesc());
                    }
                }
            }
        });
        //上传试题
        homeViewModel.getUploadTestBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<UploadTestBean>() {
                    @Override
                    public void onChanged(UploadTestBean uploadTestBean) {

                        if (uploadTestBean.getResult() == 200) {

                            homeViewModel.requestUpdateEXP(403, uploadTestBean.getId(), 25);
                        }
                    }
                });
        //获取积分
        homeViewModel.getExpIntBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExpBean>() {
                    @Override
                    public void onChanged(ExpBean expBean) {

                        if (expBean.getResult() == 200) {

                            if (homeAdapter != null) {

                                List<HomeTestTitleBean.Unity.Level> levelList = new ArrayList<>();
                                List<HomeTestTitleBean.Unity> unityList = homeAdapter.getUnityList();
                                for (int i = 0; i < unityList.size(); i++) {

                                    HomeTestTitleBean.Unity unity = unityList.get(i);
                                    levelList.addAll(unity.getData());
                                }

                                //打开宝箱
                                int finPos = -1;
                                for (int i = 0; i < levelList.size(); i++) {

                                    HomeTestTitleBean.Unity.Level level = levelList.get(i);
                                    if (level.getVoaid() == expBean.getId() && level.getType().equals("box")) {

                                        level.setIsPass(1);//打开宝箱
                                        finPos = i;
                                        break;
                                    }
                                }

                                if (finPos != -1) {

                                    int nextPos = finPos + 1;
                                    if (nextPos < levelList.size()) {

                                        levelList.get(nextPos).setUnlock(true);
                                    }
                                    homeAdapter.notifyDataSetChanged();
                                    Toast.makeText(requireActivity(), "获取25积分", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

        homeViewModel.getHomeTestTitleBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<HomeTestTitleBean>() {
                    @Override
                    public void onChanged(HomeTestTitleBean homeTestTitleBean) {
                        if (homeTestTitleBean.getResult() == 200) {
                            showData(homeTestTitleBean);
                        }
                    }
                });

        loadData(type,bookId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 处理完成答题后，更新列表
     * @param testFinishEventbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TestFinishEventbus testFinishEventbus) {
        String id = testFinishEventbus.getId();
        if (homeAdapter == null) {
            return;
        }

        HomeTestTitleBean.Unity.Level findLevel = null;
        List<HomeTestTitleBean.Unity> unityList = homeAdapter.getUnityList();
        for (int i = 0; i < unityList.size(); i++) {

            HomeTestTitleBean.Unity unity = unityList.get(i);
            List<HomeTestTitleBean.Unity.Level> levelList = unity.getData();
            for (int j = 0; j < levelList.size(); j++) {

                HomeTestTitleBean.Unity.Level level = levelList.get(j);
                if (id.equalsIgnoreCase(String.valueOf(level.getVoaid()))) {
                    findLevel = level;
                    break;
                }
            }

            if (findLevel != null) {
                break;
            }
        }

        if (findLevel != null) {
            findLevel.setIsPass(1);

            //解锁下一个位置
            List<HomeTestTitleBean.Unity.Level> levelList = new ArrayList<>();
            for (int i = 0; i < unityList.size(); i++) {

                HomeTestTitleBean.Unity unity = unityList.get(i);
                levelList.addAll(unity.getData());
            }

            HomeTestTitleBean.Unity.Level unlockLevel = null;
            for (int i = 0; i < levelList.size(); i++) {

                HomeTestTitleBean.Unity.Level level = levelList.get(i);
                if (level.getIsPass() == 0) {

                    unlockLevel = level;
                    break;
                }
            }
            if (unlockLevel != null) {

                unlockLevel.setUnlock(true);
            }
            homeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选书后加载新的数据
     * @param bookChooseEventsbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookChooseEventsbus bookChooseEventsbus){
        String showType = bookChooseEventsbus.getType();
        int bookId = bookChooseEventsbus.getBookId();

        loadData(showType,bookId);
    }

    private void showPopup(View itemView, HomeTestTitleBean.Unity.Level level) {

       /* if (popupWindow != null) {

            popupWindow.dismiss();
            popupWindow = null;
            return;
        }*/

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.popup_point, null);
        TextView point_tv_title = view.findViewById(R.id.point_tv_title);
        TextView point_tv_index = view.findViewById(R.id.point_tv_index);
        TextView point_tv_status = view.findViewById(R.id.point_tv_status);
        LinearLayout point_ll_title = view.findViewById(R.id.point_ll_title);
        point_tv_title.setText(level.getTitle());
        point_tv_index.setText(String.format("第%s课", level.getVoaid() / 340000));

        if (level.isUnlock() || level.getIsPass() == 1) {

            point_tv_title.setTextColor(Color.WHITE);
            point_tv_index.setTextColor(Color.WHITE);
            point_tv_status.setBackgroundColor(Color.WHITE);
            point_ll_title.setBackgroundResource(R.drawable.shape_rctg_popup_green);
        } else {
            point_ll_title.setBackgroundResource(R.drawable.shape_rctg_popup_gray);
        }

        point_tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (level.isUnlock() || level.getIsPass() == 1) {
                    PractiseActivity.startActivity(requireActivity(), true, type, 0, level.getVoaid() + "", PractiseFragment.page_exerciseLine);
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
            }
        });

        popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(itemView, 0, 0, Gravity.CENTER_HORIZONTAL);
    }

    private void showData(HomeTestTitleBean homeTestTitleBean) {
        List<HomeTestTitleBean.Unity> delaUnityList = new ArrayList<>();
        List<HomeTestTitleBean.Unity> unityList = homeTestTitleBean.getData();
        if (unityList.isEmpty()){
            binding.homeTvTitle.setText("当前书籍暂无数据");
            binding.homeTvTitle2.setText("暂无数据");
            return;
        }

        //默认设置
        unityList.get(0).getData().get(0).setUnlock(true);

        for (int i = 0; i < unityList.size(); i++) {

            HomeTestTitleBean.Unity unity = unityList.get(i);
            int size = unity.getSize();
            if (size < 10) {
                unity.setBiasPos(0);
                unity.setShowTitle(true);
                delaUnityList.add(unity);
            } else {

                List<HomeTestTitleBean.Unity.Level> levelList = unity.getData();

                HomeTestTitleBean.Unity oneUnity = new HomeTestTitleBean.Unity();
                oneUnity.setBiasPos(0);
                oneUnity.setShowTitle(true);
                oneUnity.setUnit(unity.getUnit());
                oneUnity.setSize(5);
                oneUnity.setDesc(unity.getDesc());
                List<HomeTestTitleBean.Unity.Level> oneLevelList = new ArrayList<>();
                for (int j = 0; j < 5 && j < levelList.size(); j++) {

                    HomeTestTitleBean.Unity.Level level = levelList.get(j);
                    oneLevelList.add(level);
                }
                oneUnity.setData(oneLevelList);
                delaUnityList.add(oneUnity);
                //two
                HomeTestTitleBean.Unity twoUnity = new HomeTestTitleBean.Unity();
                twoUnity.setBiasPos(1);
                twoUnity.setShowTitle(false);
                twoUnity.setUnit(unity.getUnit());
                twoUnity.setSize(5);
                twoUnity.setDesc(unity.getDesc());
                List<HomeTestTitleBean.Unity.Level> twoLevelList = new ArrayList<>();
                for (int j = 5; j < levelList.size(); j++) {

                    HomeTestTitleBean.Unity.Level level = levelList.get(j);
                    twoLevelList.add(level);
                }
                twoUnity.setData(twoLevelList);
                delaUnityList.add(twoUnity);
            }
        }

        //存储Level
        List<HomeTestTitleBean.Unity.Level> levelList = new ArrayList<>();
        for (int i = 0; i < unityList.size(); i++) {

            levelList.addAll(unityList.get(i).getData());
        }
        //将可以闯的关卡，置位解锁
        HomeTestTitleBean.Unity.Level passLevel = null;
        for (int i = 0; i < levelList.size(); i++) {

            HomeTestTitleBean.Unity.Level level = levelList.get(i);

            if (level.getIsPass() == 0) {

                passLevel = level;
                break;
            }
        }
        if (passLevel != null) {

            passLevel.setUnlock(true);
        }

        homeAdapter = new HomeAdapter(delaUnityList);
        binding.homeRv.setAdapter(homeAdapter);
        homeAdapter.setCallback(new HomeAdapter.Callback() {
            @Override
            public void getLevel(View itemView, HomeTestTitleBean.Unity.Level level,int unitIndex) {
                if (level.getType().equals("lesson")) {//正常关卡
                    showPopup(itemView, level);
                } else if (level.getType().equals("box")) {//宝箱
                    //先判断上边是否符合要求(这里需要判断上边的都需要开启后才能)
                    //分为两部分：1：获取当前宝箱的上面的一段的课程内容，2：判断上面的数据是否都为true
                    List<HomeTestTitleBean.Unity.Level> levelList = new ArrayList<>();
                    for (int i = 0; i < delaUnityList.size(); i++) {
                        HomeTestTitleBean.Unity tempData = delaUnityList.get(i);
                        if (tempData.getUnit() == unitIndex){
                            levelList = tempData.getData();
                        }
                    }

                    //判断是否全部通过
                    boolean isAllPass = false;
                    for (int i = 0; i < levelList.size(); i++) {
                        if (levelList.get(i).getIsPass()==0){
                            isAllPass = false;
                            break;
                        }
                    }

                    if (isAllPass){
//                        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
//                        simpleDateFormat.applyPattern("yyyy-MM-dd");
//
//                        String dataStr = simpleDateFormat.format(new Date());
//
//                        String signStr = MD5Util.MD5("iyubaExam" + PractiseConstant.UID + PractiseConstant.APPID + level.getVoaid() + dataStr);
//                        String endTime = System.currentTimeMillis() / 1000 + "";
//                        String startTime = String.valueOf(Integer.parseInt(endTime) - 50);
//
//                        //testList
//                        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
//                        String testTime = simpleDateFormat.format(new Date());
//                        List<TestBean> testBeanList = new ArrayList<>();
//                        TestBean testBean = new TestBean(0, 0, 1, "", testTime, level.getVoaid() + "");
//                        testBeanList.add(testBean);
//
//                        homeViewModel.requestUpdateEnglishTestRecord(1, level.getVoaid() + "", type, level.getType()
//                                , signStr, startTime, endTime, testBeanList);

                        Toast.makeText(requireActivity(),"开启宝箱",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(requireActivity(),"请完成当前课程全部的关卡后开启宝箱",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadData(String type,int bookId){
        //获取习题列表数据
//        String sign = MD5Util.MD5("iyuba"+"0"+String.valueOf(bookId)+type+ DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD));
        homeViewModel.requestExamTitleList(bookId, type, "", PractiseConstant.UID);

        if (type.equals("power")) {//音标

            for (int i = 0; i < powerBookId.length; i++) {

                if (bookId == powerBookId[i]) {
                    powerPos = i;
                    break;
                }
            }
            binding.homeTvTitle.setText(String.format("第%s阶段，第1单元", powerPos + 1));

        } else if (type.equals("concept")) {//新概念
            binding.homeTvTitle.setText(String.format("第%s阶段，第1单元", bookId));
        }
    }
}