import java.io.*;
import org.apache.commons.codec.binary.*;
import java.util.*;


public  class MQl_provider
{
	protected BufferedWriter bw = null;
	protected BufferedWriter tbw = null;
	protected String _error_ = "";
	protected String DB_name = "";
	protected void __open_new(String tb_name) throws IOException
	{
		if(this.DB_name.length() == 0) {  throw new Error("[MQl Data Store Error] connect object isn't defined yet");}
		FileWriter fw = new FileWriter("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+this.DB_name+"/"+tb_name+".table",true);   this.tbw = new BufferedWriter(fw); 
	}
	private boolean __open_db__(String db_name) throws IOException
	{
		
		try{  this.DB_name = db_name; File i = new File("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+db_name+""); i.mkdir();  FileWriter fw = new FileWriter("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+db_name+"/"+db_name+".db",true);   this.bw = new BufferedWriter(fw); return true;}
		catch(Exception e) {System.out.println("failed to create MQl database"); return false;}
	}
	protected String _encode_data_(String p)
	{
		byte[] b = Base64.encodeBase64(p.getBytes());
		return new String(b);
	}
	protected boolean __open__(String name,String password) throws IOException
	{
		boolean ff = false;
		try
		{
			this.__open_db__(name); 
			FileReader f = new FileReader("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+name+"/"+name+".db");
			BufferedReader br = new BufferedReader(f);
			String l = "",r = "";
			while((l = br.readLine()) != null)
			{
				r += l+"\n";
			}
			if(r.length() != 0&& r.split("\n")[0].split(":")[0].equals("pass"))
			{

			}else {
				this.bw.write("pass:"+new String(Base64.encodeBase64(password.getBytes()))+"\n"); this.bw.flush();
			}
			ff = true; 
		}
		catch(Exception e)
		{this._error_ = "[MQl CreateDataBase Error] can't create or open the database"; ff = false;System.out.println(e.getMessage());}
		return ff;
	}
	protected String __update_db_refrence__() throws IOException
	{
		if(this.DB_name.length() == 0 || this.bw == null) {throw new Error("[MQl Update DB Refrence Error] connect object isn't defined yet");}
		FileReader f = new FileReader("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+this.DB_name+"/"+this.DB_name+".db");
		BufferedReader br = new BufferedReader(f);
		String l = "",r = "";
		while((l = br.readLine()) != null)
		{
			r += l+"\n";
		}
		return r;
	}
	protected String __read_lines__(String p) throws IOException
	{
		FileReader f = new FileReader("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+this.DB_name+"/"+p);
		BufferedReader br = new BufferedReader(f);
		String l = "",r = "";
		while((l = br.readLine()) != null)
		{
			r += l+"\n";
		}
		return r;
	}
	protected String _decode_data_(String p)
	{
		return new String(Base64.decodeBase64(p.getBytes()));
	}
}
