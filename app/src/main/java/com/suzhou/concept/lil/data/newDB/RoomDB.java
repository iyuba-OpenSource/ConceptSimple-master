package com.suzhou.concept.lil.data.newDB;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.suzhou.concept.AppClient;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultDao;
import com.suzhou.concept.lil.data.newDB.exercise.ExerciseResultDao;
import com.suzhou.concept.lil.data.newDB.exercise.ExerciseResultEntity;
import com.suzhou.concept.lil.data.newDB.exercise.MultiChoiceDao;
import com.suzhou.concept.lil.data.newDB.exercise.MultipleChoiceEntity;
import com.suzhou.concept.lil.data.newDB.exercise.VoaStructureDao;
import com.suzhou.concept.lil.data.newDB.exercise.VoaStructureExerciseEntity;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectDao;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_pass;
import com.suzhou.concept.lil.data.newDB.word.pass.WordFourDao;
import com.suzhou.concept.lil.data.newDB.word.pass.WordJuniorDao;
import com.suzhou.concept.lil.data.newDB.word.pass.WordPassDao;
import com.suzhou.concept.lil.event.RefreshEvent;
import com.suzhou.concept.lil.util.DateUtil;
import com.suzhou.concept.lil.util.LibRxTimer;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Database(entities = {EvalResultBean.class, WordCollectBean.class, MultipleChoiceEntity.class, VoaStructureExerciseEntity.class, ExerciseResultEntity.class, WordEntity_Four.class, WordEntity_Junior.class, WordEntity_pass.class}, version = 4, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static final String TAG = "RoomDB";

    private static final String DB_NAME = "NewConceptEnglish.db";
    private static RoomDB instance;

    public static RoomDB getInstance() {
        if (instance == null) {
            synchronized (RoomDB.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(AppClient.Companion.getContext(), RoomDB.class, DB_NAME)
                            .addCallback(callback)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    //回调
    private static RoomDatabase.Callback callback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //增加单词数据预存
            preSaveWordData(db);
        }
    };

    //评测的dao
    public abstract EvalResultDao getEvalResultDao();

    //单词收藏的dao
    public abstract WordCollectDao getWordCollectDao();

    //选择题的dao
    public abstract MultiChoiceDao getMultiChoiceDao();

    //填空题的dao
    public abstract VoaStructureDao getVoaStructureDao();

    //做题结果的dao
    public abstract ExerciseResultDao getExerciseResultDao();

    //单词-全四册的dao
    public abstract WordFourDao getWordFourDao();

    //单词-青少版的dao
    public abstract WordJuniorDao getWordJuniorDao();

    //单词-闯关的dao
    public abstract WordPassDao getWordPassDao();

    /********************************数据库操作方法******************/
    //预存单词数据
    private static void preSaveWordData(SupportSQLiteDatabase database) {
        if (isTableExist(database, WordEntity_Four.class.getSimpleName())
                && isTableExist(database, WordEntity_Junior.class.getSimpleName())) {
            //插入数据
            String wordPreTimeTag = "wordPreDataTimer";
            String wordPreData = "preData/preData_concept_word_20240506.sql";
            if (!isDataExist(database, WordEntity_Four.class.getSimpleName(), "voaId", "4048")
                    && !isDataExist(database, WordEntity_Junior.class.getSimpleName(), "voaId", "321232")) {
                LibRxTimer.getInstance().timerInIO(wordPreTimeTag, 0, new LibRxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        LibRxTimer.getInstance().cancelTimer(wordPreTimeTag);
                        insertDataByAssetsSql(database, wordPreData);
                    }
                });
            }
        }
    }

    /********************************其他方法********************/
    //从assets中读取sql数据并且插入到数据库中(目前看来是可以的，如果存在问题请及时解决)
    private static void insertDataByAssetsSql(SupportSQLiteDatabase db, String sqlPath) {
        String startTime = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS);

        try {
            Log.d(TAG, "insertDataByAssetsSql: --start--" + startTime + "---" + sqlPath);
            //获取文件数据流
            InputStream is = AppClient.Companion.getContext().getAssets().open(sqlPath);
            //读取并且插入
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("##")) {
                    //这个是自定义的标识符，不参与数据插入
                    continue;
                }

//                if (sqlPath.equals("database/junior/preData_junior_word.sql")){
//                    Log.d(TAG, "执行操作--"+line);
//                }
                db.execSQL(line);
            }

//            RxTimer.cancelTimer(dbDataLoadTag);
            String endTime = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS);
            Log.d(TAG, "insertDataByAssetsSql: --finish--" + endTime + "---" + sqlPath);
            //回调单词界面数据
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.WORD_PASS_REFRESH,null));
        } catch (Exception e) {
            Log.d(TAG, "Inserting data failed, using network data！！！" + sqlPath);
        }
    }

    //判断表是否存在
    private static boolean isTableExist(SupportSQLiteDatabase db, String tabName) {
        boolean isTableExist = false;
        Cursor cursor = null;
        try {
            String sql = "select name from sqlite_master where type='table' and name='" + tabName + "'";
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isTableExist = true;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isTableExist;
    }

    //判断数据是否存在
    private static boolean isDataExist(SupportSQLiteDatabase db, String tableName, String columnName, String searchData) {
        boolean isColumnExist = false;
        Cursor cursor = null;
        try {
            String sql = "select * from " + tableName + " where " + columnName + " = " + searchData;
            cursor = db.query(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                isColumnExist = true;
            }
        } catch (Exception e) {
            isColumnExist = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isColumnExist;
    }
}
