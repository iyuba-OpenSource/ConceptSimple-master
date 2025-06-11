package com.suzhou.concept.lil.ui.study.eval.util;

import android.os.Build;
import android.os.Environment;

import com.suzhou.concept.AppClient;

/**
 * @desction: 文件管理
 * @date: 2023/3/2 19:36
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class FileManager {

    private static FileManager instance;

    public static FileManager getInstance(){
        if (instance==null){
            synchronized (FileManager.class){
                if (instance==null){
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    private static final String DIR_COURSE = "course";//课程
    private static final String DIR_TALK = "talk";//口语秀
    private static final String DIR_EVAL = "eval";//评测

    //文件夹总路径
    private String dirPath(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            return AppClient.Companion.getContext().getExternalFilesDir(null).getPath();
        }else {
            return Environment.getExternalStorageDirectory().getPath()+"/NewConcept2023";
        }
    }

    /********课程*********/
    //课程音频：course/voaId/voaId.mp3
    //课程视频：course/voaId/voaId.mp4
    //课程评测：course/voaId/eval/voaId_paraId_indexId.mp3
    public String getCourseAudioPath(String voaId){
        String name = voaId+ ".mp3";
        return dirPath()+"/"+DIR_COURSE+"/"+voaId+"/"+name;
    }

    public String getCourseVideoPath(String voaId){
        String name = voaId+ ".mp4";
        return dirPath()+"/"+DIR_COURSE+"/"+voaId+"/"+name;
    }

    public String getCourseEvalAudioPath(String voaId,String paraId,String indexId){
        String name = voaId+"_"+paraId+"_"+indexId+ ".mp3";
        return dirPath()+"/"+DIR_COURSE+"/"+voaId+"/"+DIR_EVAL+"/"+name;
    }


    //单词评测路径
    public String getWordEvalAudioPath(String word){
        String name = "eval_"+word+ ".mp3";
        return dirPath()+"/word/"+name;
    }

    /*********口语秀*******/
    //口语秀音频：talk/voaId/voaId.mp3
    //口语秀视频：talk/voaId/voaId.mp4
    //口语秀评测：talk/voaId/eval/voaId_paraId_indexId.mp3
    public String getTalkAudioPath(String voaId){
        String name = voaId+ ".mp3";
        return dirPath()+"/"+DIR_TALK+"/"+voaId+"/"+name;
    }

    public String getTalkVideoPath(String voaId){
        String name = voaId+ ".mp4";
        return dirPath()+"/"+DIR_TALK+"/"+voaId+"/"+name;
    }

    public String getTalkEvalAudioPath(String voaId,String paraId,String indexId){
        String name = voaId+"_"+paraId+"_"+indexId+ ".mp3";
        return dirPath()+"/"+DIR_TALK+"/"+voaId+"/"+DIR_EVAL+"/"+name;
    }
}
