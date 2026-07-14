package de.ba.services.prometheus.exporters.github.pipelines

import de.ba.services.GithubDataManager
import de.ba.services.github.GithubService
import de.ba.services.github.client.Pipeline
import de.ba.services.github.client.Project
import de.ba.services.github.configuration.GithubConfiguration
import de.ba.services.prometheus.exporters.MetricsExporter
import de.ba.services.utils.toPipelineMultiGauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.MultiGauge
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class GithubPipelineMetricsExporter(
    meterRegistry: MeterRegistry,
    private val githubService: GithubService,
    private val githubDataManager: GithubDataManager,
    private val githubConfiguration: GithubConfiguration,
) : MetricsExporter {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var githubCiPipelineId: MultiGauge =
        MultiGauge.builder("github_ci_pipeline_id")
            .register(meterRegistry)


    override fun updateData(project: Project) {
        try {
            logger.info("Begin update github pipeline metrics")
            githubConfiguration.branches.forEach { branch ->
                githubService.latestPipeline(projectId = project.id, branch = branch)
                    ?.let { pipeline ->
                        githubDataManager.addPipeline(
                            project = project,
                            pipeline = pipeline
                        )
                    }
            }

            logger.info("End update github pipeline metrics")
        } catch (ex: Exception) {
            logger.error(ex.message, ex)
        }

    }

    override fun order(): Int {
        return 1
    }

    override fun updateMetrics() {
        githubCiPipelineId.register(githubDataManager.getAllData().toPipelineMultiGauge(), true)
    }

}
