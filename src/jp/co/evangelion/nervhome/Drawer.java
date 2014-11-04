package jp.co.evangelion.nervhome;

public interface Drawer {

	public boolean hasFocus();

	public boolean requestFocus();
	


	public void setDragger(DragController dragger);
	public void setLauncher(Launcher launcher);
	public void setAdapter(ApplicationsAdapter adapter);

	public void open();

	public void close();

}
