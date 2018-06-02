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

    @Scheduled(fixedDelay=120000)    // every 120 seconds
    public void retrieve_480_Periodically() {
        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri("20594", "PT2H", "7023",1000);
        siriProcessService.process(result); // asynchronous invocation
    }

    @Scheduled(fixedDelay=295000)    // every 295 seconds
    public void retrieve_394_Periodically() {
        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri("28627", "PT6H", "7453",1000);
        siriProcessService.process(result); // asynchronous invocation
    }

}
