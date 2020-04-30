package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;


public class SelectServer extends AbstractTableModel  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2565290011427015108L;

	private Vector<Vector<Object>> content = null;

    private String[] title_name = { "房间号","用户A","用户B","用户C","用户D","庄家","局/最大局","癞子皮","房间人数","房间状态","模式"};

    public SelectServer() {
        content = new Vector<Vector<Object>>();
    }

    public SelectServer(int count) {
        content = new Vector<Vector<Object>>(count);
    }

    /** 
     * 加入一空行 
     * @param row 行号 
     */
    public void addRow(int row) {
        Vector<Object> v = new Vector<Object>(8);
        v.add(0, null);
        v.add(1, null);
        v.add(2, null);
        v.add(3, null);
        v.add(4, null);
        v.add(5, null);
        v.add(6, null);
        v.add(7, null);
        v.add(8, null);
        v.add(9, null);
        v.add(10, null);
        content.add(row, v);
    }

    /** 
     * 加入一行内容 
     */
    public void addRow(Map<String,Object> map) {
        Vector<Object> v = new Vector<Object>(8);
        v.add(0, map.get("roomNo"));
        v.add(1,map.get("userA"));
        v.add(2,map.get("userB"));
        v.add(3,map.get("userC"));
        v.add(4,map.get("userD"));
        v.add(5,map.get("banker"));
        v.add(6,map.get("gamenumber")+"/"+map.get("maxnumber"));
        v.add(7,map.get("draw_s"));
        v.add(8,map.get("max_person"));
        v.add(9,map.get("state"));
        v.add(10,map.get("game_type"));
        content.add(v);
    }
    public void removeRow() {
        content.removeAllElements(); 
    }
    public void removeRow(int row) {
        content.remove(row);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(rowIndex == 2) {
            return false;
        }
        return true;
    }

    public void setValueAt(Object value, int row, int col) {
        ((Vector<?>) content.get(row)).remove(col);
        ((Vector<Object>) content.get(row)).add(col, value);
        this.fireTableCellUpdated(row, col);
    }

    public String getColumnName(int col) {
        return title_name[col];
    }

    public int getColumnCount() {
        return title_name.length;
    }

    public int getRowCount() {
        return content.size();
    }

    public Object getValueAt(int row, int col) {
        return ((Vector<Object>) content.get(row)).get(col);
    }

    public Class<? extends Object> getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
    public static void main(String[] args) {
    	Map<String,Object> map1 = new HashMap<String, Object>();
    	Map<String,Object> map2 = new HashMap<String, Object>();
    	Map<String,Object> map3 = new HashMap<String, Object>();
    	map3.put("a","1");
    	Map<String,Object> map4 = new HashMap<String, Object>();
    	map4.put("b","1");
    	map1.put("majiong",map3);
    	map1.put("majiongb",map4);
    	map1.put("A","123");
    	map2.putAll(map1);
    	map2.put("majiong","123");
    	System.out.println(map1);
    	System.out.println(map2);
    	
    	
	}
}
