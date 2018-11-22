package price.tracker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.opencsv.CSVReader;

public class ResultsDisplayWindow extends Panel implements MouseListener {
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

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		repaint();
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException ex) {
			
		}
	}

	public void clearTable() {
		remove(pane);
		update();
	}

	public Dimension getPreferredSize() {
		return dMinimum;
	}

	public void setMinimumSize(Dimension d) {
		dMinimum = d;
	}

	public boolean handleEvent(Event e) {
		switch (e.id) {
		case Event.SCROLL_LINE_UP:
		case Event.SCROLL_LINE_DOWN:
		case Event.SCROLL_PAGE_UP:
		case Event.SCROLL_PAGE_DOWN:
		case Event.SCROLL_ABSOLUTE:
			repaint();
			return true;
		}
		return super.handleEvent(e);
	}

	public void update() {
		repaint();
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int row = e.getY() / table.getRowHeight();
		System.out.println(row);
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
