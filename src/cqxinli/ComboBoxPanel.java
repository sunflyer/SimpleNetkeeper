package cqxinli;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboBoxPanel<T> extends JPanel{
	
	private static final long serialVersionUID = -347156112474612375L;
	private String gLabName;
	private T[] gComboBoxList;
	private JComboBox<T> gComboBox;
	private JLabel gLab;
	
	public ComboBoxPanel(String labName,T[] list){
		this.gLabName=labName;
		this.gComboBoxList=list;
		initComponent();
	}
	
	public ComboBoxPanel(String labName,T[] list,ItemListener i){
		this(labName,list);
		this.addItemListener(i);
	}
	
	public void setComboBoxList(T[] list){
		if(list!=null){
			 this.gComboBoxList=list;
			 this.gComboBox.removeAllItems();
			 for(int i=0;i<this.gComboBoxList.length;i++){
				 this.gComboBox.addItem(this.gComboBoxList[i]);
			 }
		}
	}
	
	public void setLabelName(String Name){
		if(Name!=null){
			this.gLabName=Name;
			gLab.setText(this.gLabName);
		}
	}
	
	public void setSelectedItem(T item){
		this.gComboBox.setSelectedItem(item);
	}
	
	public void setAllowSelect(boolean i){
		this.gComboBox.setEnabled(i);
	}
	
	public void addItemListener(ItemListener i){
		if(i!=null)
			this.gComboBox.addItemListener(i);
	}
	
	private void initComponent(){
		this.setLayout(new GridLayout(1,2));
		this.setBackground(Color.white);
		this.gComboBox=new JComboBox<T>();
		this.gLab=new JLabel(this.gLabName);
		this.gLab.setBackground(Color.white);
		this.add(this.gLab);
		this.gComboBox.setBackground(Color.white);
		this.setComboBoxList(gComboBoxList);
		this.add(gComboBox);
	}
}
