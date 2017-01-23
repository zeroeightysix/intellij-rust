package org.rust.ide.template

import com.intellij.openapi.actionSystem.IdeActions
import org.intellij.lang.annotations.Language
import org.rust.lang.RsTestBase

class RsLiveTemplatesTest : RsTestBase() {
    override val dataPath = "org/rust/ide/template/fixtures"

    fun testStructField() = expandSnippet("""
        struct S {
            f/*caret*/
        }
    """, """
        struct S {
            foo: u32,
        }
    """)

    fun testPrint() = expandSnippet("""
        fn main() {
            p/*caret*/
        }
    """, """
        fn main() {
            println!("");
        }
    """)

    fun testAttribute() = noSnippet("""
        #[macro/*caret*/]
        extern crate std;

        fn main() { }
    """)

    fun testComment() = noSnippet("""
        fn main() {
            // p/*caret*/
        }
    """)

    fun testDocComment() = noSnippet("""
        /// p/*caret*/
        fn f() {}
    """)

    fun testStringLiteral() = noSnippet("""
        fn main() {
            let _ = "p/*caret*/";
        }
    """)

    fun testRawStringLiteral() = noSnippet("""
        fn main() {
            let _ = r##"p/*caret*/"##;
        }
    """)

    fun testByteStringLiteral() = noSnippet("""
        fn main() {
            let _ = b"p/*caret*/";
        }
    """)

    fun testFieldExpression() = noSnippet("""
        fn main() {
            let _ = foo.p/*caret*/
        }
    """)

    fun testMethodExpression() = noSnippet("""
        fn main() {
            let _ = foo.p/*caret*/()
        }
    """)

    fun testPath() = noSnippet("""
        fn main() {
            let _ = foo::p/*caret*/
        }
    """)

    private fun expandSnippet(@Language("Rust") before: String, @Language("Rust") after: String) =
        checkByText(before, after) {
            myFixture.performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)
        }

    private fun noSnippet(@Language("Rust") code: String) = expandSnippet(code, code)
}
