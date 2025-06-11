package com.suzhou.concept.lil.util.download;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.util.LibRxUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * 新的下载工具类
 */
public class DownloadNewFileManager {
    private static DownloadNewFileManager instance;

    public static DownloadNewFileManager getInstance(){
        if (instance==null){
            synchronized (DownloadNewFileManager.class){
                if (instance==null){
                    instance = new DownloadNewFileManager();
                }
            }
        }
        return instance;
    }

    /*****************************************下载*****************************/
    //准备下载
    private Disposable downloadFileDis;

    //下载音视频数据
    private int curIndex = 0;
    private List<Pair<String, Pair<String,String>>> pairList = null;
    private Pair<String,Pair<String,String>> pair = null;//文件类型-文件url-文件保存路径

    public void downloadFile(List<Pair<String,Pair<String,String>>> list){
        if (list!=null&&list.size()>0){
            this.pairList = list;
        }

        if (curIndex >= pairList.size()){
            //完成
            return;
        }

        if (pairList!=null&&pairList.size()>curIndex){
            pair = pairList.get(curIndex);
        }

        if (pair!=null){
            String downloadUrl = pair.second.first;
            RetrofitUtil.getInstance().downloadFile(downloadUrl)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(new io.reactivex.Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            downloadFileDis = d;
                        }

                        @Override
                        public void onNext(ResponseBody response) {
                            InputStream is = response.byteStream();
                            saveFile(pair.first,pair.second.second,is, response.contentLength());
                        }

                        @Override
                        public void onError(Throwable e) {
                            String fileType = "";
                            if (pair.first.equals(DownloadNewFileEvent.type_audio)){
                                fileType = "音频";
                            }else if (pair.first.equals(DownloadNewFileEvent.type_video)){
                                fileType = "视频";
                            }

                            EventBus.getDefault().post(new DownloadNewFileEvent(DownloadNewFileEvent.state_error,fileType+"文件下载异常，请重试~"));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void saveFile(String fileType, String savePath, InputStream inputStream,long fileLength){
        //文件类型
        String showFileType = "";
        if (fileType.equals(DownloadNewFileEvent.type_audio)){
            showFileType = "音频";
        }else if (fileType.equals(DownloadNewFileEvent.type_video)){
            showFileType = "视频";
        }
        //创建文件
        File saveFile = new File(savePath);
        try {
            if (saveFile.exists()){
                saveFile.delete();
            }else {
                if (!saveFile.getParentFile().exists()){
                    saveFile.getParentFile().mkdirs();
                }
            }

            boolean isCreateFile = saveFile.createNewFile();
            if (isCreateFile){
                //累加长度
                long progressLength = 0;

                OutputStream os = new FileOutputStream(savePath);
                byte[] bytes = new byte[2048];
                int length = 0;

                while ((length = inputStream.read(bytes))!=-1){
                    os.write(bytes,0,length);

                    //刷新进度显示
                    progressLength+=length;
                    int progress = (int) (progressLength*100/fileLength);
                    String showMsg = showFileType+"文件下载进度("+progress+"%)";
                    EventBus.getDefault().post(new DownloadNewFileEvent(DownloadNewFileEvent.state_downloading,showMsg));
                    Log.d("下载进度", showFileType+showMsg);
                }

                os.flush();
                os.close();

                //下一个文件
                curIndex++;
                downloadFile(null);
                if (fileType.equals(DownloadNewFileEvent.type_audio)){
                    EventBus.getDefault().post(new DownloadNewFileEvent(DownloadNewFileEvent.state_downloading,"准备下载视频文件"));
                }
            }else {
                EventBus.getDefault().post(new DownloadNewFileEvent(DownloadNewFileEvent.state_error,"创建文件异常"));
            }
        }catch (Exception e){
            EventBus.getDefault().post(new DownloadNewFileEvent(DownloadNewFileEvent.state_error,"创建文件异常"));
        }
    }

    //取消下载
    public void cancelDownload(){
        LibRxUtil.unDisposable(downloadFileDis);
        //删除文件
        for (int i = 0; i < pairList.size(); i++) {
            Pair<String,Pair<String,String>> showPair = pairList.get(i);
            String showPath = showPair.second.second;

            File showFile = new File(showPath);
            if (showFile.exists()){
                showFile.delete();
            }
        }
    }

    /*******************************************保存路径**************************************/
    //获取文件的保存路径
    public String getFileSavePath(int voaId,String fileType){
        if (voaId == 0){
            return null;
        }

        String prefixPath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            prefixPath = AppClient.context.getApplicationContext().getExternalFilesDir(null).getPath();
        }else {
            prefixPath = Environment.getExternalStorageDirectory().getPath();
        }

        prefixPath = prefixPath+"/talkshow/"+voaId+"/"+voaId;

        //获取后缀
        if (fileType.equals(DownloadNewFileEvent.type_audio)){
            prefixPath+=".mp3";
        }

        if (fileType.equals(DownloadNewFileEvent.type_video)){
            prefixPath+=".mp4";
        }

        return prefixPath;
    }

    //获取文件的下载链接(按理说这里是不对的，因为视频的链接需要获取本地数据后获取的，但是暂时这么处理)

}
