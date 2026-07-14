package de.ba.services.prometheus

import de.ba.services.GithubDataManager
import de.ba.services.github.GithubService
import de.ba.services.prometheus.exporters.MetricsExporter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class MetricUpdater(
    private val metricExporters: List<MetricsExporter>,
    private val meterRegistry: MeterRegistry,
    private val githubDataManager: GithubDataManager,
    private val githubService: GithubService
) {
    private var lastScrapeTimeStamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()

    @PostConstruct
    fun postConstruct() {
        Gauge.builder("github_ci_metrics_timestamp", this::lastScrapeTimeStamp)
            .description("Last github project metrics scraping timestamp in seconds").strongReference(true)
            .register(meterRegistry)
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(
        fixedDelayString = $$"${prometheus-exporter.update-duration}",
        initialDelayString = $$"${prometheus-exporter.update-initial-delay-duration}"
    )
    fun updateAll() {
        githubDataManager.clearAll()
        val projects = githubService.fetchAllProjects()
        metricExporters.sortedBy { it.order() }.forEach { exporter ->
            projects.forEach { project ->
                exporter.updateData(project = project)
            }
            exporter.updateMetrics()
        }
        lastScrapeTimeStamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        logger.info("End update all metrics")
    }
}