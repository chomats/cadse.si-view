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
package fede.workspace.model.manager.properties.impl.ic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;


/**
 * sources [] item de divers types
 * linktype acceptable
 * itemtype acceptable;.
 * 
 * @author chomats
 */
public class PartContentProvider implements ITreeContentProvider {
	
	/** The lt. */
	final private LinkType lt;
	
	/** The comparator. */
	final private Comparator<Object> comparator;
	
	/** The selectable value. */
	final private Object[] selectableValue;
	
	/**
	 * Instantiates a new part content provider.
	 * 
	 * @param lt the lt
	 * @param comparator the comparator
	 * @param selectableValue the selectable value
	 */
	public PartContentProvider(LinkType lt, Comparator<Object> comparator, Object[] selectableValue) {
		this.lt = lt;
		this.comparator = comparator;
		this.selectableValue = selectableValue;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ItemType){
			return sort(((ItemType)parentElement).getItems().toArray());
		}
		
		if (parentElement instanceof Item) {
			Item i = ((Item)parentElement);
			if (i.isInstanceOf(lt.getSource()));
				Collection<Item> outgoingItems = i.getOutgoingItems(lt, true);
				if (selectableValue != null) {
					HashSet<Object> o = new HashSet<Object>(Arrays.asList(selectableValue));
					o.retainAll(outgoingItems);
					return sort(o.toArray());
				}
				return sort(outgoingItems.toArray());
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof Item) {
			Item i = ((Item)element);
			if (i.isInstanceOf(lt.getDestination()));
				return i.getPartParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length != 0;
	}

	/**
	 * Accept ItemType, Item[], and List of Item
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ItemType){
			return sort(((ItemType)inputElement).getItems().toArray());
		}
		if (inputElement instanceof Item[]) {
			return sort((Item[])inputElement);
		}
		if (inputElement instanceof List) {
			return sort(((List)inputElement).toArray());
		}
		 return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	/**
	 * Sort.
	 * 
	 * @param selectableValues the selectable values
	 * 
	 * @return the object[]
	 */
	protected Object[] sort(Object[] selectableValues) {
		Arrays.sort(selectableValues, this.comparator);
		return selectableValues;
	}

	
}