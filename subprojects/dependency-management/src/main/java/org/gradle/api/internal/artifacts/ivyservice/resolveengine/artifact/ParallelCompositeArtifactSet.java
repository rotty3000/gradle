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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.gradle.api.Action;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.internal.operations.BuildOperationProcessor;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.internal.operations.RunnableBuildOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A ResolvedArtifactSet wrapper that prepares artifacts in parallel when visiting the delegate.
 * This is done by collecting all artifacts to prepare and/or visit in a first step.
 * The collected artifacts are prepared in parallel and subsequently visited in sequence.
 */
public class ParallelCompositeArtifactSet implements ResolvedArtifactSet {
    private final List<ResolvedArtifactSet> sets;
    private final BuildOperationProcessor buildOperationProcessor;

    public static ResolvedArtifactSet of(Collection<? extends ResolvedArtifactSet> sets, BuildOperationProcessor buildOperationProcessor) {
        List<ResolvedArtifactSet> filtered = new ArrayList<ResolvedArtifactSet>(sets.size());
        for (ResolvedArtifactSet set : sets) {
            if (set != ResolvedArtifactSet.EMPTY) {
                filtered.add(set);
            }
        }
        if (filtered.isEmpty()) {
            return EMPTY;
        }
        if (filtered.size() == 1) {
            return filtered.get(0);
        }
        return new ParallelCompositeArtifactSet(filtered, buildOperationProcessor);
    }

    private ParallelCompositeArtifactSet(List<ResolvedArtifactSet> sets, BuildOperationProcessor buildOperationProcessor) {
        this.sets = sets;
        this.buildOperationProcessor = buildOperationProcessor;
    }

    @Override
    public void addResolveActions(Collection<Runnable> actions, ArtifactVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ResolvedArtifact> getArtifacts() {
        Set<ResolvedArtifact> allArtifacts = new LinkedHashSet<ResolvedArtifact>();
        for (ResolvedArtifactSet set : sets) {
            allArtifacts.addAll(set.getArtifacts());
        }
        return allArtifacts;
    }

    @Override
    public void collectBuildDependencies(Collection<? super TaskDependency> dest) {
        for (ResolvedArtifactSet set : sets) {
            set.collectBuildDependencies(dest);
        }
    }

    @Override
    public void visit(final ArtifactVisitor visitor) {
        final Set<ResolvedArtifactSet> failed = Sets.newConcurrentHashSet();

        // Execute all 'prepare' calls in parallel
        buildOperationProcessor.run(new Action<BuildOperationQueue<RunnableBuildOperation>>() {
            @Override
            public void execute(BuildOperationQueue<RunnableBuildOperation> buildOperationQueue) {
                List<Runnable> ops = Lists.newArrayList();
                for (final ResolvedArtifactSet set : sets) {
                    set.addResolveActions(ops, visitor);
                }
                for (final Runnable op : ops) {
                    buildOperationQueue.add(new RunnableBuildOperation() {
                        @Override
                        public void run() {
                            op.run();
                        }

                        @Override
                        public String getDescription() {
                            return "Prepare artifacts";
                        }
                    });
                }
            }
        });

        for (ResolvedArtifactSet set : sets) {
            if (!failed.contains(set)) {
                set.visit(visitor);
            }
        }
    }
}
