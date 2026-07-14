package de.ba.services.github

import de.ba.services.github.client.GitHubClient
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

        val allRepositories = fetchAllUserProjects(authToken, pageSize)

        val result = mutableListOf<Project>()

        for (projectPath in githubConfiguration.projects) {
            val matched = allRepositories.filter { it.path == projectPath }
            result.addAll(matched)

        }

        return result.distinctBy { it.id }
    }

    fun latestPipeline(projectId: String, branch: String): Pipeline? {
        val token = "Bearer ${githubConfiguration.api.authentication.token}"
        val response = gitHubClient.latestPipeline(token = token, projectId = projectId, branch = branch)
        return response.pipelines.firstOrNull()
    }


    fun mergeRequests(projectId: String): List<MergeRequest> {
        val token = "Bearer ${githubConfiguration.api.authentication.token}"
        val response = gitHubClient.mergeRequests(token = token, projectId = projectId)
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