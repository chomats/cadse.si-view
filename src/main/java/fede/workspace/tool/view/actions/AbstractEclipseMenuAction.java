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
 */
package fede.workspace.tool.view.actions;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import fr.imag.adele.cadse.core.IMenuAction;

public abstract class AbstractEclipseMenuAction extends IMenuAction {

	private ImageDescriptor	hoverImageDescriptor;
	private ImageDescriptor	imageDescriptor;
	private ImageDescriptor	disabledImageDescriptor;
	private String			description;

	public AbstractEclipseMenuAction() {
		super();
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	protected void setHoverImageDescriptor(ImageDescriptor imageDescriptor) {
		this.hoverImageDescriptor = imageDescriptor;
	}

	protected void setImageDescriptor(ImageDescriptor imageDescriptor) {
		this.imageDescriptor = imageDescriptor;
	}

	protected void setDisabledImageDescriptor(ImageDescriptor imageDescriptor) {
		this.disabledImageDescriptor = imageDescriptor;
	}

	@Override
	public Object getDisabledImageDescriptor() {
		return disabledImageDescriptor;
	}

	@Override
	public Object getHoverImageDescriptor() {
		return this.hoverImageDescriptor;
	}

	@Override
	public Object getImageDescriptor() {
		return this.imageDescriptor;
	}

	@Override
	public URL getImage() {
		return null;
	}

}