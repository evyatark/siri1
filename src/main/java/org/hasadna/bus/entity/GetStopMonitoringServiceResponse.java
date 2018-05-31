package org.hasadna.bus.entity;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetStopMonitoringServiceResponseStructure", namespace = "http://new.webservice.namespace")
@XmlRootElement
public class GetStopMonitoringServiceResponse {

    @XmlElement(name = "Answer")
    StopMonitoringAnswer answer;


    public StopMonitoringAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(StopMonitoringAnswer answer) {
        this.answer = answer;
    }
}
