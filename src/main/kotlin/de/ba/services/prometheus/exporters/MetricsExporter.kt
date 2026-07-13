package de.ba.services.prometheus.exporters



interface MetricsExporter {
   // fun updateData(projectOrGroup: ProjectGroup, gitlabProject: GitlabProject)
    fun updateMetrics()
    fun order(): Int
}