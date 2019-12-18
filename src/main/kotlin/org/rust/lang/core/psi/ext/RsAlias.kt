/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.psi.ext

import org.rust.lang.core.psi.RsAlias
import org.rust.lang.core.psi.RsElementTypes

val RsAlias.nameLikeText: String
    get() = greenStub?.nameLikeText
        ?: node.findChildByType(RsElementTypes.IDENTIFIER)?.text
        ?: node.findChildByType(RsElementTypes.UNDERSCORE)?.text
        ?: error("Alias without name: `${node.text}`")
