package de.ba.services.github.client

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam


@FeignClient(name = "githubClient", url = "\${prometheus-exporter.github.api.url}")
interface GitHubClient {

    @GetMapping("/user/repos")
    fun getUserRepositories(
        @RequestHeader("Authorization") token: String,
        @RequestParam("per_page") pageSize: Int,
        @RequestParam("page") page: Int
    ): List<Project>

    @GetMapping("/repositories/{id}/actions/runs")
    fun latestPipeline(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") projectId: String,
        @RequestParam("branch") branch: String,
        @RequestParam("per_page") page: Int=1
    ): Run

}



data class Project(
    @param:JsonProperty("id")
    @field:JsonProperty("id")
    val id: String,
    @param:JsonProperty("name")
    @field:JsonProperty("name")
    val name: String,

    @param:JsonProperty("full_name")
    @field:JsonProperty("full_name")
    val path: String,
)

data class Run(
    @param:JsonProperty("workflow_runs")
    @field:JsonProperty("workflow_runs")
    val pipelines: List<Pipeline>,
)
data class Pipeline(
    @param:JsonProperty("id")
    @field:JsonProperty("id")
    val id: String,
    @param:JsonProperty("head_branch")
    @field:JsonProperty("head_branch")
    val branch: String,

    @param:JsonProperty("status")
    @field:JsonProperty("status")
    val status: String,

    @param:JsonProperty("conclusion")
    @field:JsonProperty("conclusion")
    val conclusion: String?,
)
