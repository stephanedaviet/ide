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
package com.codenvy.ide.jseditor.client.reconciler;

import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.util.executor.BasicIncrementalScheduler;

/**
 * Factory for {@link Reconciler} instances.
 */
public interface ReconcilerFactory {

    Reconciler create(String partitioning, BasicIncrementalScheduler scheduler, DocumentPartitioner partitioner);
}
