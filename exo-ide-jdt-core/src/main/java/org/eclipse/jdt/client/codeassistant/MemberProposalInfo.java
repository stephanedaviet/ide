/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.codeassistant;

import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.runtime.Assert;

/**
 * Proposal info that computes the javadoc lazily when it is queried.
 * 
 * @since 3.1
 */
public abstract class MemberProposalInfo extends ProposalInfo
{
   /* configuration */
   protected final CompletionProposal fProposal;

   /**
    * Creates a new proposal info.
    * 
    * @param project the java project to reference when resolving types
    * @param proposal the proposal to generate information for
    */
   public MemberProposalInfo(CompletionProposal proposal)
   {
      Assert.isNotNull(proposal);
      fProposal = proposal;
   }

}
