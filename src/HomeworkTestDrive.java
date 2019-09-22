import java.util.Scanner;
import java.io.*;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class HomeworkTestDrive{
	//main函数用来展示一下该程序的使用说明
	public static void main(String[] args){
		//声明一个文件处理器的变量
		FileProcessor fileProcessor;
		while(true){
			System.out.println("-------------------WC.EXE-------------------");
			System.out.println("基本功能使用说明：");
			System.out.println("统计字符数:-c [fileName]");
			System.out.println("统计词数:-w [fileName]");
			System.out.println("统计行数:-l [fileName]");
			System.out.println("");
			System.out.println("拓展功能使用说明：");
			System.out.println("统计空行、代码行、注释行:-a [fileName]");
			System.out.println("递归处理目录下符合条件的文件:-s [fileName]");
			System.out.println("");
			System.out.println("高级功能使用说明：");
			System.out.println("图形化界面:-x");
			System.out.println("--------------------------------------------");
			
			System.out.println("请输入指令：");	
			Scanner command = new Scanner(System.in);
			//将用户输入的命令通过分离空格分成用户指令和文件分开存进数组中
			String[] arr = command.nextLine().split("\\s");
			int len = arr.length;
			//创建文件处理器对象，通过该对象执行用户指令
			fileProcessor = new FileProcessor();
			fileProcessor.operateByCommand(arr,len,0,arr[arr.length-1]);
		}
	}
}

class FileProcessor{
	public void operateByCommand(String[] arr,int len,int start,String fileName){
		if(arr[0].equals("-x")){
			WcGui wcGui = new WcGui();
			wcGui.go();
		}else{
			try{
				for(int i = start;i < len-1;i++){
					//使用GBK编码
					String encoding = "GBK";
					//根据文件名创建File对象
					File file = new File(fileName);
					//传入FileInputStream和指定编码创建InputStream对象
					InputStreamReader readFile = new InputStreamReader(new FileInputStream(file),encoding);
					//利用BufferedReader对象可以比较方便地统计字符数、行数等等
					BufferedReader fileContent = new BufferedReader(readFile);
					//根据不同的用户指令选择要调用的方法
					switch(arr[i]){
						case "-c":
							countChars(fileContent);
							break;
						case "-w":
							countWords(fileContent);
							break;
						case "-l":
							countLines(fileContent);
							break;
						case "-a":
							countSpecial(fileContent);
							break;
						case "-s":
							recursiveProcessing(arr);
							break;
						default:
							System.out.println("哥哥...输入上面说明的正确指令啊......");
					}
				}
			}catch(Exception e){
				System.out.println("亲，这边找不到文件呢~");
				e.printStackTrace();
			}	
		}	
	}
	
	public static int countChars(BufferedReader fileContent) throws IOException{
		//记录文件每一行的内容
		String lineContent = null;
		//定义一个变量记录字符数
		int charNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			//调用trim()方法，去掉字符串两端的空格，方便统计
			lineContent = lineContent.trim();
			for(int i = 0;i < lineContent.length();i++){
				//利用循环依此获取每个字符
				char ch = lineContent.charAt(i);
				//如果不是空格、换行符、制表符就将charNum加1
				if(ch != '\n' && ch != '\t' && ch != ' ')
					charNum++;
			}
		}
		System.out.println("字符数：" + charNum);
		return charNum;
	}
	
	public static int countWords(BufferedReader fileContent) throws IOException{
		//定义一个正则表达式
		String REGEX = "[a-zA-Z]+\\b";
		//定义一个匹配模式
		Pattern pattern = Pattern.compile(REGEX);
		//记录文件每一行的内容
		String lineContent = null;
		//定义一个变量记录词数
		int wordNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			lineContent = lineContent.trim();
			Matcher matcher = pattern.matcher(lineContent);
			while(matcher.find()){
				wordNum++;
			}
		}
		System.out.println("词数：" + wordNum);
		return wordNum;
	}
	
	public static int countLines(BufferedReader fileContent) throws IOException{
		//记录文件每一行的内容
		String lineContent = null;
		//定义一个变量记录行数
		int lineNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			//只要这行不为空，则lineNum加1
			lineNum++;
		}
		System.out.println("行数：" + lineNum);
		return lineNum;
	}
	
	public static ArrayList<Integer> countSpecial(BufferedReader fileContent)throws IOException{
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		
		String lineContent = null; //用来保存每行的内容
		boolean isComment = false; //记录当前行是否属于注释状态
		
		int codeLineNum = 0; //记录代码行数
		int blankLineNum = 0; //记录空行数
		int annotationLineNum = 0; //记录注释行数
		
		while((lineContent = fileContent.readLine()) != null){
			//多行注释的开始
			if(lineContent.contains("/*")){
				annotationLineNum++;
				isComment = true;
			}else if(isComment){
				annotationLineNum++;
				//多行注释的结束
				if(lineContent.contains("*/")){
					isComment = false;
				}
			}else if(lineContent.contains("//")){
				//单行注释，注释行数加1
				annotationLineNum++;
			}else if(lineContent.trim().length() > 1){
				codeLineNum++;
			}else{
				blankLineNum++;
			}
		}
		
		System.out.println("空行数：" + blankLineNum);
		System.out.println("代码行数：" + codeLineNum);
		System.out.println("注释行数：" + annotationLineNum);
		resultList.add(blankLineNum);
		resultList.add(codeLineNum);
		resultList.add(annotationLineNum);
		return resultList;
	}
	
	public static void recursiveProcessing(String[] arr) throws IOException{	
		String fileDir = arr[arr.length-1].substring(0,arr[arr.length-1].lastIndexOf("\\"));
		String fileFilter = arr[arr.length-1].substring(arr[arr.length-1].lastIndexOf("."));
 	
        List<File> fileList = new ArrayList<File>();
        File file = new File(fileDir);// 指定查找目录
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        // 遍历files中的所有文件
        for (File f : files) {
            if (f.isFile()&&f.getName().endsWith(fileFilter)) {
                fileList.add(f);
                //System.out.println(f.getName());
            }
        }
        for (File f1 : fileList) {
			System.out.println(f1.getName());
			for(int i = 0;i < 4;i++){
				/*原来发现不加这个循环，四个方法只有统计字符有结果，我觉得是BufferedReader的readLine()方法
				  一直读取下一行的原因，所以增加了一个内循环，每次都重新生成新的BufferedReader，四个方法都能得到结果。*/
				String encoding = "GBK";
				InputStreamReader readFile = new InputStreamReader(new FileInputStream(f1),encoding);
				BufferedReader fileContent = new BufferedReader(readFile);
				switch(i){
					case 0:
						countChars(fileContent);
						break;
					case 1:
						countWords(fileContent);
						break;
					case 2:
						countLines(fileContent);
						break;
					case 3:
						countSpecial(fileContent);
						break;
				}
			}
			//画个分界线看起来清楚些
			System.out.println("-----------------------------");
        }
	}
}

class WcGui implements ActionListener{
	JFrame frame;
	JTextArea textArea;
	int charNum = 0; //用来接收上面统计字符数方法的返回值
	int wordNum = 0; //用来接收上面统计词数方法的返回值
	int lineNum = 0; //用来接收上面统计行数方法的返回值
	ArrayList<Integer> resultList = null; //接收上面统计特殊行数目方法的返回值，调用get()方法给下面三个变量赋值
	int blankLineNum = 0; 
	int codeLineNum = 0; 
	int annotationLineNum = 0;
	
	//简单的Gui，创建JFrame对象、用来显示结果的TextArea、以及一个button用来监听
	public void go(){
		frame = new JFrame("文件统计GUI"); 
		textArea = new JTextArea();
		frame.getContentPane().add(BorderLayout.CENTER,textArea);
		
		JButton button = new JButton("选择文件并进行各类统计");
		frame.getContentPane().add(BorderLayout.SOUTH,button);
		button.addActionListener(this);
		
		frame.setSize(500,500);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event){
		JFileChooser chooser = new JFileChooser(); //创建JFileChooser对象用来选择要统计的文件
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //限定被选文件形式
        chooser.showDialog(new JLabel(), "选择"); //选择框
        File file = chooser.getSelectedFile(); //获取文件
		for(int i = 0;i < 4;i++){ //调用上面写好的方法，并将返回值赋给相应变量
			try{
				String encoding = "GBK";
				InputStreamReader readFile = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader fileContent = new BufferedReader(readFile);
				switch(i){
				case 0:
					charNum = FileProcessor.countChars(fileContent);
					break;
				case 1:
					wordNum = FileProcessor.countWords(fileContent);
					break;
				case 2:
					lineNum = FileProcessor.countLines(fileContent);
					break;
				case 3:
					resultList = FileProcessor.countSpecial(fileContent);
					blankLineNum = resultList.get(0);
					codeLineNum = resultList.get(1);
					annotationLineNum = resultList.get(2);
					break;
				}			
			}catch(IOException e){
					System.out.println("你的文件到火星去啦~");
					e.printStackTrace();
			}
		}
		//将结果展示在TextArea上
		textArea.append("字符数：" + charNum + "\n词数：" + wordNum + "\n行数：" + lineNum + "\n空行数：" + blankLineNum + "\n代码行数：" + codeLineNum + "\n注释行数" + annotationLineNum);	
	}
}