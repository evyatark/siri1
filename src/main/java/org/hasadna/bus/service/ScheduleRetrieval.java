package org.hasadna.bus.service;

import ch.qos.logback.core.util.FixedDelay;
import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ScheduleRetrieval {

    protected final Logger logger = LoggerFactory.getLogger(ScheduleRetrieval.class);

    @Autowired
    SiriConsumeService siriConsumeService;

    @Autowired
    SiriProcessService siriProcessService;

    @Autowired
    SortedQueue queue;

    @PostConstruct
    public void init() {
        // hard coded for now
        addScheduled("20594", "PT2H", "7023",7);    // line 480 Jer-TA
        addScheduled("28627", "PT6H", "7453",7);    // line 394 Eilat-TA
        addScheduled("42978", "PT2H", "1559",7);    // line 331 Nazaret-Haifa (working on Saturday?)

        logger.info("scheduler initialized.");
    }

//    @Scheduled(fixedDelay=120000)    // every 120 seconds
//    public void retrieve_480_Periodically() {
//        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri("20594", "PT2H", "7023",7);
//        siriProcessService.process(result); // asynchronous invocation
//    }
//
//    @Scheduled(fixedDelay=295000)    // every 295 seconds
//    public void retrieve_394_Periodically() {
//        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri("28627", "PT6H", "7453",7);
//        siriProcessService.process(result); // asynchronous invocation
//    }

    public void addScheduled(String stopCode, String previewInterval, String lineRef, int maxStopVisits) {
        queue.put(new Command(stopCode, previewInterval, lineRef, maxStopVisits));
    }

    @Scheduled(fixedDelay=60000)    // every 1 minute. This method is for ALL of the retrievals!!!
    public void retrieveCommandPeriodically() {
        try {
            logger.trace("scheduling...");
            Command c = queue.takeFromQueue();
            queue.put(c.myClone());
            GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri(c.stopCode, c.previewInterval, c.lineRef, c.maxStopVisits);
            siriProcessService.process(result); // asynchronous invocation
        }
        catch (Exception ex) {
            logger.error("absorbing unhandled exception", ex);
        }
    }
}
