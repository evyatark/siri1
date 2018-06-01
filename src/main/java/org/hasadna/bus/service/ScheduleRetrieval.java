package org.hasadna.bus.service;

import ch.qos.logback.core.util.FixedDelay;
import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleRetrieval {

    @Autowired
    SiriConsumeService siriConsumeService;

    @Autowired
    SiriProcessService siriProcessService;

    @Scheduled(fixedDelay=60000)    // every 60 seconds
    public void retrieve_480_Periodically() {
        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri("20594", "PT2H", "7023",1000);
        siriProcessService.process(result); // asynchronous invocation
    }
}
