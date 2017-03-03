/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.plugin.management;

import org.gradle.api.Action;
import org.gradle.api.Incubating;

/**
 * Allows plugin resolution rules to inspect a requested plugin and modify which
 * target plugin will be used.
 *
 * @since 3.5
 */
@Incubating
public interface PluginResolveDetails {

    /**
     * Get the plugin that was requested.
     */
    PluginRequest getRequested();

    /**
     * Changes the target {@link PluginRequest}.
     */
    void useTarget(Action<? super ConfigurablePluginRequest> action);

    /**
     * The target plugin request to use.
     */
    PluginRequest getTarget();

}
