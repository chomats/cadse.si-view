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
package fr.imag.adele.cadse.ui.field.core;

import org.eclipse.swt.SWT;

import fede.workspace.model.manager.properties.IC_ForCheckedViewer;
import fede.workspace.model.manager.properties.IInteractionControllerForBrowserOrCombo;
import fede.workspace.model.manager.properties.IInteractionControllerForList;
import fede.workspace.model.manager.properties.impl.ParentPartGetAndSet;
import fede.workspace.model.manager.properties.impl.ic.IC_FileResourceForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_FolderResource_ForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_IconResourceForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List;
import fede.workspace.model.manager.properties.impl.ic.IC_PartParentForBrowser_Combo;
import fede.workspace.model.manager.properties.impl.ic.IC_DefaultForList;
import fede.workspace.model.manager.properties.impl.mc.LinkModelController;
import fede.workspace.model.manager.properties.impl.mc.MC_DefaultForList;
import fede.workspace.model.manager.properties.impl.mc.MC_DisplayNameItemProperty;
import fede.workspace.model.manager.properties.impl.mc.MC_IDItemProperty;
import fede.workspace.model.manager.properties.impl.mc.MC_ShortNameItemProperty;
import fede.workspace.model.manager.properties.impl.mc.StringToBooleanModelControler;
import fede.workspace.model.manager.properties.impl.mc.StringToOneResourceModelController;
import fede.workspace.model.manager.properties.impl.mc.StringToResourceListModelController;
import fede.workspace.model.manager.properties.impl.mc.StringToResourceSimpleModelController;
import fede.workspace.model.manager.properties.impl.ui.DBrowserUI;
import fede.workspace.model.manager.properties.impl.ui.DCheckBoxUI;
import fede.workspace.model.manager.properties.impl.ui.DCheckedListUI;
import fede.workspace.model.manager.properties.impl.ui.DComboUI;
import fede.workspace.model.manager.properties.impl.ui.DListUI;
import fede.workspace.model.manager.properties.impl.ui.DTextUI;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.impl.internal.ui.PagesImpl;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.CreationAction;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.impl.ui.PageImpl;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;

public class FieldsCore {

	static public IPage createPage(String key, String title, String description, int hspan, IActionPage action,
			UIField... fieldDescriptions) {
		return new PageImpl(key, title, title, description, false, hspan, action, fieldDescriptions);
	}

	static public IPage createPage(String key, String title, String description, int hspan,
			UIField... fieldDescriptions) {
		return new PageImpl(key, title, title, description, false, hspan, null, fieldDescriptions);
	}

	static public Object[] A(Object... param) {
		return param;
	}

	static public Pages createWizard(AbstractActionPage action, IPage... pages) {

		return new PagesImpl(false, action, pages);
	}

	static public DBrowserUI createOneFolderOrFileField(String key, String label, String title, String message,
			boolean selectFolder, String filter, int kindroot) {

		IModelController mc = new StringToOneResourceModelController();
		IC_FileResourceForBrowser_Combo_List ic = new IC_FileResourceForBrowser_Combo_List(title, message, kindroot,
				filter, selectFolder);
		DBrowserUI ui = new DBrowserUI(key, label, EPosLabel.top, mc, ic, SWT.BORDER | SWT.SINGLE);
		return ui;

	}

	static public DListUI createFolderField(String key, String label, String title, String message, int kindroot) {

		IC_FolderResource_ForBrowser_Combo_List ic = new IC_FolderResource_ForBrowser_Combo_List(title, message,
				kindroot);
		StringToResourceListModelController mc = new StringToResourceListModelController();
		DListUI ui = new DListUI(key, label, EPosLabel.top, mc, ic, true, true);
		return ui;

	}

	static public DTextUI createShortNameField() {
		return new DTextUI(CadseGCST.ITEM_at_NAME, "name:", EPosLabel.left, new MC_ShortNameItemProperty(),
				null);

	}

	static public DTextUI createShortNameField_Noborder() {
		return new DTextUI(CadseGCST.ITEM_at_NAME, "name:", EPosLabel.left, new MC_ShortNameItemProperty(),
				null, SWT.SINGLE, 1, null);
	}

	static public DTextUI createUniqueNameField() {
		return createShortNameField_Noborder();

	}

	static public DTextUI createDisplayNameField() {
		return new DTextUI(CadseGCST.ITEM_at_DISPLAY_NAME, "display name:", EPosLabel.left,
				new MC_DisplayNameItemProperty(), null, SWT.SINGLE, 1, null);
	}

	static public DTextUI createIDField() {
		return new DTextUI(CadseGCST.ITEM_at_DISPLAY_NAME, "#ID:", EPosLabel.left, new MC_IDItemProperty(),
				null, SWT.SINGLE, 1, null);
	}

	// TODO
	// static public IFieldDescription createLinkCheckDependencyField(String
	// linkName, String... linksTransitives) {
	// return createFD(linkName,
	// IFieldDescription.LABEL,"",
	// IFieldDescription.POS_LABEL, EPosLabel.none,
	// IFieldDescription.VALUE_CONTROLLER,new DefaultValueControler(),
	// IFieldDescription.FIELD_UI_CONTROLLER, new LinkViewerController(),
	// LinkViewerController.LINKS_TRANSITIVES, linksTransitives);
	// }
	public static DCheckedListUI createCheckBoxList(String key, String label, IC_ForCheckedViewer ic,
			IModelController mc) {
		return new DCheckedListUI(key, label, label == null ? EPosLabel.none : EPosLabel.top, mc, ic);
	}

	public static DCheckBoxUI createCheckBox(String key, String label) {
		return createCheckBox(key, label, null);
	}

	public static DCheckBoxUI createCheckBox(String key, String label, IInteractionController ic) {
		return new DCheckBoxUI(key, label, EPosLabel.none, new StringToBooleanModelControler(), ic);
	}

	static public DBrowserUI createLinkDependencyField(LinkType key, String label, IC_LinkForBrowser_Combo_List ic,
			boolean mandatory, String msg) {
		return createLinkDependencyField(key, label, label == null ? EPosLabel.none : EPosLabel.top, ic, mandatory, msg);

	}

	static public DBrowserUI createLinkDependencyField(LinkType key, String label, EPosLabel poslabel,
			IC_LinkForBrowser_Combo_List ic, boolean mandatory, String msg) {
		LinkModelController mc = new LinkModelController(mandatory, msg, key);
		return new DBrowserUI(key.getName(), label, poslabel, mc, ic, SWT.BORDER | SWT.SINGLE);

	}

	static public DBrowserUI createSelectContainmentItemField(String label, String selectTitle, String selectMessage) {
		return new DBrowserUI("", label, EPosLabel.left, new ParentPartGetAndSet(), new IC_PartParentForBrowser_Combo(
				selectTitle, selectMessage), SWT.BORDER | SWT.SINGLE);
	}

	public static DTextUI createIntField(String key, String label, IModelController mc, IInteractionController ic) {
		return new DTextUI(key, label, EPosLabel.left, mc, ic);
	}

	public static DTextUI createIntField(String key, String label, IModelController mc) {
		return new DTextUI(key, label, EPosLabel.left, mc, null);
	}

	public static DBrowserUI createBrowserIconField(String key, String label, EPosLabel poslabel) {
		return new DBrowserUI(key, label, poslabel, new StringToResourceSimpleModelController(),
				new IC_IconResourceForBrowser_Combo_List(), SWT.BORDER | SWT.SINGLE);
	}

	public static DBrowserUI createBrowserField(String key, String label, EPosLabel poslabel,
			IInteractionControllerForBrowserOrCombo ic, IModelController mc) {
		return new DBrowserUI(key, label, poslabel, mc, ic, SWT.BORDER | SWT.SINGLE);
	}

	public static DComboUI createComboBox(String key, String label, EPosLabel poslabel,
			IInteractionControllerForBrowserOrCombo ic, IModelController mc, boolean edit) {
		if (mc == null) {
			mc = new MC_AttributesItem();
		}
		return new DComboUI(key, label, poslabel, mc, ic, edit);
	}

	public static Pages createDefaultNameWizard(String title, String description, CreationAction action) {
		return createWizard(action, FieldsCore.createPage("page1", title, description, 2, FieldsCore
				.createShortNameField()));
	}

	public static DTextUI createTextField(String key, String label) {
		return createTextField(key, label, 1, null, null, null);
	}

	public static DTextUI createTextField(String key, String label, int vspan) {
		return createTextField(key, label, vspan, null, null, null);
	}

	public static DTextUI createTextField(String key, String label, String tooltip) {
		return createTextField(key, label, 1, tooltip, null, null);
	}

	public static DTextUI createTextField(String key, String label, IModelController mc) {
		return createTextField(key, label, 1, null, null, mc);
	}

	public static DTextUI createTextField(String key, String label, IInteractionController uc) {
		return createTextField(key, label, 1, null, uc, null);
	}

	public static DTextUI createTextField(String key, String label, int vspan, String tooltip,
			IInteractionController ic, IModelController mc) {
		if (mc == null) {
			mc = new MC_AttributesItem();
		}
		return new DTextUI(key, label, EPosLabel.left, mc, ic, 0, vspan, tooltip);
	}

	public static DListUI createList_ListOfString(String key, String label, String title, String message,
			boolean allowDuplicate, int min, int max) {
		return createList(key, label, new MC_DefaultForList(min, max), new IC_DefaultForList(title,
				message, allowDuplicate));
	}

	public static DListUI createList_ListOfString(String key, String label, MC_DefaultForList mc,
			IC_DefaultForList ic) {
		return createList(key, label, mc, ic);
	}

	public static DListUI createList(String key, String label, IModelController mc, IInteractionControllerForList ic,
			Object... objects) {
		return new DListUI(key, label, EPosLabel.top, mc, ic, true, true);

	}

}
