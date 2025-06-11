package com.suzhou.concept.lil.data.remote.service;

import com.suzhou.concept.lil.data.remote.bean.Exercise_concept;
import com.suzhou.concept.lil.data.remote.bean.Exercise_concept_submit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 练习题接口
 */
public interface ExerciseService {

    //获取新概念的练习题内容
    //http://apps.iyuba.cn/concept/getConceptExercise.jsp?bookNum=1003
    @GET()
    Observable<Exercise_concept> getConceptExerciseData(@Url String url,
                                                        @Query("bookNum") String voaId);

    //提交新概念的练习题内容
    //http://daxue.iyuba.cn/ecollege/updateTestRecordNew.jsp?format=json&uid=&appId=222&DeviceId=02:00:00:00:00:00&appName=concept&sign=3ec89f831131db59ab5ae2186db6b9b8&jsonStr=%7B%22datalist%22%3A%5B%7B%22uid%22%3A15198673%2C%22LessonId%22%3A1003%2C%22TestNumber%22%3A1%2C%22BeginTime%22%3A%222024-01-29+13%3A43%3A29%22%2C%22UserAnswer%22%3A%22a%22%2C%22RightAnswer%22%3A%22c%22%2C%22AnswerResut%22%3A0%2C%22TestTime%22%3A%222024-01-29+13%3A43%3A34%22%2C%22AppName%22%3A%22concept%22%7D%2C%7B%22uid%22%3A15198673%2C%22LessonId%22%3A1003%2C%22TestNumber%22%3A2%2C%22BeginTime%22%3A%222024-01-29+13%3A43%3A34%22%2C%22UserAnswer%22%3A%22b%22%2C%22RightAnswer%22%3A%22c%22%2C%22AnswerResut%22%3A0%2C%22TestTime%22%3A%222024-01-29+13%3A43%3A35%22%2C%22AppName%22%3A%22concept%22%7D%2C%7B%22uid%22%3A15198673%2C%22LessonId%22%3A1003%2C%22TestNumber%22%3A3%2C%22BeginTime%22%3A%222024-01-29+13%3A43%3A35%22%2C%22UserAnswer%22%3A%22a%22%2C%22RightAnswer%22%3A%22a%22%2C%22AnswerResut%22%3A1%2C%22TestTime%22%3A%222024-01-29+13%3A43%3A36%22%2C%22AppName%22%3A%22concept%22%7D%2C%7B%22uid%22%3A15198673%2C%22LessonId%22%3A1003%2C%22TestNumber%22%3A4%2C%22BeginTime%22%3A%222024-01-29+13%3A43%3A36%22%2C%22UserAnswer%22%3A%22a%22%2C%22RightAnswer%22%3A%22b%22%2C%22AnswerResut%22%3A0%2C%22TestTime%22%3A%222024-01-29+13%3A43%3A37%22%2C%22AppName%22%3A%22concept%22%7D%2C%7B%22uid%22%3A15198673%2C%22LessonId%22%3A1003%2C%22TestNumber%22%3A5%2C%22BeginTime%22%3A%222024-01-29+13%3A43%3A37%22%2C%22UserAnswer%22%3A%22b%22%2C%22RightAnswer%22%3A%22c%22%2C%22AnswerResut%22%3A0%2C%22TestTime%22%3A%222024-01-29+13%3A43%3A44%22%2C%22AppName%22%3A%22concept%22%7D%5D%7D
    //http://daxue.iyuba.cn/ecollege/updateTestRecordNew.jsp?format=json&uid=15198673&appId=222&DeviceId=huawei&appName=concept&sign=2cb0d2d252699649b62c112e9f734b7f&jsonStr=%257B%2522datalist%2522%253A%255B%257B%2522uid%2522%253A15198673%252C%2522LessonId%2522%253A%25221003%2522%252C%2522TestNumber%2522%253A%25221%2522%252C%2522BeginTime%2522%253A%25222024-01-29%2522%252C%2522UserAnswer%2522%253A%2522WELL%2522%252C%2522RightAnswer%2522%253A%2522Is%2Bthis%2Byour%2Bshirt%253F%2522%252C%2522AnswerResut%2522%253A%25220%2522%252C%2522TestTime%2522%253A%25222024-01-29%2522%252C%2522AppName%2522%253A%2522concept%2522%257D%252C%257B%2522uid%2522%253A15198673%252C%2522LessonId%2522%253A%25221003%2522%252C%2522TestNumber%2522%253A%25222%2522%252C%2522BeginTime%2522%253A%25222024-01-29%2522%252C%2522UserAnswer%2522%253A%2522GSGSGS%2522%252C%2522RightAnswer%2522%253A%2522This%2Bis%2Bher%2Bteacher.%2522%252C%2522AnswerResut%2522%253A%25220%2522%252C%2522TestTime%2522%253A%25222024-01-29%2522%252C%2522AppName%2522%253A%2522concept%2522%257D%252C%257B%2522uid%2522%253A15198673%252C%2522LessonId%2522%253A%25221003%2522%252C%2522TestNumber%2522%253A%25223%2522%252C%2522BeginTime%2522%253A%25222024-01-29%2522%252C%2522UserAnswer%2522%253A%2522OKHBV%2522%252C%2522RightAnswer%2522%253A%2522This%2Bis%2Bnot%2Bmy%2Bumbrella.%2522%252C%2522AnswerResut%2522%253A%25220%2522%252C%2522TestTime%2522%253A%25222024-01-29%2522%252C%2522AppName%2522%253A%2522concept%2522%257D%252C%257B%2522uid%2522%253A15198673%252C%2522LessonId%2522%253A%25221003%2522%252C%2522TestNumber%2522%253A%25224%2522%252C%2522BeginTime%2522%253A%25222024-01-29%2522%252C%2522UserAnswer%2522%253A%2522HHSJSHSH%2522%252C%2522RightAnswer%2522%253A%2522This%2Bis%2Bmy%2Bskirt.%2522%252C%2522AnswerResut%2522%253A%25220%2522%252C%2522TestTime%2522%253A%25222024-01-29%2522%252C%2522AppName%2522%253A%2522concept%2522%257D%252C%257B%2522uid%2522%253A15198673%252C%2522LessonId%2522%253A%25221003%2522%252C%2522TestNumber%2522%253A%25225%2522%252C%2522BeginTime%2522%253A%25222024-01-29%2522%252C%2522UserAnswer%2522%253A%2522JSHSHSHBS%2522%252C%2522RightAnswer%2522%253A%2522This%2Bisn%2527t%2Bhis%2Bpencil.%2522%252C%2522AnswerResut%2522%253A%25220%2522%252C%2522TestTime%2522%253A%25222024-01-29%2522%252C%2522AppName%2522%253A%2522concept%2522%257D%255D%257D
    @POST
    Observable<Exercise_concept_submit> submitConceptExercise(@Url String url,
                                                              @Query("format") String format,
                                                              @Query("uid") int userId,
                                                              @Query("appId") int appId,
                                                              @Query("DeviceId") String DeviceId,
                                                              @Query("appName") String appName,
                                                              @Query("sign") String sign,
                                                              @Query("jsonStr") String dataList);
}
