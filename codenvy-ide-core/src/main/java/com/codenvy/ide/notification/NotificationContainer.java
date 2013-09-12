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
package com.codenvy.ide.notification;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * The graphic container for {@link NotificationItem}. Show notification in special popup.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationContainer extends PopupPanel {
    public static final int WIDTH  = 400;
    public static final int HEIGHT = 200;
    private final NotificationManagerImpl             manager;
    private       FlowPanel                           panel;
    private       Resources                           resources;
    private       Map<Notification, NotificationItem> notificationWidget;

    /**
     * Create notification container.
     *
     * @param manager
     * @param resources
     */
    public NotificationContainer(NotificationManagerImpl manager, Resources resources) {
        super(true);

        this.manager = manager;
        this.resources = resources;
        this.notificationWidget = new HashMap<Notification, NotificationItem>();

        ScrollPanel scrollpanel = new ScrollPanel();
        setWidget(scrollpanel);

        panel = new FlowPanel();
        panel.setWidth(WIDTH + "px");
        panel.setHeight(HEIGHT + "px");
        scrollpanel.add(panel);
    }

    /**
     * Show container in specified position.
     *
     * @param left
     *         the x-position on the browser window's client area.
     * @param top
     *         the y-position on the browser window's client area.
     */
    public void show(int left, int top) {
        setPopupPosition(left, top);
        show();
    }

    /**
     * Show notification in container.
     *
     * @param notification
     *         notification that need to show
     */
    public void addNotification(Notification notification) {
        NotificationItem item = new NotificationItem(resources, notification, manager);
        panel.add(item);
        notificationWidget.put(notification, item);
    }

    /**
     * Disable notification in container.
     *
     * @param notification
     *         notification that need to disable
     */
    public void removeNotification(Notification notification) {
        NotificationItem item = notificationWidget.get(notification);
        panel.remove(item);
    }

    /**
     * Redraw notification in container.
     *
     * @param notification
     *         notification that need to redraw
     */
    public void refreshNotification(Notification notification) {
        NotificationItem item = notificationWidget.get(notification);
        item.refresh();
    }
}