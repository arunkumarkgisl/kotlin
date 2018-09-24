/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.contracts.model

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType

interface ESExpression {
    fun <T> accept(visitor: ESExpressionVisitor<T>): T
}

interface ESOperator : ESExpression {
    val functor: Functor
}

interface ESValue : Computation, ESExpression {
    val receiverValue: ReceiverValue?
}

abstract class AbstractESValue : ESValue {
    override val effects: List<ESEffect> = listOf()
    override val receiverValue: ReceiverValue? = null
    override val type: KotlinType? = null
}

class ESFunction(val descriptor: FunctionDescriptor) : AbstractESValue() {
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitFunction(this)
}

class ESReceiverReference(val lambda: ESValue) : AbstractESValue() {
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitReceiverReference(this)
}

interface ESReceiver : ESValue {
    override val receiverValue: ReceiverValue
}

open class ESReceiverValue(override val receiverValue: ReceiverValue) : AbstractESValue(), ESReceiver {
    override fun <T> accept(visitor: ESExpressionVisitor<T>): T = visitor.visitReceiver(this)
}