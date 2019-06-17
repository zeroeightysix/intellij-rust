/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.runconfig.test

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration

class CargoTestConsoleProperties(
    config: RunConfiguration,
    executor: Executor
) : CargoTestConsolePropertiesBase(config, executor)
