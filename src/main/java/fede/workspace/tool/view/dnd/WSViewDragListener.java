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
package fede.workspace.tool.view.dnd;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import fede.workspace.tool.view.ItemInViewer;

/**
 * Supports dragging gadgets from a structured viewer.
 */
public class WSViewDragListener extends DragSourceAdapter {
	private TreeViewer viewer;

	public WSViewDragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		if (!event.doit)
			return;
//		// if the gadget was moved, remove it from the source viewer
//		if (event.detail == DND.DROP_MOVE) {
//			IStructuredSelection selection = (IStructuredSelection) viewer
//					.getSelection();
//			// for (Iterator it = selection.iterator(); it.hasNext();) {
//			// ((Gadget)it.next()).setParent(null);
//			// }
//			viewer.refresh();
//		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		
		
		ItemInViewer[] items = (ItemInViewer[]) selection.toList().toArray(new
				 ItemInViewer[selection.size()]);;
		if (ItemTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = items;
		}
		// else if
		// (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
		// byte[] data = GadgetTransfer.getInstance().toByteArray(gadgets);
		// event.data = new
		// PluginTransferData("org.eclipse.ui.examples.gdt.gadgetDrop", data);
		// }
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		if (viewer.getSelection().isEmpty()) {
			event.doit = false; return;
		}
		
		////StructuredSelection structuredSelection = ((StructuredSelection)viewer.getSelection());
		//ItemInViewer[] items = (ItemInViewer[]) structuredSelection.toList().toArray(new
		//		 ItemInViewer[structuredSelection.size()]);;
		//ItemInViewer 
		/* <ROOT Item>
		 * <Link, Item>
		 * <Link ...>
		 * <Categorie>
		 * */
		event.doit = !viewer.getSelection().isEmpty();
	}
}