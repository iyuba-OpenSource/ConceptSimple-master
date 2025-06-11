package com.jn.yyz.practise.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.activity.PractiseActivity;
import com.jn.yyz.practise.adapter.ListAdapter;
import com.jn.yyz.practise.databinding.FragmentHomeBinding;
import com.jn.yyz.practise.entity.ListShowBean;
import com.jn.yyz.practise.entity.TestBean;
import com.jn.yyz.practise.event.BookChooseEventsbus;
import com.jn.yyz.practise.event.TestFinishEventbus;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.util.MD5Util;
import com.jn.yyz.practise.vm.HomeViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 练习界面-列表
 */
public class ListFragment extends Fragment {

    private HomeViewModel listModel;
    private FragmentHomeBinding binding;

    private ListAdapter listAdapter;

    private String type;
    private int bookId;

    private int[] powerBookId = new int[]{500, 501, 502, 503, 504, 505, 506};
    private int powerPos = 0;

    public static ListFragment newInstance(String type, int bookId) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        bundle.putInt("BOOK_ID", bookId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("TYPE");
            bookId = bundle.getInt("BOOK_ID");
        }
        listModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: 2025/3/27 根据展雪梅的要求，关闭上边的显示
        binding.topLayout.setVisibility(View.GONE);
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
                    if (firstItem<0){
                        return;
                    }
                    List<ListShowBean> showList = listAdapter.getShowList();
                    ListShowBean showBean = showList.get(firstItem);

                    binding.homeTvTitle.setText(String.format("第%s阶段，第%s单元", powerPos + 1, showBean.getUnit()));
                    if (showBean.getUnitTitle() != null && !showBean.getUnitTitle().isEmpty()) {
                        binding.homeTvTitle2.setText(showBean.getUnitTitle());
                    }
                }
            }
        });

        //上传试题
        listModel.getUploadTestBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<UploadTestBean>() {
                    @Override
                    public void onChanged(UploadTestBean uploadTestBean) {
                        if (uploadTestBean.getResult() == 200) {
                            listModel.requestUpdateEXP(403, uploadTestBean.getId(), 25);
                        }
                    }
                });
        //获取积分
        listModel.getExpIntBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExpBean>() {
                    @Override
                    public void onChanged(ExpBean expBean) {

                        if (expBean.getResult() == 200) {
                            loadData(type,bookId);
                        }
                    }
                });
        //列表数据回调
        listModel.getHomeTestTitleBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<HomeTestTitleBean>() {
                    @Override
                    public void onChanged(HomeTestTitleBean titleBean) {
                        if (titleBean.getResult() == 200) {
                            //转换数据并显示
                            List<ListShowBean> showList = transDataToShow(titleBean.getData());
                            if (showList.size()<=0){
                                binding.homeTvTitle.setText("当前书籍暂无数据");
                                binding.homeTvTitle2.setText("暂无数据");
                            }

                            listAdapter.refreshData(showList);
                        }else {
                            Toast.makeText(requireActivity(),"获取列表数据失败，请重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //初始化
        initView();
        //加载数据
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
        type = testFinishEventbus.getType();
        bookId = TextUtils.isEmpty(testFinishEventbus.getId())?1:Integer.parseInt(testFinishEventbus.getId());
        loadData(type,bookId);
    }

    /**
     * 选书后加载新的数据
     * @param bookChooseEventsbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookChooseEventsbus bookChooseEventsbus){
        type = bookChooseEventsbus.getType();
        bookId = bookChooseEventsbus.getBookId();
        loadData(type,bookId);
    }


    private void loadData(String type,int bookId){
        //获取习题列表数据
//        String sign = MD5Util.MD5("iyuba"+"0"+String.valueOf(bookId)+type+ DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD));
        listModel.requestExamTitleList(bookId, type, "", PractiseConstant.UID);

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

    private void initView(){
        listAdapter = new ListAdapter(getActivity(),new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.homeRv.setLayoutManager(manager);
        binding.homeRv.setAdapter(listAdapter);
        binding.homeRv.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onClick(boolean isCanExercise,ListShowBean showBean) {
                if (!isCanExercise){
                    Toast.makeText(getActivity(),"请先完成上一个的练习",Toast.LENGTH_SHORT).show();
                    return;
                }

                int userId = TextUtils.isEmpty(PractiseConstant.UID)?0:Integer.parseInt(PractiseConstant.UID);
                if (userId<=0){
                    Toast.makeText(getActivity(),"请登录后使用",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (showBean.getType().equals("lesson")){
                    //课程
                    PractiseActivity.startActivity(requireActivity(), true, type, 0, showBean.getVoaId() + "", PractiseFragment.page_exerciseList);
                }else if (showBean.getType().equals("box")){
                    //宝箱
                    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                    simpleDateFormat.applyPattern("yyyy-MM-dd");

                    String dataStr = simpleDateFormat.format(new Date());

                    String signStr = MD5Util.MD5("iyubaExam" + PractiseConstant.UID + PractiseConstant.APPID + showBean.getVoaId() + dataStr);
                    String endTime = System.currentTimeMillis() / 1000 + "";
                    String startTime = String.valueOf(Integer.parseInt(endTime) - 50);

                    //testList
                    simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                    String testTime = simpleDateFormat.format(new Date());
                    List<TestBean> testBeanList = new ArrayList<>();
                    TestBean testBean = new TestBean(0, 0, 1, "", testTime, showBean.getVoaId() + "");
                    testBeanList.add(testBean);

                    listModel.requestUpdateEnglishTestRecord(1, showBean.getVoaId() + "", type, showBean.getType()
                            , signStr, startTime, endTime, testBeanList);
                }
            }
        });
    }

    private List<ListShowBean> transDataToShow(List<HomeTestTitleBean.Unity> list){
        List<ListShowBean> showList = new ArrayList<>();
        if (list==null || list.size()==0){
            return showList;
        }

        for (int i = 0; i < list.size(); i++) {
            HomeTestTitleBean.Unity unityData = list.get(i);
            for (int j = 0; j < unityData.getData().size(); j++) {
                HomeTestTitleBean.Unity.Level levelData = unityData.getData().get(j);
                // TODO: 2025/3/27 李涛和展雪梅确认：列表中不显示宝箱，但是限制顺序练习
                if (levelData.getType().equals("box")){
                    continue;
                }

                showList.add(new ListShowBean(
                        unityData.getUnit(),
                        unityData.getDesc(),
                        levelData.getTitle(),
                        levelData.getIsPass() == 1,
                        levelData.getType(),
                        levelData.getVoaid()
                ));
            }
        }

        return showList;
    }
}