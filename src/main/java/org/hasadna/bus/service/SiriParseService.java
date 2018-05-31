package org.hasadna.bus.service;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;

public interface SiriParseService {
    String parseShortSummary(GetStopMonitoringServiceResponse sm);
}
