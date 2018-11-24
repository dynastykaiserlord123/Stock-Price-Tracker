package price.tracker;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class UIManager extends Applet implements ActionListener {
	private static WebClient webClient;
	private JFrame fMain;
	private ResultsDisplayWindow window;
	private Panel pResult;
	private TextField txtCommand;
	private JButton butAddNew;
	private JButton butDelete;
	private JButton butRefresh;
	private JButton butExit;
	private static ArrayList<String> stockNames;
	private static String website;
	private static HtmlPage page;
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
		txtCommand = new TextField(15);
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

	/**
	 * Configures and sets up the web client for loading web pages
	 */
	private static void getWebClientconfig() {
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
	}

	/**
	 * The application will search for the stock given the name or symbol and add
	 * its information into the display table and the stock_data.csv file. If the
	 * user attempts to add a stock that is already listed, the application will
	 * tell the user that the stock they are trying to add already exists. Likewise,
	 * if the user incorrectly spells the symbol or name of the stock, they will be
	 * given a warning that no stock could be found under the input name
	 * 
	 * @param nameOfStock
	 *            The name or symbol of the stock to be added
	 */
	private static void addNew(String nameOfStock) {
		boolean checkedCurrentPrice = false;
		try {
			page = webClient.getPage(website + nameOfStock + "/?p=" + nameOfStock);
			String[] sections = page.asText().split("\n");
			StringBuilder sb = new StringBuilder();
			sb.append(nameOfStock);
			String currentPriceInfoLine;
			String[] currentPrice = { null };
			for (int i = 0; i < sections.length; i++) {
				// Reads the text line buy line to look for the relevant fields and
				// corresponding data
				String[] parts = sections[i].split("\t");
				if (sections[i].contains(nameOfStock) && i > 30 && checkedCurrentPrice == false) {
					currentPriceInfoLine = sections[i + 3].replaceAll("\r", "").replaceAll(",", "");
					currentPrice = currentPriceInfoLine.replaceAll("-", "+").split("\\+");
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
			} else {
				System.out.println("Warning: The stock name or symbol you listed could not be found!");
			}
		} catch (FailingHttpStatusCodeException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Removes the selected line in the display table and the corresponding record
	 * in the save file
	 */
	private void deleteStock() {
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
				e1.printStackTrace();
			}
			stockNames.remove(rowContent);
		}
	}

	public void actionPerformed(ActionEvent event) {
		String s = event.getActionCommand();
		if (s.equals("Add new Stock")) {
			String newName = txtCommand.getText().toUpperCase();
			if (newName != null && newName.length() > 0) {
				if (stockNames.contains(newName)) {
					System.out.println("Warning: Stock is already listed!");
				} else {
					addNew(newName);
					stockNames.add(newName);
				}
			}
		} else if (s.equals("Delete Stock")) {
			deleteStock();
		} else if (s.equals("Refresh Values")) {
			refreshValues();
		} else if (s.equals("Quit")) {
			webClient.close();
			System.exit(0);
		}
		window.setData("stock_data.csv");
		fMain.setVisible(true);
	}

	/**
	 * Refreshes all fields for each stock in the table to their most recent values
	 * and updates them in the appropriate save file as well
	 */
	private static void refreshValues() {
		BufferedWriter bw = null;
		try {
			String result = String.join(",", fields);
			String headers = "Stock Name/Symbol," + result;
			bw = new BufferedWriter(new FileWriter("stock_data.csv"));
			bw.write(headers.toUpperCase() + "\n");
			bw.close();
			for (String name : stockNames) {
				addNew(name);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String stockName;
		stockNames = new ArrayList<String>();
		website = "https://ca.finance.yahoo.com/quote/";
		try {
			BufferedReader br = new BufferedReader(new FileReader("stock_data.csv"));
			stockName = br.readLine();// Get rid of header
			while (stockName != null && stockName.length() > 0) {
				try {
					stockName = br.readLine().split(",")[0].toUpperCase();
					stockNames.add(stockName);
				} catch (NullPointerException ex) {
					stockName = null;
				}
			}
			br.close();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getWebClientconfig(); 
		refreshValues();
		new UIManager();
	}
}
