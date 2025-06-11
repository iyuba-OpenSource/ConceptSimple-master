package com.jn.yyz.practise.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jn.yyz.practise.model.PhoneticModel;
import com.jn.yyz.practise.model.bean.PronBean;


public class PhoneticViewModel extends ViewModel {

    private PhoneticModel phoneticModel;

    private MutableLiveData<PronBean> pronBeanMLD;

    public PhoneticViewModel() {

        this.phoneticModel = new PhoneticModel();
        pronBeanMLD = new MutableLiveData<>();
    }


    public MutableLiveData<PronBean> getPronBeanMLD() {
        return pronBeanMLD;
    }

    public void requestPronNew() {

        phoneticModel.getPronNew(new PhoneticModel.Callback() {
            @Override
            public void success(PronBean pronBean) {

                pronBeanMLD.postValue(pronBean);
            }

            @Override
            public void error(Exception e) {

            }
        });
    }


}
