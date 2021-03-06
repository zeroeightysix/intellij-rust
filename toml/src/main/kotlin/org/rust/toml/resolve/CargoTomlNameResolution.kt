/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.toml.resolve

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import org.rust.lang.core.psi.ext.childrenOfType
import org.rust.toml.getValueWithKey
import org.rust.toml.isDependencyListHeader
import org.rust.toml.isFeatureListHeader
import org.rust.toml.isSpecificDependencyTableHeader
import org.toml.lang.psi.*
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind

fun TomlFile.resolveFeature(featureName: String): Array<ResolveResult> =
    allFeatures()
        .filter { it.text == featureName }
        .map { PsiElementResolveResult(it) }
        .toList()
        .toTypedArray()

fun TomlFile.allFeatures(): Sequence<TomlKeySegment> = childrenOfType<TomlTable>()
    .asSequence()
    .flatMap { table ->
        val header = table.header
        when {
            // [features]
            header.isFeatureListHeader -> {
                table.entries.asSequence().mapNotNull { it.key.segments.singleOrNull() }
            }

            // # Optional dependencies are features too:
            // [dependencies]
            // bar = { version = "*", optional = true }
            header.isDependencyListHeader -> {
                table.entries
                    .asSequence()
                    .filter {
                        (it.value as? TomlInlineTable)?.getValueWithKey("optional")?.asBoolean() == true
                    }
                    .mapNotNull { it.key.segments.singleOrNull() }
            }

            // [dependencies.bar]
            // version = "*"
            // optional = true
            header.isSpecificDependencyTableHeader -> {
                if (table.getValueWithKey("optional")?.asBoolean() == true) {
                    val lastKey = header.key?.segments?.last()
                    if (lastKey != null) sequenceOf(lastKey) else emptySequence()
                } else {
                    emptySequence()
                }
            }
            else -> {
                emptySequence()
            }
        }
    }

private fun TomlValue.asBoolean(): Boolean? =
    ((this as? TomlLiteral)?.kind as? TomlLiteralKind.Boolean)?.value

private val TomlLiteralKind.Boolean.value: Boolean
    get() = node.text == "true"
