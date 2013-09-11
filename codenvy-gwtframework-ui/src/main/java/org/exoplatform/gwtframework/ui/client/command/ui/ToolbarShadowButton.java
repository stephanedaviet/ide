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
package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ToolbarShadowButton extends FlowPanel {

    // image size 115 * 20
    private Image image;
    
    private ImageResource imageResource;
    
    private ImageResource imageResourceHover;
    
    private ClickHandler clickHandler;
    
    public ToolbarShadowButton(final ImageResource imageResource, final ImageResource imageResourceHover, ClickHandler clickHandler) {
        this.imageResource = imageResource;
        this.imageResourceHover = imageResourceHover;
        this.clickHandler = clickHandler;
        
        setSize((imageResource.getWidth() + 4) + "px", "24px");

        image = new Image(imageResource);
        image.setSize(imageResource.getWidth() + "px", "20px");
        add(image);

        image.getElement().getStyle().setPosition(Position.RELATIVE);

        image.getElement().getStyle().setLeft(2, Unit.PX);
        image.getElement().getStyle().setTop(2, Unit.PX);
        //image.getElement().getStyle().setProperty("boxShadow", "2px 2px 2px #888888");
        
        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONCLICK);        
    }
    
    /** Handle browser's events. */
    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                mouseOver();
                break;

            case Event.ONMOUSEOUT:
                mouseOut();
                break;

            case Event.ONMOUSEDOWN:
                if (event.getButton() != Event.BUTTON_LEFT) {
                    return;
                }

                mouseDown();
                break;

            case Event.ONMOUSEUP:
                mouseUp();
                break;

            case Event.ONCLICK:
                click();
                break;
        }
    }
    
    private void mouseOver() {
        image.setResource(imageResourceHover);
    }
    
    private void mouseOut() {
        image.setResource(imageResource);
//        image.getElement().getStyle().setLeft(1, Unit.PX);
//        image.getElement().getStyle().setTop(1, Unit.PX);
//        image.getElement().getStyle().setProperty("boxShadow", "2px 2px 2px #888888");        
    }
    
    private void mouseDown() {
//        image.getElement().getStyle().setLeft(3, Unit.PX);
//        image.getElement().getStyle().setTop(3, Unit.PX);
//        image.getElement().getStyle().setProperty("boxShadow", "0px 0px 2px #000000");
    }
    
    private void mouseUp() {
//        image.getElement().getStyle().setLeft(1, Unit.PX);
//        image.getElement().getStyle().setTop(1, Unit.PX);
//        image.getElement().getStyle().setProperty("boxShadow", "2px 2px 2px #888888");        
    }
    
    private void click() {
        clickHandler.onClick(new ClickEvent() {});
    }
    
}
