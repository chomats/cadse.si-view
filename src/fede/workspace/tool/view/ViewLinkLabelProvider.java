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
package fede.workspace.tool.view;

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;

public class ViewLinkLabelProvider extends LabelProvider implements IFontProvider {

	/**
     * 
     */
    private Font italique;
    WSLinkView view;
    
    /**
     * @param view
     */
    public ViewLinkLabelProvider(Composite parent, WSLinkView view) {
        Font f = parent.getFont();
        FontData fd = f.getFontData()[0];
        fd.setStyle(SWT.ITALIC);
        italique = new Font(null,fd); 
        this.view = view;
    }
    
    public Font getFont(Object element) {
		if (element instanceof Item) {
			Item iiv = (Item) element;
			if (!iiv.isResolved())
				return this.italique;
		}
		if (element instanceof Link) {
			Link iiv = (Link) element;
			if (!iiv.isLinkResolved())
				return this.italique;
		}
		return null;
	}
    
    @Override
	public Image getImage(Object obj) {
		if (obj instanceof Item) {
			Item item = (Item) obj;
			return createImage(item.getType(), item);
		}
		if (obj instanceof Link) {
			Link link = (Link) obj;
			return createImage(link.getLinkType().getSource(), link.getSource());
		}
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
	
	private Image createImage(ItemType it, Item item) {
		return WSPlugin.getDefault().getImageFrom(it, item);
	}

	@Override
	public String getText(Object obj) {
		if (obj instanceof Item) {
			Item item = (Item) obj;
			return item.getName();
		}
		if (obj instanceof Link) {
			Link link = (Link) obj;
			return toStringKind(link);
		}
		return obj.toString();
	}
    
    private String toStringKind(Link lt) {
		StringBuilder ret = new StringBuilder();
		ret.append("[");
		if (lt.getLinkType().isPart())
			ret.append("p");
		else
			ret.append(" ");
        if (lt.isAggregation())
            ret.append("a");
		else
			ret.append(" ");
        if (lt.isRequire())
            ret.append("r");
		else
			ret.append(" ");
        if (lt.isComposition())
            ret.append("c");
		else
			ret.append(" ");
        ret.append("] ");
        ret.append(lt.getLinkType().getName());
        
		return ret.toString();
    	
	}

	@Override
    public void dispose() {
        super.dispose();
        if (italique != null && !italique.isDisposed())
            italique.dispose();
                
    }
}