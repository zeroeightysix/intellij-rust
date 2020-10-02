/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import org.rust.cargo.project.settings.toolchain
import org.rust.ide.inspections.InspectionRequirement.*
import org.rust.ide.utils.isEnabledByCfg
import org.rust.lang.core.psi.RsFile
import org.rust.lang.core.psi.RsVisitor

abstract class RsLocalInspectionTool : LocalInspectionTool() {
    final override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        val file = session.file
        return if (file is RsFile && requirements.all { it.check(file) }) {
            buildVisitor(holder, isOnTheFly)
        } else {
            PsiElementVisitor.EMPTY_VISITOR
        }
    }

    final override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        buildVisitor(RsProblemsHolder(holder), isOnTheFly) ?: super.buildVisitor(holder, isOnTheFly)

    open fun buildVisitor(holder: RsProblemsHolder, isOnTheFly: Boolean): RsVisitor? = null

    /**
     * Set of requirements for Rust file suitable for the inspection.
     *
     * Most of the inspections should analyze only files that:
     * - have configured and valid [Toolchain]
     * - belong to [Workspace]
     * - are included in module tree with [CrateRoot]
     *
     * Requirements of syntax-only or naming lints should be empty
     */
    open val requirements: Set<InspectionRequirement> = setOf(
        Toolchain,
        Workspace,
        CrateRoot
    )
}

class RsProblemsHolder(private val holder: ProblemsHolder) {
    val manager: InspectionManager get() = holder.manager
    val file: PsiFile get() = holder.file
    val project: Project get() = holder.project
    val isOnTheFly: Boolean get() = holder.isOnTheFly

    fun registerProblem(element: PsiElement, descriptionTemplate: String, vararg fixes: LocalQuickFix?) {
        if (element.isEnabledByCfg) {
            holder.registerProblem(element, descriptionTemplate, *fixes)
        }
    }

    fun registerProblem(
        element: PsiElement,
        descriptionTemplate: String,
        highlightType: ProblemHighlightType,
        vararg fixes: LocalQuickFix
    ) {
        if (element.isEnabledByCfg) {
            holder.registerProblem(element, descriptionTemplate, highlightType, *fixes)
        }
    }

    fun registerProblem(problemDescriptor: ProblemDescriptor) {
        if (problemDescriptor.psiElement.isEnabledByCfg) {
            holder.registerProblem(problemDescriptor)
        }
    }

    fun registerProblem(element: PsiElement, rangeInElement: TextRange, message: String, vararg fixes: LocalQuickFix?) {
        if (element.isEnabledByCfg) {
            holder.registerProblem(element, rangeInElement, message, *fixes)
        }
    }

    fun registerProblem(
        element: PsiElement,
        message: String,
        highlightType: ProblemHighlightType,
        rangeInElement: TextRange,
        vararg fixes: LocalQuickFix?
    ) {
        if (element.isEnabledByCfg) {
            holder.registerProblem(element, message, highlightType, rangeInElement, *fixes)
        }
    }
}

sealed class InspectionRequirement {
    abstract fun check(file: RsFile): Boolean

    object Toolchain : InspectionRequirement() {
        override fun check(file: RsFile): Boolean = file.project.toolchain?.looksLikeValidToolchain() == true
    }

    object Workspace : InspectionRequirement() {
        override fun check(file: RsFile): Boolean = file.cargoWorkspace != null
    }

    object CrateRoot : InspectionRequirement() {
        override fun check(file: RsFile): Boolean = file.crateRoot != null
    }
}
