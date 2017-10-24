package com.airbnb.paris.proxies

import android.support.test.*
import android.support.test.runner.*
import android.view.*
import android.widget.*
import com.airbnb.paris.styles.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewGroupProxyStyleApplier_StyleBuilderTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: ViewGroup
    private lateinit var programmaticStyleBuilder: ProgrammaticStyle.Builder
    private lateinit var styleBuilder: ViewGroupProxyStyleApplier.StyleBuilder

    @Before
    fun setup() {
        view = FrameLayout(context)
        programmaticStyleBuilder = ProgrammaticStyle.builder().debugName("test")
        styleBuilder = ViewGroupProxyStyleApplier.StyleBuilder().debugName("test")
    }

    @Test
    fun auto() {
        for (mapping in (VIEW_MAPPINGS + VIEW_GROUP_MAPPINGS)) {
            mapping as BaseViewMapping<Any, Any, ViewGroup, Any>

            // For normal values
            mapping.testValues.forEach {
                setup()

                programmaticStyleBuilder.put(mapping.attrRes, it)
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                Assert.assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
            }

            // For resource ids
            setup()

            programmaticStyleBuilder.putRes(mapping.attrRes, ARBITRARY_RESOURCE_ID)
            mapping.setStyleBuilderResFunction(styleBuilder, ARBITRARY_RESOURCE_ID)
            Assert.assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
        }
    }
}