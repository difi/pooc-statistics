package no.difi.statistics.api;

import no.difi.statistics.model.TimeSeriesPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;

@RestController
public class StatisticsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Statistics statistics;

    public StatisticsController(Statistics statistics) {
        this.statistics = statistics;
    }

    @ExceptionHandler
    public void handleException(HttpRequest request, Exception exception) {
        logger.error("Request " + request.getMethod() + " " + request.getURI() + " failed", exception);
    }

    @GetMapping("/")
    public String index() throws IOException {
        return format(
                "Statistics API version %s",
                System.getProperty("difi.version", "N/A")
        );
    }

    @GetMapping("minutes/{seriesName}/{type}")
    public List<TimeSeriesPoint> minutes(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return statistics.minutes(seriesName, type, from, to);
    }

    @PostMapping("minutes/{seriesName}/{type}")
    public List<TimeSeriesPoint> minutesAbovePercentile(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to,
            @RequestBody TimeSeriesFilter filter
    ) {
        return statistics.minutes(seriesName, type, from, to, filter);
    }

    @GetMapping("hours/{seriesName}/{type}")
    public List<TimeSeriesPoint> hours(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return statistics.hours(seriesName, type, from, to);
    }

    @GetMapping("days/{seriesName}/{type}")
    public List<TimeSeriesPoint> days(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return statistics.days(seriesName, type, from, to);
    }

    @GetMapping("months/{seriesName}/{type}")
    public List<TimeSeriesPoint> months(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return statistics.months(seriesName, type, from, to);
    }

    @GetMapping("years/{seriesName}/{type}")
    public List<TimeSeriesPoint> years(
            @PathVariable String seriesName,
            @PathVariable String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to
    ) {
        return statistics.years(seriesName, type, from, to);
    }

}
