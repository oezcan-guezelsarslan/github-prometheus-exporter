package de.ba.services

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
)
