package de.ba.services.prometheus.exporters.github.mergerequest

import de.ba.services.GithubDataManager
import de.ba.services.github.GithubService
import de.ba.services.github.client.Project
import de.ba.services.prometheus.exporters.MetricsExporter
import de.ba.services.utils.toMergeRequestMultiGauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.MultiGauge
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class GithubMergeRequestsMetricsExporter(
    meterRegistry: MeterRegistry,
    private val githubService: GithubService,
    private val githubDataManager: GithubDataManager,
) : MetricsExporter {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var githubMergeRequests: MultiGauge =
        MultiGauge.builder("github_ci_merge_request")
            .register(meterRegistry)


    override fun updateData(project: Project) {
        try {
            logger.info("Begin update github merge requests metrics")
            githubService.mergeRequests(projectId = project.id).forEach { mergeRequest ->
                githubDataManager.addMergeRequest(
                    project = project,
                    mergeRequest = mergeRequest
                )
            }
            logger.info("End update github merge requests metrics")

        } catch (ex: Exception) {
            logger.error(ex.message, ex)
        }

    }

    override fun order(): Int {
        return 2
    }

    override fun updateMetrics() {
        githubMergeRequests.register(githubDataManager.getAllData().toMergeRequestMultiGauge(), true)
    }
}
