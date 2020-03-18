import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import  java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

import javax.swing.*;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class LibraryDB {

	public static void main(String[] args) {


		  JFrame frame = new JFrame("Database");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PushCounterPanel panel = new PushCounterPanel();
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setVisible(true);

		LibraryDB library = new LibraryDB();
		if (!library.loginProxy()) {
			System.out.println("Login proxy failed, please re-examine your username and password!");
			return;
		}
		if (!library.loginDB()) {
			System.out.println("Login database failed, please re-examine your username and password!");
			return;
		}
		System.out.println("Login succeed!");
		try {
			library.run();
		} finally {
			library.close();
		}
	}


    class _Database extends JPanel {
        private JButton Book_Search;
         private JButton Book_Borrow;
         private JButton Book_Return;
         private JButton Book_Renew;
         private JButton Book_Reserve;
        private JLabel label;


        public _Database()
        {
            Book_Search = new JButton("Book Search ");
            label = new JLabel();
             push.addActionListener(new ButtonListener());
            add(Book_Search);
            add(Book_Borrow);
            add(Book_Return);
            add(Book_Renew);
            add(Book_Reserve);
            add(label);
            setBackground(Color.cyan);
            setPreferredSize(new Dimension(300, 40));

        }
    }


    public void actionPeformed(ActionEvent event ){

         label.setText("Im in the library System");
    }

	Scanner in = null;
	Connection conn = null;
	// Database Host
	final String databaseHost = "";
	// Database Port
	final int databasePort =;
	// Database name
	final String database = "";
	final String proxyHost = "";
	final int proxyPort = ;
	final String forwardHost = "localhost";
	int forwardPort;
	Session proxySession = null;
	boolean noException = true;

	// JDBC connecting host
	String jdbcHost;
	// JDBC connecting port
	int jdbcPort;


	String[] options = { // if you want to add an option, append to the end of
			// this array
			"Book search(by ISBN)", "Borrow a Book (by ISBN and SID)", "Book Return(by ISBN and SID)",
			"Book Renew(by ISBN and SID)", "Book Reserve(by ISBN and SID)", "exit"
	};


	/**
	 * Get YES or NO. Do not change this function.
	 *
	 * @return boolean
	 */
	boolean getYESorNO(String message) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(message));
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog dialog = pane.createDialog(null, "Question");
		dialog.setVisible(true);
		boolean result = JOptionPane.YES_OPTION == (int) pane.getValue();
		dialog.dispose();
		return result;
	}

	/**
	 * Get username & password. Do not change this function.
	 *
	 * @return username & password
	 */
	String[] getUsernamePassword(String title) {
		JPanel panel = new JPanel();
		final TextField usernameField = new TextField();
		final JPasswordField passwordField = new JPasswordField();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("Username"));
		panel.add(usernameField);
		panel.add(new JLabel("Password"));
		panel.add(passwordField);
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
			private static final long serialVersionUID = 1L;

			@Override
			public void selectInitialValue() {
				usernameField.requestFocusInWindow();
			}
		};
		JDialog dialog = pane.createDialog(null, title);
		dialog.setVisible(true);
		dialog.dispose();
		return new String[] { usernameField.getText(), new String(passwordField.getPassword()) };
	}

	/**
	 * Login the proxy. Do not change this function.
	 *
	 * @return boolean
	 */
	public boolean loginProxy() {
		if (getYESorNO("Using ssh tunnel or not?")) { // if using ssh tunnel
			String[] namePwd = getUsernamePassword("Login cs lab computer");
			String sshUser = namePwd[0];
			String sshPwd = namePwd[1];
			try {
				proxySession = new JSch().getSession(sshUser, proxyHost, proxyPort);
				proxySession.setPassword(sshPwd);
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				proxySession.setConfig(config);
				proxySession.connect();
				proxySession.setPortForwardingL(forwardHost, 0, databaseHost, databasePort);
				forwardPort = Integer.parseInt(proxySession.getPortForwardingL()[0].split(":")[0]);
			} catch (JSchException e) {
				e.printStackTrace();
				return false;
			}
			jdbcHost = forwardHost;
			jdbcPort = forwardPort;
		} else {
			jdbcHost = databaseHost;
			jdbcPort = databasePort;
		}
		return true;
	}

	/**
	 * Login the oracle system. Change this function under instruction.
	 *
	 * @return boolean
	 */
	public boolean loginDB() {
		String username = "e1234567";//Replace e1234567 to your username
		String password = "e1234567";//Replace e1234567 to your password

		/* Do not change the code below */
		if(username.equalsIgnoreCase("e1234567") || password.equalsIgnoreCase("e1234567")) {
			String[] namePwd = getUsernamePassword("Login sqlplus");
			username = namePwd[0];
			password = namePwd[1];
		}
		String URL = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;

		try {
			System.out.println("Logging " + URL + " ...");
			conn = DriverManager.getConnection(URL, username, password);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Close the manager. Do not change this function.
	 */
	public void close() {
		System.out.println("Thanks for using library database! Bye...");
		try {
			if (conn != null)
				conn.close();
			if (proxySession != null) {
				proxySession.disconnect();
			}
			in.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor of Library manager Do not change this function.
	 */
	public LibraryDB() {
		System.out.println("Welcome to use this Library!");
		in = new Scanner(System.in);
	}

	/**
	 * Main function
	 *
	 * @param args
	 */


	/**
	 * Show the options. If you want to add one more option, put into the
	 * options array above.
	 */
	public void showOptions() {
		System.out.println("Please choose following option:");
		for (int i = 0; i < options.length; ++i) {
			System.out.println("(" + (i + 1) + ") " + options[i]);
		}
	}

	public void run() {
		while (noException) {
			showOptions();
			String line = in.nextLine();
			if (line.equalsIgnoreCase("exit"))
				return;
			int choice = -1;
			try {
				choice = Integer.parseInt(line);
			} catch (Exception e) {
				System.out.println("This option is not available");
				continue;
			}
			if (!(choice >= 1 && choice <= options.length)) {
				System.out.println("This option is not available");
				continue;
			}
			if (options[choice - 1].equals("Book search(by ISBN)")) {
				BookSearch();
			} else if (options[choice - 1].equals("Borrow a Book (by ISBN and SID)")) {
				BorrowBook();
			} else if (options[choice - 1].equals("Book Return(by ISBN and SID)")) {
				ReturnBook();
			} else if (options[choice - 1].equals("Book Renew(by ISBN and SID)")) {
				RenewBook();
			} else if (options[choice - 1].equals("Book Reserve(by ISBN and SID)")) {
				ReserveBook();
			} else if (options[choice - 1].equals("exit")) {
				break;
			}
		}
	}

	private String BookSearch() {

		System.out.println("Please input the ISBN:");
		String line = in.nextLine();
		line = line.trim();
		if (line.equalsIgnoreCase("exit"))
			return null;
		//printBookInfo(line);
		return line;
	}


	private void BorrowBook() {

		String isbn = BookSearch();
		printBookInfo(isbn);
		String stu_no = SidInput();
		System.out.println("You have borrowed "+BorrowCount(stu_no)+" books.");

		if(GetBookAmount(isbn)!=0) {
			if(Overdued(stu_no)==false) {
				if(BorrowCount(stu_no)<5) {
					if((FirstRes(isbn,stu_no)==true) || (BookReserved(isbn)==false) ) {

						try {
									Statement stm = conn.createStatement();
									String sql1 = "UPDATE RECORDS "
											+ "SET stu_no = '" + stu_no + "', borrow_date = CURRENT_TIMESTAMP "
											+" WHERE call_no >= ALL(SELECT call_no FROM RECORDS"
																+ " WHERE isbn = '"+isbn+"' AND stu_no is null) AND isbn = '"+isbn+"' ";


									String sql2 = " INSERT INTO DUE_RENEW "
											+ "VALUES ('" + isbn +"', '"+stu_no+"', 0, CURRENT_TIMESTAMP+INTERVAL'28'DAY) ";
									String sql3 = " DELETE FROM RESERVE"
												+" WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"' ";
									//System.out.println(sql);
									stm.executeQuery(sql1);
									stm.executeQuery(sql2);
									stm.executeQuery(sql3);
									stm.close();
									System.out.println("----------------------------------");
									System.out.println("Succeed to borrow the book: "+isbn);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.out.println(e.getMessage());
									System.out.println("Fail to borrow the book :( ");
									noException = false;
								}
					}else {
						System.out.println("firstres ="+FirstRes(isbn,stu_no)+ "bookreserved"+BookReserved(isbn));
						System.out.println("Reasons for borrow failure: The book is reserved by others");
					}
				}else {
					System.out.println("Reasons for borrow failure: You have borrowed more than 5 books");
				}
			}else {
				System.out.println("Reasons for borrow failure: You have overdue record");
			}
		}else {
			System.out.println("Reasons for borrow failure: Copy insufficience.");
		}

	}

	private void ReturnBook() {
		String stu_no = SidInput();
		printBorrowed(stu_no);
		String isbn = BookSearch();

		if(Borrowed(isbn,stu_no)==true) {
			try {
				Statement stm = conn.createStatement();
				String sql1 = "UPDATE RECORDS "
						+ "SET stu_no = null, borrow_date = null "
						+" WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"' ";

				String sql2 = " DELETE FROM DUE_RENEW "+" WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"' ";

				//System.out.println(sql);
				stm.executeQuery(sql1);
				stm.executeQuery(sql2);
				stm.close();
				System.out.println("----------------------------------");
				System.out.println("Succeed to return the book: "+isbn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
				System.out.println("Fail to return the book :( ");
				noException = false;
			}
		}else {
			System.out.println("There's no record of you borrowing this book.");
		}



	}

	private void RenewBook() {
		String stu_no = SidInput();
		printBorrowed(stu_no);
		String isbn = BookSearch();
		if(Renewed(isbn,stu_no)==false) {
			if(Overdued(stu_no)==false) {
				if(BookReserved(isbn)==false) {
					if(SecondPeriod(isbn,stu_no)==true) {
						try {
							Statement stm = conn.createStatement();
							String sql = "UPDATE DUE_RENEW "
									+ "SET renew_count=1, due = due + INTERVAL'14'DAY"
									+" WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"' ";

							//System.out.println(sql);
							stm.executeQuery(sql);
							stm.close();
							System.out.println("Succeed to renew the book :) ");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println(e.getMessage());
							System.out.println("Fail to renew the book :( ");
							noException = false;
						}
					}else {
						System.out.println("You are not in the second period or the borrow time. Renew it later.");
					}
				}else {
					System.out.println("Someone reserved the book. You can't renew the book.");
				}

			}else {
				System.out.println("You can't renew the book due to overdue record.");
			}
		}else {
			System.out.println("You have already renewed once.");
		}
	}

	private void ReserveBook() {
		String stu_no = SidInput();
		String isbn = BookSearch();
		printBookInfo(isbn);
		if(Borrowed(isbn,stu_no)==false) {
			if(GetBookAmount(isbn)==0) {
				if(MadeRes(stu_no)==false) {
					try {
						Statement stm = conn.createStatement();
						String sql = "INSERT INTO RESERVE "
								+ "VALUES ('"+isbn+"', '"+stu_no+"', CURRENT_TIMESTAMP)";

						//System.out.println(sql);
						stm.executeQuery(sql);
						stm.close();
						System.out.println("Succeed to reserve the book :) ");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(e.getMessage());
						System.out.println("Fail to reserve the book :( ");
						noException = false;
					}
				}else {
					System.out.println("Reservation failed: You can at most reserved one book.");
				}
			}else {
				System.out.println("Reservation failed: Copy available.");
			}
		}else {
			System.out.println("Reservation failed: You have already borrowed.");
		}
	}



	private String SidInput() {
		System.out.println("Please input the Student ID to enter:");
		String line = in.nextLine();
		line = line.trim();
		if (line.equalsIgnoreCase("exit")) {
			return null;
		}
		printSid(line);
		return line;
	}

	private void printBookInfo(String isbn) {
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT * FROM BOOKS WHERE isbn = '" + isbn + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			String[] info = { "isbn", "title", "author", "location", "amount"};
			for (int i = 0; i < 5; i++) { // Book table 5 attributes
				try {
					System.out.println(info[i] + " : " + rs.getString(i+1)); // attribute
																				// id
																				// starts
																				// with
																				// 1
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			noException = false;
		}
	}

	private void printSid(String stu_no) {
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT * FROM students WHERE stu_no = '" + stu_no + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			String[] info = { "student.no", "name"};
			for (int i = 0; i < 2; i++) {
				try {
					System.out.println(info[i] + " : " + rs.getString(i+1));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			noException = false;
		}
	}

	private void printBorrowed(String stu_no) {
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT isbn, borrow_date FROM RECORDS WHERE stu_no = '" + stu_no + "'";
			ResultSet rs = stm.executeQuery(sql);
			if (!rs.next())
				return;
			String[] info = { "isbn: ", "borrow time: "};
			for (int i = 0; i < 2; i++) {
				try {
					System.out.println(info[i] + " : " + rs.getString(i+1)); // attribute
																				// id
																				// starts
																				// with
																				// 1
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			noException = false;
		}
	}

	private int GetBookAmount(String isbn)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT amount FROM BOOKS "
					+ "WHERE isbn = '"+isbn+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				return result;
			}
			return (Integer) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Integer) null;
		}

	}

	private boolean Renewed(String isbn, String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = " SELECT renew_count FROM DUE_RENEW "
					+ " WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				if(result==0) {
					return false;
				}else {
					return true;
				}
			}
			return (Boolean) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}

	}

	private int BorrowCount(String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT count(*) FROM RECORDS "
					+ "WHERE stu_no = '" +stu_no+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				return result;
			}
			return (Integer) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Integer) null;
		}

	}

	private boolean Borrowed(String isbn, String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT count(*) FROM RECORDS "
					+ "WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				if(result != 0){
					return true;
				}else{
					return false;
				}
			}
			return (Boolean) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}

	}


	private boolean Overdued(String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT count(*) FROM DUE_RENEW "
					+ "WHERE current_timestamp > due AND stu_no = '"+stu_no+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				if(result != 0){
					return true;
				}else{
					return false;
				}
			}
			return (Boolean) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}
	}

	private boolean MadeRes(String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT count(*) FROM RESERVE "
					+ "WHERE stu_no = '" +stu_no+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				if(result != 0){
					return true;
				}else{
					return false;
				}
			}
			return (Boolean) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}
	}

	private boolean BookReserved(String isbn)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT count(*) FROM RESERVE "
					+ "WHERE isbn = '" +isbn+"'";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			int result = rs.getInt(1);
				if(result == 0 ){
					return false;
				}else{
					return true;
				}
			}
			return (Boolean) null;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}
	}

	private boolean FirstRes(String isbn, String stu_no)
	{
		try{
			Statement stm = conn.createStatement();
			String sql = "  SELECT stu_no FROM RESERVE "
					+ "WHERE isbn = '" +isbn+"' AND res_date = (SELECT MIN(res_date) FROM RESERVE "
														+ "WHERE isbn = '" +isbn+ "' )";

			ResultSet rs = stm.executeQuery(sql);

			while (rs.next())
			{
			String result = rs.getString(1).trim();
				//System.out.println("stu_no ="+stu_no + "result ="+result+"equals="+stu_no.equals(result));
				return stu_no.equals(result);

			}
			return false;
		}
		catch (SQLException e) {
		e.printStackTrace();
		noException = false;
		return (Boolean) null;
		}

	}

	 private boolean SecondPeriod(String isbn, String stu_no)
		{
			try{
				Statement stm = conn.createStatement();
				String sql = "  SELECT (due - INTERVAL'14'DAY) FROM DUE_RENEW "
							+ "WHERE isbn = '"+isbn+"' AND stu_no = '" +stu_no+"'";

				ResultSet rs = stm.executeQuery(sql);

				while (rs.next())
				{	Timestamp current = new Timestamp(System.currentTimeMillis());
					Timestamp result = rs.getTimestamp(1);
					if(Overdued(stu_no)==false) {
						return current.after(result);
					}else {
						return false;
					}
				}
				return (Boolean) null;
			}
			catch (SQLException e) {
			e.printStackTrace();
			noException = false;
			return (Boolean) null;
			}

		}

}
