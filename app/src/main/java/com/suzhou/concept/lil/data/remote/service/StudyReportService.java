package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_result;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 学习报告数据上传
 */
public interface StudyReportService {

    //上传单词的闯关学习报告
    //http://daxue.iyuba.cn/ecollege/updateExamRecordNew.jsp
    //{
    //	"DeviceId": "02:00:00:00:00:00",
    //	"appId": 260,
    //	"format": "json",
    //	"lesson": "206",
    //	"mode": 2,
    //	"scoreList": [],
    //	"sign": "2b1049f6a06b6f8193b5a92994e49857",
    //	"testList": [{
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "chair",
    //		"TestId": 1,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "chair"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "where",
    //		"TestId": 7,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "where"
    //	}, {
    //		"AnswerResut": 1,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "desk",
    //		"TestId": 2,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "desk"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "on",
    //		"TestId": 4,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "on"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "under",
    //		"TestId": 6,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "under"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "in",
    //		"TestId": 5,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "in"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "the",
    //		"TestId": 8,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "the"
    //	}, {
    //		"AnswerResut": 0,
    //		"BeginTime": "2024-04-08",
    //		"Category": "单词闯关",
    //		"LessonId": "1",
    //		"RightAnswer": "blackboard",
    //		"TestId": 3,
    //		"TestMode": "W",
    //		"TestTime": "2024-04-08",
    //		"UserAnswer": "blackboard"
    //	}],
    //	"uid": "15351268"
    //}
    @POST
    Observable<Report_wordBreak_result> submitWordBreakReport(@Url String url,
                                                              @Body RequestBody body);
}
