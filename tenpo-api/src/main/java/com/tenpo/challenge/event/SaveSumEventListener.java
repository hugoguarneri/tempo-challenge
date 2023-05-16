package com.tenpo.challenge.event;

import com.tenpo.challenge.dto.SumDto;
import com.tenpo.challenge.service.SumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SaveSumEventListener implements ApplicationListener<SaveSumEvent> {

    private final SumService sumService;

    @Autowired
    public SaveSumEventListener(SumService sumService) {
        this.sumService = sumService;
    }

    @Override
    public void onApplicationEvent(SaveSumEvent event) {
        SumDto sumDto = event.getSumDto();
        sumService.save(sumDto);
    }
}
