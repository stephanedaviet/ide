/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.git.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.Util;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.git.client.add.AddToIndexPresenter;
import org.exoplatform.ide.git.client.branch.BranchPresenter;
import org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter;
import org.exoplatform.ide.git.client.commit.CommitPresenter;
import org.exoplatform.ide.git.client.control.*;
import org.exoplatform.ide.git.client.delete.DeleteRepositoryCommandHandler;
import org.exoplatform.ide.git.client.fetch.FetchPresenter;
import org.exoplatform.ide.git.client.history.HistoryPresenter;
import org.exoplatform.ide.git.client.init.InitRepositoryPresenter;
import org.exoplatform.ide.git.client.init.ShowProjectGitReadOnlyUrlPresenter;
import org.exoplatform.ide.git.client.merge.MergePresenter;
import org.exoplatform.ide.git.client.pull.PullPresenter;
import org.exoplatform.ide.git.client.push.PushToRemotePresenter;
import org.exoplatform.ide.git.client.remote.RemotePresenter;
import org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter;
import org.exoplatform.ide.git.client.reset.ResetFilesPresenter;
import org.exoplatform.ide.git.client.reset.ResetToCommitPresenter;
import org.exoplatform.ide.git.client.status.StatusCommandHandler;

/**
 * Git extension to be added to IDE application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 12:53:29 PM anya $
 */
public class GitExtension extends Extension implements InitializeServicesHandler {

    public static final GitLocalizationConstant MESSAGES            = GWT.create(GitLocalizationConstant.class);

    public static final String                  GIT_REPOSITORY_PROP = "isGitRepository";

    public static final GitAutoBeanFactory      AUTO_BEAN_FACTORY   = GWT.create(GitAutoBeanFactory.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        // Add controls:
        IDE.getInstance().addControl(new InitRepositoryControl());
        IDE.getInstance().addControl(new CloneRepositoryControl());
        IDE.getInstance().addControl(new DeleteRepositoryControl());
        IDE.getInstance().addControl(new AddFilesControl());
        IDE.getInstance().addControl(new ResetFilesControl());
        IDE.getInstance().addControl(new ResetToCommitControl());
        IDE.getInstance().addControl(new RemoveFilesControl());
        IDE.getInstance().addControl(new CommitControl());
        IDE.getInstance().addControl(new BranchesControl());
        IDE.getInstance().addControl(new MergeControl());
        IDE.getInstance().addControl(new PushToRemoteControl());
        IDE.getInstance().addControl(new FetchControl());
        IDE.getInstance().addControl(new PullControl());
        IDE.getInstance().addControl(new RemoteControl());
        IDE.getInstance().addControl(new RemotesControl());

        IDE.getInstance().addControl(new ShowHistoryControl());
        IDE.getInstance().addControl(new ShowStatusControl());
        IDE.getInstance().addControl(new ShowProjectGitReadOnlyUrl());
        IDE.getInstance().addControlsFormatter(new GitControlsFormatter());


        new CodeNowHandler();
        // Create presenters:
        new CloneRepositoryPresenter();
        new InitRepositoryPresenter();
        new StatusCommandHandler();
        new AddToIndexPresenter();
        new RemoveFromIndexPresenter();
        new ResetFilesPresenter();
        new ResetToCommitPresenter();
        new RemotePresenter();

        new CommitPresenter();
        new PushToRemotePresenter();
        new BranchPresenter();
        new FetchPresenter();
        new PullPresenter();
        new HistoryPresenter();
        new DeleteRepositoryCommandHandler();
        new MergePresenter();

        new ShowProjectGitReadOnlyUrlPresenter();

    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new GitClientServiceImpl(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader(), IDE.messageBus());
    }


}
