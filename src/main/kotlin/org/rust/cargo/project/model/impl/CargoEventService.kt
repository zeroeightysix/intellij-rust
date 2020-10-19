/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.model.impl

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.rust.cargo.project.model.CargoProjectsService.CargoProjectsListener
import org.rust.cargo.project.model.CargoProjectsService.Companion.CARGO_PROJECTS_TOPIC
import org.rust.cargo.runconfig.command.workingDirectory
import org.rust.stdext.mapToSet
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class CargoEventService(project: Project) {

    val metadataCallTimestamps: ConcurrentMap<Path, Long> = ConcurrentHashMap()

    init {
        project.messageBus.connect().subscribe(CARGO_PROJECTS_TOPIC, CargoProjectsListener { _, projects ->
            val projectDirs = projects.mapToSet { it.workingDirectory }
            metadataCallTimestamps.keys.removeIf { it !in projectDirs }
        })
    }

    fun onMetadataCall(projectDirectory: Path) {
        metadataCallTimestamps[projectDirectory] = System.currentTimeMillis()
    }

    fun extractTimestamp(projectDirectory: Path): Long? = metadataCallTimestamps.remove(projectDirectory)

    companion object {
        fun getInstance(project: Project): CargoEventService = project.service()
    }
}
