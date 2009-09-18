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

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.CadseDomain;
import fede.workspace.tool.Messages;
import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.dialog.create.InteractifTreeController;

public class IC_ItemTypeForTreeUI extends IC_Abstract implements InteractifTreeController {

	public String getText(Object obj) {
		if (obj instanceof LinkType)
			return getTreeText((LinkType) obj);
		return getTreeText((ItemType) obj);
	}
	
	protected String getTreeText(LinkType lt) {
		if (lt.getDestination().getId().equals(CadseDomain.ITEM_TYPE_ID)) 
        	return lt.getName();
        return MessageFormat.format(Messages.getString("dialog.run.create-item.4"), lt.getName() ,lt.getDestination().getId()); //$NON-NLS-1$
    }
	
	protected String getTreeText(ItemType it) {
        return it.getName();
    }
	
	public Image getImage(Object obj) {
		if (obj instanceof LinkType) {
			LinkType lt = (LinkType) obj;
			obj = lt.getDestination();
			if (obj == null) return null;
		}
		ItemType it = (ItemType) obj;
        return WSPlugin.getDefault().getImageFrom(it, null);
	}


	public Object[] getChildren(Object obj) {
		ItemType superItemType;
		if (obj instanceof LinkType)
			superItemType = ((LinkType)obj).getDestination();
		else 
			superItemType = (ItemType) obj;
		
		return superItemType.getSubTypes();
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
