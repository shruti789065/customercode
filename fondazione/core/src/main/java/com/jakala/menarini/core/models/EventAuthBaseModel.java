package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.EventEnrollment;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class EventAuthBaseModel extends AuthBaseModel {



    private List<EventEnrollment> userEventList = new ArrayList<>();


    @Override
    @PostConstruct
    protected void init()  {
        super.init();
        if(this.isAuth()) {

        }

    }

}
