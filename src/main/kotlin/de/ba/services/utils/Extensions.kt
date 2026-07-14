package de.ba.services.utils

import de.ba.services.ProjectData
import de.ba.services.github.client.Pipeline
import de.ba.services.github.client.Project
import io.micrometer.core.instrument.MultiGauge.Row
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags

const val STATUS_LABEL = "status"
const val CONCLUSION_LABEL = "conclusion"
const val REF_LABEL = "ref"
const val PATH_LABEL = "path"
const val PROJECT_ID_LABEL = "project_id"
const val PIPELINE_ID_LABEL = "pipeline_id"
const val SERVICE_LABEL = "service"

fun List<ProjectData>.toPipelineMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.pipelines.forEach { pipelineContainer ->
                rows.add(
                    Row.of(
                        Tags.of(
                            REF_LABEL,
                            pipelineContainer.pipeline.branch,
                            STATUS_LABEL,
                            pipelineContainer.pipeline.status,
                            PIPELINE_ID_LABEL,
                            pipelineContainer.pipeline.id,
                            CONCLUSION_LABEL,
                            pipelineContainer.pipeline.conclusion ?: pipelineContainer.pipeline.status
                        ).addProjectTags(projectData.project), 1
                    )
                )
            }

    }
    return rows
}

private fun Tags.addProjectTags(project: Project): Tags {
    val tags = mutableListOf<Tag>()
    val pathParts = project.path.split("/")
    tags.add(
        Tag.of(
            SERVICE_LABEL,
            pathParts[pathParts.size - 1]
        )
    )
    tags.add(
        Tag.of(
           PATH_LABEL,
            project.path
        )
    )
    tags.add(
        Tag.of(
            PROJECT_ID_LABEL,
            project.id
        )
    )
    return this.and(tags)
}
