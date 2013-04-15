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
package org.exoplatform.ide.extension.gadget.client.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.gadget.client.GadgetClientBundle;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class ShowGadgetPreviewControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Run/Show Gadget Preview";

    public static final String TITLE = "Show Gadget Preview";

    public ShowGadgetPreviewControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(GadgetClientBundle.INSTANCE.preview(), GadgetClientBundle.INSTANCE.previewDisabled());
        setEvent(new PreviewGadgetEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (MimeType.GOOGLE_GADGET.equals(event.getFile().getMimeType())) {
            setVisible(true);
            if (!event.getFile().isPersisted()) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        } else {
            setVisible(false);
            setEnabled(false);
        }
    }
}