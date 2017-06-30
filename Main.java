import java.io.*;
import org.apache.commons.codec.binary.*;

public class Main
{
	public static void main(String[] args) throws IOException, InterruptedException
	{

		MQl mql =  new MQl();
		mql.CreateDataBase("test","root");
    	mql.CreateTable(new String[]{"users","id","name","age"});
		mql.Connect("test","root");
		if(!mql.MQlConnected) {throw new Error(mql.MQlError);}
		mql.UseTable("users");
		mql.Query("id");
		String[] data = mql.DataAsArray;
		int index = 0;
	//	while(index<data.length)	{  DoStuff	}
		mql.EmptyTable();
		mql.InsertData(new String[]{"0","root"});
	//  mql.Reset();
		mql.Disconnect();
	//	System.out.print(mql.MQlConnected);

	}
}
