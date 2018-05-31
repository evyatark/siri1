package org.hasadna.bus.service;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;

import javax.xml.soap.SOAPMessage;

public interface SiriConsumeService {
    String retrieveFromSiri(String request);
    String retrieveOneStop(String stopCode, String previewInterval, int maxStopVisits);
    String retrieveSpecificLineAndStop(String stopCode, String previewInterval, String lineRef, int maxStopVisits);
    GetStopMonitoringServiceResponse retrieveSiri(String stopCode, String previewInterval, String lineRef, int maxStopVisits);
}
