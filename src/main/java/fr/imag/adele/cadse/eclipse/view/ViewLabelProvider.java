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
/**
 * 
 */
package fr.imag.adele.cadse.eclipse.view;

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.IItemNode;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.LinkNode;
import fede.workspace.tool.view.node.LinkTypeNode;

public class ViewLabelProvider extends LabelProvider implements IFontProvider {

	/**
     * 
     */
	IViewDisplayConfiguration view;
    
    /**
     * @param view
     */
    public ViewLabelProvider(IViewDisplayConfiguration view) {
        this.view = view;
    }
    
    public Font getFont(Object element) {
    	if (element instanceof ItemNode) {
			return view.getDisplayFont((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return view.getDisplayFont((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return view.getDisplayFont((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return view.getDisplayFont((ItemTypeNode) element);
		}
		return null;
	}
    
    @Override
	public Image getImage(Object element) {
    	if (element instanceof ItemNode) {
			return view.getDisplayImage((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return view.getDisplayImage((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return view.getDisplayImage((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return view.getDisplayImage((ItemTypeNode) element);
		}
		return null;
	}
	
	

	@Override
	public String getText(Object element) {
    	if (element instanceof ItemNode) {
			return view.getDisplayText((IItemNode) element);
		}
		if (element instanceof LinkNode) {
			return view.getDisplayText((LinkNode) element);
		}
		if (element instanceof LinkTypeNode) {
			return view.getDisplayText((LinkTypeNode) element);
		}
		if (element instanceof ItemTypeNode) {
			return view.getDisplayText((ItemTypeNode) element);
		}
		return element.toString();
	}
    
    

}