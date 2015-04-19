package cn.sunflyer.simpnk.obj;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PluginRouter extends PluginConfig{

	public PluginRouter(String szFileName) throws FileNotFoundException,
			IOException {
		super(szFileName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean register() {
		// TODO Auto-generated method stub
		return false;
	}

}
