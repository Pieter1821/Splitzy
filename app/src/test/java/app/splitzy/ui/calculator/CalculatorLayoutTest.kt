package app.splitzy.ui.calculator

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorLayoutTest {

    @Test
    fun `compact 320dp phone gets one column and a narrow gutter`() {
        val layout = CalculatorLayout.forWindow(320.dp, 640.dp, fontScale = 1f)
        assertFalse(layout.twoPane)
        assertEquals(16.dp, layout.gutter)
        assertTrue(layout.showTagline)
    }

    @Test
    fun `standard 411dp phone keeps copy and share side by side`() {
        val layout = CalculatorLayout.forWindow(411.dp, 914.dp, fontScale = 1f)
        assertFalse(layout.twoPane)
        assertEquals(24.dp, layout.gutter)
        assertFalse(layout.stackActions)
    }

    @Test
    fun `medium and expanded windows use two panes`() {
        assertTrue(CalculatorLayout.forWindow(600.dp, 960.dp, 1f).twoPane)
        assertTrue(CalculatorLayout.forWindow(1280.dp, 800.dp, 1f).twoPane)
        assertEquals(40.dp, CalculatorLayout.forWindow(1280.dp, 800.dp, 1f).gutter)
    }

    @Test
    fun `short windows like landscape phones drop the tagline`() {
        assertFalse(CalculatorLayout.forWindow(914.dp, 411.dp, 1f).showTagline)
    }

    @Test
    fun `large font scale drops the tagline and stacks actions on phones`() {
        val layout = CalculatorLayout.forWindow(411.dp, 914.dp, fontScale = 2f)
        assertFalse(layout.showTagline)
        assertTrue(layout.stackActions)
    }
}
