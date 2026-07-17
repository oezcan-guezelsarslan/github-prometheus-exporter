package de.ba.services.prometheus.exporters.github.branch

import de.ba.services.GithubDataManager
import de.ba.services.github.GithubService
import de.ba.services.github.client.Project
import de.ba.services.github.configuration.GithubConfiguration
import de.ba.services.prometheus.exporters.MetricsExporter
import de.ba.services.utils.toBranchMultiGauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.MultiGauge
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.AntPathMatcher


@Service
class BranchMetricsExporter(
    meterRegistry: MeterRegistry,
    private val githubService: GithubService,
    private val githubDataManager: GithubDataManager,
    private val githubConfiguration: GithubConfiguration,
) : MetricsExporter {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pathMatcher = AntPathMatcher()
    private var githubMergeRequests: MultiGauge =
        MultiGauge.builder("github_ci_branch")
            .register(meterRegistry)


    override fun updateData(project: Project) {
        try {

            logger.info("Begin update github branch metrics")
            githubService.fetchAllBranches(path = project.path).filter { branch ->
                githubConfiguration.branches.any { pattern ->
                    val matches = githubConfiguration.branches.any { pattern ->
                        pathMatcher.match(pattern, branch.name)
                    }
                    logger.info("Branch ${branch.name} matched: $matches")
                    matches
                }
            }.forEach { branch ->
                githubDataManager.addBranch(
                    project = project,
                    branch = branch
                )
            }
            logger.info("End update github branch metrics")

        } catch (ex: Exception) {
            logger.error(ex.message, ex)
        }

    }

    override fun order(): Int {
        return 1
    }

    override fun updateMetrics() {
        githubMergeRequests.register(githubDataManager.getAllData().toBranchMultiGauge(), true)
    }
}
