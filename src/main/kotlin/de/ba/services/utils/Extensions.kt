package de.ba.services.utils

import de.ba.services.ProjectData
import de.ba.services.github.client.Project
import io.micrometer.core.instrument.MultiGauge.Row
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import java.time.Duration

const val PROJECT_ID_LABEL = "project_id"
const val PATH_LABEL = "path"
const val SERVICE_LABEL = "service"
const val DURATION_LABEL = "duration"

//Pipeline labels
const val PIPELINE_ID_LABEL = "pipeline_id"
const val REF_LABEL = "ref"
const val STATUS_LABEL = "status"

//Job labels
const val JOB_ID = "job_id"
const val JOB_NAME = "name"

//Step labels
const val STEP_JOB_ID = "job_id"
const val STEP_NAME = "name"
const val STEP_NUMBER = "number"


//Merge request labels
const val MERGE_REQUEST_ID_LABEL = "merge_request_id"
const val MERGE_REQUEST_STATE_LABEL = "state"
const val MERGE_REQUEST_SOURCE_LABEL = "source"
const val MERGE_REQUEST_TARGET_LABEL = "target"

//Branch request labels
const val BRANCH_NAME = "ref"
const val BRANCH_PROTECTED = "protected"


fun List<ProjectData>.toBranchMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.branches.forEach { branch ->
            rows.add(
                Row.of(
                    Tags.of(
                        BRANCH_NAME,
                        branch.name,
                        BRANCH_PROTECTED,
                        branch.protected.toString(),
                    ).addProjectTags(projectData.project), 1
                )
            )
        }
    }
    return rows
}

fun List<ProjectData>.toMergeRequestMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.mergeRequests.forEach { mergeRequest ->
            rows.add(
                Row.of(
                    Tags.of(
                        MERGE_REQUEST_ID_LABEL,
                        mergeRequest.id,
                        MERGE_REQUEST_STATE_LABEL,
                        mergeRequest.state,
                        MERGE_REQUEST_SOURCE_LABEL,
                        mergeRequest.sourceBranch.name,
                        MERGE_REQUEST_TARGET_LABEL,
                        mergeRequest.targetBranch.name
                    ).addProjectTags(projectData.project), 1
                )
            )
        }
    }
    return rows
}

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
                        pipelineContainer.pipeline.conclusion ?: pipelineContainer.pipeline.status,
                        PIPELINE_ID_LABEL,
                        pipelineContainer.pipeline.id,
                        DURATION_LABEL,
                        pipelineContainer.pipeline.updatedAt?.let { updatedAt ->
                            Duration.between(pipelineContainer.pipeline.createdAt, updatedAt).toSeconds().toString()
                        } ?: "0"
                    ).addProjectTags(projectData.project), 1
                )
            )
        }

    }
    return rows
}

fun List<ProjectData>.toPipelineDurationMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.pipelines.forEach { pipelineContainer ->
            rows.add(
                Row.of(
                    Tags.of(
                        REF_LABEL,
                        pipelineContainer.pipeline.branch,
                        STATUS_LABEL,
                        pipelineContainer.pipeline.conclusion ?: pipelineContainer.pipeline.status,
                        PIPELINE_ID_LABEL,
                        pipelineContainer.pipeline.id,
                        DURATION_LABEL,
                        pipelineContainer.pipeline.updatedAt?.let { updatedAt ->
                            Duration.between(pipelineContainer.pipeline.createdAt, updatedAt).toSeconds().toString()
                        } ?: "0"
                    ).addProjectTags(projectData.project), pipelineContainer.pipeline.updatedAt?.let { updatedAt ->
                        Duration.between(pipelineContainer.pipeline.createdAt, updatedAt).toSeconds()
                    } ?: 0
                )
            )
        }

    }
    return rows
}

fun List<ProjectData>.toJobMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.pipelines.forEach { pipelineContainer ->
            pipelineContainer.pipeline.jobs.forEach { job ->
                rows.add(
                    Row.of(
                        Tags.of(
                            JOB_ID,
                            job.id,
                            JOB_NAME,
                            job.name,
                            STATUS_LABEL,
                            job.conclusion ?: job.status,
                            PIPELINE_ID_LABEL,
                            job.runId,
                            REF_LABEL,
                            pipelineContainer.pipeline.branch
                        ).addProjectTags(projectData.project), job.id.toLong()
                    )
                )
            }
        }
    }
    return rows
}

fun List<ProjectData>.toJobStatusMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.pipelines.forEach { pipelineContainer ->
            pipelineContainer.pipeline.jobs.forEach { job ->
                rows.add(
                    Row.of(
                        Tags.of(
                            JOB_ID,
                            job.id,
                            JOB_NAME,
                            job.name,
                            STATUS_LABEL,
                            job.conclusion ?: job.status,
                            PIPELINE_ID_LABEL,
                            job.runId,
                            REF_LABEL,
                            pipelineContainer.pipeline.branch,
                            DURATION_LABEL,
                            job.completedAt?.let { completedAt ->
                                Duration.between(job.startedAt, completedAt).toSeconds().toString()
                            } ?: 0.toString()
                        ).addProjectTags(projectData.project), 1
                    )
                )
            }
        }
    }
    return rows
}


fun List<ProjectData>.toStepMultiGauge(): List<Row<Number>> {
    val rows = mutableListOf<Row<Number>>()
    this.forEach { projectData ->
        projectData.pipelines.forEach { pipelineContainer ->
            pipelineContainer.pipeline.jobs.forEach { job ->
                job.steps.forEach { step ->
                    rows.add(
                        Row.of(
                            Tags.of(
                                STEP_JOB_ID,
                                job.id,
                                STEP_NAME,
                                step.name,
                                STEP_NUMBER,
                                step.number,
                                STATUS_LABEL,
                                step.conclusion ?: step.status,
                                PIPELINE_ID_LABEL,
                                job.runId,
                                REF_LABEL,
                                pipelineContainer.pipeline.branch,
                                DURATION_LABEL,
                                step.completedAt?.let { completedAt ->
                                    Duration.between(step.startedAt, completedAt).toSeconds().toString()
                                } ?: 0.toString()
                            ).addProjectTags(projectData.project), 1
                        )
                    )
                }
            }
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
