package org.hasadna.bus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SiriPersistFileService implements SiriPersistService {

    // this logger should be defined to a specific file
    protected final Logger logger = LoggerFactory.getLogger("SiriRealTimeData");

    @Override
    public void persistShortSummary(String summary) {
        logger.info(summary);
    }
}
