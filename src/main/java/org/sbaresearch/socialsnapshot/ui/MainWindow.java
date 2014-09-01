package org.sbaresearch.socialsnapshot.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.SSNOOB;
import org.sbaresearch.socialsnapshot.auth.fb.FacebookAuthenticator;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator;
import org.sbaresearch.socialsnapshot.cookies.BrowserFinder;
import org.sbaresearch.socialsnapshot.cookies.BrowserType;
import org.sbaresearch.socialsnapshot.cookies.CookieExtractor;
import org.sbaresearch.socialsnapshot.util.Utils;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * The window displayed by {@link SSNOOB}.
 *
 * @author Stefan Haider (shaider@sba-research.org)
 */
public class MainWindow extends JFrame {

	private JTextField txtUsername;
	private JTextField txtPassword;
	private JList<String> listCookies;
	private JButton btnLoginUsingCredentials;
	private JButton btnLoginUsingCookie;
	private JTextArea textAreaLogConsole;

	private CookieListModel cookieListModel;

	private ICredentialWindowEventListener listener;

	private static Logger log = Logger.getLogger(MainWindow.class);
	private AppenderJTextArea appender;
	private static final long serialVersionUID = -7563323163283215900L;
	private JFormattedTextField frmtdtxtfldSnapshotPath;
	private JButton btnCancelCurrentOperation;
	private JButton btnSnapshotPathChooser;
	private JFormattedTextField frmtdtxtfldSnapshotDepth;
	private JCheckBox chckbxDownloadImages;

	private boolean extractionInProgress = false;
	private boolean finishedExtraction = false;

	/**
	 * Create the application.
	 */
	public MainWindow(final ICredentialWindowEventListener listener) {
		this.listener = listener;
		
		initializeComponents();
		initLogging();
		initCookieList();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.listener.onClose();
	}

	public void downloadFinished() {
		try {
			if (!this.finishedExtraction) {
				this.finishedExtraction = true;
				JOptionPane.showMessageDialog(this, "Download of FB content is complete. If you want to restart the export please restart this application.", "Operation complete",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			// silent exception
		}
	}

	public void downloadStarted() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				MainWindow.this.btnCancelCurrentOperation.setEnabled(true);
				MainWindow.this.btnLoginUsingCookie.setEnabled(false);
				MainWindow.this.btnLoginUsingCredentials.setEnabled(false);
			}
		});
	}

	private void initCookieList() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				MainWindow.this.cookieListModel = new CookieListModel(SSNOOB.cookieDomain);
				IFacebookAuthenticator authenticator = new FacebookAuthenticator();
				
				log.info("Extracting cookies from browsers..");
				Set<Cookie> chromeFBCookies = Utils.filterCookiesForDomain(CookieExtractor.getInstance().getCookies(BrowserType.Chrome), SSNOOB.cookieDomain);
				Set<Cookie> firefoxFBCookies = Utils.filterCookiesForDomain(CookieExtractor.getInstance().getCookies(BrowserType.Firefox), SSNOOB.cookieDomain);
				Set<Cookie> internetExplorerFBCookies = Utils.filterCookiesForDomain(CookieExtractor.getInstance().getCookies(BrowserType.InternetExplorer), SSNOOB.cookieDomain);
				
				String chromeString = "Chrome " + BrowserFinder.getInstance().getChromeVersion();
				String firefoxString = "Firefox " + BrowserFinder.getInstance().getFirefoxVersion();
				String ieString = "InternetExplorer " + BrowserFinder.getInstance().getIeVersion();
				
				if (chromeFBCookies.size() > 0) {
					log.info("Checking cookie validity for Chrome");
					if (authenticator.checkSessionCookieValidity(chromeFBCookies, BrowserType.Chrome)) {
						MainWindow.this.cookieListModel.addAllElements(chromeFBCookies, chromeString);
					}
				}
				if (firefoxFBCookies.size() > 0) {
					log.info("Checking cookie validity for Firefox");
					if (authenticator.checkSessionCookieValidity(firefoxFBCookies, BrowserType.Firefox)) {
						MainWindow.this.cookieListModel.addAllElements(firefoxFBCookies, firefoxString);
					}
				}
				if (internetExplorerFBCookies.size() > 0) {
					log.info("Checking cookie validity for IE");
					if (authenticator.checkSessionCookieValidity(internetExplorerFBCookies, BrowserType.InternetExplorer)) {
						MainWindow.this.cookieListModel.addAllElements(internetExplorerFBCookies, ieString);
					}
				}
				
				MainWindow.this.listCookies.setModel(MainWindow.this.cookieListModel);
			}
		});
		t.start();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			log.error("Unable to set systems look and feel", e1);
		}
		
		setTitle("SSNoob");
		setBounds(100, 100, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[300:n,grow][grow]", "[][][grow]"));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Login", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel, "cell 0 1,growx,aligny top");
		panel.setLayout(new MigLayout("", "[grow]", "[][][]"));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "User Credentials", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_2, "cell 0 2,growx");
		panel_2.setLayout(new MigLayout("", "[grow]", "[][][][][]"));
		
		JLabel lblUserName = new JLabel("Username");
		panel_2.add(lblUserName, "cell 0 0");
		
		this.txtUsername = new JTextField();
		panel_2.add(this.txtUsername, "cell 0 1,growx");
		this.txtUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		panel_2.add(lblPassword, "cell 0 2");
		
		this.txtPassword = new JPasswordField();
		this.txtPassword.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loginCredientials();
			}
		});
		panel_2.add(this.txtPassword, "cell 0 3,growx");
		this.txtPassword.setColumns(10);
		
		this.btnLoginUsingCredentials = new JButton("Login using credentials");
		this.btnLoginUsingCredentials.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loginCredientials();
			}
		});
		panel_2.add(this.btnLoginUsingCredentials, "cell 0 4,grow");
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Cookies", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_3, "cell 0 1,growx");
		panel_3.setLayout(new MigLayout("", "[grow]", "[50px:150px:500px][]"));
		
		this.listCookies = new JList<String>();
		this.listCookies.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					loginCookie();
				}
			}
		});
		panel_3.add(this.listCookies, "cell 0 0,grow");
		
		this.btnLoginUsingCookie = new JButton("Login using cookie");
		this.btnLoginUsingCookie.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				log.debug(MainWindow.this.btnLoginUsingCookie.getText() + "clicked");
				loginCookie();
			}
		});
		panel_3.add(this.btnLoginUsingCookie, "cell 0 1,growx");

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel_4, "cell 0 0,growx,aligny top");
		panel_4.setLayout(new MigLayout("", "[grow][]", "[][][][][]"));

		JLabel lblSnapshotPath = new JLabel("Snapshot path");
		panel_4.add(lblSnapshotPath, "cell 0 0");

		this.frmtdtxtfldSnapshotPath = new JFormattedTextField();
		panel_4.add(this.frmtdtxtfldSnapshotPath, "cell 0 1,growx");
		this.frmtdtxtfldSnapshotPath.setColumns(10);
		this.frmtdtxtfldSnapshotPath.addPropertyChangeListener("value", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Config.setSnapshotDirectory(evt.getNewValue().toString());
			}
		});
		this.frmtdtxtfldSnapshotPath.setValue(new File(Config.getSnapshotDirectory()));
		
		this.btnSnapshotPathChooser = new JButton("...");
		this.btnSnapshotPathChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser((File) MainWindow.this.frmtdtxtfldSnapshotPath.getValue());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(MainWindow.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					log.debug("Selected snapshot path: " + chooser.getSelectedFile().getAbsolutePath());
					MainWindow.this.frmtdtxtfldSnapshotPath.setValue(chooser.getSelectedFile());
				}
			}
		});
		panel_4.add(this.btnSnapshotPathChooser, "cell 1 1");

		JLabel lblSnapshotDepth = new JLabel("Snapshot depth");
		panel_4.add(lblSnapshotDepth, "cell 0 2");
		
		this.frmtdtxtfldSnapshotDepth = new JFormattedTextField();
		panel_4.add(this.frmtdtxtfldSnapshotDepth, "cell 0 3 2 1,growx");
		this.frmtdtxtfldSnapshotDepth.addPropertyChangeListener("value", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Config.getInstance();
				Config.setMaxSnapshotDepth((int) evt.getNewValue());
			}
		});
		this.frmtdtxtfldSnapshotDepth.setValue(Config.getMaxSnapshotDepth());
		
		this.chckbxDownloadImages = new JCheckBox("Download images");
		panel_4.add(this.chckbxDownloadImages, "cell 0 4 2 1,growx");
		this.chckbxDownloadImages.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Config.setImageDownloadEnabled(MainWindow.this.chckbxDownloadImages.isSelected());
			}
		});
		this.chckbxDownloadImages.setSelected(Config.isImageDownloadEnabled());

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Console", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel_1, "cell 1 0 1 3,grow");
		panel_1.setLayout(new BorderLayout(0, 0));

		this.textAreaLogConsole = new JTextArea();
		this.textAreaLogConsole.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(this.textAreaLogConsole);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_1.add(scrollPane, BorderLayout.CENTER);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Operations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(panel_5, "cell 0 2,grow");
		panel_5.setLayout(new MigLayout("", "[grow]", "[]"));

		this.btnCancelCurrentOperation = new JButton("Cancel current operation");
		this.btnCancelCurrentOperation.setEnabled(false);
		this.btnCancelCurrentOperation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Requesting Queue to stop..");
				MainWindow.this.listener.onClose();
			}
		});
		panel_5.add(this.btnCancelCurrentOperation, "cell 0 0,grow");
		
	}

	private void initLogging() {
		this.appender = new AppenderJTextArea(this.textAreaLogConsole, Level.ALL);
		Logger log = Logger.getRootLogger();
		log.addAppender(this.appender);
	}
	
	private void loginCookie() {
		if (!this.extractionInProgress) {
			int idx = MainWindow.this.listCookies.getSelectedIndex();
			if (idx >= 0) {
				String browserString = MainWindow.this.cookieListModel.getBrowser(idx);
				Set<Cookie> facebookCookies = MainWindow.this.cookieListModel.getBrowsersCookies(browserString);
				String cookieString = Utils.cookieCollectionToString(facebookCookies);
				
				this.extractionInProgress = true;
				downloadStarted();
				this.listener.onUserCookieProvided(cookieString, BrowserType.parseBrowserType(browserString));
			} else {
				JOptionPane.showMessageDialog(this, "Please select a browser first", "Browser select", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void loginCredientials() {
		if (!this.extractionInProgress) {
			String userName = MainWindow.this.txtUsername.getText();
			String password = MainWindow.this.txtPassword.getText();
			if (userName.length() > 0 && password.length() > 0) {
				this.extractionInProgress = true;
				downloadStarted();
				this.listener.onUserCredentialsProvided(userName, password);
			} else {
				JOptionPane.showMessageDialog(this, "Please provide a valid username and password", "Credentials input", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

}
