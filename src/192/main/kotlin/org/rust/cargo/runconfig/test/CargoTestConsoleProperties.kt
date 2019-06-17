/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.runconfig.test

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.Printer
import com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT
import com.intellij.execution.ui.ConsoleViewContentType.SYSTEM_OUTPUT

class CargoTestConsoleProperties(
    config: RunConfiguration,
    executor: Executor
) : CargoTestConsolePropertiesBase(config, executor) {

    override fun printExpectedActualHeader(printer: Printer, expected: String?, actual: String?) {
        printer.print("\n", ERROR_OUTPUT)
        printer.print("Left  :", SYSTEM_OUTPUT)
        printer.print(expected + "\n", ERROR_OUTPUT)
        printer.print("Right :", SYSTEM_OUTPUT)
        printer.print(actual, ERROR_OUTPUT)
    }
}
