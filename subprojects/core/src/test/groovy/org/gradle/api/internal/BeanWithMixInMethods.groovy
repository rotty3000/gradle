/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal

import org.gradle.internal.metaobject.InvokeMethodResult
import org.gradle.internal.metaobject.MethodAccess
import org.gradle.internal.metaobject.MethodMixIn

class BeanWithMixInMethods implements MethodMixIn {
    String prop

    String thing(Number l) {
        return l.toString()
    }

    @Override
    MethodAccess getAdditionalMethods() {
        return new MethodAccess() {
            @Override
            boolean hasMethod(String name, Object... arguments) {
                return name == "dyno"
            }

            @Override
            void invokeMethod(String name, InvokeMethodResult result, Object... arguments) {
                if (name == "dyno") {
                    result.result(Arrays.toString(arguments))
                }
            }
        }
    }

    Object methodMissing(String name, Object params) {
        throw new AssertionError("Should not be called")
    }
}
