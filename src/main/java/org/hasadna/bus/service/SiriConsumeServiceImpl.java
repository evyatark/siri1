package org.hasadna.bus.service;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Profile("production")
public class SiriConsumeServiceImpl implements SiriConsumeService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    final String SIRI_SERVICES_URL = "http://siri.motrealtime.co.il:8081/Siri/SiriServices";

    final String sampleRequestXml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                   xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:acsb=\"http://www.ifopt.org.uk/acsb\"\n" +
            "                   xmlns:datex2=\"http://datex2.eu/schema/1_0/1_0\" xmlns:ifopt=\"http://www.ifopt.org.uk/ifopt\"\n" +
            "                   xmlns:siri=\"http://www.siri.org.uk/siri\" xmlns:siriWS=\"http://new.webservice.namespace\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                   xsi:schemaLocation=\"http://192.241.154.128/static/siri/siri.xsd\">\n" +
            "    <SOAP-ENV:Header/>\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <siriWS:GetStopMonitoringService>\n" +
            "            <Request xsi:type=\"siri:ServiceRequestStructure\">\n" +
            "                <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
            "                <siri:RequestorRef xsi:type=\"siri:ParticipantRefStructure\">ML909091</siri:RequestorRef>\n" +
            "                <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\">0100700:1351669188:4684</siri:MessageIdentifier>\n" +
            "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
            "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
            "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
            "                    <siri:PreviewInterval>PT60M</siri:PreviewInterval>\n" +
            "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">42658</siri:MonitoringRef>\n" +
            "                    <siri:MaximumStopVisits>1000</siri:MaximumStopVisits>\n" +
            "                </siri:StopMonitoringRequest>\n" +
            "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
            "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
            "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
            "                    <siri:PreviewInterval>PT60M</siri:PreviewInterval>\n" +
            "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">42684</siri:MonitoringRef>\n" +
            "                    <siri:MaximumStopVisits>1000</siri:MaximumStopVisits>\n" +
            "                </siri:StopMonitoringRequest>\n" +
            "            </Request>\n" +
            "        </siriWS:GetStopMonitoringService>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>\n" ;

    @Override
    public String retrieveOneStop(String stopCode, String previewInterval, int maxStopVisits) {
        final String oneStopRequestXml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                   xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:acsb=\"http://www.ifopt.org.uk/acsb\"\n" +
                "                   xmlns:datex2=\"http://datex2.eu/schema/1_0/1_0\" xmlns:ifopt=\"http://www.ifopt.org.uk/ifopt\"\n" +
                "                   xmlns:siri=\"http://www.siri.org.uk/siri\" xmlns:siriWS=\"http://new.webservice.namespace\"\n" +
                "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                   xsi:schemaLocation=\"http://192.241.154.128/static/siri/siri.xsd\">\n" +
                "    <SOAP-ENV:Header/>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <siriWS:GetStopMonitoringService>\n" +
                "            <Request xsi:type=\"siri:ServiceRequestStructure\">\n" +
                "                <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                <siri:RequestorRef xsi:type=\"siri:ParticipantRefStructure\">ML909091</siri:RequestorRef>\n" +
                "                <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\">0100700:1351669188:4684</siri:MessageIdentifier>\n" +
                "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
                "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
                "                    <siri:PreviewInterval>__PREVIEW_INTERVAL__</siri:PreviewInterval>\n" +
                "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">__STOP_CODE__</siri:MonitoringRef>\n" +
                "                    <siri:MaximumStopVisits>__MAX_STOP_VISITS__</siri:MaximumStopVisits>\n" +
                "                </siri:StopMonitoringRequest>\n" +
                "            </Request>\n" +
                "        </siriWS:GetStopMonitoringService>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n" ;
        RestTemplate restTemplate = new RestTemplate();
        String url = SIRI_SERVICES_URL;
        String requestXmlString = oneStopRequestXml.replaceAll("__TIMESTAMP__", generateTimestamp())
                        .replaceAll("__MAX_STOP_VISITS__", Integer.toString(maxStopVisits))
                        .replaceAll("__PREVIEW_INTERVAL__", previewInterval)
                        .replaceAll("__STOP_CODE__", stopCode);

        HttpEntity<String> entity = new HttpEntity<String>(requestXmlString, createHeaders());
        ResponseEntity<String> r = restTemplate.postForEntity(url, entity, String.class);
        logger.info("status={}", r.getStatusCode());
        logger.info("statusCodeValue={}", r.getStatusCodeValue());
        return r.getBody();
    }

// lineRef 19740 is 947
    // localhost:9000/data/oneStop/20594/7023/PT4H - 480 Jer-TA
    @Override
    public String retrieveSpecificLineAndStop(String stopCode, String previewInterval, String lineRef, int maxStopVisits) {
        final String oneStopRequestXml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                   xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:acsb=\"http://www.ifopt.org.uk/acsb\"\n" +
                "                   xmlns:datex2=\"http://datex2.eu/schema/1_0/1_0\" xmlns:ifopt=\"http://www.ifopt.org.uk/ifopt\"\n" +
                "                   xmlns:siri=\"http://www.siri.org.uk/siri\" xmlns:siriWS=\"http://new.webservice.namespace\"\n" +
                "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "                   xsi:schemaLocation=\"http://192.241.154.128/static/siri/siri.xsd\">\n" +
                "    <SOAP-ENV:Header/>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <siriWS:GetStopMonitoringService>\n" +
                "            <Request xsi:type=\"siri:ServiceRequestStructure\">\n" +
                "                <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                <siri:RequestorRef xsi:type=\"siri:ParticipantRefStructure\">ML909091</siri:RequestorRef>\n" +
                "                <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\">0100700:1351669188:4684</siri:MessageIdentifier>\n" +
                "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
                "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
                "                    <siri:PreviewInterval>__PREVIEW_INTERVAL__</siri:PreviewInterval>\n" +
                "                    <siri:StartTime>__START1__</siri:StartTime>\n" +
                "                    <siri:LineRef>__LINE_REF__</siri:LineRef>\n" +
                "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">__STOP_CODE__</siri:MonitoringRef>\n" +
                "                    <siri:MaximumStopVisits>__MAX_STOP_VISITS__</siri:MaximumStopVisits>\n" +
                "                </siri:StopMonitoringRequest>\n" +
                "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
                "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
                "                    <siri:PreviewInterval>__PREVIEW_INTERVAL__</siri:PreviewInterval>\n" +
                "                    <siri:StartTime>__START2__</siri:StartTime>\n" +
                "                    <siri:LineRef>__LINE_REF__</siri:LineRef>\n" +
                "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">__STOP_CODE__</siri:MonitoringRef>\n" +
                "                    <siri:MaximumStopVisits>__MAX_STOP_VISITS__</siri:MaximumStopVisits>\n" +
                "                </siri:StopMonitoringRequest>\n" +
                "                <siri:StopMonitoringRequest version=\"IL2.7\" xsi:type=\"siri:StopMonitoringRequestStructure\">\n" +
                "                    <siri:RequestTimestamp>__TIMESTAMP__</siri:RequestTimestamp>\n" +
                "                    <siri:MessageIdentifier xsi:type=\"siri:MessageQualifierStructure\"></siri:MessageIdentifier>\n" +
                "                    <siri:PreviewInterval>__PREVIEW_INTERVAL__</siri:PreviewInterval>\n" +
                "                    <siri:StartTime>__START3__</siri:StartTime>\n" +
                "                    <siri:LineRef>__LINE_REF__</siri:LineRef>\n" +
                "                    <siri:MonitoringRef xsi:type=\"siri:MonitoringRefStructure\">__STOP_CODE__</siri:MonitoringRef>\n" +
                "                    <siri:MaximumStopVisits>__MAX_STOP_VISITS__</siri:MaximumStopVisits>\n" +
                "                </siri:StopMonitoringRequest>\n" +
                "            </Request>\n" +
                "        </siriWS:GetStopMonitoringService>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n" ;
        RestTemplate restTemplate = new RestTemplate();
        String url = SIRI_SERVICES_URL;
        String requestXmlString = oneStopRequestXml.replaceAll("__TIMESTAMP__", generateTimestamp())
                .replaceAll("__MAX_STOP_VISITS__", Integer.toString(maxStopVisits))
                .replaceAll("__PREVIEW_INTERVAL__", previewInterval)
                .replaceAll("__LINE_REF__", lineRef)
                .replaceAll("__STOP_CODE__", stopCode);
        String start1 = generateTimestamp( LocalDateTime.now() ) ;
        String start2 = generateTimestamp( LocalDateTime.now().plusMinutes(15) );
        String start3 = generateTimestamp( LocalDateTime.now().plusMinutes(30) );
        requestXmlString = requestXmlString.replaceAll("__START1__", start1).replaceAll("__START2__", start2).replaceAll("__START3__", start3);

        HttpEntity<String> entity = new HttpEntity<String>(requestXmlString, createHeaders());
        ResponseEntity<String> r = restTemplate.postForEntity(url, entity, String.class);
        logger.info("status={}", r.getStatusCode());
        logger.info("statusCodeValue={}", r.getStatusCodeValue());
        logger.info(r.getBody());
        return r.getBody();
    }

    @Override
    public String retrieveFromSiri(String request) {
        logger.info("timestamp={}", generateTimestamp());
        RestTemplate restTemplate = new RestTemplate();
        String url = SIRI_SERVICES_URL;
        String requestXmlString = sampleRequestXml.replaceAll("__TIMESTAMP__", generateTimestamp());
        HttpEntity<String> entity = new HttpEntity<String>(requestXmlString, createHeaders());
        //ResponseEntity<String> r = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        ResponseEntity<String> r = restTemplate.postForEntity(url, entity, String.class);
        logger.info("status={}", r.getStatusCode());
        logger.info("statusCodeValue={}", r.getStatusCodeValue());
        return r.getBody();
    }

    @Override
    public GetStopMonitoringServiceResponse retrieveSiri(String stopCode, String previewInterval, String lineRef, int maxStopVisits) {
        try {
            logger.info("retrieveSiri");
            String content = retrieveSpecificLineAndStop(stopCode, previewInterval, lineRef, maxStopVisits);

            JAXBContext jaxbContext = JAXBContext.newInstance(GetStopMonitoringServiceResponse.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(content));
            JAXBElement<GetStopMonitoringServiceResponse> je = jaxbUnmarshaller.unmarshal(streamSource, GetStopMonitoringServiceResponse.class);

            GetStopMonitoringServiceResponse response = (GetStopMonitoringServiceResponse)je.getValue();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/xml; charset=utf-8");
        return headers;
    }

    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private String generateTimestamp(LocalDateTime ldt) {
        return ldt.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
