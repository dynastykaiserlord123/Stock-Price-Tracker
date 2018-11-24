package price.tracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.opencsv.CSVReader;

public class ResultsDisplayWindow extends Panel{
	private Dimension dMinimum;
	protected int iRowCount;
	protected String[] sColHead = new String[0];
	protected Font fFont;
	private JScrollPane pane;
	public JTable table;
	public DefaultTableModel tableModel;

	public ResultsDisplayWindow() {
		super();
		setLayout(new BorderLayout());
		table = new JTable();
		pane = new JScrollPane(table);
		add(pane, BorderLayout.CENTER);
	}

	/**
	 * Sets the data model for the display table given a path to a csv file
	 * 
	 * @param path the path of the csv file that contains the data
	 */
	public void setData(String path) {
		remove(pane);
		Object[] columnnames;
		try {
			CSVReader reader = new CSVReader(new FileReader(path));
			List<String[]> myEntries = reader.readAll();
			columnnames = (String[]) myEntries.get(0);
			tableModel = new DefaultTableModel(columnnames, myEntries.size() - 1);
			int rowcount = tableModel.getRowCount();
			for (int x = 0; x < rowcount + 1; x++) {
				int columnnumber = 0;
				if (x > 0) {
					for (String thiscellvalue : (String[]) myEntries.get(x)) {
						tableModel.setValueAt(thiscellvalue, x - 1, columnnumber);
						columnnumber++;
					}
				}
			}
			table = new JTable(tableModel);
			pane = new JScrollPane(table);
			pane.add(table.getTableHeader());
			add(pane, BorderLayout.CENTER);
			update();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the visuals for the table with the updated and refreshed data
	 */
	public void update() {
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return dMinimum;
	}

	public void setMinimumSize(Dimension d) {
		dMinimum = d;
	}
	
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		repaint();
	}
}
