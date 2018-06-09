package org.hasadna.bus.service.gtfs;

import org.hasadna.bus.entity.gtfs.Route;
import org.hasadna.bus.entity.gtfs.Stop;
import org.hasadna.bus.entity.gtfs.StopTimes;
import org.hasadna.bus.entity.gtfs.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ReadRoutesFile {

    private String dirOfGtfsFiles = "/home/evyatar/Downloads/israel-public-transportation May 2018/" ;
    private String fileName = "routes.txt";
    private String fullPath = dirOfGtfsFiles + fileName;

    private String tripsFileName = "trips.txt";
    private String tripsFullPath = dirOfGtfsFiles + tripsFileName;

    private String stopTimesFileName = "stop_times.txt";
    private String stopTimesFullPath = dirOfGtfsFiles + stopTimesFileName;

    private String stopsFileName = "stops.txt";
    private String stopsFullPath = dirOfGtfsFiles + stopsFileName;

    protected final static Logger logger = LoggerFactory.getLogger("console");

    public static void main(String[] args)
    {

        ReadRoutesFile rrf = new ReadRoutesFile();
        rrf.findRouteByPublishedName("597", Optional.of("בית שמש")).forEach(r -> logger.info(r.toString()) );
        //List<Route> routeIds = rrf.findRouteByPublishedName("597", Optional.of("בית שמש"));
        String routeId = "15454";
        List<Trip> trips = rrf.findTrips(routeId);
        Set<String> lastStopsInTrip = new HashSet<>();
        lastStopsInTrip = trips.
                stream().
                map(trip -> rrf.findLastStopIdOfTrip(trip.tripId)).
                map(stopTimes -> { System.out.print("+"); return stopTimes;}).
                map(stopTimes -> stopTimes.stopId).
                collect(Collectors.toSet());
        //rrf.findStopTimes("24155063_130117");
        lastStopsInTrip.forEach(stopId -> logger.info("stopId of last stop: {}", stopId));
        List<String> stopCodes = lastStopsInTrip.stream().map(stId -> rrf.findStopCode(stId)).collect(Collectors.toList());
        logger.info("stopCodes (last stop in route {}): {}", routeId, stopCodes);
    }

    private static List<StopTimes> cacheStopTimes = null;

    public List<StopTimes> findStopTimes(String tripId) {
        if (!(Paths.get(stopTimesFullPath).toFile().exists() &&
                Paths.get(stopTimesFullPath).toFile().canRead())) {
            logger.error("file {} does not exist or can't be read", stopTimesFullPath);
            return new ArrayList<>();
        }
        try {
            // This will read content of file to memory!
            if (cacheStopTimes == null) {
                logger.info("...");
                cacheStopTimes = Files.lines(Paths.get(stopTimesFullPath)).
                        map(ReadRoutesFile::parseStopTimesLine).
                        flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).
                        //map(st -> { System.out.print(","); return st;}).
                        collect(Collectors.toList());
                logger.info("!");
            }
            List<StopTimes> stopTimes = cacheStopTimes.stream().
                    filter(stopTime -> stopTime.tripId.equals(tripId)).
                    //map(st -> { System.out.print("."); return st;}).
                    collect(Collectors.toList());
            return stopTimes;
        } catch (IOException e) {
            logger.error("", e);
            return new ArrayList<>();
        }
    }

    public StopTimes findLastStopIdOfTrip(String tripId) {
        List<StopTimes> stopTimes = findStopTimes(tripId);
        StopTimes lastStop = findLastStopInSequence(stopTimes);
        //logger.trace("tripId={}, lastStop: {}", tripId, lastStop);
        return lastStop;
    }

    private StopTimes findLastStopInSequence(List<StopTimes> stopTimes) {
        return stopTimes.
                stream().
                sorted((st1, st2) -> {return st2.stopSequence - st1.stopSequence;}).    // reversed order!!
                findFirst().
                get();
    }

    public Stop findStopById(String stopId) {
        if (!(Paths.get(stopsFullPath).toFile().exists() &&
                Paths.get(stopsFullPath).toFile().canRead())) {
            logger.error("file {} does not exist or can't be read", stopsFullPath);
        }
        try {
            Stop stop = Files.
                    lines(Paths.get(stopsFullPath)).
                    map(ReadRoutesFile::parseStopLine).
                    flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).
                    filter(st -> st.stopId.equals(stopId)).
                    collect(Collectors.toList()).get(0);
            //logger.debug("{}", stop);
            return stop;
        } catch (IOException ex) {
            logger.error("", ex);
            return null;
        }
    }

    public String findStopCode(String stopId) {
        Stop stop = findStopById(stopId);
        logger.debug("{}", stop);
        return stop.stopCode;
    }


    public List<Trip> findTrips(String routeId) {
        if (!(Paths.get(tripsFullPath).toFile().exists() &&
                Paths.get(tripsFullPath).toFile().canRead())) {
            logger.error("file {} does not exist or can't be read", tripsFullPath);
        }
        try {
            List<Trip> trips = Files.
                    lines(Paths.get(tripsFullPath)).
                    map(ReadRoutesFile::parseTripsLine).
                    flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).
                    filter(trip -> trip.routeId.equals(routeId)).
                    collect(Collectors.toList());
            logger.debug("{}",trips.toString());//.replaceAll("},", "}\n").replace("[", "[\n "));
            return trips;
        } catch (IOException ex) {
            logger.error("", ex);
            return new ArrayList<>();
        }
    }

    public List<Route> findRouteByPublishedName(String linePublishedName, Optional<String> cityInRouteName) {

        Path p = Paths.get(fullPath);
        p.toFile().exists();

        p.toFile().canRead();

        try {
            Stream<String> stream = Files.lines(Paths.get(fullPath));

            List<Route> routes =
                stream.
                    map(ReadRoutesFile::parseLine).
                    flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).
                    filter(route -> route.routeShortName.equals(linePublishedName)).
                    collect(Collectors.toList());
            if (cityInRouteName.isPresent()) {
                routes = routes.stream().filter(route -> route.routeLongName.contains(cityInRouteName.get())).collect(Collectors.toList());
            }
            return routes;
        } catch (IOException ex) {
            logger.error("", ex);
            return new ArrayList<>();
        }
    }

    private static Optional<Route> parseLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return Optional.empty();
        }
        String[] ss = line.split(",");
        // route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_color
        Route r = new Route(ss[0], ss[1], ss[2], ss[3], ss[4]);
        // TODO route_type, route_color
        return Optional.of(r);
    }

    private static Optional<Trip> parseTripsLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return Optional.empty();
        }
        String[] ss = line.split(",");
        // route_id,service_id,trip_id,direction_id,shape_id
        String shapeId = null;
        if (ss.length >= 5) {
            shapeId = ss[4];
        }
        Trip r = new Trip(ss[2], ss[1], ss[0], ss[3], shapeId);
        return Optional.of(r);
    }

    private static Optional<Stop> parseStopLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return Optional.empty();
        }
        String[] ss = line.split(",");
        // stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,location_type,parent_station,zone_id
        String shapeId = null;
        if (ss.length >= 5) {
            shapeId = ss[4];
        }
        Stop r = new Stop(ss[0], ss[1], ss[2], ss[3], ss[4], ss[5]);
        return Optional.of(r);
    }

    private static Optional<StopTimes> parseStopTimesLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return Optional.empty();
        }
        try {
            String[] ss = line.split(",");
            // trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type,shape_dist_traveled
            // 24043234_130117,14:20:00,14:20:00,36782,1,0,1,0
            StopTimes r = new StopTimes(ss[0], ss[1], ss[2], ss[3], Integer.parseInt(ss[4]), ss[5], ss[6], ss[7]);
            return Optional.of(r);
        }
        catch (Exception ex) {
            return Optional.empty();
        }
    }

}
