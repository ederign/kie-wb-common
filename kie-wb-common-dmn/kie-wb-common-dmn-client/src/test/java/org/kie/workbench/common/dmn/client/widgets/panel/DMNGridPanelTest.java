/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.widgets.panel;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.Scheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridPanelTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private RestrictedMousePanMediator mousePanMediator;

    @Mock
    private TransformMediator transformMediator;

    @Mock
    private Transform transform;

    @Mock
    private Transform newTransform;

    @Mock
    private Viewport viewport;

    private DMNGridPanel gridPanel;

    @Before
    public void setup() {
        this.gridPanel = spy(new DMNGridPanel(gridLayer,
                                              mousePanMediator));
        doAnswer((o) -> {
            ((Scheduler.ScheduledCommand) o.getArguments()[0]).execute();
            return null;
        }).when(gridPanel).doResize(any(Scheduler.ScheduledCommand.class));

        doNothing().when(gridPanel).updatePanelSize();
        doNothing().when(gridPanel).refreshScrollPosition();

        doReturn(viewport).when(gridLayer).getViewport();
        doReturn(transform).when(viewport).getTransform();
        doReturn(transformMediator).when(mousePanMediator).getTransformMediator();
        doReturn(newTransform).when(transformMediator).adjust(eq(transform),
                                                              any(Bounds.class));
    }

    @Test
    public void testOnResize() {
        gridPanel.onResize();

        verify(gridPanel).updatePanelSize();
        verify(gridPanel).refreshScrollPosition();
        verify(viewport).setTransform(eq(newTransform));
        verify(gridLayer).batch();
    }
}
