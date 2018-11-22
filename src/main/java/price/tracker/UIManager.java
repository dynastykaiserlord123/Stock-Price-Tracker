package price.tracker;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class UIManager extends Applet implements ActionListener, WindowListener, KeyListener {

	JFrame fMain;
	ResultsDisplayWindow window;
	Panel pResult;
	TextField txtCommand;
	JButton butAddNew;
	JButton butDelete;
	JButton butRefresh;
	JButton butExit;
	static ArrayList<String> stockNames;
	private final static String[] fields = { "Previous Close", "Open", "Bid", "Ask", "Day's Range", "52 Week Range",
			"Volume", "Avg. Volume", "Market Cap", "Beta (3Y Monthly)", "PE Ratio (TTM)", "EPS (TTM)", "Earnings Date",
			"Forward Dividend & Yield", "Ex-Dividend Date", "1y Target Est", "Current Price" };

	public UIManager() {
		fMain = new JFrame("Stock Price tracker");
		fMain.setSize(720, 540);
		fMain.add("Center", this);
		pResult = new Panel();
		pResult.setLayout(new BorderLayout());
		window = new ResultsDisplayWindow();
		window.setData("stock_data.csv");
		pResult.add("Center", window);
		butAddNew = new JButton("Add new Stock");
		butDelete = new JButton("Delete Stock");
		butRefresh = new JButton("Refresh Values");
		butExit = new JButton("Quit");
		butAddNew.addActionListener(this);
		butDelete.addActionListener(this);
		butRefresh.addActionListener(this);
		butExit.addActionListener(this);
		txtCommand = new TextField(20);
		Panel pane = new Panel();
		pane.setLayout(new GridLayout());
		pane.add(txtCommand);
		pane.add(butAddNew);
		pane.add(butDelete);
		pane.add(butRefresh);
		pane.add(butExit);
		pane.setVisible(true);
		Panel pCommand = new Panel();
		pCommand.setLayout(new BorderLayout());
		pCommand.add("South", pane);
		pResult.add("South", pCommand);
		fMain.add(pResult);
		Dimension size = fMain.getSize();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setLayout(new BorderLayout());
		if (d.width >= 720) {
			fMain.setLocation((d.width - size.width) / 2, (d.height - size.height) / 2);
		} else {
			fMain.setLocation(0, 0);
			fMain.setSize(d);
		}
		fMain.setVisible(true);
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		((Frame) source).dispose();
		fMain.dispose();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String s = e.getActionCommand();
		boolean checkedCurrentPrice = false;
		if (s.equals("Add new Stock")) {
			String newName = txtCommand.getText().toUpperCase();
			if (newName != null && newName.length() > 0) {
				if (stockNames.contains(newName)) {
					System.out.println("Stock is already listed");
				} else {
					WebClient webClient = new WebClient(BrowserVersion.CHROME);
					webClient.getOptions().setUseInsecureSSL(true);
					webClient.getOptions().setThrowExceptionOnScriptError(false);
					webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
					webClient.getOptions().setCssEnabled(false);
					webClient.getOptions().setJavaScriptEnabled(false);
					String website = "https://ca.finance.yahoo.com/quote/" + newName + "/?p=" + newName;
					HtmlPage page;
					try {
						page = webClient.getPage(website);
						String[] sections = page.asText().split("\n");

						StringBuilder sb = new StringBuilder();
						sb.append(newName);
						String line41;
						String currentPriceUnsplit;
						String[] currentPrice = { null };
						for (int i = 0; i < sections.length; i++) {
							String[] parts = sections[i].split("\t");
							if (sections[i].contains(newName) && i > 30 && checkedCurrentPrice == false) {
								line41 = sections[i + 3].replaceAll("\r", "").replaceAll(",", "");
								currentPriceUnsplit = line41.replaceAll("-", "+");
								currentPrice = currentPriceUnsplit.split("\\+");
								checkedCurrentPrice = true;
							}
							if (Arrays.asList(fields).contains(parts[0])) {
								sb.append("," + parts[1].replaceAll(",", "").replaceAll("\r", ""));
							}
						}
						sb.append("," + currentPrice[0]);
						String extract = sb.toString();
						String exists = extract.split(",")[1];
						if (exists != null && !exists.equals("null")) {
							BufferedWriter bw = new BufferedWriter(new FileWriter("stock_data.csv", true));
							bw.write(extract + "\n");
							bw.close();
							stockNames.add(newName);
						} else {
							System.out.println("The stock name or symbol you listed could not be found");
						}
					} catch (FailingHttpStatusCodeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						webClient.close();	
					}									
				}
			}

		} else if (s.equals("Delete Stock")) {
			Integer row = window.table.getSelectedRow();
			if (row >= 0) {
				String rowContent = (String) window.table.getValueAt(row, 0);
				File file = new File("stock_data.csv");
				List<String> out;
				try {
					out = Files.lines(file.toPath()).filter(line -> !line.contains(rowContent))
							.collect(Collectors.toList());
					Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				stockNames.remove(rowContent);
			}
		} else if (s.equals("Refresh Values")) {
			refreshValues();
		} else if (s.equals("Quit")) {
			System.exit(0);
		}
		window.setData("stock_data.csv");
		fMain.setVisible(true);
	}

	private static void refreshValues() {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		BufferedWriter bw = null;
		boolean checkedCurrentPrice = false;
		try {
			String result = String.join(",", fields);
			bw = new BufferedWriter(new FileWriter("stock_data.csv"));
			String headers = "Stock Name/Symbol," + result;
			bw.write(headers.toUpperCase() + "\n");
			for (String name : stockNames) {
				String website = "https://ca.finance.yahoo.com/quote/" + name + "/?p=" + name;
				HtmlPage page = webClient.getPage(website);
				String[] sections = page.asText().split("\n");
				StringBuilder sb = new StringBuilder();
				sb.append(name);
				String line41;
				String currentPriceUnsplit;
				String[] currentPrice = { null };
				for (int i = 0; i < sections.length; i++) {
					String[] parts = sections[i].split("\t");
					if (sections[i].contains(name) && i > 30 && checkedCurrentPrice == false) {
						line41 = sections[i + 3].replaceAll("\r", "").replaceAll(",", "");
						currentPriceUnsplit = line41.replaceAll("-", "+");
						currentPrice = currentPriceUnsplit.split("\\+");
						checkedCurrentPrice = true;
					}
					if (Arrays.asList(fields).contains(parts[0])) {
						sb.append("," + parts[1].replaceAll(",", "").replaceAll("\r", ""));
					}
				}
				sb.append("," + currentPrice[0]);
				String extract = sb.toString();
				bw.write(extract + "\n");
				checkedCurrentPrice = false;
			}
		} catch (IOException ex) {

		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		webClient.close();
	}

	public static void main(String[] args) {
		String stockName;
		stockNames = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("stock_data.csv"));
			try {
				br.readLine();// Get rid of header
				stockName = br.readLine().split(",")[0];
				while (stockName != null) {
					stockNames.add(stockName);
					stockName = br.readLine().split(",")[0];
				}
			} catch (NullPointerException ex) {
				stockName = null;
			}
			br.close();

		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		}
		refreshValues();
		new UIManager();
	}
}
