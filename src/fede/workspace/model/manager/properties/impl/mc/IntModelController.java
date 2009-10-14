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
package fede.workspace.model.manager.properties.impl.mc;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

public final class IntModelController extends MC_AttributesItem {
	int		min;
	int		max;
	String	msg_min;
	String	msg_max;
	Integer	defaultValue;

	public IntModelController(int min, int max, String msg_min, String msg_max, Integer defaultValue) {
		this.min = min;
		this.max = max;
		this.msg_min = msg_min;
		this.msg_max = msg_max;
		this.defaultValue = defaultValue;
	}

	public IntModelController(CompactUUID id) {
		super(id);
	}

	@Override
	public boolean validValueChanged(UIField field, Object value) {
		try {
			int intValue = Integer.parseInt((String) value);
			if (msg_min != null && intValue < min) {
				setMessageError(msg_min);
				return true;
			}

			if (msg_max != null && intValue > max) {
				setMessageError(msg_max);
				return true;
			}
		} catch (NumberFormatException e) {
			setMessageError(e.getMessage());
			return true;
		}
		return false;
	}

	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, Convert.toInteger(value));
	}

	@Override
	public Object defaultValue() {
		if (defaultValue == null) {
			IAttributeType<?> refAttributeDefinition = getUIField().getAttributeDefinition();
			if (refAttributeDefinition != null) {
				return refAttributeDefinition.getDefaultValue();
			}
		}

		return defaultValue;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseGCST.INT_MODEL_CONTROLLER_at_ERROR_MSG_MAX_ == type) {
			return (T) msg_max;
		}
		if (CadseGCST.INT_MODEL_CONTROLLER_at_MAX_ == type) {
			return (T) (Integer.valueOf(max));
		}
		if (CadseGCST.INT_MODEL_CONTROLLER_at_ERROR_MSG_MIN_ == type) {
			return (T) msg_min;
		}
		if (CadseGCST.INT_MODEL_CONTROLLER_at_MIN_ == type) {
			return (T) (Integer.valueOf(min));
		}
		if (CadseGCST.INT_MODEL_CONTROLLER_at_DEFAULT_VALUE_ == type) {
			return (T) defaultValue;
		}
		return super.internalGetOwnerAttribute(type);
	}
	
	@Override
	public ItemType getType() {
		return CadseGCST.INT_MODEL_CONTROLLER;
	}
}