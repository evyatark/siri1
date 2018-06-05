package org.hasadna.bus.controller;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.hasadna.bus.service.SiriConsumeService;
import org.hasadna.bus.service.SiriParseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

@RestController
@RequestMapping("/data")
public class SiriController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SiriConsumeService siriConsumeService;

    @Autowired
    SiriParseService siriParseService ;


    @RequestMapping(value="/soap/oneStop/{stopCode}/{lineRef}/{previewInterval}/{maxStopVisits}", method={RequestMethod.GET}, produces = "application/xml")
    public GetStopMonitoringServiceResponse retrieveSiriDataOneStopAndLineRefAndPreviewIntervalSoap(@PathVariable String stopCode, @PathVariable String lineRef, @PathVariable String previewInterval, @PathVariable int maxStopVisits) {
        logger.info("before requesting Siri: stopCode={}, lineRef={}, previewInterval={}", stopCode, lineRef, previewInterval);
        GetStopMonitoringServiceResponse result = siriConsumeService.retrieveSiri(stopCode, previewInterval, lineRef,maxStopVisits);
        logger.info("after requesting Siri: stopCode={}, lineRef={}, previewInterval={}", stopCode, lineRef, previewInterval);
        logger.info("result:responseTimestamp={}",result.getAnswer().getResponseTimestamp());
        String summary = siriParseService.parseShortSummary(result).orElse("---\n");
        logger.info(summary);
        return result;
    }


    @RequestMapping(value="/current/{linePublishedName}", method={RequestMethod.GET}, produces = "application/xml")
    public String retrieveCurrentSiriDataForLineByPublishedName(@PathVariable String linePublishedName) {
        logger.info("before requesting Siri: linePublishedName={}", linePublishedName);
        String result = "123";// siriConsumeService.retrieveSpecificLineAndStop(stopCode, previewInterval, lineRef,1000);
        //logger.info("after requesting Siri: stopCode={}, lineRef={}, previewInterval={}", stopCode, lineRef, previewInterval);
        logger.info(result);
        return result;
    }





//    @RequestMapping(value="/sample/{dummy}", method={RequestMethod.GET}, produces = "application/xml")
//    public String retrieveSiriData(@PathVariable String dummy) {
//        logger.info("before requesting Siri: {}", dummy);
//        String result = siriConsumeService.retrieveFromSiri(dummy);
//        logger.info("after requesting Siri: {}", dummy);
//        logger.info(result);
//        return result;
//    }

    @RequestMapping(value="/oneStop/{stopCode}", method={RequestMethod.GET}, produces = "application/xml")
    public String retrieveSiriDataOneStop(@PathVariable String stopCode) {
        logger.info("before requesting Siri: stopCode={}", stopCode);
        String result = siriConsumeService.retrieveOneStop(stopCode, "PT60M", 1000);
        logger.info("after requesting Siri: stopCode={}", stopCode);
        logger.info(result);
        return result;
    }

    @RequestMapping(value="/oneStop/{stopCode}/{previewInterval}", method={RequestMethod.GET}, produces = "application/xml")
    public String retrieveSiriDataOneStopAndPreviewInterval(@PathVariable String stopCode, @PathVariable String previewInterval) {
        logger.info("before requesting Siri: stopCode={}, previewInterval={}", stopCode, previewInterval);
        String result = siriConsumeService.retrieveOneStop(stopCode, previewInterval, 1000);
        logger.info("after requesting Siri: stopCode={}, previewInterval={}", stopCode, previewInterval);
        logger.info(result);
        return result;
    }

    @RequestMapping(value="/oneStop/{stopCode}/{lineRef}/{previewInterval}/{maxStopVisits}", method={RequestMethod.GET}, produces = "application/xml")
    public String retrieveSiriDataOneStopAndLineRefAndPreviewInterval(@PathVariable String stopCode, @PathVariable String lineRef,@PathVariable String previewInterval, @PathVariable int maxStopVisits) {
        logger.info("before requesting Siri: stopCode={}, lineRef={}, previewInterval={}", stopCode, lineRef, previewInterval);
        String result = siriConsumeService.retrieveSpecificLineAndStop(stopCode, previewInterval, lineRef,1000);
        logger.info("after requesting Siri: stopCode={}, lineRef={}, previewInterval={}", stopCode, lineRef, previewInterval);
        logger.info(result);
        return result;
    }

    /**
     * passes the xml as is to Siri
     * The only replacement is of __TIMESTAMP__
     * @param xmlBody
     * @return
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String post(String xmlBody) {
        return siriConsumeService.retrieveFromSiri(xmlBody);
    }


}
