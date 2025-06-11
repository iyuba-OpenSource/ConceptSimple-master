package com.jn.yyz.practise.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.Ink;
import com.google.mlkit.vision.digitalink.RecognitionResult;
import com.jn.yyz.practise.PractiseConstant;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.adapter.ChooseAdapter;
import com.jn.yyz.practise.adapter.FillInAdapter;
import com.jn.yyz.practise.adapter.PairAdapter;
import com.jn.yyz.practise.adapter.PicAdapter;
import com.jn.yyz.practise.adapter.SoundFillInAdapter;
import com.jn.yyz.practise.adapter.SoundPairAdapter;
import com.jn.yyz.practise.adapter.TranslateAdapter;
import com.jn.yyz.practise.adapter.TybcChooseAdapter;
import com.jn.yyz.practise.adapter.WordChooseAdapter;
import com.jn.yyz.practise.adapter.WordFillInAdapter;
import com.jn.yyz.practise.adapter.WordFillInChooseAdapter;
import com.jn.yyz.practise.databinding.FragmentPractiseBinding;
import com.jn.yyz.practise.entity.FillIn;
import com.jn.yyz.practise.entity.Pair;
import com.jn.yyz.practise.entity.SoundFillIn;
import com.jn.yyz.practise.entity.Translate;
import com.jn.yyz.practise.entity.WordFillIn;
import com.jn.yyz.practise.event.PLoginEventbus;
import com.jn.yyz.practise.event.TestFinishEventbus;
import com.jn.yyz.practise.model.bean.EvalBean;
import com.jn.yyz.practise.model.bean.ExamBean;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.util.DpUtil;
import com.jn.yyz.practise.util.GridSpacingItemDecoration;
import com.jn.yyz.practise.util.LineItemDecoration;
import com.jn.yyz.practise.util.MD5Util;
import com.jn.yyz.practise.util.OptionUtil;
import com.jn.yyz.practise.util.customize.HandwritingView;
import com.jn.yyz.practise.util.popup.LoadingPopup;
import com.jn.yyz.practise.util.span.RoundBackgroundSpan;
import com.jn.yyz.practise.util.span.WordClickSpan;
import com.jn.yyz.practise.vm.PractiseViewModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 练习界面
 */
public class PractiseFragment extends Fragment {

    //这里针对操作下，根据来源界面处理下操作
    public static final String page_exerciseNote = "exerciseNote";//错题本界面
    public static final String page_exerciseList = "exerciseList";//列表界面
    public static final String page_exerciseLine = "exerciseLine";//连线界面
    public static final String page_exercisePron = "exercisePron";//音标界面
    public static final String page_exerciseOther = "exerciseOther";

    private FragmentPractiseBinding fragmentPractiseBinding;

    private PractiseViewModel practiseViewModel;

    private int pos = 0;

    private ChooseAdapter chooseAdapter;

    private boolean showBack = false;

    private List<ExamBean.DataDTO> dataDTOList;

    private MediaPlayer mediaPlayer;

    private PicAdapter picAdapter;

    private TranslateAdapter answerTranslateAdapter;
    private TranslateAdapter chhooseTranslateAdapter;

    //配对
    private PairAdapter leftPairAdapter;
    private PairAdapter rightPairAdapter;

    //音频配对
    private SoundPairAdapter leftSoundPairAdapter;
    private SoundPairAdapter rightSoundPairAdapter;
    //填空题
    private FillInAdapter fillInAdapter;

    private SoundFillInAdapter soundFillInAdapter;

    private boolean isRecord = false;

    private MediaRecorder mediaRecorder;
    private String mp3Path;

    //加载popup
    private LoadingPopup loadingPopup;
    private View view209;

    private View view210;

    //204  播放的是否是左边的
    private boolean isLeftSound = false;

    //211
    private WordFillInChooseAdapter translate211Adapter;
    private boolean isCheck211 = true;
    private WordFillInAdapter wordFillInAdapter;

    //212
    private FillInAdapter fillIn212Adapter;

    //213
    private WordChooseAdapter wordChooseAdapter;

    //214
    private TybcChooseAdapter tybcChooseAdapter;

    //215
    private View view215;

    //216
    private View view216;

    //平移动画
    private TranslateAnimation addTranslateAnimation;

    //需要接收的参数
    private String type = "";
    private int maxId = 0;
    private String lessonId = "0";
    private String title;
    //判断界面来源
    private String pageType = page_exerciseOther;
    /**
     * 做题开始时间
     */
    private String beginTime;

    private String endTime;

    /**
     * 显示toolbar
     */
    private boolean showToolbar = false;
    //获取积分
    private ExpBean expBean;

    private ActivityResultLauncher<String> recordResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                    if (o) {

                    } else {

                    }
                }
            });

    /**
     *
     * @param showBack  是否显示返回
     * @param type 默认smallvideo，试题类型，有power、concept、smallvideo
     * @param maxId lessonType是pron时必填；其他类型不填，maxId为上一次请求列表中最后一条数据的id值，如果没有则为0
     * @param lessonId 课程号/小视频编号，音标练习题可以默认填0
     * @return
     */
    public static PractiseFragment newInstance(boolean showBack, boolean showToolbar, String title,
                                               String type, int maxId, String lessonId, String pageType) {

        PractiseFragment practiseFragment = new PractiseFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("SHOW_BACK", showBack);
        bundle.putString("TITLE", title);
        bundle.putString("TYPE", type);
        bundle.putInt("MAX_ID", maxId);
        bundle.putString("LESSON_ID", lessonId);
        bundle.putBoolean("SHOW_TOOLBAR", showToolbar);
        bundle.putString("PAGE_TYPE", pageType);
        practiseFragment.setArguments(bundle);
        return practiseFragment;
    }


    public PractiseFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            showBack = bundle.getBoolean("SHOW_BACK", false);
            type = bundle.getString("TYPE");
            maxId = bundle.getInt("MAX_ID");
            lessonId = bundle.getString("LESSON_ID");
            title = bundle.getString("TITLE");
            showToolbar = bundle.getBoolean("SHOW_TOOLBAR", false);
            pageType = bundle.getString("PAGE_TYPE");
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
            }
        });
        beginTime = System.currentTimeMillis() / 1000 + "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {

            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentPractiseBinding = FragmentPractiseBinding.inflate(inflater, container, false);
        return fragmentPractiseBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentPractiseBinding.toolbar.toolbarTvTitle.setText(title);

        if (showToolbar) {//是否显示toolbar
            if (!showBack) {
                fragmentPractiseBinding.toolbar.toolbarIvBack.setVisibility(View.GONE);
            } else {
                fragmentPractiseBinding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean isF = isFinish();
                        if (isF) {
                            requireActivity().finish();
                        } else {
                            Activity activity = requireActivity();

                            new AlertDialog.Builder(activity)
                                    .setMessage("等等，先别走！现在离开的话，你的进度就没了！")
                                    .setNegativeButton("继续努力", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                            requireActivity().finish();
                                        }
                                    }).create().show();
                        }
                    }
                });
            }
        } else {
            fragmentPractiseBinding.toolbar.getRoot().setVisibility(View.GONE);
        }
        practiseViewModel = new ViewModelProvider(this).get(PractiseViewModel.class);

        //网络异常等信息
        practiseViewModel.getIntegerMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {

                        if (integer == 30001) {//请求练习数据，网络异常

                            fragmentPractiseBinding.practiseLlError.setVisibility(View.VISIBLE);
                            fragmentPractiseBinding.practiseLlTest.setVisibility(View.GONE);
                            fragmentPractiseBinding.practiseTvError.setText("请求超时，点击重试");
                            fragmentPractiseBinding.practiseLlError.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    fragmentPractiseBinding.practiseLlError.setVisibility(View.GONE);
                                    practiseViewModel.requestExam(type, maxId, 0, 1, 20, lessonId, 1);
                                }
                            });
                        } else if (integer == 30002) {//评测异常

                            if (view209 != null) {

                                FrameLayout test209_fl_record = view209.findViewById(R.id.test209_fl_record);
                                test209_fl_record.setVisibility(View.VISIBLE);
                                ImageView test209_iv_recording = view209.findViewById(R.id.test209_iv_recording);
                                test209_iv_recording.setVisibility(View.GONE);
                            }
                            if (view215 != null) {

                                FrameLayout test215_fl_zbrecord = view215.findViewById(R.id.test215_fl_zbrecord);
                                test215_fl_zbrecord.setVisibility(View.VISIBLE);
                                ImageView test215_iv_recording = view215.findViewById(R.id.test215_iv_recording);
                                test215_iv_recording.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        //练习试题数据
        practiseViewModel.getExamBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExamBean>() {
                    @Override
                    public void onChanged(ExamBean examBean) {


                        if (examBean.getResult() == 200) {//成功获取数据

                            dataDTOList = examBean.getData();
                            if (dataDTOList != null && !dataDTOList.isEmpty() && pos < dataDTOList.size()) {

                                ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                                showTest(dataDTO);
                            }
                        } else if (examBean.getResult() == 0) {//没有数据

                            fragmentPractiseBinding.practiseLlError.setVisibility(View.VISIBLE);
                            fragmentPractiseBinding.practiseLlTest.setVisibility(View.GONE);
                        }
                    }
                });

        //错题本内容显示
        if (pageType.equals(page_exerciseNote)) {

            lessonId = "0";
            SharedPreferences wrongSp = requireActivity().getSharedPreferences("WRONG_LIST", MODE_PRIVATE);
            Gson gson = new Gson();
            Type type1 = new TypeToken<List<ExamBean.DataDTO>>(){}.getType();

            dataDTOList = gson.fromJson(wrongSp.getString("DATA", null), type1);
            if (dataDTOList != null && !dataDTOList.isEmpty() && pos < dataDTOList.size()) {
                ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                showTest(dataDTO);
            }
        } else {
            practiseViewModel.requestExam(type, maxId, Integer.parseInt(PractiseConstant.UID), 1, 20, lessonId, 1);
        }

        //获取评测数据
        practiseViewModel.getEvalBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<EvalBean>() {
                    @Override
                    public void onChanged(EvalBean evalBean) {

                        if (loadingPopup != null) {

                            loadingPopup.dismiss();
                        }

                        ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                        if (evalBean.getData().getScores() >= 80) {

                            dataDTO.setCorrectFlag(1);
                            dataDTO.setUserAnswer(evalBean.getData().getScores() + "");
                        } else {

                            dataDTO.setCorrectFlag(0);
                            dataDTO.setUserAnswer(evalBean.getData().getScores() + "");
                        }

                        if (view209 != null) {

                            mediaPlayer.setOnCompletionListener(null);
                            FrameLayout test209_fl_record = view209.findViewById(R.id.test209_fl_record);
                            test209_fl_record.setVisibility(View.VISIBLE);
                            ImageView test209_iv_recording = view209.findViewById(R.id.test209_iv_recording);
                            test209_iv_recording.setVisibility(View.GONE);
                            FrameLayout test209_fl_content = view209.findViewById(R.id.test209_fl_content);
                            test209_fl_content.setEnabled(false);

                            view209 = null;
                            //检测按钮
                            if (evalBean.getData().getScores() >= 80) {

                                checkSuccess();
                            } else {

                                check203And204Error(null);
                            }
                        } else if (view215 != null) {

                            FrameLayout test215_fl_zbrecord = view215.findViewById(R.id.test215_fl_zbrecord);
                            test215_fl_zbrecord.setVisibility(View.VISIBLE);
                            ImageView test215_iv_recording = view215.findViewById(R.id.test215_iv_recording);
                            test215_iv_recording.setVisibility(View.GONE);
                            FrameLayout test215_fl_record = view215.findViewById(R.id.test215_fl_record);
                            test215_fl_record.setEnabled(false);

                            view215 = null;
                            //检测按钮
                            if (evalBean.getData().getScores() >= 80) {

                                checkSuccess();
                            } else {

                                check203And204Error(null);
                            }
                        }
                    }
                });
        //上传试题
        practiseViewModel.getUploadTestBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<UploadTestBean>() {
                    @Override
                    public void onChanged(UploadTestBean uploadTestBean) {
                        practiseViewModel.requestUpdateEXP(401, lessonId, dataDTOList);
                    }
                });
        //获取积分
        practiseViewModel.getExpBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExpBean>() {
                    @Override
                    public void onChanged(ExpBean expBean) {

                        if (expBean.getSrid() != 401) {
                            //显示单独积分显示界面
                            showSingleIntegralPage(expBean);
                        }
                    }
                });
        //获取积分
        fragmentPractiseBinding.practiseTvBack.setOnClickListener(v -> {
            practiseViewModel.requestUpdateEXP(402, lessonId, dataDTOList);
        });
        //继续
        fragmentPractiseBinding.practiseTvGoon.setOnClickListener(v -> {
            //刷新结果回调(将刷新回调放在这里)
            EventBus.getDefault().post(new TestFinishEventbus(type, lessonId));

            if (type.equalsIgnoreCase("pron")) {//如果是音标类型，点击返回按钮，返回上一页
                requireActivity().finish();
            } else if (type.equalsIgnoreCase("power")) {
                if (expBean != null && expBean.getScore() != 0) {
                    //积分数不等于0，就能进入下一关

                    //这里暂时不用，因为好像数据完成后不走这里，无法刷新数据
//                    EventBus.getDefault().post(new TestFinishEventbus(type, lessonId));
                    requireActivity().finish();
                } else {
                    requireActivity().finish();
                }
            } else {
                //这里如果是连线、列表和错题本，则直接退出即可
                if (pageType.equals(page_exerciseLine)
                        ||pageType.equals(page_exerciseList)
                        ||pageType.equals(page_exerciseNote)
                        ||pageType.equals(page_exercisePron)){
                    requireActivity().finish();
                }else {
                    pos = 0;
                    fragmentPractiseBinding.practiseLlQues.setVisibility(View.VISIBLE);
                    fragmentPractiseBinding.practiseLlComplete.setVisibility(View.GONE);
                    fragmentPractiseBinding.practiseLlJf.setVisibility(View.GONE);

                    ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                    showTest(dataDTO);
                }
            }
        });

        //检查
        fragmentPractiseBinding.practiseTvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PractiseConstant.UID.equals("0")) {
                    EventBus.getDefault().post(new PLoginEventbus());
                    return;
                }


                if (fragmentPractiseBinding.practiseTvCheck.getText().equals("继续")
                        || fragmentPractiseBinding.practiseTvCheck.getText().equals("知道了")) {

                    pos++;
                    if (pos < dataDTOList.size()) {//处理

                        ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                        showTest(dataDTO);
                    } else {

                        submitTest();

                        //显示积分混合界面
                        showIntegralMixPage();

                        ExamBean.DataDTO dataDTO = dataDTOList.get(dataDTOList.size() - 1);
                        SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("PRON", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("MAX_ID", dataDTO.getId()).apply();
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/complete.mp3");
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (dataDTOList != null && !dataDTOList.isEmpty() && pos < dataDTOList.size()) {

                    ExamBean.DataDTO dataDTO = dataDTOList.get(pos);
                    int testType = dataDTO.getTestType();
                    if (testType == 201) {
                        check201(dataDTO);
                    } else if (testType == 202) {
                        check202(dataDTO);
                    } else if (testType == 203) {
                        check203And204(dataDTO, answerTranslateAdapter);
                    } else if (testType == 204) {
                        check203And204(dataDTO, answerTranslateAdapter);
                    } else if (testType == 207) {
                        check207(dataDTO);
                    } else if (testType == 208) {
                        check208(dataDTO);
                    } else if (testType == 210) {
                        check210(dataDTO);
                    } else if (testType == 211) {
                        check211(dataDTO);
                    } else if (testType == 212) {
                        check212(dataDTO);
                    } else if (testType == 213) {
                        check213(dataDTO);
                    } else if (testType == 214) {
                        check214(dataDTO);
                    } else if (testType == 216) {
                        check216(dataDTO);
                    }
                }
            }
        });
    }


    /**
     * 显示问题
     * @param dataDTO
     */
    private void showTest(ExamBean.DataDTO dataDTO) {

        fragmentPractiseBinding.practisePbP.setMax(dataDTOList.size());
        fragmentPractiseBinding.practisePbP.setProgress(pos + 1);

        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        //做题时间
        dataDTO.setTestTime(simpleDateFormat.format(new Date()));

        int testType = dataDTO.getTestType();
        if (testType == 201) {

            test201(dataDTO);
        } else if (testType == 202) {

            test202(dataDTO);
        } else if (testType == 203) {

            test203(dataDTO);
        } else if (testType == 204) {

            test204(dataDTO);
        } else if (testType == 205) {

            test205(dataDTO);
        } else if (testType == 206) {

            test206(dataDTO);
        } else if (testType == 207) {

            test207(dataDTO);
        } else if (testType == 208) {

            test208(dataDTO);
        } else if (testType == 209) {

            test209(dataDTO);
        } else if (testType == 210) {

            test210(dataDTO);
        } else if (testType == 211) {

            test211(dataDTO);
        } else if (testType == 212) {

            test212(dataDTO);
        } else if (testType == 213) {

            test213(dataDTO);
        } else if (testType == 214) {

            test214(dataDTO);
        } else if (testType == 215) {

            test215(dataDTO);
        } else if (testType == 216) {

            test216(dataDTO);
        }
    }

    private String dealAnswer(String answer, ExamBean.DataDTO dataDTO) {

        String answerStr = "";
        if (answer.equalsIgnoreCase("a")) {

            answerStr = dataDTO.getAnswer1();
        } else if (answer.equalsIgnoreCase("b")) {

            answerStr = dataDTO.getAnswer2();
        } else if (answer.equalsIgnoreCase("c")) {

            answerStr = dataDTO.getAnswer3();
        } else if (answer.equalsIgnoreCase("d")) {

            answerStr = dataDTO.getAnswer4();
        }
        return answerStr;
    }

    private void check212(ExamBean.DataDTO dataDTO) {

        fillIn212Adapter.setInput(false);
        fillIn212Adapter.notifyDataSetChanged();

        FillIn useFillIn = null;
        List<FillIn> inList = fillIn212Adapter.getFillInList();

        Pattern pattern = Pattern.compile("_+");
        for (int i = 0; i < inList.size(); i++) {

            FillIn fillIn = inList.get(i);
            Matcher matcher = pattern.matcher(fillIn.getWord());
            if (matcher.find() && fillIn.getUsetInput() != null) {

                useFillIn = fillIn;
                break;
            }
        }

        if (useFillIn != null) {

            String userInput = useFillIn.getUsetInput();
            if (userInput.equalsIgnoreCase(dataDTO.getAnswer())) {

                dataDTO.setCorrectFlag(1); //答题状态
                dataDTO.setUserAnswer(dataDTO.getAnswer()); //用户的答案
                checkSuccess();
            } else {

                dataDTO.setCorrectFlag(0);//答题状态
                dataDTO.setUserAnswer(userInput);//用户的答案
                check203And204Error(dataDTO);
            }
        }
    }


    /**
     * 下载墨水识别的库
     */
    private void downloadModel() {
        try {
            DigitalInkRecognitionModelIdentifier dirmi = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en");

            DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(dirmi).build();
            RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();

            remoteModelManager
                    .download(model, new DownloadConditions.Builder().build())
                    .addOnSuccessListener(aVoid -> {
                        Log.i("xd---------------", "Model downloaded");
                    })
                    .addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {

                                                  Log.i("xd---------------", "Model downloaded");
                                              }
                                          }
                    );
        } catch (MlKitException e) {
            throw new RuntimeException(e);
        }
    }

    private void check216(ExamBean.DataDTO dataDTO) {

        TextView test216_tv_reset = view216.findViewById(R.id.test216_tv_reset);
        TextView test216_tv_identify = view216.findViewById(R.id.test216_tv_identify);

        test216_tv_reset.setEnabled(false);
        test216_tv_identify.setEnabled(false);

        if (dataDTO.getAnswer().equalsIgnoreCase(dataDTO.getUserAnswer())) {

            checkSuccess();
        } else {

            check203And204Error(dataDTO);
        }
    }

    private void test216(ExamBean.DataDTO dataDTO) {

        resetDefault("写出听到的内容");

        view216 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_216, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view216);

        testAnimation(view216);

        downloadModel();

        ImageView test216_iv_sound = view216.findViewById(R.id.test216_iv_sound);
        HandwritingView test216_hv_hand = view216.findViewById(R.id.test216_hv_hand);
        TextView test216_tv_reset = view216.findViewById(R.id.test216_tv_reset);
        TextView test216_tv_identify = view216.findViewById(R.id.test216_tv_identify);
        TextView test216_tv_content = view216.findViewById(R.id.test216_tv_content);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test216_iv_sound.setImageResource(R.mipmap.icon_laba);
            }
        });
        test216_tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test216_tv_content.setText("识别结果：");

                test216_hv_hand.setEnabled(true);
                test216_hv_hand.clear();

                fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                fragmentPractiseBinding.practiseTvCheck.setText("检测");
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
            }
        });
        //识别
        test216_tv_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DigitalInkRecognitionModelIdentifier modelIdentifier = null;
                try {
                    modelIdentifier =
                            DigitalInkRecognitionModelIdentifier.fromLanguageTag("en");
                } catch (MlKitException e) {
                    // language tag failed to parse, handle error.
                }
                if (modelIdentifier == null) {

                    downloadModel();
                    return;
                }

                DigitalInkRecognitionModel model =
                        DigitalInkRecognitionModel.builder(modelIdentifier).build();

                DigitalInkRecognizer recognizer =
                        DigitalInkRecognition.getClient(
                                DigitalInkRecognizerOptions.builder(model).build());

                Ink ink = test216_hv_hand.getInk();
                if (ink != null) {

                    recognizer.recognize(ink)
                            .addOnSuccessListener(new OnSuccessListener<RecognitionResult>() {
                                @Override
                                public void onSuccess(RecognitionResult recognitionResult) {

                                    String content = recognitionResult.getCandidates().get(0).getText();
                                    Log.d("xd---------------", content);
                                    test216_tv_content.setText("识别结果：" + content);

                                    dataDTO.setUserAnswer(content);
                                    test216_hv_hand.setEnabled(false);

                                    //检查按钮
                                    fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("xd---------------", e.toString());

                                    fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                                    fragmentPractiseBinding.practiseTvCheck.setText("检测");
                                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
                                }
                            });
                }
            }
        });

        test216_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.reset();
                Glide.with(view216.getContext()).load(R.mipmap.gif_laba).placeholder(R.mipmap.icon_laba).into(test216_iv_sound);
                try {
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
                Glide.with(view216.getContext()).load(R.mipmap.gif_laba).into(test216_iv_sound);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void test215(ExamBean.DataDTO dataDTO) {

        resetDefault("选择听到的内容");

        isRecord = false;
        //view
        view215 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_215, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view215);

        testAnimation(view215);

        //图片
        ImageView test215_iv_pic = view215.findViewById(R.id.test215_iv_pic);
        Glide.with(view215.getContext()).load(dataDTO.getPic()).into(test215_iv_pic);
        //音频
        ImageView test215_iv_word = view215.findViewById(R.id.test215_iv_word);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test215_iv_word.setImageResource(R.mipmap.laba);
            }
        });

        test215_iv_word.setOnClickListener(v -> {
            Glide.with(view215.getContext()).load(R.mipmap.gif_laba2).placeholder(R.mipmap.laba).into(test215_iv_word);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //单词
        TextView test215_tv_word = view215.findViewById(R.id.test215_tv_word);
        TextView test215_tv_show = view215.findViewById(R.id.test215_tv_show);
        test215_tv_show.setOnClickListener(v -> {
            test215_tv_word.setText(dataDTO.getQuestion());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    test215_tv_word.setText("");
                }
            }, 2000);
        });

        //录音
        ImageView test215_iv_recording = view215.findViewById(R.id.test215_iv_recording);
        FrameLayout test215_fl_zbrecord = view215.findViewById(R.id.test215_fl_zbrecord);
        FrameLayout test215_fl_record = view215.findViewById(R.id.test215_fl_record);
        test215_fl_record.setOnClickListener(v -> {
            int pStatus = ActivityCompat.checkSelfPermission(view215.getContext(), Manifest.permission.RECORD_AUDIO);
            if (pStatus == PackageManager.PERMISSION_GRANTED) {//已经授权
                if (isRecord) {//正在录音
                    if (mediaRecorder != null) {

                        mediaRecorder.stop();
                        isRecord = false;

                        loadingPopup = new LoadingPopup();
                        loadingPopup.getPopup(requireActivity());
                        practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                , "0", "0", "0"
                                , dataDTO.getQuestion());
                    }
                } else {
                    isRecord = true;
                    test215_fl_zbrecord.setVisibility(View.GONE);
                    test215_iv_recording.setVisibility(View.VISIBLE);
                    Glide.with(test215_iv_recording.getContext()).load(R.drawable.wavy_line).into(test215_iv_recording);
                    //录音
                    record();
                }
            } else {
                //请求权限
                recordResultLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }

            //使用新的权限框架进行处理(前期不使用，后期使用)
            /*if (XXPermissions.isGranted(requireActivity(),Permission.RECORD_AUDIO)){
                if (isRecord) {//正在录音
                    if (mediaRecorder != null) {

                        mediaRecorder.stop();
                        isRecord = false;

                        loadingPopup = new LoadingPopup();
                        loadingPopup.getPopup(requireActivity());
                        practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                , "0", "0", "0"
                                , dataDTO.getQuestion());
                    }
                } else {
                    isRecord = true;
                    test215_fl_zbrecord.setVisibility(View.GONE);
                    test215_iv_recording.setVisibility(View.VISIBLE);
                    Glide.with(test215_iv_recording.getContext()).load(R.drawable.wavy_line).into(test215_iv_recording);
                    //录音
                    record();
                }
            }else {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("权限申请")
                        .setMessage("当前功能需要授权 录音/麦克风权限 后使用，是否授权？\n\n录音/麦克风权限：\n用于评测功能录音后进行评测")
                        .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                XXPermissions.with(requireActivity())
                                        .permission(Permission.RECORD_AUDIO)
                                        .request(new OnPermissionCallback() {
                                            @Override
                                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                                if (allGranted){
                                                    if (isRecord) {//正在录音
                                                        if (mediaRecorder != null) {

                                                            mediaRecorder.stop();
                                                            isRecord = false;

                                                            loadingPopup = new LoadingPopup();
                                                            loadingPopup.getPopup(requireActivity());
                                                            practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                                                    , "0", "0", "0"
                                                                    , dataDTO.getQuestion());
                                                        }
                                                    } else {
                                                        isRecord = true;
                                                        test215_fl_zbrecord.setVisibility(View.GONE);
                                                        test215_iv_recording.setVisibility(View.VISIBLE);
                                                        Glide.with(test215_iv_recording.getContext()).load(R.drawable.wavy_line).into(test215_iv_recording);
                                                        //录音
                                                        record();
                                                    }
                                                }else {
                                                    Toast.makeText(requireActivity(),"请授权录音/麦克风权限后使用",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                                Toast.makeText(requireActivity(),"请授权录音/麦克风权限后使用",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("取消", null)
                        .create().show();
            }*/
        });
    }

    private void check214(ExamBean.DataDTO dataDTO) {

        tybcChooseAdapter.setCheck(false);
        int position = tybcChooseAdapter.getPosition();
        if (position == 0 && dataDTO.getAnswer().equalsIgnoreCase("a")) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer("A");
            checkSuccess();
        } else if (position == 1 && dataDTO.getAnswer().equalsIgnoreCase("b")) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer("B");
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            if (position == 0) {

                dataDTO.setUserAnswer("A");
            } else if (position == 1) {

                dataDTO.setUserAnswer("B");
            }
            check203And204Error(dataDTO);
        }
    }

    private void test214(ExamBean.DataDTO dataDTO) {

        resetDefault("选择听到的内容");

        //view
        View view214 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_214, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view214);

        testAnimation(view214);

        ImageView test214_iv_top = view214.findViewById(R.id.test214_iv_top);
        ImageView test214_iv_bottom = view214.findViewById(R.id.test214_iv_bottom);
        RecyclerView test214_rv_choose = view214.findViewById(R.id.test214_rv_choose);
        test214_rv_choose.setLayoutManager(new LinearLayoutManager(view214.getContext()));
        LineItemDecoration lineItemDecoration = new LineItemDecoration(view214.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test214_rv_choose.addItemDecoration(lineItemDecoration);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test214_iv_top.setImageResource(R.mipmap.laba);
                test214_iv_bottom.setImageResource(R.mipmap.laba);
            }
        });

        String[] sounds = dataDTO.getSounds().split("\\++");

        test214_iv_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sounds.length > 0) {

                    Glide.with(view214.getContext()).load(R.mipmap.gif_laba2).placeholder(R.mipmap.laba).into(test214_iv_top);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(sounds[0]);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        test214_iv_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sounds.length >= 2) {

                    Glide.with(view214.getContext()).load(R.mipmap.gif_laba2).placeholder(R.mipmap.laba).into(test214_iv_bottom);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(sounds[1]);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        List<String> stringList = new ArrayList<>();
        if (dataDTO.getAnswer1() != null && !dataDTO.getAnswer1().isEmpty()) {

            stringList.add(dataDTO.getAnswer1());
        }

        if (dataDTO.getAnswer2() != null && !dataDTO.getAnswer2().isEmpty()) {

            stringList.add(dataDTO.getAnswer2());
        }
        tybcChooseAdapter = new TybcChooseAdapter(stringList);
        test214_rv_choose.setAdapter(tybcChooseAdapter);
        tybcChooseAdapter.setCallback(new TybcChooseAdapter.Callback() {
            @Override
            public void click(String data) {

                fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                fragmentPractiseBinding.practiseTvCheck.setText("检查");
            }
        });
    }

    private void check213(ExamBean.DataDTO dataDTO) {

        wordChooseAdapter.setCheck(false);
        int position = wordChooseAdapter.getPosition();
        boolean isCorrect = false;
        String answerChoose = null;
        if (position == 0 && dataDTO.getAnswer().equalsIgnoreCase("a")) {

            isCorrect = true;
            answerChoose = "A";
        } else if (position == 1 && dataDTO.getAnswer().equalsIgnoreCase("b")) {

            isCorrect = true;
            answerChoose = "B";
        } else {

            isCorrect = false;
            if (position == 0) {

                answerChoose = "A";
            } else if (position == 1) {

                answerChoose = "B";
            }
        }

        if (isCorrect) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(answerChoose);
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(answerChoose);
            check203And204Error(dataDTO);
        }
    }

    private void test213(ExamBean.DataDTO dataDTO) {

        resetDefault("选择听到的内容");

        //view
        View view213 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_213, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view213);

        testAnimation(view213);

        ImageView test213_iv_sound = view213.findViewById(R.id.test213_iv_sound);
        RecyclerView test213_rv_word = view213.findViewById(R.id.test213_rv_word);
        test213_rv_word.setLayoutManager(new GridLayoutManager(view213.getContext(), 2));
        GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(2, DpUtil.dpToPx(view213.getContext(), 15), false);
        test213_rv_word.addItemDecoration(gridSpacingItemDecoration);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test213_iv_sound.setImageResource(R.mipmap.icon_laba);
            }
        });


        test213_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(view213.getContext()).load(R.mipmap.gif_laba).placeholder(R.mipmap.icon_laba).into(test213_iv_sound);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        List<String> chooseStringList = new ArrayList<>();
        if (dataDTO.getAnswer1() != null && !dataDTO.getAnswer1().isEmpty()) {

            chooseStringList.add(dataDTO.getAnswer1());
        }
        if (dataDTO.getAnswer2() != null && !dataDTO.getAnswer2().isEmpty()) {

            chooseStringList.add(dataDTO.getAnswer2());
        }
        wordChooseAdapter = new WordChooseAdapter(chooseStringList);
        test213_rv_word.setAdapter(wordChooseAdapter);
        wordChooseAdapter.setCallback(new WordChooseAdapter.Callback() {
            @Override
            public void click(String data) {

                fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                fragmentPractiseBinding.practiseTvCheck.setText("检查");
            }
        });
        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
                Glide.with(view213.getContext()).load(R.mipmap.gif_laba).into(test213_iv_sound);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void test212(ExamBean.DataDTO dataDTO) {

        resetDefault("输入所缺单词");

        //view
        View view212 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_212, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view212);

        testAnimation(view212);

        ImageView test212_iv_sound = view212.findViewById(R.id.test212_iv_sound);
        RecyclerView test212_rv_word = view212.findViewById(R.id.test212_rv_word);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(view212.getContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        test212_rv_word.setLayoutManager(flexboxLayoutManager);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test212_iv_sound.setImageResource(R.mipmap.icon_laba);
            }
        });

        test212_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(view212.getContext()).load(R.mipmap.gif_laba).into(test212_iv_sound);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        String[] sentence = dataDTO.getQuestion().split("\\s+");
        List<FillIn> fillInList = new ArrayList<>();
        for (int i = 0; i < sentence.length; i++) {

            String string = sentence[i];
            fillInList.add(new FillIn(string));
        }
        fillIn212Adapter = new FillInAdapter(fillInList);
        test212_rv_word.setAdapter(fillIn212Adapter);
        fillIn212Adapter.setContentCallback(new FillInAdapter.ContentCallback() {
            @Override
            public void textChange() {

                FillIn useFillIn = null;
                List<FillIn> inList = fillIn212Adapter.getFillInList();

                Pattern pattern = Pattern.compile("_+");
                for (int i = 0; i < inList.size(); i++) {

                    FillIn fillIn = inList.get(i);
                    Matcher matcher = pattern.matcher(fillIn.getWord());
                    if (matcher.find() && fillIn.getUsetInput() != null) {

                        useFillIn = fillIn;
                        break;
                    }
                }
                if (useFillIn == null) {//检查 不可点击

                    fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                    fragmentPractiseBinding.practiseTvCheck.setText("检测");
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
                } else { //检查可点击

                    fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                }
            }
        });
        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
                Glide.with(view212.getContext()).load(R.mipmap.gif_laba).into(test212_iv_sound);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void check211(ExamBean.DataDTO dataDTO) {

        //禁止点击句子上的答案
        isCheck211 = false;
        translate211Adapter.setChoose(false);
        wordFillInAdapter.setChoose(false);

        List<WordFillIn> wordFillInList = wordFillInAdapter.getWordFillInList();
        StringBuilder uAnswerStringBuilder = new StringBuilder();
        for (int i = 0; i < wordFillInList.size(); i++) {

            WordFillIn wordFillIn = wordFillInList.get(i);
            Translate translate = wordFillIn.getTranslate();
            if (translate != null) {

                uAnswerStringBuilder.append(translate.getData());
                uAnswerStringBuilder.append("++");
            }
        }
        if (uAnswerStringBuilder.toString().endsWith("++")) {

            uAnswerStringBuilder.delete(uAnswerStringBuilder.toString().length() - 2, uAnswerStringBuilder.toString().length());
        }

        if (dataDTO.getAnswer().equalsIgnoreCase(uAnswerStringBuilder.toString())) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(dataDTO.getAnswer());
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(uAnswerStringBuilder.toString());
            check203And204Error(dataDTO);
        }
    }

    private void test211(ExamBean.DataDTO dataDTO) {

        resetDefault("选词填空");

        //view
        View view211 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_211, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view211);

        testAnimation(view211);

        RecyclerView test211_rv_word = view211.findViewById(R.id.test211_rv_word);

        //句子
        RecyclerView test211_rv_sentence = view211.findViewById(R.id.test211_rv_sentence);
        test211_rv_sentence.setLayoutManager(new FlexboxLayoutManager(view211.getContext()));
        String[] words = dataDTO.getQuestion().split("\\s+");
        List<String> wordlist = Arrays.asList(words);

        List<WordFillIn> wordFillInList = new ArrayList<>();
        for (int i = 0; i < wordlist.size(); i++) {

            String wordStr = wordlist.get(i);
            wordFillInList.add(new WordFillIn(wordStr, null));
        }
        wordFillInAdapter = new WordFillInAdapter(wordFillInList);
        test211_rv_sentence.setAdapter(wordFillInAdapter);
        wordFillInAdapter.setCallback(new WordFillInAdapter.Callback() {
            @Override
            public void click(WordFillIn wordFillIn, Translate translate) {

                if (translate != null) {

                    translate.setCheck(false);
                    animation211Bottom(test211_rv_sentence, test211_rv_word, wordFillIn);
                }
            }
        });

        //单词选项
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(view211.getContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        test211_rv_word.setLayoutManager(flexboxLayoutManager);

        String[] chooseStr = dataDTO.getAnswer1().split("\\++");

        List<Translate> translateList = new ArrayList<>();
        for (int i = 0; i < chooseStr.length; i++) {

            String cStr = chooseStr[i];
            translateList.add(new Translate(cStr));
        }
        translate211Adapter = new WordFillInChooseAdapter(translateList);
        test211_rv_word.setAdapter(translate211Adapter);
        translate211Adapter.setClickCallback(new WordFillInChooseAdapter.ClickCallback() {
            @Override
            public void click(Translate translate) {

                Pattern pattern = Pattern.compile("_+");
                if (addTranslateAnimation != null) {//

                    addTranslateAnimation.cancel();
                    addTranslateAnimation = null;

                    new Handler().postDelayed(() -> {

                        WordFillIn _WordFillIn = null;
                        List<WordFillIn> fillInList = wordFillInAdapter.getWordFillInList();
                        for (int i = 0; i < fillInList.size(); i++) {

                            WordFillIn wordFillIn = fillInList.get(i);
                            Matcher matcher = pattern.matcher(wordFillIn.getWord());
                            if (matcher.find() && wordFillIn.getTranslate() == null) {

                                _WordFillIn = wordFillIn;
                                break;
                            }
                        }
                        translate.setCheck(true);
                        if (_WordFillIn != null) {

                            _WordFillIn.setTranslate(translate);
                            animation211Top(test211_rv_sentence, test211_rv_word, translate, dataDTO.getQuestion());
                        }
                    }, 100);
                } else {

                    WordFillIn _WordFillIn = null;
                    List<WordFillIn> fillInList = wordFillInAdapter.getWordFillInList();
                    for (int i = 0; i < fillInList.size(); i++) {

                        WordFillIn wordFillIn = fillInList.get(i);
                        Matcher matcher = pattern.matcher(wordFillIn.getWord());
                        if (matcher.find() && wordFillIn.getTranslate() == null) {

                            _WordFillIn = wordFillIn;
                            break;
                        }
                    }
                    if (_WordFillIn != null) {

                        translate.setCheck(true);
                        _WordFillIn.setTranslate(translate);

                        animation211Top(test211_rv_sentence, test211_rv_word, translate, dataDTO.getQuestion());
                        //检查按钮
                        fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                        fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                        fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                        fragmentPractiseBinding.practiseTvCheck.setText("检查");
                    }
                }
            }
        });
    }

    /**
     * 向下
     */
    private void animation211Bottom(RecyclerView test211_rv_sentence, RecyclerView test211_rv_word, WordFillIn wordFillIn) {

        Translate translate = wordFillIn.getTranslate();
        //寻找对应的itemview
        WordFillInAdapter.WordFillInViewHolder _Holder = null;
        int count = test211_rv_sentence.getChildCount();
        for (int i = 0; i < count; i++) {

            WordFillInAdapter.WordFillInViewHolder wordFIVH = (WordFillInAdapter.WordFillInViewHolder) test211_rv_sentence.findViewHolderForAdapterPosition(i);
            if (wordFIVH != null && wordFIVH.getWordFillIn().getTranslate() == translate) {

                _Holder = wordFIVH;
                break;
            }
        }
        wordFillIn.setTranslate(null);

        if (_Holder != null) {

            //目的地ViewHolder
            WordFillInChooseAdapter.WordFIChViewHolder translateViewHolder = getViewHolder211(test211_rv_word, translate);
            if (translateViewHolder != null) {

                int[] location = new int[2];
                _Holder.wfi_tv_word.getLocationOnScreen(location);

                int[] chooseLocation = new int[2];
                translateViewHolder.itemView.getLocationOnScreen(chooseLocation);

                addTranslateAnimation = new TranslateAnimation(0, chooseLocation[0] - location[0], 0, chooseLocation[1] - location[1]);
                addTranslateAnimation.setDuration(350);
                addTranslateAnimation.setFillAfter(true);
                _Holder.wfi_tv_word.startAnimation(addTranslateAnimation);
                addTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        addTranslateAnimation = null;

                        wordFillInAdapter.notifyDataSetChanged();
                        translate211Adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    /**
     * 向上
     * @param test211_rv_sentence
     * @param test211_rv_word
     * @param translate
     * @param ques
     */
    private void animation211Top(RecyclerView test211_rv_sentence, RecyclerView test211_rv_word, Translate translate, String ques) {

        Pattern pattern = Pattern.compile("_+");
        WordFillInAdapter.WordFillInViewHolder _Holder = null;
        int count = test211_rv_sentence.getChildCount();
        for (int i = 0; i < count; i++) {

            WordFillInAdapter.WordFillInViewHolder holder = (WordFillInAdapter.WordFillInViewHolder) test211_rv_sentence.findViewHolderForAdapterPosition(i);
            if (holder != null) {

                WordFillIn wordFillIn = holder.getWordFillIn();
                Matcher matcher = pattern.matcher(wordFillIn.getWord());
                if (wordFillIn != null && matcher.find() && wordFillIn.getTranslate() == translate) {

                    _Holder = holder;
                    break;
                }
            }
        }

        if (_Holder != null) {

            int[] location = new int[2];
            _Holder.itemView.getLocationOnScreen(location);

            WordFillInChooseAdapter.WordFIChViewHolder translateViewHolder = getViewHolder211(test211_rv_word, translate);
            if (translateViewHolder != null) {

                int[] chooseLocation = new int[2];
                translateViewHolder.itemView.getLocationOnScreen(chooseLocation);

                addTranslateAnimation = new TranslateAnimation(0, location[0] - chooseLocation[0], 0, location[1] - chooseLocation[1]);
                addTranslateAnimation.setDuration(350);
                addTranslateAnimation.setFillAfter(true);
                translateViewHolder.itemView.startAnimation(addTranslateAnimation);
                addTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        addTranslateAnimation = null;

                        wordFillInAdapter.notifyDataSetChanged();
                        translate211Adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    /**
     * 获取指定translate的viewholder
     * @param test211_rv_word
     * @param translate
     * @return
     */
    private WordFillInChooseAdapter.WordFIChViewHolder getViewHolder211(RecyclerView test211_rv_word, Translate translate) {

        WordFillInChooseAdapter.WordFIChViewHolder translateViewHolder = null;
        int count = test211_rv_word.getChildCount();
        for (int i = 0; i < count; i++) {

            WordFillInChooseAdapter.WordFIChViewHolder tVHolder = (WordFillInChooseAdapter.WordFIChViewHolder) test211_rv_word.findViewHolderForAdapterPosition(i);
            if (tVHolder != null && tVHolder.getTranslate() == translate) {
                translateViewHolder = tVHolder;
                break;
            }
        }
        return translateViewHolder;
    }

    private SpannableStringBuilder dealString(String question
            , List<Translate> choosedList, TranslateAdapter translateAdapter
            , TextView test211_tv_sentence) {

        int index = 0;
        String[] sentences = question.split("\\s+");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < sentences.length; i++) {

            String senStr = sentences[i];
            if (senStr.matches("[_]+")) {

                if (index < choosedList.size()) {//小于选择单词的数量

                    Translate translate = choosedList.get(index);

                    if (i == 0) {

                        spannableStringBuilder.append(translate.getData());
                    } else {

                        spannableStringBuilder.append(" ").append(translate.getData());
                    }
                    int start = spannableStringBuilder.length() - translate.getData().length();
                    int end = spannableStringBuilder.length();

//                    spannableStringBuilder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new RoundBackgroundSpan(Color.parseColor("#55C9C9C9"), Color.BLACK, 10, 40), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    WordClickSpan wordClickSpan = new WordClickSpan(translate);
                    wordClickSpan.setClickCallback(new WordClickSpan.ClickCallback() {
                        @Override
                        public void click(Translate translate) {


                            if (isCheck211) {

                                //置位不选中，在选中列表中，
                                translate.setCheck(false);
                                choosedList.remove(translate);
                                translateAdapter.notifyDataSetChanged();

                                SpannableStringBuilder spannableStringBuilder = dealString(question, choosedList, translateAdapter, test211_tv_sentence);
                                test211_tv_sentence.setText(spannableStringBuilder);

                                if (choosedList.isEmpty()) {//选择list，不为空，检测按钮可用

                                    fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                                    fragmentPractiseBinding.practiseTvCheck.setText("检测");
                                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
                                } else {

                                    //检查按钮
                                    fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                                }
                            }
                        }
                    });
                    spannableStringBuilder.setSpan(wordClickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    index++;
                } else {

                    if (i == 0) {

                        spannableStringBuilder.append(senStr);
                    } else {

                        spannableStringBuilder.append(" ").append(senStr);
                    }
                }
            } else {

                if (i == 0) {

                    spannableStringBuilder.append(senStr);
                } else {

                    spannableStringBuilder.append(" ").append(senStr);
                }
            }
        }
        return spannableStringBuilder;
    }


    private void check210(ExamBean.DataDTO dataDTO) {

        EditText test210_et_input = view210.findViewById(R.id.test210_et_input);
        test210_et_input.setEnabled(false);
        String content = test210_et_input.getText().toString();
        if (content.equalsIgnoreCase(dataDTO.getAnswer())) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(content);
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(content);
            check203And204Error(dataDTO);
        }
    }

    private void test210(ExamBean.DataDTO dataDTO) {

        resetDefault("翻译单词");

        //view
        view210 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_210, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view210);

        testAnimation(view210);

        ImageView test210_iv_pic = view210.findViewById(R.id.test210_iv_pic);
        EditText test210_et_input = view210.findViewById(R.id.test210_et_input);
        TextView test210_iv_ques = view210.findViewById(R.id.test210_iv_ques);

        test210_iv_ques.setText(dataDTO.getQuestion());
        Glide.with(view210).load(dataDTO.getPic()).into(test210_iv_pic);
        test210_et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() != 0) {

                    //检查按钮
                    fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                } else {

                    fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                    fragmentPractiseBinding.practiseTvCheck.setText("检测");
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
                }
            }
        });
    }

    private void test209(ExamBean.DataDTO dataDTO) {

        resetDefault("朗读下面的句子");

        //view
        view209 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_209, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view209);

        testAnimation(view209);

        ImageView test209_iv_pic = view209.findViewById(R.id.test209_iv_pic);
        ImageView test209_iv_sound = view209.findViewById(R.id.test209_iv_sound);
        TextView test209_tv_text = view209.findViewById(R.id.test209_tv_text);
        FrameLayout test209_fl_record = view209.findViewById(R.id.test209_fl_record);
        ImageView test209_iv_recording = view209.findViewById(R.id.test209_iv_recording);
        FrameLayout test209_fl_content = view209.findViewById(R.id.test209_fl_content);


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test209_iv_sound.setImageResource(R.mipmap.laba);

                String jpgUrl = dataDTO.getPic().replace(".gif", ".jpg");
                Glide.with(view209.getContext()).load(jpgUrl).into(test209_iv_pic);
            }
        });

        //文字
        test209_tv_text.setText(dataDTO.getQuestion());
        //图片
        Glide.with(view209.getContext()).load(dataDTO.getPic()).into(test209_iv_pic);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(view209.getContext()).load(dataDTO.getPic()).into(test209_iv_pic);
                Glide.with(view209.getContext()).load(R.mipmap.gif_laba2).into(test209_iv_sound);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        test209_iv_sound.setOnClickListener(onClickListener);
        test209_tv_text.setOnClickListener(onClickListener);

        test209_fl_content.setOnClickListener(v -> {
            int pStatus = ActivityCompat.checkSelfPermission(view209.getContext(), Manifest.permission.RECORD_AUDIO);
            if (pStatus == PackageManager.PERMISSION_GRANTED) {//已经授权
                if (isRecord) {//正在录音
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        isRecord = false;

                        loadingPopup = new LoadingPopup();
                        loadingPopup.getPopup(requireActivity());
                        practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                , "0", "0", "0"
                                , dataDTO.getQuestion());
                    }
                } else {
                    isRecord = true;
                    test209_iv_recording.setVisibility(View.VISIBLE);
                    test209_fl_record.setVisibility(View.GONE);
                    Glide.with(test209_iv_recording.getContext()).load(R.drawable.wavy_line).into(test209_iv_recording);

                    record();
                }
            } else {//请求权限
                recordResultLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }

            //使用新的权限框架进行处理(前期不适用，后期使用)
            /*if (XXPermissions.isGranted(requireActivity(),Permission.RECORD_AUDIO)){
                if (isRecord) {//正在录音
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        isRecord = false;

                        loadingPopup = new LoadingPopup();
                        loadingPopup.getPopup(requireActivity());
                        practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                , "0", "0", "0"
                                , dataDTO.getQuestion());
                    }
                } else {

                    isRecord = true;
                    test209_iv_recording.setVisibility(View.VISIBLE);
                    test209_fl_record.setVisibility(View.GONE);
                    Glide.with(test209_iv_recording.getContext()).load(R.drawable.wavy_line).into(test209_iv_recording);

                    record();
                }
            }else {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("权限申请")
                        .setMessage("当前功能需要授权 录音/麦克风权限 后使用，是否授权？\n\n录音/麦克风权限：\n用于评测功能录音后进行评测")
                        .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                XXPermissions.with(requireActivity())
                                        .permission(Permission.RECORD_AUDIO)
                                        .request(new OnPermissionCallback() {
                                            @Override
                                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                                if (allGranted){
                                                    if (isRecord) {//正在录音

                                                        if (mediaRecorder != null) {

                                                            mediaRecorder.stop();
                                                            isRecord = false;

                                                            loadingPopup = new LoadingPopup();
                                                            loadingPopup.getPopup(requireActivity());
                                                            practiseViewModel.requestEval(mp3Path, "279", "iyuba", "0"
                                                                    , "0", "0", "0"
                                                                    , dataDTO.getQuestion());
                                                        }
                                                    } else {

                                                        isRecord = true;
                                                        test209_iv_recording.setVisibility(View.VISIBLE);
                                                        test209_fl_record.setVisibility(View.GONE);
                                                        Glide.with(test209_iv_recording.getContext()).load(R.drawable.wavy_line).into(test209_iv_recording);

                                                        record();
                                                    }
                                                }else {
                                                    Toast.makeText(requireActivity(),"请授权录音/麦克风权限后使用",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                                Toast.makeText(requireActivity(),"请授权录音/麦克风权限后使用",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("取消",null)
                        .create().show();
            }*/
        });

        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void record() {

        mp3Path = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC) + File.separator + UUID.randomUUID() + ".mp3";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(mp3Path);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void check208(ExamBean.DataDTO dataDTO) {

        boolean isCorrect = false;
        int pos = soundFillInAdapter.getPosition();
        String answerChoose = null;


        if (pos == 0) {

            answerChoose = "A";
        } else if (pos == 1) {

            answerChoose = "B";
        } else if (pos == 2) {

            answerChoose = "C";
        } else if (pos == 3) {

            answerChoose = "D";
        }
        if (pos == 0 && dataDTO.getAnswer().equalsIgnoreCase("a")) {

            isCorrect = true;
        } else if (pos == 1 && dataDTO.getAnswer().equalsIgnoreCase("b")) {

            isCorrect = true;
        } else if (pos == 2 && dataDTO.getAnswer().equalsIgnoreCase("c")) {

            isCorrect = true;
        } else if (pos == 3 && dataDTO.getAnswer().equalsIgnoreCase("d")) {

            isCorrect = true;
        }
        soundFillInAdapter.setChoose(false);
        if (isCorrect) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(answerChoose);
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(answerChoose);
            check203And204Error(null);
        }
    }

    private void test208(ExamBean.DataDTO dataDTO) {

        resetDefault("选择音频填空");

        //view
        View view208 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_208, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view208);

        testAnimation(view208);

        LinearLayout test208_ll_ques = view208.findViewById(R.id.test208_ll_ques);
        ImageView test208_iv_icon = view208.findViewById(R.id.test208_iv_icon);
        TextView test208_tv_text = view208.findViewById(R.id.test208_tv_text);
        RecyclerView test208_rv_choose = view208.findViewById(R.id.test208_rv_choose);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test208_iv_icon.setImageResource(R.mipmap.laba);

                List<SoundFillIn> fillInList = soundFillInAdapter.getSoundFillIns();
                for (int i = 0; i < fillInList.size(); i++) {

                    fillInList.get(i).setPlaying(false);
                }
                soundFillInAdapter.notifyDataSetChanged();
            }
        });

        test208_rv_choose.setLayoutManager(new LinearLayoutManager(view208.getContext()));
        LineItemDecoration lineItemDecoration = new LineItemDecoration(view208.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test208_rv_choose.addItemDecoration(lineItemDecoration);

        test208_tv_text.setText(dataDTO.getQuestion());
        test208_ll_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(view208.getContext()).load(R.mipmap.gif_laba2).into(test208_iv_icon);
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //选项
        List<SoundFillIn> soundFillInList = new ArrayList<>();
        if (dataDTO.getAnswer1() != null && !dataDTO.getAnswer1().isEmpty()) {

            soundFillInList.add(new SoundFillIn(dataDTO.getAnswer1()));
        }
        if (dataDTO.getAnswer2() != null && !dataDTO.getAnswer2().isEmpty()) {

            soundFillInList.add(new SoundFillIn(dataDTO.getAnswer2()));
        }
        if (dataDTO.getAnswer3() != null && !dataDTO.getAnswer3().isEmpty()) {

            soundFillInList.add(new SoundFillIn(dataDTO.getAnswer3()));
        }
        if (dataDTO.getAnswer4() != null && !dataDTO.getAnswer4().isEmpty()) {

            soundFillInList.add(new SoundFillIn(dataDTO.getAnswer4()));
        }
        if (dataDTO.getAnswer5() != null && !dataDTO.getAnswer5().isEmpty()) {

            soundFillInList.add(new SoundFillIn(dataDTO.getAnswer5()));
        }
        soundFillInAdapter = new SoundFillInAdapter(soundFillInList);
        test208_rv_choose.setAdapter(soundFillInAdapter);
        soundFillInAdapter.setClickCallback(new SoundFillInAdapter.ClickCallback() {
            @Override
            public void click(SoundFillIn soundFillIn) {

                soundFillIn.setPlaying(true);
                soundFillInAdapter.notifyDataSetChanged();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(soundFillIn.getPath());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //检查按钮
                fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                fragmentPractiseBinding.practiseTvCheck.setText("检查");
            }
        });

        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            Glide.with(view208.getContext()).load(R.mipmap.gif_laba2).into(test208_iv_icon);
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void check207(ExamBean.DataDTO dataDTO) {

        fillInAdapter.setInput(false);
        fillInAdapter.notifyDataSetChanged();

        List<FillIn> fillInList = fillInAdapter.getFillInList();
        String[] answers = dataDTO.getAnswer().split("\\++");
        List<String> answerList = Arrays.asList(answers);

        Pattern pattern = Pattern.compile("_+");
        List<FillIn> editInList = new ArrayList<>();
        for (int i = 0; i < fillInList.size(); i++) {

            FillIn fillIn = fillInList.get(i);
            Matcher matcher = pattern.matcher(fillIn.getWord());
            if (matcher.find()) {

                editInList.add(fillIn);
            }
        }

        //用户答案
        StringBuilder userAnswer = new StringBuilder();
        for (int i = 0; i < editInList.size(); i++) {

            FillIn fillIn = editInList.get(i);
            if (i == 0) {

                if (fillIn.getUsetInput() != null) {

                    userAnswer.append(fillIn.getUsetInput());
                } else {

                    userAnswer.append(" ");
                }
            } else {

                if (fillIn.getUsetInput() != null) {

                    userAnswer.append("++").append(fillIn.getUsetInput());
                } else {

                    userAnswer.append("++ ");
                }
            }
        }

        if (dataDTO.getAnswer().equalsIgnoreCase(userAnswer.toString())) {

            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(dataDTO.getAnswer());
            checkSuccess();
        } else {

            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(userAnswer.toString());
            check203And204Error(dataDTO);
        }
    }

    private void test207(ExamBean.DataDTO dataDTO) {

        resetDefault("完成翻译");

        //view
        View view207 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_207, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view207);

        testAnimation(view207);

        TextView test207_tv_explain = view207.findViewById(R.id.test207_tv_explain);
        test207_tv_explain.setText(dataDTO.getExplain());

        ImageView test207_iv_pic = view207.findViewById(R.id.test207_iv_pic);
        RecyclerView test207_rv_content = view207.findViewById(R.id.test207_rv_content);
        test207_rv_content.setLayoutManager(new FlexboxLayoutManager(view207.getContext()));
        Glide.with(view207.getContext()).load(dataDTO.getPic()).into(test207_iv_pic);

        String[] words = dataDTO.getQuestion().split("\\s+");
        List<String> wordList = Arrays.asList(words);
        List<FillIn> fillInList = new ArrayList<>();
        for (int i = 0; i < wordList.size(); i++) {
            String wordStr = wordList.get(i);
            fillInList.add(new FillIn(wordStr));
        }


        fillInAdapter = new FillInAdapter(fillInList);
        test207_rv_content.setAdapter(fillInAdapter);
        fillInAdapter.setContentCallback(new FillInAdapter.ContentCallback() {
            @Override
            public void textChange() {

                boolean isTest = false;
                List<FillIn> fillIns = fillInAdapter.getFillInList();
                Pattern pattern = Pattern.compile("_+");
                for (int i = 0; i < fillIns.size(); i++) {

                    FillIn fillIn = fillIns.get(i);
                    Matcher matcher = pattern.matcher(fillIn.getWord());
                    if (matcher.find() && fillIn.getUsetInput() != null && !fillIn.getUsetInput().isEmpty()) {//空格

                        isTest = true;
                        break;
                    }
                }
                if (isTest) {//检测可点击

                    //检测按钮
                    fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                } else {

                    //检测按钮
                    fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
                    fragmentPractiseBinding.practiseTvCheck.setText("检查");
                    fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
                    fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
                }
            }
        });
    }

    private void test206(ExamBean.DataDTO dataDTO) {

        resetDefault("选择配对");

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                List<Pair> pairList = leftSoundPairAdapter.getPairList();
                for (int i = 0; i < pairList.size(); i++) {

                    Pair pair = pairList.get(i);
                    pair.setPlaying(false);
                }
                leftSoundPairAdapter.notifyDataSetChanged();
            }
        });
        //view
        View view205 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_205_206, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view205);

        testAnimation(view205);

        //left
        RecyclerView test205_rv_left = view205.findViewById(R.id.test205_rv_left);
        test205_rv_left.setLayoutManager(new LinearLayoutManager(view205.getContext(), LinearLayoutManager.VERTICAL, false));
        LineItemDecoration lineItemDecoration = new LineItemDecoration(test205_rv_left.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test205_rv_left.addItemDecoration(lineItemDecoration);

        //音频地址
        String[] sounds = dataDTO.getSounds().split("\\++");
        List<String> soundList = Arrays.asList(sounds);
        //答案
        String[] answers = dataDTO.getAnswer().split("\\++");
        List<String> answerList = Arrays.asList(answers);
        //创建PairList
        List<Pair> leftPairList = new ArrayList<>();
        for (int i = 0; i < soundList.size() && i < answerList.size(); i++) {
            String soundStr = soundList.get(i);
            String answerStr = answerList.get(i);
            leftPairList.add(new Pair(null, answerStr, soundStr));
        }
        leftSoundPairAdapter = new SoundPairAdapter(leftPairList);
        test205_rv_left.setAdapter(leftSoundPairAdapter);
        leftSoundPairAdapter.setClickCallback(new SoundPairAdapter.ClickCallback() {
            @Override
            public void click(Pair pair) {

                pair.setPlaying(true);
                String sound = pair.getSound();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(sound);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //处理问题
                int pos = rightSoundPairAdapter.getPosition();
                if (pos != -1) {

                    Pair rightPair = rightSoundPairAdapter.getPairList().get(pos);
                    if (pair.getAnswer().equalsIgnoreCase(rightPair.getName())) {

                        pair.setPair(true);
                        rightPair.setPair(true);
                        leftSoundPairAdapter.setPosition(-1);
                        rightSoundPairAdapter.setPosition(-1);
                        leftSoundPairAdapter.notifyDataSetChanged();
                        rightSoundPairAdapter.notifyDataSetChanged();


                        boolean isComplete = true;
                        List<Pair> leftPairList = rightSoundPairAdapter.getPairList();
                        for (int i = 0; i < leftPairList.size(); i++) {

                            Pair leftPair = leftPairList.get(i);
                            if (!leftPair.isPair()) {

                                isComplete = false;
                                break;
                            }
                        }
                        if (isComplete) {//显示成功

                            leftSoundPairAdapter.setChoose(false);
                            rightSoundPairAdapter.setChoose(false);
                            checkSuccess();
                        }
                    } else {//不相同，错误

                        pair.setError(true);
                        rightPair.setError(true);
                        leftSoundPairAdapter.notifyDataSetChanged();
                        rightSoundPairAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                pair.setError(false);
                                rightPair.setError(false);
                                leftSoundPairAdapter.setPosition(-1);
                                rightSoundPairAdapter.setPosition(-1);
                                leftSoundPairAdapter.notifyDataSetChanged();
                                rightSoundPairAdapter.notifyDataSetChanged();
                            }
                        }, 800);
                    }
                }
            }
        });

        //right
        RecyclerView test205_rv_right = view205.findViewById(R.id.test205_rv_right);
        test205_rv_right.setLayoutManager(new LinearLayoutManager(view205.getContext(), LinearLayoutManager.VERTICAL, false));
        LineItemDecoration lineItemDecoration2 = new LineItemDecoration(test205_rv_right.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test205_rv_right.addItemDecoration(lineItemDecoration2);
        //文字选项

        String[] chooses = dataDTO.getAnswer1().split("\\++");
        List<String> chooseList = Arrays.asList(chooses);
        List<Pair> rightPairList = new ArrayList<>();
        for (int i = 0; i < chooseList.size(); i++) {

            String chooseStr = chooseList.get(i);
            rightPairList.add(new Pair(chooseStr));
        }
        rightSoundPairAdapter = new SoundPairAdapter(rightPairList);
        test205_rv_right.setAdapter(rightSoundPairAdapter);
        rightSoundPairAdapter.setClickCallback(new SoundPairAdapter.ClickCallback() {
            @Override
            public void click(Pair pair) {

                int pos = leftSoundPairAdapter.getPosition();
                if (pos != -1) {

                    Pair leftPair = leftSoundPairAdapter.getPairList().get(pos);
                    if (leftPair.getAnswer().equalsIgnoreCase(pair.getName())) {//匹配成功

                        pair.setPair(true);
                        leftPair.setPair(true);
                        leftSoundPairAdapter.setPosition(-1);
                        rightSoundPairAdapter.setPosition(-1);
                        leftSoundPairAdapter.notifyDataSetChanged();
                        rightSoundPairAdapter.notifyDataSetChanged();

                        boolean isComplete = true;
                        List<Pair> leftPairList = leftSoundPairAdapter.getPairList();
                        for (int i = 0; i < leftPairList.size(); i++) {

                            Pair lPair = leftPairList.get(i);
                            if (!lPair.isPair()) {

                                isComplete = false;
                                break;
                            }
                        }
                        if (isComplete) {//显示成功

                            leftSoundPairAdapter.setChoose(false);
                            rightSoundPairAdapter.setChoose(false);
                            checkSuccess();
                        }
                    } else {//不相同

                        pair.setError(true);
                        leftPair.setError(true);
                        leftSoundPairAdapter.notifyDataSetChanged();
                        rightSoundPairAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                pair.setError(false);
                                leftPair.setError(false);
                                leftSoundPairAdapter.setPosition(-1);
                                rightSoundPairAdapter.setPosition(-1);
                                leftSoundPairAdapter.notifyDataSetChanged();
                                rightSoundPairAdapter.notifyDataSetChanged();
                            }
                        }, 800);
                    }
                }
            }
        });
    }

    private void test205(ExamBean.DataDTO dataDTO) {

        resetDefault("选择配对");

        //view
        View view205 = LayoutInflater.from(requireActivity()).inflate(R.layout.practise_item_test_205_206, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view205);

        testAnimation(view205);

        RecyclerView test205_rv_left = view205.findViewById(R.id.test205_rv_left);
        test205_rv_left.setLayoutManager(new LinearLayoutManager(view205.getContext(), LinearLayoutManager.VERTICAL, false));
        LineItemDecoration lineItemDecoration = new LineItemDecoration(test205_rv_left.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test205_rv_left.addItemDecoration(lineItemDecoration);

        RecyclerView test205_rv_right = view205.findViewById(R.id.test205_rv_right);
        test205_rv_right.setLayoutManager(new LinearLayoutManager(view205.getContext(), LinearLayoutManager.VERTICAL, false));
        LineItemDecoration lineItemDecoration2 = new LineItemDecoration(test205_rv_left.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.shape_drawable_space_10));
        test205_rv_right.addItemDecoration(lineItemDecoration2);
        //中文
        String[] cnWord = dataDTO.getQuestion().split("\\++");
        List<String> cnWordList = Arrays.asList(cnWord);
        String[] answers = dataDTO.getAnswer().split("\\++");
        List<String> answersList = Arrays.asList(answers);
        List<Pair> cnPairList = new ArrayList<>();
        for (int i = 0; i < cnWordList.size() && i < answersList.size(); i++) {
            String cnWordStr = cnWordList.get(i);
            String answerStr = answersList.get(i);
            cnPairList.add(new Pair(cnWordStr, answerStr));
        }
        leftPairAdapter = new PairAdapter(cnPairList);
        test205_rv_left.setAdapter(leftPairAdapter);
        leftPairAdapter.setClickCallback(new PairAdapter.ClickCallback() {
            @Override
            public void click(Pair pair) {
                int pos = rightPairAdapter.getPosition();
                if (pos != -1) {
                    Pair rightPair = rightPairAdapter.getPairList().get(pos);
                    if (pair.getAnswer().equalsIgnoreCase(rightPair.getName())) {
                        pair.setPair(true);
                        rightPair.setPair(true);
                        leftPairAdapter.setPosition(-1);
                        rightPairAdapter.setPosition(-1);
                        leftPairAdapter.notifyDataSetChanged();
                        rightPairAdapter.notifyDataSetChanged();

                        boolean isComplete = true;
                        List<Pair> leftPairList = rightPairAdapter.getPairList();
                        for (int i = 0; i < leftPairList.size(); i++) {
                            Pair leftPair = leftPairList.get(i);
                            if (!leftPair.isPair()) {
                                isComplete = false;
                                break;
                            }
                        }
                        if (isComplete) {//显示成功
                            leftPairAdapter.setChoose(false);
                            rightPairAdapter.setChoose(false);
                            checkSuccess();
                        }
                    } else {//不相同，错误
                        pair.setError(true);
                        rightPair.setError(true);
                        leftPairAdapter.notifyDataSetChanged();
                        rightPairAdapter.notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pair.setError(false);
                                rightPair.setError(false);
                                leftPairAdapter.setPosition(-1);
                                rightPairAdapter.setPosition(-1);
                                leftPairAdapter.notifyDataSetChanged();
                                rightPairAdapter.notifyDataSetChanged();
                            }
                        }, 300);
                    }
                }
            }
        });
        //英文
        String[] enWord = dataDTO.getAnswer1().split("\\++");
        List<String> enWordList = Arrays.asList(enWord);
        List<Pair> enPairList = new ArrayList<>();
        for (int i = 0; i < enWordList.size(); i++) {

            String enWordStr = enWordList.get(i);
            enPairList.add(new Pair(enWordStr));
        }
        rightPairAdapter = new PairAdapter(enPairList);
        test205_rv_right.setAdapter(rightPairAdapter);
        rightPairAdapter.setClickCallback(new PairAdapter.ClickCallback() {
            @Override
            public void click(Pair pair) {

                int pos = leftPairAdapter.getPosition();
                if (pos != -1) {
                    Pair leftPair = leftPairAdapter.getPairList().get(pos);
                    if (leftPair.getAnswer().equalsIgnoreCase(pair.getName())) {//匹配成功

                        pair.setPair(true);
                        leftPair.setPair(true);
                        leftPairAdapter.setPosition(-1);
                        rightPairAdapter.setPosition(-1);
                        leftPairAdapter.notifyDataSetChanged();
                        rightPairAdapter.notifyDataSetChanged();

                        boolean isComplete = true;
                        List<Pair> leftPairList = leftPairAdapter.getPairList();
                        for (int i = 0; i < leftPairList.size(); i++) {

                            Pair lPair = leftPairList.get(i);
                            if (!lPair.isPair()) {
                                isComplete = false;
                                break;
                            }
                        }
                        if (isComplete) {//显示成功
                            leftPairAdapter.setChoose(false);
                            rightPairAdapter.setChoose(false);
                            checkSuccess();
                        }
                    } else {//不相同
                        pair.setError(true);
                        leftPair.setError(true);
                        leftPairAdapter.notifyDataSetChanged();
                        rightPairAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pair.setError(false);
                                leftPair.setError(false);
                                leftPairAdapter.setPosition(-1);
                                rightPairAdapter.setPosition(-1);
                                leftPairAdapter.notifyDataSetChanged();
                                rightPairAdapter.notifyDataSetChanged();
                            }
                        }, 300);
                    }
                }
            }
        });
    }

    private void check203And204(ExamBean.DataDTO dataDTO, TranslateAdapter answerTranslateAdapter) {

        answerTranslateAdapter.setChoose(false);
        chhooseTranslateAdapter.setChoose(false);
        //防止播放完音频没有数据，会崩溃的问题
        mediaPlayer.setOnCompletionListener(null);

        boolean status = true;
        List<Translate> translateList = answerTranslateAdapter.getTranslateList();

        StringBuilder userStringBuilder = new StringBuilder();
        for (int i = 0; i < translateList.size(); i++) {
            Translate translate = translateList.get(i);
            if (i == 0) {
                userStringBuilder = new StringBuilder(translate.getData());
            } else {
                userStringBuilder.append("++").append(translate.getData());
            }
        }

        // TODO: 2025/4/11 这里为了避免答案中增加其他符号导致出现问题，因此需先处理下，同时处理答案和
        String rightAnswer = formatText(dataDTO.getAnswer());
        String userAnswer = formatText(userStringBuilder.toString());

        if (userAnswer!=null && userAnswer.equalsIgnoreCase(rightAnswer)) {
            dataDTO.setCorrectFlag(1);
            dataDTO.setUserAnswer(dataDTO.getAnswer());
            checkSuccess();
        } else {
            dataDTO.setCorrectFlag(0);
            dataDTO.setUserAnswer(userStringBuilder.toString());
            check203And204Error(dataDTO);
        }
    }

    /**
     * 显示答案的动画
     */
    private void answerAnimation() {

        TranslateAnimation translateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(300);
        fragmentPractiseBinding.practiseLlAnswer.startAnimation(translateAnimation);
    }

    /**
     * 检测成功
     */
    private void checkSuccess() {

        //背景
        fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#559FFC75"));
        fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
        //状态图标
        fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.zhengque);
        //状态文字
        fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.parseColor("#22C134"));
        fragmentPractiseBinding.practiseTvStatus.setText("真厉害");
        //答案说明
        fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.INVISIBLE);
        //检测按钮
        fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
        fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
        fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
        fragmentPractiseBinding.practiseTvCheck.setText("继续");

        answerAnimation();

        //音效
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/correct.mp3");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void check203And204Error(ExamBean.DataDTO dataDTO) {

        //背景
        fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#55FFA5A3"));
        fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
        //状态图标
        fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.cuowu);
        //状态文字
        fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.RED);
        fragmentPractiseBinding.practiseTvStatus.setText("不正确");

        //检测按钮
        fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
        fragmentPractiseBinding.practiseTvCheck.setText("知道了");
        fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_red);
        fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);


        answerAnimation();

        //答案说明
        if (dataDTO != null) {

            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.VISIBLE);
            if (dataDTO.getTestType() == 213) {

                String answer = null;
                if (dataDTO.getAnswer().equalsIgnoreCase("a")) {

                    answer = dataDTO.getAnswer1();
                } else if (dataDTO.getAnswer().equalsIgnoreCase("b")) {

                    answer = dataDTO.getAnswer2();
                }
                fragmentPractiseBinding.practiseTvAnswer.setText("正确答案：" + answer);
            } else if (dataDTO.getTestType() == 214 && !dataDTO.getExplain().isEmpty()) {

                fragmentPractiseBinding.practiseTvAnswer.setText("正确答案：" + dataDTO.getAnswer() + ",解析" + dataDTO.getExplain());
            } else {

                fragmentPractiseBinding.practiseTvAnswer.setText("正确答案：" + dataDTO.getAnswer().replace("++", " "));
            }
        } else {

            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.INVISIBLE);
        }

        //音效
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/error.mp3");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void test204(ExamBean.DataDTO dataDTO) {
        resetDefault("选择听到的内容");

        View view204 = LayoutInflater.from(requireContext()).inflate(R.layout.practise_item_test_204, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view204);

        testAnimation(view204);

        ImageView test204_iv_common = view204.findViewById(R.id.test204_iv_common);
        ImageView test204_iv_slow = view204.findViewById(R.id.test204_iv_slow);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (isLeftSound) {

                    test204_iv_common.setImageResource(R.mipmap.icon_laba);
                } else {

                    test204_iv_slow.setImageResource(R.mipmap.icon_slow);
                }
            }
        });

        //常速
        test204_iv_common.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    isLeftSound = true;
                    Glide.with(view204.getContext()).load(R.mipmap.gif_laba).into(test204_iv_common);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //慢速
        test204_iv_slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    isLeftSound = false;
                    Glide.with(view204.getContext()).load(R.mipmap.gif_slow).into(test204_iv_slow);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    PlaybackParams playbackParams = new PlaybackParams();
                    playbackParams.setSpeed(0.65f);
                    mediaPlayer.setPlaybackParams(playbackParams);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        RecyclerView test204_rv_answer = view204.findViewById(R.id.test204_rv_answer);
        RecyclerView test204_rv_choose = view204.findViewById(R.id.test204_rv_choose);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(test204_rv_answer.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        test204_rv_answer.setLayoutManager(layoutManager);
        answerTranslateAdapter = new TranslateAdapter(new ArrayList<>(), 1);
        test204_rv_answer.setAdapter(answerTranslateAdapter);
        answerTranslateAdapter.setClickCallback(new TranslateAdapter.ClickCallback() {
            @Override
            public void click(Translate translate) {

            }

            @Override
            public void clickRemove(Translate translate) {
                if (addTranslateAnimation != null) {
                    addTranslateAnimation.cancel();
                    answerTranslateAdapter.notifyDataSetChanged();
                    chhooseTranslateAdapter.notifyDataSetChanged();
                    addTranslateAnimation = null;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            translate.setCheck(false);
                            answerTranslateAdapter.getTranslateList().remove(translate);

                            TranslateAdapter.TranslateViewHolder answerViewHolder = getViewHolder(test204_rv_answer, translate);
                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test204_rv_choose, translate);
                            if (answerViewHolder != null && chooseViewHolder != null) {

                                animationBottom(answerViewHolder.itemView, chooseViewHolder.itemView);
                                test203DealAnswer(answerTranslateAdapter);
                            }
                        }
                    }, 100);
                } else {

                    translate.setCheck(false);
                    answerTranslateAdapter.getTranslateList().remove(translate);
                    //找对应的viewholder
                    TranslateAdapter.TranslateViewHolder answerViewHolder = getViewHolder(test204_rv_answer, translate);
                    TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test204_rv_choose, translate);
                    if (chooseViewHolder != null && answerViewHolder != null) {

                        animationBottom(answerViewHolder.itemView, chooseViewHolder.itemView);
                        test203DealAnswer(answerTranslateAdapter);
                    }
                }
            }
        });

        //选项
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(test204_rv_answer.getContext());
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setJustifyContent(JustifyContent.FLEX_START);
        test204_rv_choose.setLayoutManager(layoutManager2);

        String[] answers = dataDTO.getAnswer1().split("\\++");
        List<String> contentList = Arrays.asList(answers);
        List<Translate> translateList = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {

            translateList.add(new Translate(contentList.get(i)));
        }
        chhooseTranslateAdapter = new TranslateAdapter(translateList, 2);
        test204_rv_choose.setAdapter(chhooseTranslateAdapter);
        chhooseTranslateAdapter.setClickCallback(new TranslateAdapter.ClickCallback() {

            @Override
            public void click(Translate translate) {
                if (addTranslateAnimation != null) {
                    translate.setCheck(true);
                    addTranslateAnimation.cancel();
                    answerTranslateAdapter.notifyDataSetChanged();
//                    chhooseTranslateAdapter.notifyDataSetChanged();
                    addTranslateAnimation = null;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerTranslateAdapter.add2(translate);

                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test204_rv_choose, translate);
                            if (chooseViewHolder != null) {
                                animationTop(chooseViewHolder.translate_fl_all, test204_rv_answer);
                            }
                        }
                    }, 100);
                } else {
                    translate.setCheck(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerTranslateAdapter.add2(translate);
                            //找对应的viewholder
                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test204_rv_choose, translate);
                            if (chooseViewHolder != null) {
                                animationTop(chooseViewHolder.translate_fl_all, test204_rv_answer);
                            }
                        }
                    },100);
                }
            }

            @Override
            public void clickRemove(Translate translate) {

            }
        });

        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            isLeftSound = true;
            Glide.with(view204.getContext()).load(R.mipmap.gif_laba).into(test204_iv_common);
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理203、204
     * @param dataDTO
     */
    private void test203(ExamBean.DataDTO dataDTO) {
        resetDefault("翻译这句话");

        View view203 = LayoutInflater.from(requireContext()).inflate(R.layout.practise_item_test_203, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view203);

        testAnimation(view203);

        //音频
        ImageView test203_iv_sound = view203.findViewById(R.id.test203_iv_sound);
        //句子
        TextView test203_tv_sentence = view203.findViewById(R.id.test203_tv_sentence);
        //图片
        ImageView test203_iv_pic = view203.findViewById(R.id.test203_iv_pic);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                test203_iv_sound.setImageResource(R.mipmap.laba);
                String jpgUrl = dataDTO.getPic().replace(".gif", ".jpg");
                Glide.with(view203.getContext()).load(jpgUrl).into(test203_iv_pic);
            }
        });
        test203_iv_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(view203.getContext()).load(R.mipmap.gif_laba2).placeholder(R.mipmap.laba).into(test203_iv_sound);
                Glide.with(view203.getContext()).load(dataDTO.getPic()).into(test203_iv_pic);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(dataDTO.getSounds());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        test203_tv_sentence.setText(dataDTO.getQuestion());

        //图片和音频
//        Glide.with(test203_iv_pic.getContext()).load(dataDTO.getPic()).into(test203_iv_pic);

        RecyclerView test203_rv_answer = view203.findViewById(R.id.test203_rv_answer);
        RecyclerView test203_rv_choose = view203.findViewById(R.id.test203_rv_choose);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(test203_rv_answer.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        test203_rv_answer.setLayoutManager(layoutManager);
        answerTranslateAdapter = new TranslateAdapter(new ArrayList<>(), 1);
        test203_rv_answer.setAdapter(answerTranslateAdapter);
        answerTranslateAdapter.setClickCallback(new TranslateAdapter.ClickCallback() {
            @Override
            public void click(Translate translate) {
            }

            @Override
            public void clickRemove(Translate translate) {


                if (addTranslateAnimation != null) {

                    addTranslateAnimation.cancel();
                    answerTranslateAdapter.notifyDataSetChanged();
                    chhooseTranslateAdapter.notifyDataSetChanged();
                    addTranslateAnimation = null;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            translate.setCheck(false);
                            answerTranslateAdapter.getTranslateList().remove(translate);

                            TranslateAdapter.TranslateViewHolder answerViewHolder = getViewHolder(test203_rv_answer, translate);
                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test203_rv_choose, translate);
                            if (answerViewHolder != null && chooseViewHolder != null) {

                                animationBottom(answerViewHolder.itemView, chooseViewHolder.itemView);
                                test203DealAnswer(answerTranslateAdapter);
                            }
                        }
                    }, 100);
                } else {

                    translate.setCheck(false);
                    answerTranslateAdapter.getTranslateList().remove(translate);
                    //找对应的viewholder
                    TranslateAdapter.TranslateViewHolder answerViewHolder = getViewHolder(test203_rv_answer, translate);
                    TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test203_rv_choose, translate);
                    if (chooseViewHolder != null && answerViewHolder != null) {

                        animationBottom(answerViewHolder.itemView, chooseViewHolder.itemView);
                        test203DealAnswer(answerTranslateAdapter);
                    }
                }
            }
        });

        //选项
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(test203_rv_answer.getContext());
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setJustifyContent(JustifyContent.FLEX_START);
        test203_rv_choose.setLayoutManager(layoutManager2);

        String[] answers = dataDTO.getAnswer1().split("\\++");
        List<String> contentList = Arrays.asList(answers);
        List<Translate> translateList = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {

            translateList.add(new Translate(contentList.get(i)));
        }
        chhooseTranslateAdapter = new TranslateAdapter(translateList, 2);
        test203_rv_choose.setAdapter(chhooseTranslateAdapter);
        chhooseTranslateAdapter.setClickCallback(new TranslateAdapter.ClickCallback() {
            @Override
            public void click(Translate translate) {
                if (addTranslateAnimation != null) {
                    translate.setCheck(true);
                    addTranslateAnimation.cancel();
                    answerTranslateAdapter.notifyDataSetChanged();
//                    chhooseTranslateAdapter.notifyDataSetChanged();
                    addTranslateAnimation = null;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            answerTranslateAdapter.add2(translate);

                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test203_rv_choose, translate);
                            if (chooseViewHolder != null) {

                                animationTop(chooseViewHolder.translate_fl_all, test203_rv_answer);
                            }
                        }
                    }, 100);
                } else {

                    translate.setCheck(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            answerTranslateAdapter.add2(translate);
                            //找对应的viewholder
                            TranslateAdapter.TranslateViewHolder chooseViewHolder = getViewHolder(test203_rv_choose, translate);
                            if (chooseViewHolder != null) {

                                animationTop(chooseViewHolder.translate_fl_all, test203_rv_answer);
                            }
                        }
                    }, 100);
                }
            }

            @Override
            public void clickRemove(Translate translate) {

            }
        });

        //自动播放
        if (dataDTO.getSounds() != null && !dataDTO.getSounds().isEmpty()) {

            //加载图片但不直接展示
            Glide.with(test203_iv_pic.getContext()).load(dataDTO.getPic()).into(test203_iv_pic);
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取对应的ViewHolder
     * @param test203_rv_choose
     * @param translate
     * @return
     */
    private TranslateAdapter.TranslateViewHolder getViewHolder(RecyclerView test203_rv_choose, Translate translate) {

        int count = test203_rv_choose.getChildCount();
        TranslateAdapter.TranslateViewHolder chooseViewHolder = null;
        for (int i = 0; i < count; i++) {

            TranslateAdapter.TranslateViewHolder translateViewHolder = (TranslateAdapter.TranslateViewHolder) test203_rv_choose.findViewHolderForAdapterPosition(i);
            if (translateViewHolder != null) {

                Translate tslt = translateViewHolder.getTranslate();
                if (tslt == translate) {

                    chooseViewHolder = translateViewHolder;
                }
            }
        }
        return chooseViewHolder;
    }


    /**
     * 动画向下移动
     */
    private void animationBottom(View answerView, View chooseView) {

        //点击的元素坐标
        int[] location = new int[2];
        answerView.getLocationOnScreen(location);

        //目的坐标
        int[] chooseLocation = new int[2];
        chooseView.getLocationOnScreen(chooseLocation);

        addTranslateAnimation = new TranslateAnimation(0, chooseLocation[0] - location[0], 0, chooseLocation[1] - location[1]);
        addTranslateAnimation.setDuration(350);
        addTranslateAnimation.setFillAfter(true);
        answerView.startAnimation(addTranslateAnimation);
        addTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animation.cancel();
                addTranslateAnimation = null;
                answerTranslateAdapter.notifyDataSetChanged();
                chhooseTranslateAdapter.notifyDataSetChanged();
                test203DealAnswer(answerTranslateAdapter);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 动画向上移动
     * @param animationView
     * @param test203_rv_answer
     */
    private void animationTop(View animationView, RecyclerView test203_rv_answer) {

        //点击的元素坐标
        int[] location = new int[2];
        animationView.getLocationOnScreen(location);

        //目的坐标
        int[] answerLocation = new int[2];
        FlexboxLayoutManager answerFlexboxLayoutManager = (FlexboxLayoutManager) test203_rv_answer.getLayoutManager();
        if (answerFlexboxLayoutManager != null) {

            int lastPos = answerFlexboxLayoutManager.findLastVisibleItemPosition();
            if (lastPos == RecyclerView.NO_POSITION) {//没有找到

                test203_rv_answer.getLocationOnScreen(answerLocation);
            } else {//找到viewholder

                TranslateAdapter.TranslateViewHolder answerViewHolder = (TranslateAdapter.TranslateViewHolder) test203_rv_answer.findViewHolderForAdapterPosition(lastPos);
                if (answerViewHolder != null) {

                    View view = answerViewHolder.itemView;
                    view.getLocationOnScreen(answerLocation);
                    answerLocation[0] = answerLocation[0] + view.getWidth();

                    //获取rv的右边坐标
                    int rvRight = (int) (test203_rv_answer.getX() + test203_rv_answer.getWidth());

                    int right = answerLocation[0] + animationView.getWidth();
                    if (right >= rvRight) {//如果大于屏幕宽度，则在y轴上增加

                        int[] startLocation = new int[2];
                        test203_rv_answer.getLocationOnScreen(startLocation);
                        answerLocation[0] = startLocation[0];//x坐标从头开始
                        answerLocation[1] = answerLocation[1] + view.getHeight();
                    }
                }
            }
        }

        addTranslateAnimation = new TranslateAnimation(0, answerLocation[0] - location[0], 0, answerLocation[1] - location[1]);
        addTranslateAnimation.setDuration(350);
        addTranslateAnimation.setFillAfter(true);
        animationView.startAnimation(addTranslateAnimation);
        addTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animation.cancel();
                addTranslateAnimation = null;
                answerTranslateAdapter.notifyDataSetChanged();
                chhooseTranslateAdapter.notifyDataSetChanged();
                test203DealAnswer(answerTranslateAdapter);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 判断是否可以点击检查
     */
    private void test203DealAnswer(TranslateAdapter answerTranslateAdapter) {

        if (answerTranslateAdapter != null && !answerTranslateAdapter.getTranslateList().isEmpty()) {

            fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
            fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
        } else {

            fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
            fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
        }
    }

    public void check202(ExamBean.DataDTO dataDTO) {

        String answer = dataDTO.getAnswer();
        picAdapter.setCheck(false);

        int pos = picAdapter.getPos();
        boolean statusbol = false;
        String userAnswer = null;
        if (pos == 0) {

            userAnswer = "A";
        } else if (pos == 1) {

            userAnswer = "B";
        } else if (pos == 2) {

            userAnswer = "C";
        } else if (pos == 3) {

            userAnswer = "D";
        }
        if (pos == 0 && answer.equalsIgnoreCase("a")) {

            statusbol = true;
        } else if (pos == 1 && answer.equalsIgnoreCase("b")) {

            statusbol = true;
        } else if (pos == 2 && answer.equalsIgnoreCase("c")) {

            statusbol = true;
        } else if (pos == 3 && answer.equalsIgnoreCase("d")) {

            statusbol = true;
        }

        if (statusbol) {

            dataDTO.setUserAnswer(userAnswer);
            dataDTO.setCorrectFlag(1);
        } else {

            dataDTO.setUserAnswer(userAnswer);
            dataDTO.setCorrectFlag(0);
        }

        if (statusbol) {

            //背景
            fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#559FFC75"));
            fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
            //状态图标
            fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.zhengque);
            //状态文字
            fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.parseColor("#22C134"));
            fragmentPractiseBinding.practiseTvStatus.setText("真厉害");
            //答案说明
            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.INVISIBLE);
            //检测按钮
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
            fragmentPractiseBinding.practiseTvCheck.setText("继续");

            answerAnimation();
            //音效
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/correct.mp3");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            //背景
            fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#55FFA5A3"));
            fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
            //状态图标
            fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.cuowu);
            //状态文字
            fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.RED);
            fragmentPractiseBinding.practiseTvStatus.setText("不正确");
            //答案说明
            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.INVISIBLE);
            //检测按钮
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_red);
            fragmentPractiseBinding.practiseTvCheck.setText("知道了");

            answerAnimation();
            //音效
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(requireContext(), Uri.parse("http://staticvip2.iyuba.cn/exam/error.mp3"));
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void test202(ExamBean.DataDTO dataDTO) {

        resetDefault("选择对应的图片");

        View view202 = LayoutInflater.from(requireContext()).inflate(R.layout.practise_item_test_202, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view202);

        testAnimation(view202);

        ImageView test_iv_sound = view202.findViewById(R.id.test_iv_sound);
        TextView test_tv_en = view202.findViewById(R.id.test_tv_en);
        RecyclerView test_rv_pic = view202.findViewById(R.id.test_rv_pic);
        test_rv_pic.setLayoutManager(new GridLayoutManager(test_rv_pic.getContext(), 2));
        GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(2, 25, true);
        test_rv_pic.addItemDecoration(gridSpacingItemDecoration);

        List<String> stringList = new ArrayList<>();
        stringList.add(dataDTO.getAnswer1());
        stringList.add(dataDTO.getAnswer2());
        stringList.add(dataDTO.getAnswer3());
        stringList.add(dataDTO.getAnswer4());
        picAdapter = new PicAdapter(stringList);
        test_rv_pic.setAdapter(picAdapter);
        picAdapter.setClickCallback(new PicAdapter.ClickCallback() {
            @Override
            public void click(String data) {

                fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        test_iv_sound.setImageResource(R.mipmap.icon_laba);
                    }
                }, 500);
            }
        });
        test_tv_en.setText(dataDTO.getQuestion());
        test_iv_sound.setOnClickListener(v -> {

            mediaPlayer.reset();
            try {
                Glide.with(view202.getContext()).load(R.mipmap.gif_laba).into(test_iv_sound);
                mediaPlayer.setDataSource(dataDTO.getSounds());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void check201(ExamBean.DataDTO dataDTO) {

        String answer = dataDTO.getAnswer();
        chooseAdapter.setChoose(false);
        int pos = chooseAdapter.getPos();

        boolean statusbol = false;
        String userAnswer = null;
        if (pos == 0 && answer.equalsIgnoreCase("a")) {

            userAnswer = "A";
            statusbol = true;
        } else if (pos == 1 && answer.equalsIgnoreCase("b")) {

            userAnswer = "B";
            statusbol = true;
        } else if (pos == 2 && answer.equalsIgnoreCase("c")) {

            userAnswer = "C";
            statusbol = true;
        } else if (pos == 3 && answer.equalsIgnoreCase("d")) {

            userAnswer = "4";
            statusbol = true;
        } else {

            userAnswer = OptionUtil.getOption(pos);
            statusbol = false;
        }

        if (statusbol) {

            dataDTO.setUserAnswer(userAnswer);
            dataDTO.setCorrectFlag(1);
        } else {

            dataDTO.setUserAnswer(userAnswer);
            dataDTO.setCorrectFlag(0);
        }

        //是否正确
        if (statusbol) {

            //背景
            fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#559FFC75"));
            fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
            //状态图标
            fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.zhengque);
            //状态文字
            fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.parseColor("#22C134"));
            fragmentPractiseBinding.practiseTvStatus.setText("真厉害");
            //答案说明
            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.INVISIBLE);
            //检测按钮
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
            fragmentPractiseBinding.practiseTvCheck.setText("继续");

            answerAnimation();
            //音效
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/correct.mp3");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            //背景
            fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.parseColor("#55FFA5A3"));
            fragmentPractiseBinding.practiseLlStatus.setVisibility(View.VISIBLE);
            //状态图标
            fragmentPractiseBinding.practiseIvStatus.setImageResource(R.mipmap.cuowu);
            //状态文字
            fragmentPractiseBinding.practiseTvStatus.setTextColor(Color.RED);
            fragmentPractiseBinding.practiseTvStatus.setText("不正确");
            //答案说明
            fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.VISIBLE);
            fragmentPractiseBinding.practiseTvAnswer.setTextColor(Color.RED);
            String answerStr = dealAnswer(answer, dataDTO);
            fragmentPractiseBinding.practiseTvAnswer.setText(String.format("正确答案：%s", answerStr));
            //检测按钮
            fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_red);
            fragmentPractiseBinding.practiseTvCheck.setText("知道了");

            answerAnimation();
            //音效
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource("http://staticvip2.iyuba.cn/exam/error.mp3");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 重置页面
     * @param content
     */
    public void resetDefault(String content) {

        //初始化控件
        //背景
        fragmentPractiseBinding.practiseLlAnswer.setBackgroundColor(Color.WHITE);
        fragmentPractiseBinding.practiseLlStatus.setVisibility(View.GONE);
        //答案说明
        fragmentPractiseBinding.practiseTvAnswer.setVisibility(View.GONE);

        //检测按钮
        fragmentPractiseBinding.practiseTvCheck.setEnabled(false);
        fragmentPractiseBinding.practiseTvCheck.setText("检测");
        fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test);
        fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.parseColor("#737373"));
        //说明
        fragmentPractiseBinding.practiseTvDesc.setText(content);
    }

    public void testAnimation(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setDuration(200);
        view.setAnimation(translateAnimation);
    }

    public void test201(ExamBean.DataDTO dataDTO) {

        resetDefault("选择正确的翻译");

        View view201 = LayoutInflater.from(requireContext()).inflate(R.layout.practise_item_test_201, null);
        fragmentPractiseBinding.practiseFlContent.removeAllViews();
        fragmentPractiseBinding.practiseFlContent.addView(view201);
        //动画
        testAnimation(view201);

        TextView test201_tv_ques = view201.findViewById(R.id.test201_tv_ques);
        ImageView test201_iv_pic = view201.findViewById(R.id.test201_iv_pic);
        RecyclerView test201_rv_choose = view201.findViewById(R.id.test201_rv_choose);
        LineItemDecoration lineItemDecoration = new LineItemDecoration(view201.getContext(), LinearLayoutManager.VERTICAL);
        lineItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_drawable_space_10, null));
        test201_rv_choose.setLayoutManager(new LinearLayoutManager(view201.getContext()));
        test201_rv_choose.addItemDecoration(lineItemDecoration);
        Glide.with(test201_iv_pic.getContext()).load(dataDTO.getPic()).into(test201_iv_pic);

        //ques
        test201_tv_ques.setText(dataDTO.getQuestion());
        //选项数据
        List<String> stringList = new ArrayList<>();
        stringList.add(dataDTO.getAnswer1());
        stringList.add(dataDTO.getAnswer2());
        stringList.add(dataDTO.getAnswer3());
        if (dataDTO.getAnswer4() != null && !dataDTO.getAnswer4().isEmpty()) {

            stringList.add(dataDTO.getAnswer4());
        }
        if (dataDTO.getAnswer5() != null && !dataDTO.getAnswer5().isEmpty()) {

            stringList.add(dataDTO.getAnswer5());
        }

        chooseAdapter = new ChooseAdapter(stringList);
        test201_rv_choose.setAdapter(chooseAdapter);
        chooseAdapter.setClickCallback(new ChooseAdapter.ClickCallback() {
            @Override
            public void click(String data) {

                fragmentPractiseBinding.practiseTvCheck.setEnabled(true);
                fragmentPractiseBinding.practiseTvCheck.setBackgroundResource(R.drawable.shape_test_green);
                fragmentPractiseBinding.practiseTvCheck.setTextColor(Color.WHITE);
            }
        });
    }

    /**
     * 上传试题
     */
    private void submitTest() {

        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");

        String dataStr = simpleDateFormat.format(new Date());

        String signStr = MD5Util.MD5("iyubaExam" + PractiseConstant.UID + PractiseConstant.APPID + lessonId + dataStr);
        endTime = System.currentTimeMillis() / 1000 + "";
        practiseViewModel.requestUpdateEnglishTestRecord(1, lessonId, type, "课后练习"
                , signStr, beginTime, endTime, dataDTOList);
    }

    /**
     * 是否完成闯关
     * @return
     */
    public boolean isFinish() {


        if (dataDTOList == null) {

            return false;
        }

        boolean isF = true;
        for (int i = 0; i < dataDTOList.size(); i++) {

            ExamBean.DataDTO dataDTO = dataDTOList.get(i);
            if (dataDTO.getCorrectFlag() == -1) {

                isF = false;
                break;
            }
        }
        return isF;
    }

    /*******************************界面显示*************************************/
    //显示积分混合界面
    private void showIntegralMixPage(){
        fragmentPractiseBinding.practiseLlQues.setVisibility(View.GONE);
        fragmentPractiseBinding.practiseLlComplete.setVisibility(View.VISIBLE);
        fragmentPractiseBinding.practiseLlJf.setVisibility(View.GONE);

        //总经验
        int scores = 0;
        for (int i = 0; i < dataDTOList.size(); i++) {
            ExamBean.DataDTO dataDTO = dataDTOList.get(i);
            if (dataDTO.getCorrectFlag() == 1) {
                scores++;
            }
        }
        int p = (int) (scores * 100.0 / dataDTOList.size());
        fragmentPractiseBinding.practiseTvExp.setText(scores + "");
        //正确率
        fragmentPractiseBinding.practiseTvP.setText(p + "%");
        //时间
        long interval = Integer.parseInt(endTime) - Integer.parseInt(beginTime);
        int s = (int) (interval % 60);
        int m = (int) interval / 60;
        DecimalFormat format = new DecimalFormat("#00");
        fragmentPractiseBinding.practiseTvTime.setText(format.format(m) + ":" + format.format(s));
    }

    //显示单独的积分界面
    private void showSingleIntegralPage(ExpBean expBean){
        fragmentPractiseBinding.practiseLlQues.setVisibility(View.GONE);
        fragmentPractiseBinding.practiseLlComplete.setVisibility(View.GONE);
        fragmentPractiseBinding.practiseLlJf.setVisibility(View.VISIBLE);

        PractiseFragment.this.expBean = expBean;
        String str = "获得" + expBean.getScore() + "积分";
        fragmentPractiseBinding.practiseTvJf.setText(str);

        //显示正确率
        int scores = 0;
        for (int i = 0; i < dataDTOList.size(); i++) {
            ExamBean.DataDTO dataDTO = dataDTOList.get(i);
            if (dataDTO.getCorrectFlag() == 1) {
                scores++;
            }
        }
        fragmentPractiseBinding.rightCount.setText(scores+"/"+dataDTOList.size());
        //显示时间
        long interval = Integer.parseInt(endTime) - Integer.parseInt(beginTime);
        int s = (int) (interval % 60);
        int m = (int) interval / 60;
        DecimalFormat format = new DecimalFormat("#00");
        fragmentPractiseBinding.totalTime.setText(format.format(m)+":"+format.format(s));
    }

    //文字样式规范
    private String formatText(String showText){
        if (TextUtils.isEmpty(showText)){
            return showText;
        }

        if (showText.endsWith(".")
                ||showText.endsWith("。")
                ||showText.endsWith("?")
                ||showText.endsWith("？")
                ||showText.endsWith("!")
                ||showText.endsWith("！")
                ||showText.endsWith(",")
                ||showText.endsWith("，")){
            showText = showText.substring(0,showText.length()-1);
        }

        return showText;
    }
}
