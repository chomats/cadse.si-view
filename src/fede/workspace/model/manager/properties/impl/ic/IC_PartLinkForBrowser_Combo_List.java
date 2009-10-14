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
package fede.workspace.model.manager.properties.impl.ic;

import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.WSPlugin;





/**
 * The Class IC_PartLinkForBrowser_Combo_List.
 * 
 * @generated
 */
public class IC_PartLinkForBrowser_Combo_List extends IC_LinkForBrowser_Combo_List {
	
	/** The part link type. */
	LinkType partLinkType ;
	
	/** The error message. */
	String errorMessage;
	
	/**
	 * The Constructor.
	 * 
	 * @param title the title
	 * @param message the message
	 * @param linkType the link type
	 * @param partLinkType the part link type
	 * @param errormessage the errormessage
	 * 
	 * @generated
	 */
	public IC_PartLinkForBrowser_Combo_List( String title, String message, LinkType linkType, 
			LinkType partLinkType, String errormessage) {
		super( title, message, linkType);
		this.partLinkType = partLinkType;
		this.errorMessage = errormessage;
	}
	

	public IC_PartLinkForBrowser_Combo_List(CompactUUID id) {
		super(id);
	}


	/* (non-Javadoc)
	 * @see fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List#getTreeContentProvider()
	 */
	@Override
	protected ITreeContentProvider getTreeContentProvider() {
		return new PartContentProvider(partLinkType, getComparator(), getValues());
	}

	/* (non-Javadoc)
	 * @see fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List#getInputValues()
	 */
	@Override
	protected Object getInputValues() {
		return getSelectablePartParentValue();
	}

	/**
	 * Gets the selectable part parent value. 
	 * On peut retournner 
	 * 	- un ItemType -> tous les items de ce type, 
	 *  - Item[] -> tous ces items
	 * @see PartContentProvider#getElements(Object)
	 * @return the selectable part parent value
	 */
	protected Object getSelectablePartParentValue(){
		return partLinkType.getSource();
	}
	
	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * Gets the part link type.
	 * 
	 * @return the part link type
	 */
	public LinkType getPartLinkType() {
		return partLinkType;
	}

	/**
	 * Gets the comparator.
	 * 
	 * @return the comparator
	 */
	protected Comparator<Object> getComparator() {
		return new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				return IC_PartLinkForBrowser_Combo_List.this.toString(arg0).compareTo(IC_PartLinkForBrowser_Combo_List.this.toString(arg1));
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List#validate(java.lang.Object[])
	 */
	@Override
	public IStatus validate(Object[] selection) {
		if (selection == null || selection.length != 1)
			return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, errorMessage, null);
		Object o = selection[0];
		if (o instanceof Item && ((Item)o).isInstanceOf(partLinkType.getDestination())) {
			return Status.OK_STATUS;
		}
		
		return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, errorMessage, null);
	}
	
	@Override
	public ItemType getType() {
		return CadseGCST.IC_PART_LINK_FOR_BROWSER_COMBO_LIST;
	}
}