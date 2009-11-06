package fede.workspace.tool.view.node;

import fr.imag.adele.cadse.core.IItemNode;

public interface ItemNodeIsSelected {

	/**
	 * return if the node is selected, grayed, deselected
	 * 
	 * @param node
	 *            a node of a the tree
	 * @return {@link IItemNode#DESELECTED}, {@link IItemNode#GRAY_SELECTED},
	 *         {@link IItemNode#SELECTED}
	 */
	public int isSelected(IItemNode node);
}
