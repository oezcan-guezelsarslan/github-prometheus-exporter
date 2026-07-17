package de.ba.services.github.client

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @GetMapping("/repos/{id}/branches")
    fun branches(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") path: String
    ): List<Branch>


    @GetMapping("/repos/{id}/actions/runs")
    fun latestPipeline(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") path: String,
        @RequestParam("branch") branch: String,
        @RequestParam("per_page") page: Int = 1
    ): Run

    @GetMapping("/repos/{id}/actions/runs/{pipelineId}/jobs")
    fun jobs(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") path: String,
        @PathVariable("pipelineId") pipelineId: String,
    ): JobResponse

    @GetMapping("/repos/{id}/pulls")
    fun mergeRequests(
        @RequestHeader("Authorization") token: String,
        @PathVariable("id") path: String, // "open", "closed", or "all"
        @RequestParam("per_page", defaultValue = "30") perPage: Int=30,
        @RequestParam("page", defaultValue = "1") page: Int=1
    ): List<MergeRequest>

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

data class MergeRequest(
    @param:JsonProperty("id")
    @field:JsonProperty("id")
    val id: String,

    @param:JsonProperty("state")
    @field:JsonProperty("state")
    val state: String, // e.g., "open" or "closed"

    @param:JsonProperty("head")
    @field:JsonProperty("head")
    val sourceBranch: BranchInfo, // Contains source branch details

    @param:JsonProperty("base")
    @field:JsonProperty("base")
    val targetBranch: BranchInfo  // Contains target branch details
)

data class Branch(
    @param:JsonProperty("name")
    @field:JsonProperty("name")
    val name: String,
    @param:JsonProperty("protected")
    @field:JsonProperty("protected")
    val protected: Boolean
)


data class BranchInfo(
    @param:JsonProperty("ref")
    @field:JsonProperty("ref")
    val name: String,
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
    val conclusion: String?
) {
    // Moved to the class body so Jackson ignores it during instantiation
    @JsonIgnore
    var jobs: MutableList<Job> = mutableListOf()
}

data class JobResponse(

    @param:JsonProperty("jobs")
    @field:JsonProperty("jobs")
    val jobs: List<Job>,
)

data class Job(
    @param:JsonProperty("id")
    @field:JsonProperty("id")
    val id: String,
    @param:JsonProperty("run_id")
    @field:JsonProperty("run_id")
    val runId: String,

    @param:JsonProperty("name")
    @field:JsonProperty("name")
    val name: String,

    @param:JsonProperty("head_branch")
    @field:JsonProperty("head_branch")
    val branch: String,

    @param:JsonProperty("status")
    @field:JsonProperty("status")
    val status: String,

    @param:JsonProperty("conclusion")
    @field:JsonProperty("conclusion")
    val conclusion: String?,

    @param:JsonProperty("steps")
    @field:JsonProperty("steps")
    val steps: List<Step>,

)

data class Step(

    @param:JsonProperty("name")
    @field:JsonProperty("name")
    val name: String,

    @param:JsonProperty("number")
    @field:JsonProperty("number")
    val number: String,

    @param:JsonProperty("status")
    @field:JsonProperty("status")
    val status: String,

    @param:JsonProperty("conclusion")
    @field:JsonProperty("conclusion")
    val conclusion: String?,
)


