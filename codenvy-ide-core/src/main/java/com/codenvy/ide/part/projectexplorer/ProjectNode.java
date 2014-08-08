/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;

/**
 * Tree node to for {@link ProjectReference}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectNode extends AbstractTreeNode<ProjectReference> {
    public ProjectNode(AbstractTreeNode parent, ProjectReference data) {
        super(parent, data);
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return true;
    }
}
