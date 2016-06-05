package dictionary;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicFrame extends JFrame {
	// Components for searching function
	JLabel read_label;
	JTextField read_tf;
	JButton read_button;
	// Components for adding function
	JLabel add_label;
	JTextField add_tf;
	JButton add_button;
	// Components for deleting function
	JLabel del_label;
	JTextField del_tf;
	JButton del_button;
	
	String filename;
	
	public DicFrame(String filename) {
		// Set layout with 3x3 Grid
		setLayout(new GridLayout(3, 3));
		this.filename = filename;
        // label font
        Font g = new Font("Gothic", Font.PLAIN, 20);
        Font g2 = new Font("Gothic", Font.BOLD, 15);
        // Text field font
        Font h = new Font("serif", Font.PLAIN, 20);
        // Button font
        Font f = new Font("Gothic", Font.BOLD, 40);
        
		// Search Meaning
		read_label = new JLabel("search meaning");
		read_label.setFont(g);
		add(read_label);
		read_tf = new JTextField(20);
		read_tf.setFont(f);
		add(read_tf);
		read_button = new JButton("SEARCH");
		read_button.setFont(f);
		add(read_button);
		SearchMeaning e1 = new SearchMeaning();
		read_button.addActionListener(e1);
		
		// Add Texts
		add_label = new JLabel("add new word <word: meaning>");
		add_label.setFont(g2);
		add(add_label);
		add_tf = new JTextField(50);
		add_tf.setFont(h);
		add(add_tf);
		add_button = new JButton("ADD");
		add_button.setFont(f);
		add(add_button);
		AddNewOne e2 = new AddNewOne();
		add_button.addActionListener(e2);
		
		// Delete Texts
		del_label = new JLabel("delete word");
		del_label.setFont(g);
		add(del_label);
		del_tf = new JTextField(20);
		del_tf.setFont(f);
		add(del_tf);
		del_button = new JButton("DELETE");
		del_button.setFont(f);
		add(del_button);
		DelFromDic e3 = new DelFromDic();
		del_button.addActionListener(e3);
	}
	
	public class SearchMeaning implements ActionListener {
		public void actionPerformed(ActionEvent e1) {
			try {
				String readWord = read_tf.getText().toLowerCase();
				HashMap<String, String> map = 
						 (HashMap<String, String>) makeHashMap(filename);
				// Find value in map with read word as key
				if(map.containsKey(readWord)) {
					JOptionPane.showMessageDialog(null, map.get(readWord));
				} else {
					JOptionPane.showMessageDialog(null, "The word doesn't exist!");
				}
			}	catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class AddNewOne implements ActionListener {
		public void actionPerformed(ActionEvent e2) {
			try {
				String addedWord = add_tf.getText();
				// Exit function if input form is incorrect
				if(!addedWord.contains(": ")) {
					JOptionPane.showMessageDialog(null, "please write in correct form.");
					return;
				}
				HashMap<String, String> map = 
						 (HashMap<String, String>) makeHashMap(filename);
				// Get key by splitting added word.
				String key = addedWord.split(": ")[0];
				// If there is a word same with key (already exist), deleted it and add input as new word.
				if(map.containsKey(key)) {
					try {
						deleteLine(filename, key);
					    JOptionPane.showMessageDialog(null, "Your word has been updated!");
					}catch (Exception e) {
					    e.printStackTrace();
					}
				}
				// Add word and meaning to text file.
				try {
				    Files.write(Paths.get(filename), 
				    		(addedWord + "\n").toLowerCase().getBytes(), StandardOpenOption.APPEND);
				    JOptionPane.showMessageDialog(null, "Your word has been saved!");
				}catch (IOException e) {
				    e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class DelFromDic implements ActionListener {
		public void actionPerformed(ActionEvent e3) {
			try {
				String deledWord = del_tf.getText().toLowerCase();
				HashMap<String, String> map = 
						 (HashMap<String, String>) makeHashMap(filename);
				// Find the pair of word and meaning and delete it from text file.
				if(map.containsKey(deledWord)) {
					deleteLine(filename, deledWord);
					JOptionPane.showMessageDialog(null, "Your word has been deleted.");
				} else {
					JOptionPane.showMessageDialog(null, "The word doesn't exist!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Generating a Hash map with list of Strings from text file
	public Map<String, String> makeHashMap(String filename) {
		try {
			List<String> lines = Files.readAllLines(
					Paths.get("", filename), StandardCharsets.UTF_8);
			Map<String, String> map = new HashMap<>();
			for(String line : lines) {
				String[] entry = line.toLowerCase().split(": ");
				map.put(entry[0], entry[1]);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void deleteLine(String filename, String key) {
		HashMap<String, String> map = 
				 (HashMap<String, String>) makeHashMap(filename);
		String toDel = key + ": " + map.get(key);
		try {
			File file = new File(filename);
			// Make a temporary file which will replace the existing text file.
			File tmp = File.createTempFile("file", ".txt", file.getParentFile());
			// Read line from existing file
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "UTF-8"));
			// Write line to Temporary file
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"));
			for(String line; (line = reader.readLine()) != null;) {
				// If the word to delete is found, write temporary file null and pass to next line
				if(line.equals(toDel)) {
					writer.print("");
					continue;
				}
				writer.println(line);
			}
			reader.close();
			writer.close();
			// Delete existing file and rename temporary file as same as deleted one.
			file.delete();
			tmp.renameTo(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String filename = "file.txt";
		try {
	      File file = new File(filename);
	      
	      if (file.createNewFile()) {
	        System.out.println("File is created!");
	      } else {
	        System.out.println("File already exists.");
	      }
    	} catch (IOException e) {
    		e.printStackTrace();
		}
		DicFrame gui = new DicFrame(filename);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(800, 350);
		gui.setTitle("Interactive Dictionary");
		gui.setVisible(true);
	}
}
