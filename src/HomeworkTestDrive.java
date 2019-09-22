import java.util.Scanner;
import java.io.*;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class HomeworkTestDrive{
	//main��������չʾһ�¸ó����ʹ��˵��
	public static void main(String[] args){
		//����һ���ļ��������ı���
		FileProcessor fileProcessor;
		while(true){
			System.out.println("-------------------WC.EXE-------------------");
			System.out.println("��������ʹ��˵����");
			System.out.println("ͳ���ַ���:-c [fileName]");
			System.out.println("ͳ�ƴ���:-w [fileName]");
			System.out.println("ͳ������:-l [fileName]");
			System.out.println("");
			System.out.println("��չ����ʹ��˵����");
			System.out.println("ͳ�ƿ��С������С�ע����:-a [fileName]");
			System.out.println("�ݹ鴦��Ŀ¼�·����������ļ�:-s [fileName]");
			System.out.println("");
			System.out.println("�߼�����ʹ��˵����");
			System.out.println("ͼ�λ�����:-x");
			System.out.println("--------------------------------------------");
			
			System.out.println("������ָ�");	
			Scanner command = new Scanner(System.in);
			//���û����������ͨ������ո�ֳ��û�ָ����ļ��ֿ����������
			String[] arr = command.nextLine().split("\\s");
			int len = arr.length;
			//�����ļ�����������ͨ���ö���ִ���û�ָ��
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
					//ʹ��GBK����
					String encoding = "GBK";
					//�����ļ�������File����
					File file = new File(fileName);
					//����FileInputStream��ָ�����봴��InputStream����
					InputStreamReader readFile = new InputStreamReader(new FileInputStream(file),encoding);
					//����BufferedReader������ԱȽϷ����ͳ���ַ����������ȵ�
					BufferedReader fileContent = new BufferedReader(readFile);
					//���ݲ�ͬ���û�ָ��ѡ��Ҫ���õķ���
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
							System.out.println("���...��������˵������ȷָ�......");
					}
				}
			}catch(Exception e){
				System.out.println("�ף�����Ҳ����ļ���~");
				e.printStackTrace();
			}	
		}	
	}
	
	public static int countChars(BufferedReader fileContent) throws IOException{
		//��¼�ļ�ÿһ�е�����
		String lineContent = null;
		//����һ��������¼�ַ���
		int charNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			//����trim()������ȥ���ַ������˵Ŀո񣬷���ͳ��
			lineContent = lineContent.trim();
			for(int i = 0;i < lineContent.length();i++){
				//����ѭ�����˻�ȡÿ���ַ�
				char ch = lineContent.charAt(i);
				//������ǿո񡢻��з����Ʊ���ͽ�charNum��1
				if(ch != '\n' && ch != '\t' && ch != ' ')
					charNum++;
			}
		}
		System.out.println("�ַ�����" + charNum);
		return charNum;
	}
	
	public static int countWords(BufferedReader fileContent) throws IOException{
		//����һ��������ʽ
		String REGEX = "[a-zA-Z]+\\b";
		//����һ��ƥ��ģʽ
		Pattern pattern = Pattern.compile(REGEX);
		//��¼�ļ�ÿһ�е�����
		String lineContent = null;
		//����һ��������¼����
		int wordNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			lineContent = lineContent.trim();
			Matcher matcher = pattern.matcher(lineContent);
			while(matcher.find()){
				wordNum++;
			}
		}
		System.out.println("������" + wordNum);
		return wordNum;
	}
	
	public static int countLines(BufferedReader fileContent) throws IOException{
		//��¼�ļ�ÿһ�е�����
		String lineContent = null;
		//����һ��������¼����
		int lineNum = 0;
		while((lineContent = fileContent.readLine()) != null){
			//ֻҪ���в�Ϊ�գ���lineNum��1
			lineNum++;
		}
		System.out.println("������" + lineNum);
		return lineNum;
	}
	
	public static ArrayList<Integer> countSpecial(BufferedReader fileContent)throws IOException{
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		
		String lineContent = null; //��������ÿ�е�����
		boolean isComment = false; //��¼��ǰ���Ƿ�����ע��״̬
		
		int codeLineNum = 0; //��¼��������
		int blankLineNum = 0; //��¼������
		int annotationLineNum = 0; //��¼ע������
		
		while((lineContent = fileContent.readLine()) != null){
			//����ע�͵Ŀ�ʼ
			if(lineContent.contains("/*")){
				annotationLineNum++;
				isComment = true;
			}else if(isComment){
				annotationLineNum++;
				//����ע�͵Ľ���
				if(lineContent.contains("*/")){
					isComment = false;
				}
			}else if(lineContent.contains("//")){
				//����ע�ͣ�ע��������1
				annotationLineNum++;
			}else if(lineContent.trim().length() > 1){
				codeLineNum++;
			}else{
				blankLineNum++;
			}
		}
		
		System.out.println("��������" + blankLineNum);
		System.out.println("����������" + codeLineNum);
		System.out.println("ע��������" + annotationLineNum);
		resultList.add(blankLineNum);
		resultList.add(codeLineNum);
		resultList.add(annotationLineNum);
		return resultList;
	}
	
	public static void recursiveProcessing(String[] arr) throws IOException{	
		String fileDir = arr[arr.length-1].substring(0,arr[arr.length-1].lastIndexOf("\\"));
		String fileFilter = arr[arr.length-1].substring(arr[arr.length-1].lastIndexOf("."));
 	
        List<File> fileList = new ArrayList<File>();
        File file = new File(fileDir);// ָ������Ŀ¼
        File[] files = file.listFiles();// ��ȡĿ¼�µ������ļ����ļ���
        if (files == null) {// ���Ŀ¼Ϊ�գ�ֱ���˳�
            return;
        }
        // ����files�е������ļ�
        for (File f : files) {
            if (f.isFile()&&f.getName().endsWith(fileFilter)) {
                fileList.add(f);
                //System.out.println(f.getName());
            }
        }
        for (File f1 : fileList) {
			System.out.println(f1.getName());
			for(int i = 0;i < 4;i++){
				/*ԭ�����ֲ������ѭ�����ĸ�����ֻ��ͳ���ַ��н�����Ҿ�����BufferedReader��readLine()����
				  һֱ��ȡ��һ�е�ԭ������������һ����ѭ����ÿ�ζ����������µ�BufferedReader���ĸ��������ܵõ������*/
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
			//�����ֽ��߿��������Щ
			System.out.println("-----------------------------");
        }
	}
}

class WcGui implements ActionListener{
	JFrame frame;
	JTextArea textArea;
	int charNum = 0; //������������ͳ���ַ��������ķ���ֵ
	int wordNum = 0; //������������ͳ�ƴ��������ķ���ֵ
	int lineNum = 0; //������������ͳ�����������ķ���ֵ
	ArrayList<Integer> resultList = null; //��������ͳ����������Ŀ�����ķ���ֵ������get()��������������������ֵ
	int blankLineNum = 0; 
	int codeLineNum = 0; 
	int annotationLineNum = 0;
	
	//�򵥵�Gui������JFrame����������ʾ�����TextArea���Լ�һ��button��������
	public void go(){
		frame = new JFrame("�ļ�ͳ��GUI"); 
		textArea = new JTextArea();
		frame.getContentPane().add(BorderLayout.CENTER,textArea);
		
		JButton button = new JButton("ѡ���ļ������и���ͳ��");
		frame.getContentPane().add(BorderLayout.SOUTH,button);
		button.addActionListener(this);
		
		frame.setSize(500,500);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event){
		JFileChooser chooser = new JFileChooser(); //����JFileChooser��������ѡ��Ҫͳ�Ƶ��ļ�
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); //�޶���ѡ�ļ���ʽ
        chooser.showDialog(new JLabel(), "ѡ��"); //ѡ���
        File file = chooser.getSelectedFile(); //��ȡ�ļ�
		for(int i = 0;i < 4;i++){ //��������д�õķ�������������ֵ������Ӧ����
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
					System.out.println("����ļ�������ȥ��~");
					e.printStackTrace();
			}
		}
		//�����չʾ��TextArea��
		textArea.append("�ַ�����" + charNum + "\n������" + wordNum + "\n������" + lineNum + "\n��������" + blankLineNum + "\n����������" + codeLineNum + "\nע������" + annotationLineNum);	
	}
}