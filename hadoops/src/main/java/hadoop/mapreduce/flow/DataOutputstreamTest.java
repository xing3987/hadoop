package hadoop.mapreduce.flow;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class DataOutputstreamTest {
	
	public static void main(String[] args) throws Exception {
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream("G:\\datas\\input\\flow\\a.dat"));
		
		dos.write("我爱你".getBytes("utf-8"));
		
		dos.close();
		
		
		DataOutputStream dos2 = new DataOutputStream(new FileOutputStream("G:\\datas\\input\\flow\\b.dat"));
		
		dos2.writeUTF("我爱你");
		
		dos2.close();
	}

}
