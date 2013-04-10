/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.rename;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class RenameItemControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, VfsChangedHandler,
                                                                ViewActivatedHandler {

    private static final String ID = "File/Rename...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.renameTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.renamePromptControl();

    private List<Item> selectedItems = new ArrayList<Item>();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo = null;

    private boolean navigationViewSelected = false;

    /**
     *
     */
    public RenameItemControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setShowInContextMenu(true);
        setImages(IDEImageBundle.INSTANCE.rename(), IDEImageBundle.INSTANCE.renameDisabled());
        setEvent(new RenameItemEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
    }

    /** Update control's state. */
    private void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            setShowInContextMenu(false);
            return;
        }
        setVisible(true);

        if (!navigationViewSelected) {
            setEnabled(false);
            setShowInContextMenu(false);
            return;
        }

        setShowInContextMenu(navigationViewSelected);

        if (selectedItems.size() != 1 || vfsInfo.getRoot().getId().equals(selectedItems.get(0).getId())) {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        navigationViewSelected = event.getView() instanceof NavigatorDisplay ||
                                 event.getView() instanceof ProjectExplorerDisplay ||
                                 event.getView() instanceof PackageExplorerDisplay;
        selectedItems = event.getSelectedItems();

        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     * .ui.api.event.ViewActivatedEvent) */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        navigationViewSelected = event.getView() instanceof NavigatorDisplay ||
                                 event.getView() instanceof ProjectExplorerDisplay ||
                                 event.getView() instanceof PackageExplorerDisplay;

        updateState();
    }

}
