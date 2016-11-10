/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Creates a new node on the target graph and creates/defines a new dock relationship so new node will be docked into the
 * given parent.
 */
@Portable
public class AddDockedNodeCommand extends AbstractGraphCompositeCommand {

    private final String parentUUID;
    private final Node candidate;

    public AddDockedNodeCommand( @MapsTo( "parentUUID" ) String parentUUID,
                                 @MapsTo( "candidate" ) Node candidate ) {
        this.parentUUID = PortablePreconditions.checkNotNull( "parentUUID",
                parentUUID );
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
    }

    protected void initialize( final GraphCommandExecutionContext context ) {
        this.addCommand( new AddNodeCommand( candidate ) )
                .addCommand( new AddDockEdgeCommand( parentUUID, candidate.getUUID() ) );
    }

    @Override
    protected CommandResult<RuleViolation> doAllow( GraphCommandExecutionContext context, Command<GraphCommandExecutionContext, RuleViolation> command ) {
        return command.allow( context );
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        final Node<?, Edge> parent = getNode( context, parentUUID );
        final Collection<RuleViolation> dockingRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager().docking().evaluate( parent, candidate ).violations();
        final Collection<RuleViolation> violations = new LinkedList<RuleViolation>();
        violations.addAll( dockingRuleViolations );
        return new GraphCommandResultBuilder( violations ).build();
    }

    @Override
    public String toString() {
        return "AddDockedNodeCommand [parent=" + parentUUID + ", candidate=" + candidate.getUUID() + "]";
    }

}