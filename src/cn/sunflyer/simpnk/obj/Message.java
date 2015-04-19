package cn.sunflyer.simpnk.obj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Message implements Iterator<Bundle>,Iterable<Bundle>{
	
	private List<Bundle> mList;
	
	public Message(){
		this.mList  = new ArrayList<Bundle>();
	}
	
	public Message(Bundle x){
		this();
		this.mList.add(x);
	}
	
	public void add(Bundle p){
		if(p != null)
			mList.add(p);
	}
	
	public Iterator<Bundle> iterator(){
		return this.mList.iterator();
	}
	
	public Bundle getBundle(int index){
		return index >= 0 ? mList.get(index) : null ;
	}

	@Override
	public boolean hasNext() {
		return mList.iterator().hasNext();
	}

	@Override
	public Bundle next() {
		return mList.iterator().next();
	}

	@Override
	public void remove() {
		mList.iterator().remove();
	}

}
