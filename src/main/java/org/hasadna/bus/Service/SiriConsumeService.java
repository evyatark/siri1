package org.hasadna.bus.Service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SiriConsumeService {
    String retrieveFromSiri(String request);
    String retrieveOneStop(String stopCode, String previewInterval, int maxStopVisits);
    String retrieveSpecificLineAndStop(String stopCode, String previewInterval, String lineRef, int maxStopVisits);
    //ResponseEntity<String> postGeneric(HttpServletRequest request);
}
