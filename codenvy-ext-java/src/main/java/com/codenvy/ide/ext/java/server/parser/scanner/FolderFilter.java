/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.server.parser.scanner;

import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.dto.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:48:06 AM Mar 7, 2012 evgen $
 */
public class FolderFilter implements Filter {


    @Override
    public boolean filter(Item item) {
        return item.getItemType() == ItemType.FOLDER;
    }

}