package com.tenpo.challenge.event;

import com.tenpo.challenge.dto.SumDto;
import org.springframework.context.ApplicationEvent;

public class SaveSumEvent extends ApplicationEvent {

    private SumDto sumDto;

    public SaveSumEvent(SumDto sumDto) {
        super(sumDto);
        this.sumDto = sumDto;
    }

    public SumDto getSumDto() {
        return sumDto;
    }

    public void setSumDto(SumDto sumDto) {
        this.sumDto = sumDto;
    }
}