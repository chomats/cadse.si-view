/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.tool.view.decorator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fede.workspace.tool.view.WSPlugin;

public class WorkspaceCopyLabelDecorator implements ILightweightLabelDecorator {

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ItemDelta) {
			if (((ItemDelta) element).isDeleted()) {
				ImageDescriptor overlay = WSPlugin.getImageDescriptor("icons/delete_ovr.gif");
				decoration.addOverlay(overlay, IDecoration.BOTTOM_LEFT);
			}

			if (((ItemDelta) element).isAdded()) {
				ImageDescriptor overlay = WSPlugin.getImageDescriptor("icons/add_ovr.gif");
				decoration.addOverlay(overlay, IDecoration.BOTTOM_LEFT);
			}
		}
	}

}
