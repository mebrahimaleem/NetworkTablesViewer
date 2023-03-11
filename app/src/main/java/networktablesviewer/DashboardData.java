package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.lang.*;

public class DashboardData implements TableModel {
	ArrayList<TableModelListener> ls = new ArrayList<TableModelListener>();;
	ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>(); //TODO: replace Object with correct class

	public void addTableModelListener(TableModelListener l){
		ls.add(l);
	}

	public Class<?> getColumnClass(int i){
		return Object.class; //TODO: implement
	}

	public int getColumnCount(){
		return 100;
	}

	public String getColumnName(int i){
		return "";
	}

	public int getRowCount(){
		return 100;
	}

	public Object getValueAt(int x, int y){
		if (y > table.size() - 1) return null;
		if (x > table.get(0).size() - 1) return null;
		return table.get(y).get(x);
	}

	public boolean isCellEditable(int x, int y){
		return false;
	}

	public void removeTableModelListener(TableModelListener l){
		ls.remove(l);
	}

	public void setValueAt(Object val, int x, int y){
		while (y + 1 > table.size()){
			table.add(new ArrayList<Object>());
		}

		while (x + 1 > table.get(0).size()){
			for (ArrayList<Object> row : table){
				row.add(null);
			}
		}

		table.get(y).set(x, val);

		for (TableModelListener l : ls){
			l.tableChanged(new TableModelEvent(this, y, y, x));
		}
	}
	
	public void updateStructure() {
		for (TableModelListener l : ls){
			l.tableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
		}
	}
}
