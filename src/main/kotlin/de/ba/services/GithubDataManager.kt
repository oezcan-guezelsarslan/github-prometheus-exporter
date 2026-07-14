package de.ba.services

import de.ba.services.github.client.MergeRequest
import de.ba.services.github.client.Pipeline
import de.ba.services.github.client.Project
import org.springframework.stereotype.Service

@Service
class GithubDataManager {
    private val _data = mutableMapOf<String, ProjectData>()

    fun getOrCreateProject(project: Project): ProjectData {
        val groupData = _data.getOrPut(project.id) { ProjectData(project) }
        return groupData
    }

    fun addPipeline(project: Project, pipeline: Pipeline) {
        val projectData = getOrCreateProject(project)
        if (projectData.pipelines.none { it.pipeline.id == pipeline.id }) {
            projectData.pipelines.add(PipelineContainer(pipeline))
        }
    }

    fun addMergeRequest(project: Project, mergeRequest: MergeRequest) {
        val projectData = getOrCreateProject(project)
        if (projectData.mergeRequests.none { it.id == mergeRequest.id }) {
            projectData.mergeRequests.add(mergeRequest)
        }
    }


    fun getAllData() = _data.values.toList()

    fun clearAll() {
        _data.clear()
    }
}

data class PipelineContainer(
    val pipeline: Pipeline,
)

data class ProjectData(
    val project: Project,
    val pipelines: MutableList<PipelineContainer> = mutableListOf(),
    val mergeRequests: MutableList<MergeRequest> = mutableListOf(),
)
