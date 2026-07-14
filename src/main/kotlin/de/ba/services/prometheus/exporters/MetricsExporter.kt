package de.ba.services.prometheus.exporters

import de.ba.services.github.client.Project


interface MetricsExporter {
    fun updateData(project: Project)
    fun updateMetrics()
    fun order(): Int
}