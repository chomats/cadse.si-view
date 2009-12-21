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
package fede.workspace.tool.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ContentChangeInfo;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.internal.ContentChangeInfoImpl;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

public class WSResourceChangeListener implements IResourceChangeListener {

	public WSResourceChangeListener() {
		super();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (CadseCore.isStopped() || !CadseCore.isStarted()) return;
		
		if (event.getType() == IResourceChangeEvent.POST_CHANGE || event.getType() == IResourceChangeEvent.PRE_DELETE) {
			Map<Item, List<ContentChangeInfo>> changes = new HashMap<Item, List<ContentChangeInfo>>();
			IResourceDelta rd = event.getDelta();
			if (rd != null) {
				compute(rd, changes);
				if (CadseCore.getLogicalWorkspace() == null) {
					return;
				}

				LogicalWorkspaceTransaction copy = CadseCore.getLogicalWorkspace().createTransaction();
				for (Item item : changes.keySet()) {
					if (item.getState() != ItemState.CREATED) {
						continue;
					}
					ItemDelta operation = copy.getItem(item.getId());
					if (operation == null) {
						continue;
					}
					ContentChangeInfo[] contentsChages = changes.get(item).toArray(new ContentChangeInfo[0]);
					// System.out.println("[WS-VIEW] "+"The item
					// "+item.getId()+" is modified.\n");
					operation.notifieChangedContent(contentsChages);
				}
				if (copy.isModified()) {
					try {
						copy.commit();
					} catch (CadseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				// bug with move project
				// Workbench send a pre_delete event
				// ILifeCyleEvent has a pre_move event !!!

				// CadseDomain wd = View.getInstance().getWorkspaceDomain();
				// if (wd.isLocked()) {
				// return;
				// }
				// if (event.getResource() != null) {
				// IResource r = event.getResource();
				// Item item = WSPlugin.getItemFromResource(r);
				// if (item != null) {
				// if (event.getType() == IResourceChangeEvent.PRE_DELETE) {
				// try {
				// item.shadow(true);
				// } catch (CadseException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// }
				// }
			}
		}
	}

	void compute(IResourceDelta rd, Map<Item, List<ContentChangeInfo>> changes) {
		// System.out.println("[WS-VIEW] "+rd+" status
		// :"+Integer.toHexString(rd.getKind()));

		Item item = WSPlugin.getItemFromResource(rd.getResource());

		if (item == null) {
			// System.err.println("No item find at
			// "+rd.getResource().getFullPath());
		}

		ContentChangeInfoImpl contentchange = null;
		if (rd.getKind() == IResourceDelta.ADDED) {
			// System.out.println("[WS-VIEW] "+"The file "+rd.getResource()+" is
			// added in "+item);
			contentchange = new ContentChangeInfoImpl(ContentChangeInfo.ADDED_RESOURCE, rd.getResource().getFullPath()
					.toPortableString(), null);
		} else if (rd.getKind() == IResourceDelta.REMOVED) {
			// System.out.println("[WS-VIEW] "+"The file "+rd.getResource()+" is
			// removed in "+item);
			contentchange = new ContentChangeInfoImpl(ContentChangeInfo.REMOVED_RESOURCE, rd.getResource()
					.getFullPath().toPortableString(), null);

		} else if (rd.getKind() == IResourceDelta.CHANGED) {
			if ((rd.getFlags() & IResourceDelta.CONTENT) != 0) {
				// System.out.println("[WS-VIEW] "+"The content of the file
				// "+rd.getResource()+" is modified in "+item);
				contentchange = new ContentChangeInfoImpl(ContentChangeInfo.CHANGED_RESOURCE, rd.getResource()
						.getFullPath().toPortableString(), null);
			}
			if ((rd.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
				// System.out.println("[WS-VIEW] "+"The description of the file
				// "+rd.getResource()+" is modified in "+item);
				contentchange = new ContentChangeInfoImpl(ContentChangeInfo.CHANGED_RESOURCE, rd.getResource()
						.getFullPath().toPortableString(), null);
			}
			if ((rd.getFlags() & IResourceDelta.MOVED_FROM) != 0) {

				// System.out.println("[WS-VIEW] "+"The file
				// "+rd.getResource()+" is moved from in "+item);
				contentchange = new ContentChangeInfoImpl(ContentChangeInfo.MOVED_FROM_RESOURCE, rd.getResource()
						.getFullPath().toPortableString(), rd.getMovedFromPath().toPortableString());

			}
			if ((rd.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				// System.out.println("[WS-VIEW] "+"The file
				// "+rd.getResource()+" is moved to in "+item);
				contentchange = new ContentChangeInfoImpl(ContentChangeInfo.MOVED_FROM_RESOURCE, rd.getResource()
						.getFullPath().toPortableString(), rd.getMovedToPath().toPortableString());
			}
		}
		if (contentchange != null) {
			if (item != null) {
				List<ContentChangeInfo> infos = changes.get(item);
				if (infos == null) {
					infos = new ArrayList<ContentChangeInfo>();
					changes.put(item, infos);
				}
				infos.add(contentchange);
			} else {
				// System.err.println("No item for "+rd+" status
				// :"+Integer.toHexString(rd.getKind()));
			}
		}
		// TODO use pattern visitor
		IResourceDelta[] rds = rd.getAffectedChildren();
		for (int i = 0; i < rds.length; i++) {
			compute(rds[i], changes);
		}
	}

}
