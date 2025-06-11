package com.suzhou.concept.dao

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.*
import com.suzhou.concept.dao.young.*
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.util.DateUtil
import com.suzhou.concept.lil.util.LibRxTimer
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
苏州爱语吧科技有限公司
 */
@Database(
    version = 17, entities = [
        LocalCollect::class,
        WordItem::class,
        EvaluationSentenceDataItem::class,
        EvaluationSentenceItem::class,
        LikeEvaluation::class,
        ConceptItem::class,
        YoungSentenceItem::class,
        YoungItem::class,
        YoungWordItem::class,
        BookItem::class,
        LikeYoung::class,
        ReDoBean::class,
        WordBreakBean::class
    ],
    autoMigrations = [
        AutoMigration(from = 3, to = 4, spec = AppDatabase.WordAutoMigration::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = AppDatabase.EvaluationMigration::class),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15),
        AutoMigration(from = 15, to = 16),
        AutoMigration(from = 16, to = 17)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    private val TAG = "AppDatabase"

    abstract fun localSentenceDao(): LocalSentenceDao
    abstract fun evaluationItemDao(): EvaluationItemDao
    abstract fun collectDao(): CollectDao
    abstract fun wordDao(): WordDao
    abstract fun likeDao(): LikeEvaluationDao
    abstract fun conceptDao(): ConceptItemDao
    abstract fun youngSentenceDao(): YoungSentenceDao
    abstract fun youngItemDao(): YoungItemDao
    abstract fun youngWordDao(): YoungWordDao
    abstract fun youngBookDao(): YoungBookDao
    abstract fun youngLikeDao(): YoungLikeDao
    abstract fun reDoDao(): ReDoRecordDao
    //单词闯关的到
    abstract fun wordBreakDao():WordBreakDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                //允许在主线程执行
                .allowMainThreadQueries()
                //清除整个数据库
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()
                .apply {
                    instance = this
                }
        }

        private val callback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                //执行数据插入操作
                preDataWord(db)
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
            }
        }

        /*************************************升级操作******************************/
        //设置单词预存的升级操作
        private fun preDataWord(database:SupportSQLiteDatabase){
            if (isTableExist(database,WordItem::class.java.simpleName)
                && isTableExist(database,YoungWordItem::class.java.simpleName)){

                val preDataTimer = "preDataTimer"
                val preDataPath = "preData/preData_concept_word.sql"

                if (!isDataExist(database,YoungWordItem::class.java.simpleName,"id","1246")){
                    LibRxTimer.getInstance().timerInIO(preDataTimer,0,object: LibRxTimer.RxActionListener{
                        override fun onAction(number: Long) {
                            LibRxTimer.getInstance().cancelTimer(preDataTimer)

                            insertDataByAssetsSql(database,preDataPath)
                        }
                    })
                }
            }
        }

        /**************************************数据预存操作****************************/
        //从assets中读取sql数据并且插入到数据库中(目前看来是可以的，如果存在问题请及时解决)
        private fun insertDataByAssetsSql(db: SupportSQLiteDatabase, sqlPath: String) {
            val startTime: String = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS)
            try {
                Log.d("数据预存", "insertDataByAssetsSql: --start--$startTime---$sqlPath")
                //获取文件数据流
                val inputStream: InputStream = AppClient.context.assets.open(sqlPath)
                //读取并且插入
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    Log.d("数据预存", line)

                    if (line.startsWith("##")) {
                        //这个是自定义的标识符，不参与数据插入
                        continue
                    }

//                if (sqlPath.equals("database/junior/preData_junior_word.sql")){
//                    Log.d(TAG, "执行操作--"+line);
//                }
                    db.execSQL(line)
                }

//            RxTimer.cancelTimer(dbDataLoadTag);
                val endTime: String = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMSS)
                Log.d("数据预存", "insertDataByAssetsSql: --finish--$endTime---$sqlPath")
                //刷新单词显示
                 EventBus.getDefault().post(RefreshEvent(RefreshEvent.WORD_REFRESH,null))
            } catch (e: Exception) {
                Log.d("数据预存", "Inserting data failed, using network data！！！$sqlPath--"+e.message)
                //刷新单词显示
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.WORD_REFRESH,null))
            }
        }

        //判断表是否存在
        private fun isTableExist(db: SupportSQLiteDatabase, tabName:String):Boolean {
            var isTableExist:Boolean = false;
            var cursor:Cursor? = null;
            try {
                val sql:String = "select name from sqlite_master where type='table' and name='" + tabName + "'";
                cursor = db.query(sql, null);
                if (cursor != null && cursor.getCount() > 0) {
                    isTableExist = true;
                }
            } catch (e:Exception) {

            } finally {
                cursor?.close()
            }
            return isTableExist;
        }

        //判断数据是否存在
        private fun isDataExist(db: SupportSQLiteDatabase, tableName: String, columnName: String, searchData: String): Boolean {
            var isColumnExist = false
            var cursor: Cursor? = null
            try {
                val sql = "select * from $tableName where $columnName = $searchData"
                cursor = db.query(sql, null)
                if (cursor != null && cursor.count > 0) {
                    isColumnExist = true
                }
            } catch (e: java.lang.Exception) {
                isColumnExist = false
            } finally {
                cursor?.close()
            }
            return isColumnExist
        }
    }

    @DeleteTable(tableName = "WordListItem")
    class WordAutoMigration : AutoMigrationSpec {}

    @RenameColumn(
        tableName = "EvaluationSentenceItem",
        fromColumnName = "show_cn",
        toColumnName = "showCn"
    )
    @DeleteColumn(tableName = "EvaluationSentenceItem", columnName = "show_cn")
    class EvaluationMigration : AutoMigrationSpec {}
}