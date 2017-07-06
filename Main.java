import java.io.*;
import org.apache.commons.codec.binary.*;
import java.util.*;

public class Main
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		double f = new Date().getSeconds();
		MQl m = new MQl();
		m.Connect("test","root");
		m.UseTable("users");
	//	m.InsertData(new String[]{"0","mohammad","18"});
		String[][] data = m.CollectDataTable(new String[]{"name","age"},MQlQuery.First);
		System.out.println(data[0][0]);
		m.Disconnect();
		System.out.println((double)new Date().getSeconds()-f);
	}
}
