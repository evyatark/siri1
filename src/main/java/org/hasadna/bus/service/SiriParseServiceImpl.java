package org.hasadna.bus.service;

import org.hasadna.bus.entity.GetStopMonitoringServiceResponse;
import org.springframework.stereotype.Component;
import uk.org.siri.siri.MonitoredStopVisitStructure;
import uk.org.siri.siri.StopMonitoringDeliveriesStructure;
import uk.org.siri.siri.StopMonitoringDeliveryStructure;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class SiriParseServiceImpl implements SiriParseService {
    @Override
    public String parseShortSummary(GetStopMonitoringServiceResponse sm) {
//        if (!sm.getAnswer().getStatus()) {
//            return "---";
//        }
        String s = "";
        BigDecimal lon = BigDecimal.ZERO;
        BigDecimal lat = BigDecimal.ZERO;
        //DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        //LocalDateTime ldt = sm.getAnswer().getResponseTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String date = formatDate(sm.getAnswer().getResponseTimestamp());
        s = s + date + "\n";
        for (StopMonitoringDeliveryStructure smd : sm.getAnswer().getStopMonitoringDelivery()) {
            for (MonitoredStopVisitStructure visit : smd.getMonitoredStopVisit()) {
                String lineRef = visit.getMonitoredVehicleJourney().getLineRef().getValue();
                visit.getMonitoredVehicleJourney().getMonitoredCall().isVehicleAtStop();
                Date expectedArrivalTime = visit.getMonitoredVehicleJourney().getMonitoredCall().getExpectedArrivalTime();
                if (visit.getMonitoredVehicleJourney().getVehicleLocation() != null) {
                    lon = visit.getMonitoredVehicleJourney().getVehicleLocation().getLongitude();
                    lat = visit.getMonitoredVehicleJourney().getVehicleLocation().getLatitude();
                }
                String lineName = visit.getMonitoredVehicleJourney().getPublishedLineName().getValue();
                visit.getMonitoredVehicleJourney().getDestinationRef();
                String licensePlate = visit.getMonitoredVehicleJourney().getVehicleRef().getValue();
                Date recordedAt = visit.getRecordedAtTime();

                String rep = stringRepresentation(lineRef, lineName, recordedAt, expectedArrivalTime, licensePlate, lon, lat);
                s = s + rep + "\n";
            }
        }
        return s;
    }

    private String stringRepresentation(String lineRef, String lineName, Date recordedAt, Date expectedArrivalTime, String licensePlate, BigDecimal lon, BigDecimal lat) {
        String s = MessageFormat.format("line {0} vehicle {1} will arrive at {2} ({3}:({4},{5})",
                lineName, licensePlate,
                formatTime(expectedArrivalTime),
                formatTime(recordedAt),
                lon.toString(), lat.toString()
                );
        return s ;
    }

    private String formatDate(Date date) {
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String asStr = DateTimeFormatter.ISO_DATE_TIME.format(ldt);
        return asStr;
    }

    private String formatTime(Date date) {
        String dateTime = formatDate(date);
        return dateTime.split("T")[1];
    }
}
