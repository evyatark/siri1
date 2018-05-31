package org.hasadna.bus.service;

public interface SiriConsumeService {
    String retrieveFromSiri(String request);
    String retrieveOneStop(String stopCode, String previewInterval, int maxStopVisits);
    String retrieveSpecificLineAndStop(String stopCode, String previewInterval, String lineRef, int maxStopVisits);
    //ResponseEntity<String> postGeneric(HttpServletRequest request);
}
