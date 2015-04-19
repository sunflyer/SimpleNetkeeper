package cn.sunflyer.simpnk.obj;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PluginDial extends PluginConfig{

	public PluginDial(String szFileName) throws FileNotFoundException,
			IOException {
		super(szFileName);
	}

	@Override
	public boolean register() {
		// TODO Auto-generated method stub
		return false;
	}

}
