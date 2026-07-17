package de.ba.services.github

import de.ba.services.github.client.Branch
import de.ba.services.github.client.GitHubClient
import de.ba.services.github.client.Job
import de.ba.services.github.client.MergeRequest
import de.ba.services.github.client.Pipeline
import de.ba.services.github.client.Project
import de.ba.services.github.configuration.GithubConfiguration
import org.springframework.stereotype.Service
import kotlin.collections.filter

@Service
class GithubService(
    private val gitHubClient: GitHubClient,
    private val githubConfiguration: GithubConfiguration
) {

    fun fetchAllProjects(): List<Project> {
        val authToken = "Bearer ${githubConfiguration.api.authentication.token}"
        val pageSize = githubConfiguration.api.pageSize

        return fetchAllUserProjects(authToken, pageSize)
    }

    fun fetchAllBranches(path: String): List<Branch> {
        val authToken = "Bearer ${githubConfiguration.api.authentication.token}"
        return gitHubClient.branches(token = authToken, path = path)
    }


    fun latestPipeline(path: String, branch: String): Pipeline? {
        val token = "Bearer ${githubConfiguration.api.authentication.token}"
        val response = gitHubClient.latestPipeline(token = token, path = path, branch = branch)
        return response.pipelines.firstOrNull()
    }

    fun pipelineJobs(path: String, pipeline: Pipeline): List<Job> {
        val token = "Bearer ${githubConfiguration.api.authentication.token}"
        val response = gitHubClient.jobs(token = token, path = path, pipelineId = pipeline.id)
        return response.jobs
    }


    fun mergeRequests(path: String): List<MergeRequest> {
        val token = "Bearer ${githubConfiguration.api.authentication.token}"
        val response = gitHubClient.mergeRequests(token = token, path = path)
        return response.toList()
    }

    private fun fetchAllUserProjects(authToken: String, pageSize: Int): List<Project> {
        val allRepos = mutableListOf<Project>()
        var page = 1
        var hasNextPage = true

        while (hasNextPage) {
            val response = gitHubClient.getUserRepositories(authToken, pageSize, page)
            if (response.isNotEmpty()) {
                allRepos.addAll(response)
                page++
            } else {
                hasNextPage = false
            }
        }
        return allRepos
    }
}