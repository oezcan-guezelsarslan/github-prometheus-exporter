package de.ba.services.prometheus.exporters.github.pipelines

import de.ba.services.GithubDataManager
import de.ba.services.github.GithubService
import de.ba.services.github.client.Project
import de.ba.services.github.configuration.GithubConfiguration
import de.ba.services.prometheus.exporters.MetricsExporter
import de.ba.services.utils.toJobMultiGauge
import de.ba.services.utils.toJobStatusMultiGauge
import de.ba.services.utils.toPipelineMultiGauge
import de.ba.services.utils.toStepMultiGauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.MultiGauge
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher


@Service
class GithubPipelineMetricsExporter(
    meterRegistry: MeterRegistry,
    private val githubService: GithubService,
    private val githubDataManager: GithubDataManager,
    private val githubConfiguration: GithubConfiguration,
) : MetricsExporter {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pathMatcher = AntPathMatcher()

    private var githubCiPipeline: MultiGauge =
        MultiGauge.builder("github_ci_pipeline")
            .register(meterRegistry)
    private var githubCiPipelineJob: MultiGauge =
        MultiGauge.builder("github_ci_job")
            .register(meterRegistry)
    private var githubCiPipelineJobStatus: MultiGauge =
        MultiGauge.builder("github_ci_job_status")
            .register(meterRegistry)
    private var githubCiPipelineStep: MultiGauge =
        MultiGauge.builder("github_ci_step")
            .register(meterRegistry)


    override fun updateData(project: Project) {
        try {
            logger.info("Begin update github pipeline metrics")

            githubService.fetchAllBranches(path = project.path).filter { branch ->
                githubConfiguration.branches.any { pattern ->
                    pathMatcher.match(pattern, branch.name)
                }
            }.forEach { branch ->
                githubService.latestPipeline(path = project.path, branch = branch.name)
                    ?.let { pipeline ->
                        val jobs = githubService.pipelineJobs(path = project.path, pipeline = pipeline)
                        pipeline.jobs.addAll(jobs)
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
        githubCiPipeline.register(githubDataManager.getAllData().toPipelineMultiGauge(), true)
        githubCiPipelineJob.register(githubDataManager.getAllData().toJobMultiGauge(), true)
        githubCiPipelineStep.register(githubDataManager.getAllData().toStepMultiGauge(), true)
        githubCiPipelineJobStatus.register(githubDataManager.getAllData().toJobStatusMultiGauge(), true)
    }

}
