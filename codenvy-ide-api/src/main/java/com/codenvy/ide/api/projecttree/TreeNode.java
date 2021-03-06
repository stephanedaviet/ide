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
package com.codenvy.ide.api.projecttree;

import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;

/**
 * A <code>TreeNode</code> is an interface for all implementation of nodes in a project tree.
 * An <code>TreeNode</code> may also hold a reference to an associated object,
 * the use of which is left to the user.
 *
 * @param <T>
 *         the type of the associated data
 * @author Artem Zatsarynnyy
 */
public interface TreeNode<T> {

    /**
     * Returns this node's parent node.
     *
     * @return this node's parent node
     */
    TreeNode<?> getParent();

    /**
     * Sets the new parent node for this node.
     *
     * @param parent
     *         the new parent node
     */
    void setParent(TreeNode<?> parent);

    /**
     * Returns the object represented by this node                                   .
     *
     * @return the associated data
     */
    T getData();

    /**
     * Sets the new associated data for this node.
     *
     * @param data
     *         the new associated data
     */
    void setData(T data);

    /** Returns project which contains this node. */
    ProjectNode getProject();

    /** Returns the node's display name. */
    @Nonnull
    String getDisplayName();

    /** Provides an SVG icon to be used for graphical representation of the node. */
    SVGImage getDisplayIcon();

    /** Set an SVG icon to be used for graphical representation of the node. */
    void setDisplayIcon(SVGImage icon);

    /**
     * Determines may the node be expanded.
     *
     * @return <code>true</code> - if node shouldn't never be expanded in the tree,
     * <code>false</code> - if node may be expanded
     */
    boolean isLeaf();

    /**
     * Returns an array of all this node's child nodes. The array will always
     * exist (i.e. never <code>null</code>) and be of length zero if this is
     * a leaf node.
     *
     * @return an array of all this node's child nodes
     */
    @Nonnull
    Array<TreeNode<?>> getChildren();

    /**
     * Set node's children.
     *
     * @param children
     *         array of new children for this node
     */
    void setChildren(Array<TreeNode<?>> children);

    /**
     * Refresh node's children.
     *
     * @param callback
     *         callback to return node with refreshed children
     */
    void refreshChildren(AsyncCallback<TreeNode<?>> callback);

    /** Process an action on the node (e.g. double-click on the node in the view). */
    void processNodeAction();

    /** Defines whether the node may be renamed. */
    boolean isRenamable();

    /**
     * Override this method to provide a way to rename node.
     * <p/>
     * Sub-classes should invoke {@code super.delete} at the end of this method.
     *
     * @param newName
     *         new name
     * @param callback
     *         callback to return deleted node
     */
    void rename(String newName, RenameCallback callback);

    /** Defines whether the node may be deleted. */
    boolean isDeletable();

    /**
     * Override this method to provide a way to delete node.
     * <p/>
     * Sub-classes should invoke {@code super.delete} at the end of this method.
     *
     * @param callback
     *         callback to return renamed node
     */
    void delete(DeleteCallback callback);

    /**
     * Returns the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @return the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement}
     */
    TreeNodeElement<TreeNode<?>> getTreeNodeElement();

    /**
     * Sets the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @param treeNodeElement
     *         the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement}
     */
    void setTreeNodeElement(TreeNodeElement<TreeNode<?>> treeNodeElement);

    public interface RenameCallback {
        void onRenamed();

        void onFailure(Throwable exception);
    }

    public interface DeleteCallback {
        void onDeleted();

        void onFailure(Throwable exception);
    }
}
