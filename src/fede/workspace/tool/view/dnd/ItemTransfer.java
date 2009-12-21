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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemDescription;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkDescription;
import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.node.OldItemInViewer;
import fr.imag.adele.fede.workspace.si.view.View;

public class ItemTransfer extends ByteArrayTransfer {
	private static ItemTransfer	instance	= new ItemTransfer();

	private static final String	TYPE_NAME	= "cadseg-item-transfer-format";

	private static final int	TYPEID		= registerType(TYPE_NAME);

	/**
	 * Returns the singleton gadget transfer instance.
	 */
	public static ItemTransfer getInstance() {
		return instance;
	}

	/**
	 * Avoid explicit instantiation
	 */
	private ItemTransfer() {
	}

	/**
	 * Method declared on Transfer.
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/**
	 * Method declared on Transfer.
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/**
	 * Method declared on Transfer.
	 */
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray((ItemInViewer[]) object);
		if (bytes != null) {
			super.javaToNative(bytes, transferData);
		}
	}

	/**
	 * Method declared on Transfer.
	 */
	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}

	protected byte[] toByteArray(ItemInViewer[] items) {
		/**
		 * Transfer data is an array of gadgets. Serialized version is: (int)
		 * number of gadgets (Gadget) gadget 1 (Gadget) gadget 2 ... repeat for
		 * each subsequent gadget see writeGadget for the (Gadget) format.
		 */
		byte[] bytes = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);

			/* write number of iteminviewer */
			out.writeInt(items.length);

			/* write iteminviewer */
			for (int i = 0; i < items.length; i++) {
				writeItem(items[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			// when in doubt send nothing
		}
		return bytes;
	}

	/**
	 * Writes the given gadget to the stream.
	 */
	private void writeItem(ItemInViewer item, ObjectOutputStream dataOut) throws IOException {
		/**
		 * Gadget serialization format is as follows: (String) name of gadget
		 * (int) number of child gadgets (Gadget) child 1 ... repeat for each
		 * child
		 */

		dataOut.writeInt(item.getKind());
		switch (item.getKind()) {
			case ItemInViewer.LINK_OUTGOING: {
				Link l = item.getLink();
				LinkDescription ld = new LinkDescription(l);
				dataOut.writeObject(ld);
				Item i = item.getItem();
				if (i == null) {
					dataOut.writeObject(null);
				} else {
					dataOut.writeObject(new ItemDescription(i));
				}
			}
		}

	}

	protected ItemInViewer[] fromByteArray(byte[] bytes) {

		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			/* read number of iteminviewer */
			int n = in.readInt();
			/* read iteminviewer */
			ArrayList<ItemInViewer> items = new ArrayList<ItemInViewer>();
			for (int i = 0; i < n; i++) {
				ItemInViewer aitem = readItem(in);
				if (aitem == null) {
					continue;
				}
				items.add(aitem);
			}
			if (items.size() == 0) {
				return null;
			}
			return items.toArray(new ItemInViewer[items.size()]);
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads and returns a single gadget from the given stream.
	 * 
	 * @throws ClassNotFoundException
	 */
	private ItemInViewer readItem(ObjectInputStream dataIn) throws IOException, ClassNotFoundException {
		/**
		 * Gadget serialization format is as follows: (String) name of gadget
		 * (int) number of child gadgets (Gadget) child 1 ... repeat for each
		 * child
		 */
		int kind = dataIn.readInt();
		if (kind == ItemInViewer.LINK_OUTGOING) {
			LinkDescription ld = (LinkDescription) dataIn.readObject();
			ItemDescription id = (ItemDescription) dataIn.readObject();
			LogicalWorkspace model = View.getInstance().getWorkspaceDomain().getLogicalWorkspace();
			if (model == null) {
				return null;
			}
			Item s = model.getItem(ld.getSource().getId());
			if (s == null) {
				return null;
			}
			Link l = s.getOutgoingLink(s.getType().getOutgoingLinkType(ld.getType()), ld.getDestination().getId());
			if (l == null) {
				return null;
			}
			return new OldItemInViewer(null, l, ItemInViewer.LINK_OUTGOING);
		}

		return null;
	}

}
