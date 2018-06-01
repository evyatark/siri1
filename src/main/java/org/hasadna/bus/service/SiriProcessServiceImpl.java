package org.hasadna.bus.service;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SiriProcessServiceImpl implements SiriProcessService {

    @Autowired
    SiriParseService siriParseService ;

    @Autowired
    SiriPersistService siriPersistService;

    @Override
    @Async
    public void process(GetStopMonitoringServiceResponse stopMonitorResult) {
        String summary = siriParseService.parseShortSummary(stopMonitorResult) ;
        // log to file
        siriPersistService.persistShortSummary(summary);
    }
}
