package com.jn.yyz.practise.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.databinding.ActivityPractiseBinding;
import com.jn.yyz.practise.fragment.PractiseFragment;

/**
 * 练习试题的activity
 */
public class PractiseActivity extends AppCompatActivity {

    private ActivityPractiseBinding binding;

    private boolean showBack = false;

    private String type = "";
    private int maxId = 0;
    private String lessonId = "0";
    private String pageType = PractiseFragment.page_exerciseOther;

    private PractiseFragment practiseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPractiseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getData();

        practiseFragment = PractiseFragment.newInstance(showBack, true, "练习题", type, maxId, lessonId, pageType);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.practise_fl_fragment, practiseFragment)
                .show(practiseFragment)
                .commitAllowingStateLoss();

    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            showBack = bundle.getBoolean("SHOW_BACK", false);
            type = bundle.getString("TYPE");
            maxId = bundle.getInt("MAX_ID");
            lessonId = bundle.getString("LESSON_ID");
            pageType = bundle.getString("PAGE_TYPE");
        }
    }

    public static void startActivity(Activity activity, boolean showBack, String type, int maxId, String lessonId, String pageType) {

        Intent intent = new Intent(activity, PractiseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("SHOW_BACK", showBack);
        bundle.putString("TYPE", type);
        bundle.putInt("MAX_ID", maxId);
        bundle.putString("LESSON_ID", lessonId);
        bundle.putString("PAGE_TYPE", pageType);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (practiseFragment != null) {

                boolean isF = practiseFragment.isFinish();
                if (!isF) {

                    new AlertDialog.Builder(PractiseActivity.this)
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
                                    PractiseActivity.this.finish();
                                }
                            }).create().show();
                } else {

                    finish();
                }
            }
            return true;
        } else {

            return super.onKeyUp(keyCode, event);
        }
    }
}