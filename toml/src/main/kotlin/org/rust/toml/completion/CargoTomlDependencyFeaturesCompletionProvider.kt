/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import org.rust.lang.core.psi.ext.ancestorOrSelf
import org.rust.toml.findDependencyTomlFile
import org.rust.toml.resolve.allFeatures
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlElement
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlTable

/**
 * Consider `Cargo.toml`:
 * ```
 * [dependencies]
 * foo = { version = "*", features = ["<caret>"] }
 *                                    #^ Provides completion here
 *
 * [dependencies.foo]
 * features = ["<caret>"]
 *             #^ Provides completion here
 * ```
 *
 * @see [org.rust.toml.resolve.CargoTomlDependencyFeaturesReferenceProvider]
 */
class CargoTomlDependencyFeaturesCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val parentParentParent = parameters.position.ancestorOrSelf<TomlArray>()?.parent?.parent as? TomlElement ?: return
        val pkgName = if (parentParentParent is TomlTable) {
            // [dependencies.foo]
            parentParentParent.header.names.lastOrNull()
        } else {
            // [dependencies]
            // foo = { ... }
            (parentParentParent.parent as? TomlKeyValue)?.key
        }?.text ?: return

        val depToml = findDependencyTomlFile(parentParentParent, pkgName) ?: return
        // TODO avoid AST loading?
        for (feature in depToml.allFeatures()) {
            result.addElement(lookupElementForFeature(feature))
        }
    }
}
