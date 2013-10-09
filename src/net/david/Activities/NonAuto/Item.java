package net.david.Activities.NonAuto;

/** Clase para mostrar los items de los AlertDialog.
 *  Contendrá un icono y una string */
public class Item {
	private final String text;
	private final int icon;
	
    public String getText() {
		return text;
	}

	public int getIcon() {
		return icon;
	}
    
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
