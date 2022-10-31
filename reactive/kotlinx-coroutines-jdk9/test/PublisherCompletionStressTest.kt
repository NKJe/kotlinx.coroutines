/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines.jdk9

import kotlinx.coroutines.*
import org.junit.*
import java.util.*
import kotlin.coroutines.*

class PublisherCompletionStressTest : TestBase() {
    private val N_REPEATS = 10_000 * stressTestMultiplier

    private fun CoroutineScope.range(context: CoroutineContext, start: Int, count: Int) = flowPublish(context) {
        for (x in start until start + count) send(x)
    }

    @Test
    fun testCompletion() {
        val rnd = Random()
        repeat(N_REPEATS) {
            val count = rnd.nextInt(5)
            runBlocking {
                kotlinx.coroutines.time.withTimeout(5000) {
                    var received = 0
                    range(Dispatchers.Default, 1, count).collect { x ->
                        received++
                        if (x != received) error("$x != $received")
                    }
                    if (received != count) error("$received != $count")
                }
            }
        }
    }
}
